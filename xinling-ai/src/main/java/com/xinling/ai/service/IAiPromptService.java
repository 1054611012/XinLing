package com.xinling.ai.service;

import com.xinling.ai.domain.config.AiPrompt;

import java.util.List;

/**
 * AI提示词Service接口
 *
 * @author SuXia
 */
public interface IAiPromptService {

    /**
     * 查询AI提示词列表
     */
    List<AiPrompt> selectAiPromptList(AiPrompt aiPrompt);

    /**
     * 根据ID查询AI提示词
     */
    AiPrompt selectAiPromptById(Long promptId);

    /**
     * 根据会话配置ID查询关联的提示词列表
     */
    List<AiPrompt> selectPromptsByConfigId(Long configId);

    /**
     * 根据会话配置ID和模型ID查询可用的提示词列表
     * 过滤规则：status='0' AND (model_id IS NULL OR model_id = #{modelId})
     */
    List<AiPrompt> selectEnabledPromptsByConfigId(Long configId, Long modelId);

    /**
     * 新增AI提示词
     */
    int insertAiPrompt(AiPrompt aiPrompt);

    /**
     * 修改AI提示词
     */
    int updateAiPrompt(AiPrompt aiPrompt);

    /**
     * 批量删除AI提示词
     */
    int deleteAiPromptByIds(Long[] promptIds);

    /**
     * 校验提示词名称是否唯一
     */
    boolean checkPromptNameUnique(AiPrompt aiPrompt);

    /**
     * 清除指定配置的提示词Redis缓存
     */
    void clearCacheByConfigId(Long configId);

    /**
     * 清除所有提示词Redis缓存
     */
    void clearAllCache();
}
