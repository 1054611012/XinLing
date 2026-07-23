package com.xinling.ai.config;

import com.xinling.ai.service.AiDynamicModelManager;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j配置类 - 支持动态模型切换
 * 使用AiDynamicModelManager实现运行时动态加载模型
 *
 * @author SuXia
 * @date 2025/01/22
 */
@Slf4j
@Configuration
public class LangChain4jConfig {

    @Autowired
    private AiDynamicModelManager aiDynamicModelManager;

    @Autowired
    private AiConfigProperties aiConfigProperties;

    /**
     * 聊天模型（使用动态模型管理器）
     * 优先使用数据库配置的默认模型，如果没有则使用配置文件中的模型
     */
    @Bean
    public ChatModel chatLanguageModel() {
        try {
            // 尝试从数据库获取默认模型
            return aiDynamicModelManager.getDefaultChatModel();
        } catch (Exception e) {
            log.warn("无法从数据库加载默认模型，使用配置文件中的模型: {}", e.getMessage());
            // 降级到配置文件中的模型
            return createFallbackChatModel();
        }
    }

    /**
     * 流式聊天模型
     */
    @Bean
    public StreamingChatModel streamingChatLanguageModel() {
        try {
            return aiDynamicModelManager.getDefaultStreamingChatModel();
        } catch (Exception e) {
            log.warn("无法从数据库加载默认流式模型，使用配置文件中的模型: {}", e.getMessage());
            return createFallbackStreamingChatModel();
        }
    }

    /**
     * 嵌入模型
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        try {
            return aiDynamicModelManager.getDefaultEmbeddingModel();
        } catch (Exception e) {
            log.warn("无法从数据库加载默认嵌入模型，使用配置文件中的模型: {}", e.getMessage());
            return createFallbackEmbeddingModel();
        }
    }

    /**
     * 创建备用的Ollama聊天模型（当数据库未配置时使用）
     */
    private ChatModel createFallbackChatModel() {
        log.info("创建备用Ollama聊天模型: {}", aiConfigProperties.getOllama().getChatModel());
        return dev.langchain4j.model.ollama.OllamaChatModel.builder()
                .baseUrl(aiConfigProperties.getOllama().getBaseUrl())
                .modelName(aiConfigProperties.getOllama().getChatModel())
                .timeout(java.time.Duration.ofSeconds(aiConfigProperties.getOllama().getTimeoutSeconds()))
                .build();
    }

    /**
     * 创建备用的Ollama流式聊天模型
     */
    private StreamingChatModel createFallbackStreamingChatModel() {
        log.info("创建备用Ollama流式聊天模型: {}", aiConfigProperties.getOllama().getChatModel());
        return dev.langchain4j.model.ollama.OllamaStreamingChatModel.builder()
                .baseUrl(aiConfigProperties.getOllama().getBaseUrl())
                .modelName(aiConfigProperties.getOllama().getChatModel())
                .timeout(java.time.Duration.ofSeconds(aiConfigProperties.getOllama().getTimeoutSeconds()))
                .build();
    }

    /**
     * 创建备用的Ollama嵌入模型
     */
    private EmbeddingModel createFallbackEmbeddingModel() {
        log.info("创建备用Ollama嵌入模型: {}", aiConfigProperties.getOllama().getEmbeddingModel());
        return dev.langchain4j.model.ollama.OllamaEmbeddingModel.builder()
                .baseUrl(aiConfigProperties.getOllama().getBaseUrl())
                .modelName(aiConfigProperties.getOllama().getEmbeddingModel())
                .timeout(java.time.Duration.ofSeconds(aiConfigProperties.getOllama().getTimeoutSeconds()))
                .build();
    }

    /**
     * 全局唯一的向量存储（所有服务共享此实例）
     */
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    /**
     * 内容检索器（从统一 EmbeddingStore 检索）
     */
    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(aiConfigProperties.getRag().getMaxResults())
                .minScore(aiConfigProperties.getRag().getMinScore())
                .build();
    }
}
