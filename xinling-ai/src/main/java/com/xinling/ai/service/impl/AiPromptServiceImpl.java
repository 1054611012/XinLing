package com.xinling.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.xinling.ai.domain.config.AiPrompt;
import com.xinling.ai.mapper.AiPromptMapper;
import com.xinling.ai.mapper.AiSessionConfigMapper;
import com.xinling.ai.service.IAiPromptService;
import com.xinling.common.constant.RedisKeys;
import com.xinling.common.core.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * AI提示词Service业务层处理（带Redis缓存）
 *
 * 缓存策略：
 * - 首次按 configId 查询时，从DB读然后缓存到Redis
 * - TTL 1小时，到期自动刷新
 * - 增删改操作会清除相关缓存
 *
 * @author SuXia
 */
@Slf4j
@Service
public class AiPromptServiceImpl implements IAiPromptService {

    /** 提示词缓存TTL（秒） */
    private static final Integer CACHE_TTL_SECONDS = 3600;

    @Autowired
    private AiPromptMapper aiPromptMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private AiSessionConfigMapper aiSessionConfigMapper;

    @Override
    public List<AiPrompt> selectAiPromptList(AiPrompt aiPrompt) {
        return aiPromptMapper.selectAiPromptList(aiPrompt);
    }

    @Override
    public AiPrompt selectAiPromptById(Long promptId) {
        return aiPromptMapper.selectAiPromptById(promptId);
    }

    @Override
    public List<AiPrompt> selectPromptsByConfigId(Long configId) {
        String cacheKey = RedisKeys.AI_PROMPTS_CONFIG + configId;
        // 查缓存
        String cached = redisCache.getCacheObject(cacheKey);
        if (cached != null) {
            return JSON.parseArray(cached, AiPrompt.class);
        }
        // 缓存未命中，查DB
        List<AiPrompt> prompts = aiPromptMapper.selectPromptsByConfigId(configId);
        // 写入缓存
        redisCache.setCacheObject(cacheKey, JSON.toJSONString(prompts), CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        return prompts;
    }

    @Override
    public List<AiPrompt> selectEnabledPromptsByConfigId(Long configId, Long modelId) {
        String cacheKey = RedisKeys.AI_PROMPTS_CONFIG_MODEL + configId + ":" + modelId;
        // 查缓存
        String cached = redisCache.getCacheObject(cacheKey);
        if (cached != null) {
            return JSON.parseArray(cached, AiPrompt.class);
        }
        // 缓存未命中，查DB
        List<AiPrompt> prompts = aiPromptMapper.selectEnabledPromptsByConfigId(configId, modelId);
        // 写入缓存
        redisCache.setCacheObject(cacheKey, JSON.toJSONString(prompts), CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        return prompts;
    }

    @Override
    public int insertAiPrompt(AiPrompt aiPrompt) {
        int rows = aiPromptMapper.insertAiPrompt(aiPrompt);
        log.info("新增提示词: {}", aiPrompt.getPromptName());
        return rows;
    }

    @Override
    public int updateAiPrompt(AiPrompt aiPrompt) {
        int rows = aiPromptMapper.updateAiPrompt(aiPrompt);
        // 清除关联该提示词的配置缓存
        clearCacheByPromptId(aiPrompt.getPromptId());
        log.info("修改提示词: {}, 已清除相关缓存", aiPrompt.getPromptName());
        return rows;
    }

    @Override
    public int deleteAiPromptByIds(Long[] promptIds) {
        // 先清理会话配置中对该提示词的关联
        for (Long promptId : promptIds) {
            aiSessionConfigMapper.deletePromptRelationsByPromptId(promptId);
        }
        int rows = aiPromptMapper.deleteAiPromptByIds(promptIds);
        for (Long promptId : promptIds) {
            clearCacheByPromptId(promptId);
        }
        log.info("删除提示词: {}, 已清理关联和缓存", promptIds);
        return rows;
    }

    @Override
    public boolean checkPromptNameUnique(AiPrompt aiPrompt) {
        AiPrompt query = new AiPrompt();
        query.setPromptName(aiPrompt.getPromptName());
        List<AiPrompt> list = aiPromptMapper.selectAiPromptList(query);
        if (list.isEmpty()) return true;
        if (aiPrompt.getPromptId() != null) {
            return list.stream().noneMatch(p -> !p.getPromptId().equals(aiPrompt.getPromptId()));
        }
        return false;
    }

    /**
     * 清除某个提示词相关的所有缓存
     */
    private void clearCacheByPromptId(Long promptId) {
        clearAllCache();
        log.debug("提示词 {} 变更，已清除所有提示词缓存", promptId);
    }

    @Override
    public void clearCacheByConfigId(Long configId) {
        // 清除该config的两种缓存key
        redisCache.deleteObject(RedisKeys.AI_PROMPTS_CONFIG + configId);
        Set<String> modelKeys = redisCache.redisTemplate.keys(RedisKeys.AI_PROMPTS_CONFIG_MODEL + configId + ":*");
        if (modelKeys != null && !modelKeys.isEmpty()) {
            redisCache.redisTemplate.delete(modelKeys);
        }
        log.debug("已清除 configId={} 的提示词缓存", configId);
    }

    @Override
    public void clearAllCache() {
        Set<String> keys1 = redisCache.redisTemplate.keys(RedisKeys.AI_PROMPTS_CONFIG + "*");
        if (keys1 != null && !keys1.isEmpty()) {
            redisCache.redisTemplate.delete(keys1);
        }
        Set<String> keys2 = redisCache.redisTemplate.keys(RedisKeys.AI_PROMPTS_CONFIG_MODEL + "*");
        if (keys2 != null && !keys2.isEmpty()) {
            redisCache.redisTemplate.delete(keys2);
        }
        log.info("已清除所有提示词缓存");
    }
}
