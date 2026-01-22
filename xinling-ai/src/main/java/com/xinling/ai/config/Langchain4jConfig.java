package com.xinling.ai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * LangChain4j配置类
 *
 * @author SuXia
 * @date 2025/12/29
 */
@Configuration
public class Langchain4jConfig {

    @Autowired
    private AiConfigProperties aiConfigProperties;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl(aiConfigProperties.getOllama().getBaseUrl())
                .modelName(aiConfigProperties.getOllama().getModel())
                .timeout(Duration.ofSeconds(aiConfigProperties.getOllama().getTimeoutSeconds()))
                .build();
    }

    @Bean
    public StreamingChatLanguageModel streamingChatLanguageModel() {
        return OllamaStreamingChatModel.builder()
                .baseUrl(aiConfigProperties.getOllama().getBaseUrl())
                .modelName(aiConfigProperties.getOllama().getModel())
                .timeout(Duration.ofSeconds(aiConfigProperties.getOllama().getTimeoutSeconds()))
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        // 使用Ollama嵌入模型，与聊天模型保持一致
        return OllamaEmbeddingModel.builder()
                .baseUrl(aiConfigProperties.getOllama().getBaseUrl())
                .modelName(aiConfigProperties.getOllama().getModel())
                .timeout(Duration.ofSeconds(aiConfigProperties.getOllama().getTimeoutSeconds()))
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

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
