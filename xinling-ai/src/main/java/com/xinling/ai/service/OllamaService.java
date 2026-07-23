package com.xinling.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinling.ai.config.AiConfigProperties;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Ollama服务类
 *
 * @author SuXia
 * @date 2025/12/29
 */
@Slf4j
@Service
public class OllamaService {

    @Autowired
    private AiConfigProperties aiConfig;

    @Autowired
    private ChatModel chatLanguageModel;

    @Autowired
    private StreamingChatModel streamingChatModel;

    /**
     * 获取Ollama模型列表
     */
    public Flux<String> listModels() {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String baseUrl = aiConfig.getOllama().getBaseUrl();
            String url = baseUrl.endsWith("/") ? baseUrl + "api/tags" : baseUrl + "/api/tags";

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode modelsNode = rootNode.get("models");

                if (modelsNode != null && modelsNode.isArray()) {
                    List<String> modelNames = StreamSupport.stream(modelsNode.spliterator(), false)
                        .map(model -> model.get("name").asText())
                        .filter(name -> name != null && !name.isEmpty())
                        .toList();

                    return Flux.fromIterable(modelNames);
                }
            }
        } catch (Exception e) {
            log.error("获取Ollama模型列表失败", e);
        }

        return Flux.just(aiConfig.getOllama().getChatModel());
    }

    /**
     * 同步聊天
     */
    public String chat(String userMessage) {
        try {
            return chatLanguageModel.chat(userMessage);
        } catch (Exception e) {
            log.error("Ollama聊天错误", e);
            throw new RuntimeException("Ollama聊天服务错误: " + e.getMessage());
        }
    }

    /**
     * 流式聊天
     */
    public Flux<String> streamChat(String userMessage) {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        try {
            streamingChatModel.chat(userMessage, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String token) {
                    sink.tryEmitNext(token);
                }

                @Override
                public void onCompleteResponse(ChatResponse response) {
                    sink.tryEmitComplete();
                }

                @Override
                public void onError(Throwable error) {
                    log.error("流式聊天错误", error);
                    sink.tryEmitError(error);
                }
            });
        } catch (Exception e) {
            log.error("Ollama流式聊天错误", e);
            sink.tryEmitError(new RuntimeException("Ollama流式聊天服务错误: " + e.getMessage()));
        }

        return sink.asFlux();
    }

    /**
     * 带上下文的流式聊天
     */
    public Flux<String> streamChatWithContext(List<ChatMessage> messages) {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        try {
            streamingChatModel.chat(messages, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String token) {
                    sink.tryEmitNext(token);
                }

                @Override
                public void onCompleteResponse(ChatResponse response) {
                    sink.tryEmitComplete();
                }

                @Override
                public void onError(Throwable error) {
                    log.error("流式聊天错误", error);
                    sink.tryEmitError(error);
                }
            });
        } catch (Exception e) {
            log.error("Ollama流式聊天错误", e);
            sink.tryEmitError(new RuntimeException("Ollama流式聊天服务错误: " + e.getMessage()));
        }

        return sink.asFlux();
    }
}
