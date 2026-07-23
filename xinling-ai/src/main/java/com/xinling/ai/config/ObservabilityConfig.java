package com.xinling.ai.config;

import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 可观测性配置
 * 通过 ChatModelListener 记录每次 LLM 调用的请求/响应/错误信息
 *
 * @author SuXia
 * @date 2026/05/21
 */
@Slf4j
@Configuration
public class ObservabilityConfig {

    @Bean
    public ChatModelListener chatModelListener() {
        return new ChatModelListener() {
            @Override
            public void onRequest(ChatModelRequestContext requestContext) {
                log.info("[AI请求] model={}, messages={}",
                        requestContext.chatRequest().modelName(),
                        requestContext.chatRequest().messages().size());
            }

            @Override
            public void onResponse(ChatModelResponseContext responseContext) {
                var response = responseContext.chatResponse();
                var tokenUsage = response.tokenUsage();
                log.info("[AI响应] model={}, finishReason={}, inputTokens={}, outputTokens={}, totalTokens={}",
                        response.modelName(),
                        response.finishReason(),
                        tokenUsage != null ? tokenUsage.inputTokenCount() : "N/A",
                        tokenUsage != null ? tokenUsage.outputTokenCount() : "N/A",
                        tokenUsage != null ? tokenUsage.totalTokenCount() : "N/A");
            }

            @Override
            public void onError(ChatModelErrorContext errorContext) {
                log.error("[AI错误] model={}, error={}",
                        errorContext.chatRequest().modelName(),
                        errorContext.error().getMessage());
            }
        };
    }
}
