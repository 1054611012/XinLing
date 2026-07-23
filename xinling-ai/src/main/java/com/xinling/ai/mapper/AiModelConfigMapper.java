package com.xinling.ai.mapper;

import com.xinling.ai.domain.config.AiModelConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI模型配置Mapper接口
 *
 * @author SuXia
 * @date 2025/01/22
 */
public interface AiModelConfigMapper {
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
     * 删除AI模型配置
     */
    int deleteAiModelConfigById(Long modelId);

    /**
     * 批量删除AI模型配置
     */
    int deleteAiModelConfigByIds(Long[] modelIds);

    /**
     * 校验模型编码是否唯一
     */
    AiModelConfig checkModelCodeUnique(@Param("modelCode") String modelCode, @Param("modelId") Long modelId);
}
