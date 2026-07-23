package com.xinling.ai.service;

import com.xinling.ai.config.AiConfigProperties;
import com.xinling.common.constant.RedisKeys;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 向量存储初始化状态管理
 * LangChain4j 1.10.0 移除了 InMemoryEmbeddingStore 的序列化 API，
 * 改为通过 Redis 标记 + TTL 判断是否需要重新初始化
 *
 * @author SuXia
 * @date 2026/05/21
 */
@Slf4j
@Service
public class EmbeddingStorePersistenceService {

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AiConfigProperties aiConfigProperties;

    /**
     * 检查向量存储是否已初始化
     */
    public boolean isInitialized() {
        String status = redisTemplate.opsForValue().get(RedisKeys.RAG_INITIALIZED_KEY);
        return "completed".equals(status);
    }

    /**
     * 获取向量存储中已加载的条目数量
     */
    public String getStoreInfo() {
        return "EmbeddingStore type: " + embeddingStore.getClass().getSimpleName();
    }
}
