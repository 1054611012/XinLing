package com.xinling.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinling.ai.config.AiConfigProperties;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
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
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private StreamingChatLanguageModel streamingChatLanguageModel;

    /**
     * 获取Ollama模型列表
     */
    public Flux<String> listModels() {
        try {
            // 创建HTTP客户端
            HttpClient client = HttpClient.newHttpClient();

            // 构建请求URL
            String baseUrl = aiConfig.getOllama().getBaseUrl();
            String url = baseUrl.endsWith("/") ? baseUrl + "api/tags" : baseUrl + "/api/tags";

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

            // 发送请求并获取响应
            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode modelsNode = rootNode.get("models");

                if (modelsNode != null && modelsNode.isArray()) {
                    // 将JsonNode数组转换为Stream并映射为模型名称
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

        // 如果API调用失败，返回配置的模型名称
        return Flux.just(aiConfig.getOllama().getModel());
    }

    /**
     * 同步聊天
     */
    public String chat(String userMessage) {
        try {
            UserMessage userMsg = new UserMessage(userMessage);
            Response<AiMessage> response = chatLanguageModel.generate(userMsg);
            return response.content().text();
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
            UserMessage userMsg = new UserMessage(userMessage);

            streamingChatLanguageModel.generate(userMsg, new StreamingResponseHandler<AiMessage>() {
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
