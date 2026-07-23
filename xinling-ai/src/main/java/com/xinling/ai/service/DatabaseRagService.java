package com.xinling.ai.service;

import com.xinling.ai.config.AiConfigProperties;
import com.xinling.ai.domain.config.AiPrompt;
import com.xinling.ai.domain.config.AiSessionConfig;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库RAG服务
 * NL2SQL 提示词从 ai_prompt 表加载（关联 nl2sql 会话配置）
 *
 * @author SuXia
 */
@Slf4j
@Service
public class DatabaseRagService {

    @Autowired
    private ChatModel chatLanguageModel;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private AiConfigProperties aiConfigProperties;

    @Autowired
    private StreamingChatModel streamingChatLanguageModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private SchemaDiscoveryService schemaDiscoveryService;

    @Autowired
    private IAiSessionConfigService aiSessionConfigService;

    @Autowired
    private IAiPromptService aiPromptService;

    private volatile boolean initialized = false;

    public synchronized void initializeDatabaseDocuments() {
        if (initialized) {
            log.debug("数据库文档已初始化，跳过重复初始化");
            return;
        }
        try {
            log.info("开始初始化数据库文档到RAG知识库");
            if (!isEmbeddingModelAvailable()) {
                log.warn("EmbeddingModel 服务不可用，跳过数据库文档初始化");
                return;
            }
            List<String> docFiles = getDocumentFiles();
            int loadedCount = 0;
            for (String docPath : docFiles) {
                if (loadDocumentIfExists(docPath)) {
                    loadedCount++;
                }
            }
            log.info("数据库文档初始化完成，共加载 {} 个文档", loadedCount);
            initialized = true;
        } catch (Exception e) {
            log.error("初始化数据库文档失败", e);
        }
    }

    private boolean isEmbeddingModelAvailable() {
        try {
            embeddingModel.embed("test");
            return true;
        } catch (Exception e) {
            log.warn("EmbeddingModel 连接测试失败: {}", e.getMessage());
            return false;
        }
    }

    private List<String> getDocumentFiles() {
        List<String> docFiles = new ArrayList<>();
        docFiles.add("doc/database_analysis.md");
        docFiles.add("doc/sql_query_analysis.md");
        return docFiles;
    }

    private boolean loadDocumentIfExists(String docPath) {
        try {
            java.io.InputStream inputStream = getClass().getClassLoader().getResourceAsStream(docPath);
            if (inputStream != null) {
                String docContent = new java.util.Scanner(inputStream, java.nio.charset.StandardCharsets.UTF_8)
                        .useDelimiter("\\A").next();
                inputStream.close();
                Document document = Document.from(docContent);
                List<TextSegment> segments = DocumentSplitters.recursive(
                        aiConfigProperties.getRag().getChunkSize(),
                        aiConfigProperties.getRag().getChunkOverlap()).split(document);
                for (TextSegment segment : segments) {
                    Embedding embedding = embeddingModel.embed(segment).content();
                    embeddingStore.add(embedding, segment);
                }
                log.info("文档 {} 已加载到RAG知识库，共{}个分段", docPath, segments.size());
                return true;
            } else {
                log.warn("文档未找到: {}", docPath);
                return false;
            }
        } catch (Exception e) {
            log.error("加载文档失败: {}", docPath, e);
            return false;
        }
    }

    /**
     * 查询数据库相关信息（NL2SQL）
     * 提示词从 ai_prompt 表加载（关联 nl2sql 配置）
     */
    public String queryDatabaseInfo(String question) {
        try {
            ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .maxResults(aiConfigProperties.getRag().getMaxResults())
                    .minScore(aiConfigProperties.getRag().getMinScore())
                    .build();

            List<Content> relevantContents = contentRetriever.retrieve(new Query(question));

            StringBuilder userContext = new StringBuilder();
            userContext.append("【DDL】\n");
            for (Content content : relevantContents) {
                userContext.append(content.textSegment().text()).append("\n");
            }
            userContext.append("\n【用户问题】\n").append(question);

            SystemMessage systemMessage = new SystemMessage(loadSystemPromptFromDb());
            UserMessage userMessage = new UserMessage(userContext.toString());

            ChatResponse response = chatLanguageModel.chat(List.of(systemMessage, userMessage));
            return response.aiMessage().text();
        } catch (Exception e) {
            log.error("NL2SQL 查询失败", e);
            throw new RuntimeException("NL2SQL 查询失败", e);
        }
    }

    /**
     * 从数据库加载 NL2SQL system prompt（通过 ai_prompt 表 + Redis 缓存）
     */
    private String loadSystemPromptFromDb() {
        AiSessionConfig nl2sqlConfig = aiSessionConfigService.selectByConfigKey("nl2sql");
        if (nl2sqlConfig != null) {
            List<AiPrompt> prompts = aiPromptService.selectEnabledPromptsByConfigId(
                    nl2sqlConfig.getConfigId(), nl2sqlConfig.getChatModelId());
            if (prompts != null && !prompts.isEmpty()) {
                String template = prompts.get(0).getContent();
                String ddl = getDatabaseSchemaInfo();
                return template.replace("{ddl}", ddl != null ? ddl : "未找到数据库结构信息");
            }
        }
        throw new RuntimeException("未找到NL2SQL提示词，请检查 ai_prompt 表是否有 nl2sql 配置的关联提示词");
    }

    private String getDatabaseSchemaInfo() {
        String dynamicDdl = schemaDiscoveryService.getDatabaseDdl();
        if (dynamicDdl != null && !dynamicDdl.isBlank()) {
            return dynamicDdl;
        }
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(aiConfigProperties.getRag().getMaxResults())
                .minScore(aiConfigProperties.getRag().getMinScore())
                .build();
        List<Content> relevantContents = contentRetriever.retrieve(new Query("数据库表结构信息"));
        StringBuilder schemaInfo = new StringBuilder();
        for (Content content : relevantContents) {
            schemaInfo.append(content.textSegment().text()).append("\n");
        }
        return schemaInfo.toString();
    }

    public Flux<String> streamQueryDatabaseInfo(String question) {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        try {
            if (isSqlGenerationRequired(question)) {
                streamGenerateSqlQuery(question, sink);
                return sink.asFlux();
            }
            ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .maxResults(aiConfigProperties.getRag().getMaxResults())
                    .minScore(aiConfigProperties.getRag().getMinScore())
                    .build();
            List<Content> relevantContents = contentRetriever.retrieve(new Query(question));
            if (relevantContents.isEmpty()) {
                sink.tryEmitNext("抱歉，我没有找到关于数据库的足够信息来回答您的问题。");
                sink.tryEmitComplete();
                return sink.asFlux();
            }
            StringBuilder context = new StringBuilder();
            context.append("根据数据库结构信息：\n");
            for (Content content : relevantContents) {
                context.append(content.textSegment().text()).append("\n");
            }
            context.append("\n基于以上信息，回答问题：").append(question);
            streamingChatLanguageModel.chat(context.toString(), new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String token) { sink.tryEmitNext(token); }
                @Override
                public void onCompleteResponse(ChatResponse response) { sink.tryEmitComplete(); }
                @Override
                public void onError(Throwable error) {
                    log.error("流式查询失败", error);
                    sink.tryEmitError(new RuntimeException("流式查询时发生错误：" + error.getMessage()));
                }
            });
        } catch (Exception e) {
            log.error("流式查询失败", e);
            sink.tryEmitError(new RuntimeException("流式查询时发生错误：" + e.getMessage()));
        }
        return sink.asFlux();
    }

    private boolean isSqlGenerationRequired(String question) {
        String lowerQuestion = question.toLowerCase();
        return lowerQuestion.contains("查询") || lowerQuestion.contains("统计")
                || lowerQuestion.contains("列出") || lowerQuestion.contains("有多少")
                || lowerQuestion.contains("count") || lowerQuestion.contains("sum")
                || lowerQuestion.contains("avg") || lowerQuestion.contains("max")
                || lowerQuestion.contains("min") || lowerQuestion.contains("求");
    }

    private void streamGenerateSqlQuery(String question, Sinks.Many<String> sink) {
        try {
            String ddl = getDatabaseSchemaInfo();
            String systemPrompt = loadSystemPromptFromDb();
            StringBuilder userContext = new StringBuilder();
            userContext.append("【DDL】\n");
            userContext.append(ddl != null ? ddl : "未找到数据库结构信息");
            userContext.append("\n\n【用户问题】\n").append(question);
            SystemMessage systemMessage = new SystemMessage(systemPrompt);
            UserMessage userMessage = new UserMessage(userContext.toString());
            List<ChatMessage> messages = List.of(systemMessage, userMessage);
            streamingChatLanguageModel.chat(messages, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String token) { sink.tryEmitNext(token); }
                @Override
                public void onCompleteResponse(ChatResponse response) { sink.tryEmitComplete(); }
                @Override
                public void onError(Throwable error) {
                    log.error("流式SQL生成失败", error);
                    sink.tryEmitError(new RuntimeException("流式SQL生成时发生错误：" + error.getMessage()));
                }
            });
        } catch (Exception e) {
            log.error("流式SQL生成失败", e);
            sink.tryEmitError(new RuntimeException("流式SQL生成时发生错误：" + e.getMessage()));
        }
    }

    public void addDocumentToKnowledgeBase(String content) {
        try {
            Document document = Document.from(content);
            List<TextSegment> segments = DocumentSplitters.recursive(
                    aiConfigProperties.getRag().getChunkSize(),
                    aiConfigProperties.getRag().getChunkOverlap()).split(document);
            for (TextSegment segment : segments) {
                Embedding embedding = embeddingModel.embed(segment).content();
                embeddingStore.add(embedding, segment);
            }
            log.info("自定义文档已添加到RAG知识库，共{}个分段", segments.size());
        } catch (Exception e) {
            log.error("添加文档到知识库失败", e);
        }
    }
}
