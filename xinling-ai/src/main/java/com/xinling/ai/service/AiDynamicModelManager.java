package com.xinling.ai.service;

import com.xinling.ai.domain.config.AiModelConfig;
import com.xinling.ai.domain.config.AiModelProvider;
import com.xinling.ai.mapper.AiModelConfigMapper;
import com.xinling.ai.mapper.AiModelProviderMapper;
import com.xinling.ai.mapper.AiSessionConfigMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * AI动态模型管理器
 * 支持运行时动态切换模型
 *
 * @author SuXia
 * @date 2025/01/22
 */
@Slf4j
@Service
public class AiDynamicModelManager {

    @Autowired
    private AiModelConfigMapper aiModelConfigMapper;

    @Autowired
    private AiModelProviderMapper aiModelProviderMapper;

    @Autowired
    private AiSessionConfigMapper aiSessionConfigMapper;

    // 缓存已创建的模型实例
    private final Map<Long, ChatModel> chatModelCache = new ConcurrentHashMap<>();
    private final Map<Long, StreamingChatModel> streamingChatModelCache = new ConcurrentHashMap<>();
    private final Map<Long, EmbeddingModel> embeddingModelCache = new ConcurrentHashMap<>();

    /**
     * 获取对话模型（根据模型ID）
     */
    public ChatModel getChatModel(Long modelId) {
        return chatModelCache.computeIfAbsent(modelId, this::createChatModel);
    }

    /**
     * 获取流式对话模型（根据模型ID）
     */
    public StreamingChatModel getStreamingChatModel(Long modelId) {
        return streamingChatModelCache.computeIfAbsent(modelId, this::createStreamingChatModel);
    }

    /**
     * 获取嵌入模型（根据模型ID）
     */
    public EmbeddingModel getEmbeddingModel(Long modelId) {
        return embeddingModelCache.computeIfAbsent(modelId, this::createEmbeddingModel);
    }

    /**
     * 获取默认对话模型
     */
    public ChatModel getDefaultChatModel() {
        AiModelConfig defaultModel = aiModelConfigMapper.selectDefaultChatModel();
        if (defaultModel == null) {
            throw new RuntimeException("未配置默认对话模型");
        }
        return getChatModel(defaultModel.getModelId());
    }

    /**
     * 获取默认流式对话模型
     */
    public StreamingChatModel getDefaultStreamingChatModel() {
        AiModelConfig defaultModel = aiModelConfigMapper.selectDefaultChatModel();
        if (defaultModel == null) {
            throw new RuntimeException("未配置默认对话模型");
        }
        return getStreamingChatModel(defaultModel.getModelId());
    }

    /**
     * 获取默认嵌入模型
     */
    public EmbeddingModel getDefaultEmbeddingModel() {
        AiModelConfig defaultModel = aiModelConfigMapper.selectDefaultEmbeddingModel();
        if (defaultModel == null) {
            throw new RuntimeException("未配置默认嵌入模型");
        }
        return getEmbeddingModel(defaultModel.getModelId());
    }

    /**
     * 创建对话模型
     */
    private ChatModel createChatModel(Long modelId) {
        AiModelConfig modelConfig = aiModelConfigMapper.selectAiModelConfigById(modelId);
        if (modelConfig == null) {
            throw new RuntimeException("模型配置不存在: " + modelId);
        }

        String providerCode = aiModelProviderMapper.selectAiModelProviderById(modelConfig.getProviderId()).getProviderCode();
        
        log.info("创建对话模型: provider={}, model={}", providerCode, modelConfig.getModelCode());

        // 根据提供商类型创建不同的模型实例
        switch (providerCode) {
            case "ollama":
                return OllamaChatModel.builder()
                        .baseUrl(aiModelProviderMapper.selectAiModelProviderById(modelConfig.getProviderId()).getApiBaseUrl())
                        .modelName(modelConfig.getModelCode())
                        .timeout(Duration.ofSeconds(modelConfig.getTimeoutSeconds()))
                        .build();
            case "aliyun-bailian":
            case "openai":
            case "deepseek":
                return OpenAiChatModel.builder()
                        .baseUrl(aiModelProviderMapper.selectAiModelProviderById(modelConfig.getProviderId()).getApiBaseUrl())
                        .apiKey(modelConfig.getApiKey())
                        .modelName(modelConfig.getModelCode())
                        .timeout(Duration.ofSeconds(modelConfig.getTimeoutSeconds()))
                        .build();
            default:
                throw new RuntimeException("不支持的模型提供商: " + providerCode);
        }
    }

    /**
     * 创建流式对话模型
     */
    private StreamingChatModel createStreamingChatModel(Long modelId) {
        AiModelConfig modelConfig = aiModelConfigMapper.selectAiModelConfigById(modelId);
        if (modelConfig == null) {
            throw new RuntimeException("模型配置不存在: " + modelId);
        }

        String providerCode = aiModelProviderMapper.selectAiModelProviderById(modelConfig.getProviderId()).getProviderCode();

        log.info("创建流式对话模型: provider={}, model={}", providerCode, modelConfig.getModelCode());

        switch (providerCode) {
            case "ollama":
                return OllamaStreamingChatModel.builder()
                        .baseUrl(aiModelProviderMapper.selectAiModelProviderById(modelConfig.getProviderId()).getApiBaseUrl())
                        .modelName(modelConfig.getModelCode())
                        .timeout(Duration.ofSeconds(modelConfig.getTimeoutSeconds()))
                        .build();
            case "aliyun-bailian":
            case "openai":
            case "deepseek":
                return OpenAiStreamingChatModel.builder()
                        .baseUrl(aiModelProviderMapper.selectAiModelProviderById(modelConfig.getProviderId()).getApiBaseUrl())
                        .apiKey(modelConfig.getApiKey())
                        .modelName(modelConfig.getModelCode())
                        .timeout(Duration.ofSeconds(modelConfig.getTimeoutSeconds()))
                        .build();
            default:
                throw new RuntimeException("不支持的模型提供商: " + providerCode);
        }
    }

    /**
     * 创建嵌入模型
     */
    private EmbeddingModel createEmbeddingModel(Long modelId) {
        AiModelConfig modelConfig = aiModelConfigMapper.selectAiModelConfigById(modelId);
        if (modelConfig == null) {
            throw new RuntimeException("模型配置不存在: " + modelId);
        }

        String providerCode = aiModelProviderMapper.selectAiModelProviderById(modelConfig.getProviderId()).getProviderCode();
        
        log.info("创建嵌入模型: provider={}, model={}", providerCode, modelConfig.getModelCode());

        switch (providerCode) {
            case "ollama":
                return OllamaEmbeddingModel.builder()
                        .baseUrl(aiModelProviderMapper.selectAiModelProviderById(modelConfig.getProviderId()).getApiBaseUrl())
                        .modelName(modelConfig.getModelCode())
                        .timeout(Duration.ofSeconds(modelConfig.getTimeoutSeconds()))
                        .build();
            case "aliyun-bailian":
            case "openai":
                return OpenAiEmbeddingModel.builder()
                        .baseUrl(aiModelProviderMapper.selectAiModelProviderById(modelConfig.getProviderId()).getApiBaseUrl())
                        .apiKey(modelConfig.getApiKey())
                        .modelName(modelConfig.getModelCode())
                        .timeout(Duration.ofSeconds(modelConfig.getTimeoutSeconds()))
                        .build();
            default:
                throw new RuntimeException("不支持的模型提供商: " + providerCode);
        }
    }

    /**
     * 清除模型缓存（当配置更新时调用）
     */
    public void clearCache() {
        chatModelCache.clear();
        streamingChatModelCache.clear();
        embeddingModelCache.clear();
        log.info("AI模型缓存已清除");
    }

    /**
     * 清除指定模型的缓存
     */
    public void clearModelCache(Long modelId) {
        chatModelCache.remove(modelId);
        streamingChatModelCache.remove(modelId);
        embeddingModelCache.remove(modelId);
        log.info("模型缓存已清除: modelId={}", modelId);
    }

    // ==================== 模型测试 ====================

    /**
     * 测试对话模型是否可用（不缓存，创建临时实例）
     *
     * @param modelId 模型配置ID
     * @return 测试结果描述
     */
    public String testChatModelConnection(Long modelId) {
        AiModelConfig modelConfig = aiModelConfigMapper.selectAiModelConfigById(modelId);
        if (modelConfig == null) {
            return "测试失败：模型配置不存在 (modelId=" + modelId + ")";
        }

        AiModelProvider provider = aiModelProviderMapper.selectAiModelProviderById(modelConfig.getProviderId());
        if (provider == null) {
            return "测试失败：模型提供商不存在 (providerId=" + modelConfig.getProviderId() + ")";
        }

        log.info("测试对话模型: provider={}, model={}", provider.getProviderCode(), modelConfig.getModelCode());

        try {
            ChatModel testModel;
            switch (provider.getProviderCode()) {
                case "ollama":
                    testModel = OllamaChatModel.builder()
                            .baseUrl(provider.getApiBaseUrl())
                            .modelName(modelConfig.getModelCode())
                            .timeout(Duration.ofSeconds(30))
                            .build();
                    break;
                case "aliyun-bailian":
                case "deepseek":
                    testModel = OpenAiChatModel.builder()
                            .baseUrl(provider.getApiBaseUrl())
                            .apiKey(modelConfig.getApiKey())
                            .modelName(modelConfig.getModelCode())
                            .timeout(Duration.ofSeconds(30))
                            .build();
                    break;

                case "openai":
                    testModel = OpenAiChatModel.builder()
                            .baseUrl(provider.getApiBaseUrl())
                            .apiKey(modelConfig.getApiKey())
                            .modelName(modelConfig.getModelCode())
                            .timeout(Duration.ofSeconds(30))
                            .build();
                    break;
                default:
                    return "测试失败：不支持的模型提供商类型: " + provider.getProviderCode();
            }

            long startTime = System.currentTimeMillis();
            var chatResponse = testModel.chat(java.util.List.of(new UserMessage("请直接回复 OK 表示你正常工作，不要回复其他内容")));
            String response = chatResponse.aiMessage().text();
            long elapsed = System.currentTimeMillis() - startTime;

            if (response != null && !response.trim().isEmpty()) {
                log.info("模型测试成功: modelId={}, 耗时={}ms, 响应={}", modelId, elapsed, response.trim());
                return String.format("测试成功！响应时间：%dms，模型回复：%s", elapsed, response.trim());
            } else {
                return "测试失败：模型返回了空响应";
            }
        } catch (Exception e) {
            log.error("模型测试失败: modelId={}", modelId, e);
            return "测试失败：" + e.getMessage();
        }
    }

    /**
     * 测试嵌入模型是否可用（不缓存，创建临时实例）
     *
     * @param modelId 模型配置ID
     * @return 测试结果描述
     */
    public String testEmbeddingModelConnection(Long modelId) {
        AiModelConfig modelConfig = aiModelConfigMapper.selectAiModelConfigById(modelId);
        if (modelConfig == null) {
            return "测试失败：模型配置不存在 (modelId=" + modelId + ")";
        }

        AiModelProvider provider = aiModelProviderMapper.selectAiModelProviderById(modelConfig.getProviderId());
        if (provider == null) {
            return "测试失败：模型提供商不存在 (providerId=" + modelConfig.getProviderId() + ")";
        }

        log.info("测试嵌入模型: provider={}, model={}", provider.getProviderCode(), modelConfig.getModelCode());

        try {
            EmbeddingModel testModel;
            switch (provider.getProviderCode()) {
                case "ollama":
                    testModel = OllamaEmbeddingModel.builder()
                            .baseUrl(provider.getApiBaseUrl())
                            .modelName(modelConfig.getModelCode())
                            .timeout(Duration.ofSeconds(30))
                            .build();
                    break;
                case "aliyun-bailian":
                case "openai":
                    testModel = OpenAiEmbeddingModel.builder()
                            .baseUrl(provider.getApiBaseUrl())
                            .apiKey(modelConfig.getApiKey())
                            .modelName(modelConfig.getModelCode())
                            .timeout(Duration.ofSeconds(30))
                            .build();
                    break;
                default:
                    return "测试失败：不支持的模型提供商类型: " + provider.getProviderCode();
            }

            long startTime = System.currentTimeMillis();
            var embedding = testModel.embed("测试文本");
            long elapsed = System.currentTimeMillis() - startTime;

            if (embedding != null && embedding.content() != null) {
                log.info("嵌入模型测试成功: modelId={}, 耗时={}ms, 向量维度={}",
                        modelId, elapsed, embedding.content().dimension());
                return String.format("测试成功！响应时间：%dms，向量维度：%d",
                        elapsed, embedding.content().dimension());
            } else {
                return "测试失败：嵌入模型返回了空结果";
            }
        } catch (Exception e) {
            log.error("嵌入模型测试失败: modelId={}", modelId, e);
            return "测试失败：" + e.getMessage();
        }
    }
}
