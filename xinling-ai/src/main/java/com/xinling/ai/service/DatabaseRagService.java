package com.xinling.ai.service;

import com.xinling.ai.config.AiConfigProperties;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 数据库RAG服务
 *
 * @author SuXia
 * @date 2025/12/29
 */
@Slf4j
@Service
public class DatabaseRagService {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private AiConfigProperties aiConfigProperties;

    @Autowired
    private StreamingChatLanguageModel streamingChatLanguageModel;

    private EmbeddingStore<TextSegment> embeddingStore;

    public DatabaseRagService() {
        this.embeddingStore = new InMemoryEmbeddingStore<>();
    }

    // 标记是否已初始化，避免重复初始化
    private volatile boolean initialized = false;

    /**
     * 初始化数据库文档到知识库
     */
    public synchronized void initializeDatabaseDocuments() {
        if (initialized) {
            log.debug("数据库文档已初始化，跳过重复初始化");
            return;
        }

        try {
            log.info("开始初始化数据库文档到RAG知识库");

            // 获取doc目录下的所有文档文件
            List<String> docFiles = getDocumentFiles();

            // 加载所有数据库文档
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
            throw new RuntimeException("数据库文档初始化失败", e);
        }
    }

    /**
     * 获取要加载的文档文件列表
     */
    private List<String> getDocumentFiles() {
        // 返回要加载的文档文件列表
        List<String> docFiles = new ArrayList<>();
        docFiles.add("doc/database_analysis.md");
        docFiles.add("doc/sql_query_analysis.md");

        // 可以通过配置文件或其他方式扩展更多文档
        // String[] additionalDocs = aiConfig.getRag().getAdditionalDocs();
        // if (additionalDocs != null) {
        //     docFiles.addAll(Arrays.asList(additionalDocs));
        // }

        return docFiles;
    }

    /**
     * 加载文档（如果存在）
     * @param docPath 文档路径
     * @return 是否成功加载
     */
    private boolean loadDocumentIfExists(String docPath) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(docPath);

            if (inputStream != null) {
                String docContent = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
                inputStream.close();

                Document document = Document.from(docContent);

                // 分割文档
                List<TextSegment> segments = DocumentSplitters.recursive(aiConfigProperties.getRag().getChunkSize(),
                        aiConfigProperties.getRag().getChunkOverlap()).split(document);

                // 将分段存入向量存储
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
     * 查询数据库相关信息（NL2SQL 专用入口，仅返回 SQL）
     */
    public String queryDatabaseInfo(String question) {
        try {
            // 创建内容检索器（仅用于补充 DDL / 表结构）
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

            SystemMessage systemMessage = new SystemMessage(loadSystemPrompt());
            UserMessage userMessage = new UserMessage(userContext.toString());

            Response<AiMessage> response =
                    chatLanguageModel.generate(List.of(systemMessage, userMessage));

            return response.content().text();
        } catch (Exception e) {
            log.error("NL2SQL 查询失败", e);
            throw new RuntimeException("NL2SQL 查询失败", e);
        }
    }
    /**
     * 加载 NL2SQL system prompt
     */
    private String loadSystemPrompt() {
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("prompts/system-prompt.st")) {

            if (is == null) {
                throw new RuntimeException("system-prompt.st 未找到");
            }
            String template = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // 获取数据库结构信息并替换模板中的{ddl}变量
            String ddl = getDatabaseSchemaInfo();
            return template.replace("{ddl}", ddl != null ? ddl : "未找到数据库结构信息");
        } catch (Exception e) {
            throw new RuntimeException("加载 system prompt 失败", e);
        }
    }

    /**
     * 获取数据库结构信息
     */
    private String getDatabaseSchemaInfo() {
        // 创建内容检索器
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(aiConfigProperties.getRag().getMaxResults())
                .minScore(aiConfigProperties.getRag().getMinScore())
                .build();

        // 检索数据库结构相关信息
        List<Content> relevantContents = contentRetriever.retrieve(new Query("数据库表结构信息"));

        StringBuilder schemaInfo = new StringBuilder();
        for (Content content : relevantContents) {
            schemaInfo.append(content.textSegment().text()).append("\n");
        }

        return schemaInfo.toString();
    }

    /**
     * 不推荐
     * NL2SQL 场景下流式输出容易破坏 SQL 完整性
     */
    public Flux<String> streamQueryDatabaseInfo(String question) {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        try {
            // 检查是否为SQL查询
            if (isSqlGenerationRequired(question)) {
                streamGenerateSqlQuery(question, sink);
                return sink.asFlux();
            }

            // 创建内容检索器
            ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .maxResults(aiConfigProperties.getRag().getMaxResults())
                    .minScore(aiConfigProperties.getRag().getMinScore())
                    .build();

            // 检索相关内容
            List<Content> relevantContents = contentRetriever.retrieve(new Query(question));

            if (relevantContents.isEmpty()) {
                sink.tryEmitNext("抱歉，我没有找到关于数据库的足够信息来回答您的问题。");
                sink.tryEmitComplete();
                return sink.asFlux();
            }

            // 构建上下文
            StringBuilder context = new StringBuilder();
            context.append("根据数据库结构信息：\n");
            for (Content content : relevantContents) {
                context.append(content.textSegment().text()).append("\n");
            }
            context.append("\n基于以上信息，回答问题：").append(question);

            // 使用流式聊天模型
            UserMessage userMessage = new UserMessage(context.toString());
            streamingChatLanguageModel.generate(userMessage, new StreamingResponseHandler<AiMessage>() {
                @Override
                public void onNext(String token) {
                    sink.tryEmitNext(token);
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    sink.tryEmitComplete();
                }

                @Override
                public void onError(Throwable error) {
                    log.error("流式查询数据库信息失败", error);
                    sink.tryEmitError(new RuntimeException("流式查询数据库信息时发生错误：" + error.getMessage()));
                }
            });
        } catch (Exception e) {
            log.error("流式查询数据库信息失败", e);
            sink.tryEmitError(new RuntimeException("流式查询数据库信息时发生错误：" + e.getMessage()));
        }

        return sink.asFlux();
    }

    /**
     * 判断是否需要生成SQL查询
     */
    private boolean isSqlGenerationRequired(String question) {
        // 检查是否包含SQL相关关键词
        String lowerQuestion = question.toLowerCase();
        return lowerQuestion.contains("查询") ||
               lowerQuestion.contains("统计") ||
               lowerQuestion.contains("列出") ||
               lowerQuestion.contains("有多少") ||
               lowerQuestion.contains("count") ||
               lowerQuestion.contains("sum") ||
               lowerQuestion.contains("avg") ||
               lowerQuestion.contains("max") ||
               lowerQuestion.contains("min") ||
               lowerQuestion.contains("求");
    }

    /**
     * 流式生成SQL查询
     */
    private void streamGenerateSqlQuery(String question, Sinks.Many<String> sink) {
        try {
            // 获取数据库结构信息作为DDL
            String ddl = getDatabaseSchemaInfo();

            // 读取系统提示模板
            String systemPrompt = loadSystemPrompt();

            // 构建用户上下文
            StringBuilder userContext = new StringBuilder();
            userContext.append("【DDL】\n");
            userContext.append(ddl != null ? ddl : "未找到数据库结构信息");
            userContext.append("\n\n【用户问题】\n").append(question);

            // 创建系统消息和用户消息
            SystemMessage systemMessage = new SystemMessage(systemPrompt);
            UserMessage userMessage = new UserMessage(userContext.toString());

            // 使用流式聊天模型生成SQL
            List<ChatMessage> messages = List.of(systemMessage, userMessage);
            streamingChatLanguageModel.generate(messages, new StreamingResponseHandler<AiMessage>() {
                @Override
                public void onNext(String token) {
                    sink.tryEmitNext(token);
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    sink.tryEmitComplete();
                }

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

    /**
     * 添加自定义文档到知识库
     */
    public void addDocumentToKnowledgeBase(String content) {
        try {
            Document document = Document.from(content);

            // 分割文档
            List<TextSegment> segments = DocumentSplitters.recursive(aiConfigProperties.getRag().getChunkSize(),
                    aiConfigProperties.getRag().getChunkOverlap()).split(document);

            // 将分段存入向量存储
            for (TextSegment segment : segments) {
                Embedding embedding = embeddingModel.embed(segment).content();
                embeddingStore.add(embedding, segment);
            }

            log.info("自定义文档已添加到RAG知识库，共{}个分段", segments.size());
        } catch (Exception e) {
            log.error("添加文档到知识库失败", e);
        }
    }

    /**
     * 获取嵌入存储
     */
    public EmbeddingStore<TextSegment> getEmbeddingStore() {
        return embeddingStore;
    }

    /**
     * 从配置路径加载知识库文档到向量存储
     */
    public void loadKnowledgeBaseDocuments() {
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
            List<Document> pdfDocuments =
                FileSystemDocumentLoader.loadDocuments(
                    path,
                    filePath -> filePath.toString().endsWith(".pdf"),
                    new ApachePdfBoxDocumentParser()
                );
            log.info("加载了 {} 个PDF文档", pdfDocuments.size());

            // 加载文本文件
            List<Document> textDocuments =
                FileSystemDocumentLoader.loadDocuments(
                    path,
                    filePath -> filePath.toString().endsWith(".txt") || filePath.toString().endsWith(".md"),
                    new TextDocumentParser()
                );
            log.info("加载了 {} 个文本文档", textDocuments.size());

            // 合并所有文档
            pdfDocuments.addAll(textDocuments);

            // 分割文档并存入向量存储
            for (Document document : pdfDocuments) {
                List<TextSegment> segments =
                    DocumentSplitters.recursive(
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
}
