package com.xinling.ai.service.impl;

import com.xinling.ai.domain.config.AiModelConfig;
import com.xinling.ai.mapper.AiModelConfigMapper;
import com.xinling.ai.service.IAiModelConfigService;
import com.xinling.ai.service.AiDynamicModelManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AI模型配置Service业务层处理
 *
 * @author SuXia
 * @date 2025/01/22
 */
@Slf4j
@Service
public class AiModelConfigServiceImpl implements IAiModelConfigService {
    
    @Autowired
    private AiModelConfigMapper aiModelConfigMapper;

    @Autowired
    private AiDynamicModelManager aiDynamicModelManager;

    @Override
    public List<AiModelConfig> selectAiModelConfigList(AiModelConfig aiModelConfig) {
        return aiModelConfigMapper.selectAiModelConfigList(aiModelConfig);
    }

    @Override
    public AiModelConfig selectAiModelConfigById(Long modelId) {
        return aiModelConfigMapper.selectAiModelConfigById(modelId);
    }

    @Override
    public AiModelConfig selectAiModelConfigByCode(String modelCode) {
        return aiModelConfigMapper.selectAiModelConfigByCode(modelCode);
    }

    @Override
    public AiModelConfig selectDefaultChatModel() {
        return aiModelConfigMapper.selectDefaultChatModel();
    }

    @Override
    public AiModelConfig selectDefaultEmbeddingModel() {
        return aiModelConfigMapper.selectDefaultEmbeddingModel();
    }

    @Override
    public int insertAiModelConfig(AiModelConfig aiModelConfig) {
        return aiModelConfigMapper.insertAiModelConfig(aiModelConfig);
    }

    @Override
    public int updateAiModelConfig(AiModelConfig aiModelConfig) {
        return aiModelConfigMapper.updateAiModelConfig(aiModelConfig);
    }

    @Override
    public int deleteAiModelConfigByIds(Long[] modelIds) {
        return aiModelConfigMapper.deleteAiModelConfigByIds(modelIds);
    }

    @Override
    public int deleteAiModelConfigById(Long modelId) {
        return aiModelConfigMapper.deleteAiModelConfigById(modelId);
    }

    @Override
    public boolean checkModelCodeUnique(AiModelConfig aiModelConfig) {
        Long modelId = aiModelConfig.getModelId() == null ? -1L : aiModelConfig.getModelId();
        AiModelConfig info = aiModelConfigMapper.checkModelCodeUnique(aiModelConfig.getModelCode(), modelId);
        return info == null;
    }

    @Override
    @Transactional
    public int setDefaultModel(Long modelId) {
        // 先查询模型信息
        AiModelConfig model = aiModelConfigMapper.selectAiModelConfigById(modelId);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }

        // 将同类型的其他模型设置为非默认
        AiModelConfig updateNonDefault = new AiModelConfig();
        updateNonDefault.setIsDefault("0");
        AiModelConfig queryModel = new AiModelConfig();
        queryModel.setModelType(model.getModelType());
        List<AiModelConfig> sameTypeModels = aiModelConfigMapper.selectAiModelConfigList(queryModel);
        
        for (AiModelConfig sameTypeModel : sameTypeModels) {
            if (!sameTypeModel.getModelId().equals(modelId)) {
                AiModelConfig update = new AiModelConfig();
                update.setModelId(sameTypeModel.getModelId());
                update.setIsDefault("0");
                aiModelConfigMapper.updateAiModelConfig(update);
            }
        }

        // 设置当前模型为默认
        AiModelConfig updateDefault = new AiModelConfig();
        updateDefault.setModelId(modelId);
        updateDefault.setIsDefault("1");
        return aiModelConfigMapper.updateAiModelConfig(updateDefault);
    }

    @Override
    public int cancelDefaultModel(Long modelId) {
        AiModelConfig update = new AiModelConfig();
        update.setModelId(modelId);
        update.setIsDefault("0");
        return aiModelConfigMapper.updateAiModelConfig(update);
    }

    @Override
    public List<AiModelConfig> selectEnabledChatModels() {
        AiModelConfig query = new AiModelConfig();
        query.setModelType("chat");
        query.setStatus("0");
        return aiModelConfigMapper.selectAiModelConfigList(query);
    }

    @Override
    public List<AiModelConfig> selectEnabledEmbeddingModels() {
        AiModelConfig query = new AiModelConfig();
        query.setModelType("embedding");
        query.setStatus("0");
        return aiModelConfigMapper.selectAiModelConfigList(query);
    }

    @Override
    public String testModel(Long modelId) {
        // 先查询模型配置是否存在
        AiModelConfig config = aiModelConfigMapper.selectAiModelConfigById(modelId);
        if (config == null) {
            return "测试失败：模型配置不存在 (modelId=" + modelId + ")";
        }

        log.info("开始测试模型: modelId={}, modelName={}, modelType={}",
                modelId, config.getModelName(), config.getModelType());

        // 根据模型类型选择对应的测试方法
        String modelType = config.getModelType();
        if ("chat".equals(modelType)) {
            return aiDynamicModelManager.testChatModelConnection(modelId);
        } else if ("embedding".equals(modelType)) {
            return aiDynamicModelManager.testEmbeddingModelConnection(modelId);
        } else {
            return "不支持对该类型模型进行测试: " + modelType;
        }
    }
}
