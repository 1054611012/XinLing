package com.xinling.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * AI配置属性
 *
 * @author SuXia
 * @date 2025/12/29
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiConfigProperties {

    /**
     * 模型配置
     */
    private Model model = new Model();

    /**
     * Ollama配置
     */
    private Ollama ollama = new Ollama();

    /**
     * RAG配置
     */
    private Rag rag = new Rag();

    @Data
    public static class Model {
        /**
         * 模型提供商: ollama, openai等
         */
        private String provider = "ollama";

        /**
         * 默认云模型名称
         */
        private String defaultModel = "qwen2.5:3b";

        /**
         * 是否使用本地模型
         */
        private boolean useLocal = true;
    }

    @Data
    public static class Ollama {
        /**
         * Ollama基础URL
         */
        private String baseUrl = "http://localhost:11434";

        /**
         * API密钥
         */
        private String apiKey = "ollama";

        /**
         * 聊天模型名称（用于对话、NL2SQL、意图分类等）
         */
        private String chatModel = "qwen2.5:3b";

        /**
         * 嵌入模型名称（用于文档向量化，需专用Embedding模型）
         */
        private String embeddingModel = "nomic-embed-text";

        /**
         * 超时时间（秒）
         */
        private Integer timeoutSeconds = 360;
    }

    @Data
    public static class Rag {
        /**
         * 知识库文档路径
         */
        private String knowledgeBasePath = "/Volumes/Suxia/IdeaProjects/XinLing/knowledge-base";

        /**
         * 检索结果数量
         */
        private Integer maxResults = 3;

        /**
         * 最小相似度分数
         */
        private Double minScore = 0.5;

        /**
         * 文档分块大小
         */
        private Integer chunkSize = 500;

        /**
         * 文档重叠大小
         */
        private Integer chunkOverlap = 0;

        /**
         * 向量存储路径
         */
        private String vectorStorePath = "/Volumes/Suxia/IdeaProjects/XinLing/vector-store";

        /**
         * 初始化完成后状态键的TTL（小时）
         */
        private Long initializationTtlHours = 24L;
    }
}
