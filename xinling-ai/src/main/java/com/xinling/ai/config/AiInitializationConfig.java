package com.xinling.ai.config;

import com.xinling.ai.service.DatabaseRagService;
import com.xinling.ai.service.DocumentLoaderService;
import com.xinling.ai.service.EmbeddingStorePersistenceService;
import com.xinling.common.constant.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.TimeUnit;

/**
 * AI初始化配置
 * 启动时优先从文件恢复向量，未恢复时才执行完整初始化（分布式锁保护）
 *
 * @author SuXia
 * @date 2025/12/29
 */
@Slf4j
@Configuration
@EnableAsync
@ConditionalOnProperty(name = "ai.rag.enable-initialization", havingValue = "true", matchIfMissing = true)
public class AiInitializationConfig {

    @Autowired
    private DatabaseRagService databaseRagService;

    @Autowired
    private DocumentLoaderService documentLoaderService;

    @Autowired
    private EmbeddingStorePersistenceService persistenceService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AiConfigProperties aiConfigProperties;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${spring.application.name:xinling-app}")
    private String appName;

    /**
     * 应用启动时初始化RAG数据
     * 优先从本地文件恢复向量，未恢复时才走分布式锁 + 完整初始化
     */
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void initializeRagData() {
        log.info("开始检查RAG数据初始化状态，应用: {}, 端口: {}", appName, serverPort);

        // 检查是否已完成初始化（Redis状态标记 + TTL）
        if (persistenceService.isInitialized()) {
            log.info("向量存储已初始化（Redis状态标记有效），跳过重复初始化，应用: {}, 端口: {}", appName, serverPort);
            return;
        }

        // 未初始化，走分布式锁 + 完整初始化
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        Boolean setResult = valueOps.setIfAbsent(RedisKeys.RAG_INITIALIZED_KEY, "initializing", 300, TimeUnit.SECONDS);
        if (setResult != null && setResult) {
            try {
                log.info("获得初始化权限，开始初始化RAG数据，应用: {}, 端口: {}", appName, serverPort);

                // 初始化数据库文档
                databaseRagService.initializeDatabaseDocuments();

                // 初始化知识库文档
                documentLoaderService.loadKnowledgeBaseDocuments();

                // 更新状态为已完成
                valueOps.set(RedisKeys.RAG_INITIALIZED_KEY, "completed",
                        aiConfigProperties.getRag().getInitializationTtlHours(), TimeUnit.HOURS);

                log.info("RAG数据初始化完成，应用: {}, 端口: {}，TTL: {}小时",
                        appName, serverPort, aiConfigProperties.getRag().getInitializationTtlHours());
            } catch (Exception e) {
                log.error("RAG数据初始化失败，应用: {}, 端口: {}。可稍后手动初始化或重启 Ollama 后重试",
                        appName, serverPort, e);
                redisTemplate.delete(RedisKeys.RAG_INITIALIZED_KEY);
            }
        } else {
            String currentStatus = valueOps.get(RedisKeys.RAG_INITIALIZED_KEY);
            log.info("RAG数据已在其他实例中初始化（状态: {}），跳过，应用: {}, 端口: {}",
                    currentStatus, appName, serverPort);
        }
    }
}
