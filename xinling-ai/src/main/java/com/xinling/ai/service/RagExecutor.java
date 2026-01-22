package com.xinling.ai.service;

import com.xinling.ai.config.AiConfigProperties;
import com.xinling.ai.domain.entity.ChatMessage;
import com.xinling.ai.enums.RagScene;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

/**
 * RAG执行器
 *
 * @author SuXia
 * @date 2025/12/29
 */
@Slf4j
@Service
public class RagExecutor {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private ContentRetriever contentRetriever;

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private DatabaseRagService databaseRagService;

    @Autowired
    private AiConfigProperties aiConfig;

    /**
     * 执行RAG查询
     */
    public String execute(String query, RagScene scene) {
        return execute(query, scene, List.of());
    }

    /**
     * 执行RAG查询（带历史消息）
     */
    public String execute(String query, RagScene scene, List<com.xinling.ai.domain.entity.ChatMessage> historyMessages) {
        try {
            switch (scene) {
                case NL2SQL:
                    return databaseRagService.queryDatabaseInfo(query);
                case KNOWLEDGE_QA:
                case QA:
                default:
                    // 使用RAG检索相关内容
                    var relevantContents = contentRetriever.retrieve(new Query(query));

                    // 构建消息列表（包含历史消息）
                    List<dev.langchain4j.data.message.ChatMessage> messages = new ArrayList<>();
                    
                    // 添加历史消息
                    if (historyMessages != null && !historyMessages.isEmpty()) {
                        for (com.xinling.ai.domain.entity.ChatMessage historyMessage : historyMessages) {
                            if ("user".equals(historyMessage.getRole())) {
                                messages.add(new UserMessage(historyMessage.getContent()));
                            } else if ("assistant".equals(historyMessage.getRole())) {
                                messages.add(new AiMessage(historyMessage.getContent()));
                            }
                        }
                    }
                    
                    String finalQuery;
                    if (relevantContents.isEmpty()) {
                        // 如果没有找到相关内容，直接使用基础模型
                        finalQuery = query;
                    } else {
                        // 构建包含上下文的查询
                        StringBuilder context = new StringBuilder();
                        context.append("请基于以下信息回答问题：\n");
                        for (var content : relevantContents) {
                            context.append(content.textSegment().text()).append("\n");
                        }
                        context.append("\n问题：").append(query);
                        finalQuery = context.toString();
                    }
                    
                    // 添加当前查询
                    messages.add(new UserMessage(finalQuery));
                    
                    // 生成回答
                    return chatLanguageModel.generate(messages).content().text();
            }
        } catch (Exception e) {
            log.error("RAG执行错误", e);
            return "RAG执行时发生错误：" + e.getMessage();
        }
    }

    /**
     * 流式执行RAG查询
     */
    public Flux<String> stream(String query, RagScene scene) {
        return stream(query, scene, List.of());
    }

    /**
     * 流式执行RAG查询（带历史消息）
     */
    public Flux<String> stream(String query, RagScene scene, List<com.xinling.ai.domain.entity.ChatMessage> historyMessages) {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        try {
            switch (scene) {
                case NL2SQL:
                    // 对于NL2SQL场景，使用数据库RAG服务的流式方法
                    databaseRagService.streamQueryDatabaseInfo(query)
                        .subscribe(
                            token -> sink.tryEmitNext(token),
                            error -> {
                                log.error("流式NL2SQL执行错误", error);
                                sink.tryEmitError(error);
                            },
                            () -> sink.tryEmitComplete()
                        );
                    break;
                case KNOWLEDGE_QA:
                case QA:
                default:
                    // 使用RAG检索相关内容
                    var relevantContents = contentRetriever.retrieve(new Query(query));

                    String finalQuery;
                    if (relevantContents.isEmpty()) {
                        // 如果没有找到相关内容，直接使用基础查询
                        finalQuery = query;
                    } else {
                        // 构建包含上下文的查询
                        StringBuilder context = new StringBuilder();
                        context.append("请基于以下信息回答问题：\n");
                        for (var content : relevantContents) {
                            context.append(content.textSegment().text()).append("\n");
                        }
                        context.append("\n问题：").append(query);
                        finalQuery = context.toString();
                    }

                    // 使用流式聊天，结合历史消息
                    ollamaService.streamChat(finalQuery)
                        .subscribe(
                            token -> sink.tryEmitNext(token),
                            error -> {
                                log.error("流式RAG执行错误", error);
                                sink.tryEmitError(error);
                            },
                            () -> sink.tryEmitComplete()
                        );
                    break;
            }
        } catch (Exception e) {
            log.error("RAG流式执行错误", e);
            sink.tryEmitError(new RuntimeException("RAG流式执行错误: " + e.getMessage()));
        }

        return sink.asFlux();
    }
}