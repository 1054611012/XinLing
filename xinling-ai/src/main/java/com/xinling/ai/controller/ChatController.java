package com.xinling.ai.controller;

import com.xinling.ai.domain.chat.ChatMessageRecord;
import com.xinling.ai.domain.chat.ChatSession;
import com.xinling.ai.domain.config.AiModelConfig;
import com.xinling.ai.domain.enums.RagScene;
import com.xinling.ai.domain.vo.ChatRequest;
import com.xinling.ai.domain.vo.ChatResponse;
import com.xinling.ai.service.ChatHistoryService;
import com.xinling.ai.service.IAiModelConfigService;
import com.xinling.ai.service.RagExecutor;
import com.xinling.ai.service.ai.XinLingAssistant;
import com.xinling.ai.service.utils.QueryTypeUtils;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.UUID;

/**
 * AI 聊天控制器
 * 提供智能对话、多场景聊天等能力
 *
 * @author SuXia
 */
@Slf4j
@RestController
@RequestMapping("/ai")
@Tag(name = "AI 聊天控制器", description = "AI 聊天控制器")
public class ChatController {

    @Autowired
    private XinLingAssistant xinLingAssistant;

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private RagExecutor ragExecutor;

    @Autowired
    private IAiModelConfigService aiModelConfigService;

    /**
     * AI 智能聊天（v2 — Function Calling + Tool Use）
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "AI 智能聊天", description = "AI 智能聊天")
    public Flux<ChatResponse> chat(@RequestBody ChatRequest request) {
        final String sessionId;
        final boolean isNewSession;
        if (request.getSessionId() == null || request.getSessionId().trim().isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            isNewSession = true;
        } else {
            sessionId = request.getSessionId();
            isNewSession = false;
        }

        // 获取当前用户信息
        Long userId = null;
        String userName = null;
        try {
            userId = SecurityUtils.getUserId();
            userName = SecurityUtils.getUsername();
        } catch (Exception e) {
            log.warn("无法获取登录用户信息: {}", e.getMessage());
        }

        final String userPrompt = request.getPrompt();

        // 新会话：创建会话元数据
        if (isNewSession) {
            chatHistoryService.saveSession(new ChatSession(sessionId, null, userId, userName));
        }

        // 保存用户消息到 Redis
        chatHistoryService.saveMessage(sessionId, userId, userName,
                new ChatMessageRecord("user", userPrompt));

        // 记录用户信息供异步回调使用
        final Long finalUserId = userId;
        final String finalUserName = userName;

        Sinks.Many<ChatResponse> sink = Sinks.many().multicast().onBackpressureBuffer();
        StringBuilder fullResponse = new StringBuilder();

        xinLingAssistant.streamChat(sessionId, request.getPrompt())
                .onPartialResponse(token -> {
                    fullResponse.append(token);
                    sink.tryEmitNext(ChatResponse.of(token, sessionId));
                })
                .onCompleteResponse(response -> {
                    // 保存 AI 回复到 Redis
                    chatHistoryService.saveMessage(sessionId, finalUserId, finalUserName,
                            new ChatMessageRecord("assistant", fullResponse.toString()));

                    // 新会话：用用户消息自动生成标题（取前20个字符）
                    if (isNewSession) {
                        String title = userPrompt.length() > 20
                                ? userPrompt.substring(0, 20) + "…"
                                : userPrompt;
                        chatHistoryService.updateSessionTitle(sessionId, title);
                    }

                    sink.tryEmitNext(ChatResponse.finish(sessionId));
                    sink.tryEmitComplete();
                })
                .onError(error -> {
                    log.error("AI聊天错误", error);
                    sink.tryEmitNext(ChatResponse.error("AI聊天错误: " + error.getMessage(), sessionId));
                    sink.tryEmitComplete();
                })
                .start();

        return sink.asFlux();
    }

    /**
     * 获取已启用的对话模型列表（从管理后台配置中读取）
     */
    @GetMapping("/models")
    public AjaxResult listModels() {
        List<AiModelConfig> models = aiModelConfigService.selectEnabledChatModels();
        return AjaxResult.success(models);
    }

    /**
     * 智能聊天（v1 — RAG 自动选择场景）
     */
    @PostMapping(value = "/smartChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> smartChat(@RequestBody ChatRequest request) {
        RagScene scene = QueryTypeUtils.detect(request.getPrompt());
        log.info("智能聊天场景：{}，查询内容：{}", scene, request.getPrompt());

        return ragExecutor.stream(request.getPrompt(), scene)
                .map(content -> ChatResponse.of(content, request.getSessionId()))
                .concatWith(Flux.just(ChatResponse.finish(request.getSessionId())))
                .onErrorResume(error -> {
                    log.error("智能聊天错误", error);
                    return Flux.just(ChatResponse.error("智能聊天服务错误: " + error.getMessage(), request.getSessionId()));
                });
    }
}
