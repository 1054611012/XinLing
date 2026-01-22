package com.xinling.ai.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.xinling.ai.config.AiConfigProperties;

/**
 * 文档加载服务
 *
 * @author SuXia
 * @date 2025/12/29
 */
@Slf4j
@Service
public class DocumentLoaderService {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private AiConfigProperties aiConfigProperties;

    /**
     * 加载知识库文档到向量存储
     */
    public void loadKnowledgeBaseDocuments(EmbeddingStore<TextSegment> embeddingStore) {
        try {
            String knowledgeBasePath = aiConfigProperties.getRag().getKnowledgeBasePath();
            Path path = Paths.get(knowledgeBasePath);

            if (!path.toFile().exists()) {
                log.warn("知识库路径不存在: {}", knowledgeBasePath);
                // 创建目录
                path.toFile().mkdirs();
                log.info("已创建知识库目录: {}", knowledgeBasePath);
                return;
            }

            // 加载PDF文档
            List<Document> pdfDocuments = FileSystemDocumentLoader.loadDocuments(
                path,
                filePath -> filePath.toString().endsWith(".pdf"),
                new ApachePdfBoxDocumentParser()
            );
            log.info("加载了 {} 个PDF文档", pdfDocuments.size());

            // 加载文本文件 - 使用Apache Tika解析器
            List<Document> textDocuments = FileSystemDocumentLoader.loadDocuments(
                path,
                filePath -> filePath.toString().endsWith(".txt") || filePath.toString().endsWith(".md"),
                new dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser()
            );
            log.info("加载了 {} 个文本文档", textDocuments.size());

            // 合并所有文档
            pdfDocuments.addAll(textDocuments);

            // 分割文档并存入向量存储
            for (Document document : pdfDocuments) {
                List<TextSegment> segments = DocumentSplitters.recursive(
                        aiConfigProperties.getRag().getChunkSize(),
                        aiConfigProperties.getRag().getChunkOverlap()
                ).split(document);

                // 将分段存入向量存储
                for (TextSegment segment : segments) {
                    Embedding embedding = embeddingModel.embed(segment).content();
                    embeddingStore.add(embedding, segment);
                }

                log.info("文档 {} 分割为 {} 个片段并存入向量存储",
                    document.metadata("source"), segments.size());
            }

        } catch (Exception e) {
            log.error("加载知识库文档失败", e);
        }
    }

    /**
     * 获取知识库路径
     */
    public String getKnowledgeBasePath() {
        return aiConfigProperties.getRag().getKnowledgeBasePath();
    }
}
