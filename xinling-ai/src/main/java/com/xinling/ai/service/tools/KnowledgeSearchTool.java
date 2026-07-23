package com.xinling.ai.service.tools;

import com.xinling.ai.config.AiConfigProperties;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 知识库搜索工具 — LLM 可调用以检索文档知识
 *
 * @author SuXia
 * @date 2026/05/21
 */
@Slf4j
@Component
public class KnowledgeSearchTool {

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private AiConfigProperties aiConfigProperties;

    @Tool("搜索知识库中的文档内容。用于回答关于系统功能说明、操作方法、业务规则等需要文档支持的问题")
    public String searchKnowledge(String query) {
        log.info("KnowledgeSearchTool 调用: {}", query);
        try {
            ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .maxResults(aiConfigProperties.getRag().getMaxResults())
                    .minScore(aiConfigProperties.getRag().getMinScore())
                    .build();

            List<Content> results = retriever.retrieve(new Query(query));

            if (results.isEmpty()) {
                return "未找到相关知识库内容。";
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < results.size(); i++) {
                sb.append("[").append(i + 1).append("] ")
                        .append(results.get(i).textSegment().text())
                        .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("知识库搜索失败: {}", query, e);
            return "知识库搜索出错: " + e.getMessage();
        }
    }
}
