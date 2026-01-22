package com.xinling.ai.service;

import com.xinling.ai.domain.vo.ChatRequest;
import com.xinling.ai.domain.vo.ChatResponse;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 心灵AI服务接口
 *
 * @author SuXia
 * @date 2025/12/29
 */
@Service
@Slf4j
public class XinLingAiService {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private ContentRetriever contentRetriever;

    @Autowired
    private OllamaService ollamaService;

    // 会话内存存储
    private final Map<String, ChatMemory> sessionMemories = new ConcurrentHashMap<>();

    /**
     * RAG增强的聊天服务
     */
    public String ragChat(String userMessage) {
        try {
            // 创建临时对话内存
            ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

            // 添加用户消息
            chatMemory.add(new UserMessage(userMessage));

            // 检索相关内容
            List<Content> relevantContents = contentRetriever.retrieve(new Query(userMessage));

            String finalMessage = userMessage;
            if (!relevantContents.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Content content : relevantContents) {
                    sb.append(content.textSegment().text()).append("\n");
                }
                finalMessage = "基于以下信息回答问题：\n" + sb.toString() + "\n\n问题：" + userMessage;
            }

            // 生成AI响应
            String response = chatLanguageModel.generate(chatMemory.messages()).content().text();

            // 添加AI响应到内存
            chatMemory.add(new AiMessage(response));

            return response;
        } catch (Exception e) {
            log.error("RAG聊天错误", e);
            throw new RuntimeException("RAG聊天服务错误: " + e.getMessage());
        }
    }

    /**
     * RAG增强的流式聊天服务
     */
    public Flux<ChatResponse> streamRagChat(ChatRequest request) {
        try {
            // 获取或创建会话内存
            ChatMemory chatMemory = sessionMemories.computeIfAbsent(
                request.getSessionId(),
                k -> MessageWindowChatMemory.withMaxMessages(20)
            );

            // 如果启用了RAG，先进行检索
            String augmentedMessage = request.getPrompt();
            if (request.getEnableRag() != null && request.getEnableRag()) {
                // 使用contentRetriever进行检索
                List<Content> relevantContents = contentRetriever.retrieve(new Query(request.getPrompt()));

                if (!relevantContents.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Content content : relevantContents) {
                        sb.append(content.textSegment().text()).append("\n");
                    }
                    // 将检索到的内容与用户问题拼接
                    augmentedMessage = "基于以下信息回答问题：\n" + sb.toString() + "\n\n问题：" + request.getPrompt();
                }
            }

            // 添加用户消息到内存
            chatMemory.add(new UserMessage(augmentedMessage));

            // 使用流式聊天
            return ollamaService.streamChat(augmentedMessage)
                    .map(content -> ChatResponse.of(content, request.getSessionId()))
                    .concatWith(Flux.just(ChatResponse.finish(request.getSessionId())));
        } catch (Exception e) {
            log.error("RAG流式聊天错误", e);
            return Flux.just(ChatResponse.error("RAG流式聊天服务错误: " + e.getMessage(), request.getSessionId()));
        }
    }

    /**
     * 多轮对话服务
     */
    public String multiTurnChat(String sessionId, String userMessage) {
        try {
            // 获取或创建会话内存
            ChatMemory chatMemory = sessionMemories.computeIfAbsent(
                sessionId,
                k -> MessageWindowChatMemory.withMaxMessages(20)
            );

            // 添加用户消息到内存
            chatMemory.add(new UserMessage(userMessage));

            // 生成AI响应
            String response = chatLanguageModel.generate(chatMemory.messages()).content().text();

            // 添加AI响应到内存
            chatMemory.add(new AiMessage(response));

            return response;
        } catch (Exception e) {
            log.error("多轮对话错误", e);
            throw new RuntimeException("多轮对话服务错误: " + e.getMessage());
        }
    }




    /**
     * 清除会话内存
     */
    public void clearSession(String sessionId) {
        sessionMemories.remove(sessionId);
    }
}
