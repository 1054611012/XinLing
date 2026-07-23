package com.xinling.ai.service.impl;

import com.xinling.ai.domain.config.AiPrompt;
import com.xinling.ai.domain.config.AiSessionConfig;
import com.xinling.ai.mapper.AiSessionConfigMapper;
import com.xinling.ai.service.IAiPromptService;
import com.xinling.ai.service.IAiSessionConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * AI会话配置Service业务层处理
 *
 * @author SuXia
 */
@Slf4j
@Service
public class AiSessionConfigServiceImpl implements IAiSessionConfigService {

    @Autowired
    private AiSessionConfigMapper aiSessionConfigMapper;

    @Autowired
    private IAiPromptService aiPromptService;

    @Override
    public List<AiSessionConfig> selectAiSessionConfigList(AiSessionConfig aiSessionConfig) {
        List<AiSessionConfig> list = aiSessionConfigMapper.selectAiSessionConfigList(aiSessionConfig);
        // 填充每个配置的提示词关联（列表页显示用）
        for (AiSessionConfig config : list) {
            config.setPromptIds(selectPromptIdsByConfigId(config.getConfigId()).toArray(new Long[0]));
        }
        return list;
    }

    @Override
    public AiSessionConfig selectAiSessionConfigById(Long configId) {
        AiSessionConfig config = aiSessionConfigMapper.selectAiSessionConfigById(configId);
        if (config != null) {
            // 填充关联的提示词列表
            List<AiPrompt> prompts = aiPromptService.selectPromptsByConfigId(configId);
            config.setPrompts(prompts);
            config.setPromptIds(prompts.stream().map(AiPrompt::getPromptId).toArray(Long[]::new));
        }
        return config;
    }

    @Override
    public AiSessionConfig selectDefaultSessionConfig() {
        return aiSessionConfigMapper.selectDefaultSessionConfig();
    }

    @Override
    public AiSessionConfig selectByConfigKey(String configKey) {
        return aiSessionConfigMapper.selectByConfigKey(configKey);
    }

    @Override
    public int insertAiSessionConfig(AiSessionConfig aiSessionConfig) {
        return aiSessionConfigMapper.insertAiSessionConfig(aiSessionConfig);
    }

    @Override
    @Transactional
    public int updateAiSessionConfig(AiSessionConfig aiSessionConfig) {
        int rows = aiSessionConfigMapper.updateAiSessionConfig(aiSessionConfig);
        // promptIds != null 就处理关联（包含空数组=清空，非空=替换）
        if (aiSessionConfig.getPromptIds() != null) {
            updatePromptRelations(aiSessionConfig.getConfigId(), aiSessionConfig.getPromptIds());
        }
        return rows;
    }

    @Override
    @Transactional
    public int deleteAiSessionConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            aiSessionConfigMapper.deletePromptRelationsByConfigId(configId);
        }
        return aiSessionConfigMapper.deleteAiSessionConfigByIds(configIds);
    }

    @Override
    @Transactional
    public int deleteAiSessionConfigById(Long configId) {
        aiSessionConfigMapper.deletePromptRelationsByConfigId(configId);
        return aiSessionConfigMapper.deleteAiSessionConfigById(configId);
    }

    @Override
    @Transactional
    public int setDefaultConfig(Long configId) {
        List<AiSessionConfig> allConfigs = aiSessionConfigMapper.selectAiSessionConfigList(new AiSessionConfig());
        for (AiSessionConfig config : allConfigs) {
            if (!config.getConfigId().equals(configId)) {
                AiSessionConfig update = new AiSessionConfig();
                update.setConfigId(config.getConfigId());
                update.setIsDefault("0");
                aiSessionConfigMapper.updateAiSessionConfig(update);
            }
        }
        AiSessionConfig updateDefault = new AiSessionConfig();
        updateDefault.setConfigId(configId);
        updateDefault.setIsDefault("1");
        return aiSessionConfigMapper.updateAiSessionConfig(updateDefault);
    }

    @Override
    public List<AiSessionConfig> selectEnabledConfigs() {
        return aiSessionConfigMapper.selectEnabledConfigs();
    }

    @Override
    @Transactional
    public int updatePromptRelations(Long configId, Long[] promptIds) {
        aiSessionConfigMapper.deletePromptRelationsByConfigId(configId);
        int rows = 0;
        for (Long promptId : promptIds) {
            rows += aiSessionConfigMapper.insertPromptRelation(configId, promptId);
        }
        // 清除 Redis 缓存，确保后续查询返回最新数据
        aiPromptService.clearCacheByConfigId(configId);
        log.info("更新会话配置提示词关联: configId={}, promptIds={}", configId, Arrays.toString(promptIds));
        return rows;
    }

    @Override
    public List<Long> selectPromptIdsByConfigId(Long configId) {
        return aiSessionConfigMapper.selectPromptIdsByConfigId(configId);
    }
}
