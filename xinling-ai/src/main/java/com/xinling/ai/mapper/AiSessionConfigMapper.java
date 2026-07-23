package com.xinling.ai.mapper;

import com.xinling.ai.domain.config.AiSessionConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI会话配置Mapper接口
 *
 * @author SuXia
 */
public interface AiSessionConfigMapper {
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
    AiSessionConfig selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 查询所有启用的会话配置
     */
    List<AiSessionConfig> selectEnabledConfigs();

    /**
     * 新增AI会话配置
     */
    int insertAiSessionConfig(AiSessionConfig aiSessionConfig);

    /**
     * 修改AI会话配置
     */
    int updateAiSessionConfig(AiSessionConfig aiSessionConfig);

    /**
     * 删除AI会话配置
     */
    int deleteAiSessionConfigById(Long configId);

    /**
     * 批量删除AI会话配置
     */
    int deleteAiSessionConfigByIds(Long[] configIds);

    // ========== 提示词关联管理 ==========

    /**
     * 查询指定配置关联的提示词ID列表
     */
    List<Long> selectPromptIdsByConfigId(Long configId);

    /**
     * 删除指定配置的所有提示词关联
     */
    int deletePromptRelationsByConfigId(Long configId);

    /**
     * 新增一条提示词关联
     */
    int insertPromptRelation(@Param("configId") Long configId, @Param("promptId") Long promptId);

    /**
     * 删除指定提示词的所有关联（删除提示词时清理）
     */
    int deletePromptRelationsByPromptId(Long promptId);
}
