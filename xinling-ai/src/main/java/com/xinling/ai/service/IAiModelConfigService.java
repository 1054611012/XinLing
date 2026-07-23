package com.xinling.ai.service;

import com.xinling.ai.domain.config.AiModelConfig;

import java.util.List;

/**
 * AI模型配置Service接口
 *
 * @author SuXia
 * @date 2025/01/22
 */
public interface IAiModelConfigService {
    /**
     * 查询AI模型配置列表
     */
    List<AiModelConfig> selectAiModelConfigList(AiModelConfig aiModelConfig);

    /**
     * 根据ID查询AI模型配置
     */
    AiModelConfig selectAiModelConfigById(Long modelId);

    /**
     * 根据编码查询AI模型配置
     */
    AiModelConfig selectAiModelConfigByCode(String modelCode);

    /**
     * 查询默认对话模型
     */
    AiModelConfig selectDefaultChatModel();

    /**
     * 查询默认嵌入模型
     */
    AiModelConfig selectDefaultEmbeddingModel();

    /**
     * 新增AI模型配置
     */
    int insertAiModelConfig(AiModelConfig aiModelConfig);

    /**
     * 修改AI模型配置
     */
    int updateAiModelConfig(AiModelConfig aiModelConfig);

    /**
     * 批量删除AI模型配置
     */
    int deleteAiModelConfigByIds(Long[] modelIds);

    /**
     * 删除AI模型配置信息
     */
    int deleteAiModelConfigById(Long modelId);

    /**
     * 校验模型编码是否唯一
     */
    boolean checkModelCodeUnique(AiModelConfig aiModelConfig);

    /**
     * 设置默认模型
     */
    int setDefaultModel(Long modelId);

    /**
     * 取消默认模型
     */
    int cancelDefaultModel(Long modelId);

    /**
     * 获取所有启用的对话模型
     */
    List<AiModelConfig> selectEnabledChatModels();

    /**
     * 获取所有启用的嵌入模型
     */
    List<AiModelConfig> selectEnabledEmbeddingModels();

    /**
     * 测试模型连接是否可用
     *
     * @param modelId 模型配置ID
     * @return 测试结果描述（成功/失败 + 详情）
     */
    String testModel(Long modelId);

}
