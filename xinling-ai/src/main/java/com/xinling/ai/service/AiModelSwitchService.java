package com.xinling.ai.service;

import com.xinling.ai.domain.config.AiModelConfig;
import com.xinling.ai.domain.config.AiSessionConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AI模型切换服务
 * 支持运行时动态切换模型
 *
 * @author SuXia
 * @date 2025/01/22
 */
@Slf4j
@Service
public class AiModelSwitchService {

    @Autowired
    private AiDynamicModelManager aiDynamicModelManager;

    @Autowired
    private IAiModelConfigService aiModelConfigService;

    @Autowired
    private IAiSessionConfigService aiSessionConfigService;

    /**
     * 切换到指定模型（通过模型ID）
     */
    public boolean switchToModel(Long modelId) {
        try {
            AiModelConfig model = aiModelConfigService.selectAiModelConfigById(modelId);
            if (model == null) {
                log.error("模型不存在: modelId={}", modelId);
                return false;
            }

            if (!"0".equals(model.getStatus())) {
                log.error("模型已停用: modelId={}", modelId);
                return false;
            }

            // 清除缓存，强制重新加载模型
            aiDynamicModelManager.clearModelCache(modelId);
            
            // 验证新模型是否可以正常加载
            if ("chat".equals(model.getModelType())) {
                aiDynamicModelManager.getChatModel(modelId);
            } else if ("embedding".equals(model.getModelType())) {
                aiDynamicModelManager.getEmbeddingModel(modelId);
            }

            log.info("模型切换成功: modelId={}, modelCode={}", modelId, model.getModelCode());
            return true;
        } catch (Exception e) {
            log.error("模型切换失败: modelId={}", modelId, e);
            return false;
        }
    }

    /**
     * 切换到指定会话配置
     */
    public boolean switchToSessionConfig(Long configId) {
        try {
            AiSessionConfig config = aiSessionConfigService.selectAiSessionConfigById(configId);
            if (config == null) {
                log.error("会话配置不存在: configId={}", configId);
                return false;
            }

            if (!"0".equals(config.getStatus())) {
                log.error("会话配置已停用: configId={}", configId);
                return false;
            }

            // 验证关联的模型是否可以正常加载
            aiDynamicModelManager.getChatModel(config.getChatModelId());
            if (config.getEmbeddingModelId() != null) {
                aiDynamicModelManager.getEmbeddingModel(config.getEmbeddingModelId());
            }

            log.info("会话配置切换成功: configId={}", configId);
            return true;
        } catch (Exception e) {
            log.error("会话配置切换失败: configId={}", configId, e);
            return false;
        }
    }

    /**
     * 使用默认配置
     */
    public boolean useDefaultConfig() {
        try {
            AiSessionConfig defaultConfig = aiSessionConfigService.selectDefaultSessionConfig();
            if (defaultConfig == null) {
                log.error("默认会话配置不存在");
                return false;
            }
            return switchToSessionConfig(defaultConfig.getConfigId());
        } catch (Exception e) {
            log.error("使用默认配置失败", e);
            return false;
        }
    }

    /**
     * 刷新所有模型缓存（当配置更新时调用）
     */
    public void refreshAllModels() {
        log.info("刷新所有模型缓存");
        aiDynamicModelManager.clearCache();
    }
}
