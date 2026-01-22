package com.xinling.ai.config;

import com.xinling.ai.service.DatabaseRagService;
import com.xinling.ai.service.DocumentLoaderService;
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
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AiConfigProperties aiConfigProperties;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${spring.application.name:xinling-app}")
    private String appName;

    // Redis中存储初始化状态的键
    private static final String RAG_INITIALIZED_KEY = "ai:rag:initialized";

    /**
     * 应用启动时初始化RAG数据
     */
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void initializeRagData() {
        log.info("开始检查RAG数据初始化状态，应用: {}, 端口: {}", appName, serverPort);

        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        // 使用setIfAbsent原子操作确保只有一个实例能设置初始化状态
        //Boolean setResult = valueOps.setIfAbsent(RAG_INITIALIZED_KEY, "initializing", 300, TimeUnit.SECONDS);
        Boolean setResult = true;
        if (setResult != null && setResult) {
            // 成功获得锁，执行初始化
            try {
                log.info("获得初始化权限，开始初始化RAG数据，应用: {}, 端口: {}", appName, serverPort);

                // 初始化数据库文档
                databaseRagService.initializeDatabaseDocuments();

                // 初始化知识库文档
                documentLoaderService.loadKnowledgeBaseDocuments(databaseRagService.getEmbeddingStore());

                // 更新状态为已完成，使用配置的TTL时间
                // valueOps.set(RAG_INITIALIZED_KEY, "completed", aiConfigProperties.getRag().getInitializationTtlHours(), TimeUnit.HOURS);

                log.info("RAG数据初始化完成，应用: {}, 端口: {}，TTL: {}小时", appName, serverPort, aiConfigProperties.getRag().getInitializationTtlHours());
            } catch (Exception e) {
                log.error("RAG数据初始化过程中发生错误，应用: {}, 端口: {}", appName, serverPort, e);
                // 如果初始化失败，删除状态键，允许其他实例重试
                redisTemplate.delete(RAG_INITIALIZED_KEY);
            }
        } else {
            // 未能获得锁，说明其他实例已经在初始化
            String currentStatus = valueOps.get(RAG_INITIALIZED_KEY);
            log.info("RAG数据已在其他实例中初始化（状态: {}），跳过重复初始化，应用: {}, 端口: {}",
                currentStatus, appName, serverPort);
        }
    }
}
