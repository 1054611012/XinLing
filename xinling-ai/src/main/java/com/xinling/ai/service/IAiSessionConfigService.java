package com.xinling.ai.service;

import com.xinling.ai.domain.config.AiSessionConfig;

import java.util.List;

/**
 * AI会话配置Service接口
 *
 * @author SuXia
 */
public interface IAiSessionConfigService {
    /**
     * 查询AI会话配置列表
     */
    List<AiSessionConfig> selectAiSessionConfigList(AiSessionConfig aiSessionConfig);

    /**
     * 根据ID查询AI会话配置
     */
    AiSessionConfig selectAiSessionConfigById(Long configId);

    /**
     * 查询默认会话配置
     */
    AiSessionConfig selectDefaultSessionConfig();

    /**
     * 根据config_key查询会话配置
     */
    AiSessionConfig selectByConfigKey(String configKey);

    /**
     * 新增AI会话配置
     */
    int insertAiSessionConfig(AiSessionConfig aiSessionConfig);

    /**
     * 修改AI会话配置（含提示词关联更新）
     */
    int updateAiSessionConfig(AiSessionConfig aiSessionConfig);

    /**
     * 批量删除AI会话配置
     */
    int deleteAiSessionConfigByIds(Long[] configIds);

    /**
     * 删除AI会话配置信息
     */
    int deleteAiSessionConfigById(Long configId);

    /**
     * 设置默认配置
     */
    int setDefaultConfig(Long configId);

    /**
     * 获取所有启用的会话配置
     */
    List<AiSessionConfig> selectEnabledConfigs();

    // ========== 提示词关联管理 ==========

    /**
     * 更新指定配置的提示词关联（先删后插）
     */
    int updatePromptRelations(Long configId, Long[] promptIds);

    /**
     * 查询指定配置关联的提示词ID列表
     */
    List<Long> selectPromptIdsByConfigId(Long configId);
}
