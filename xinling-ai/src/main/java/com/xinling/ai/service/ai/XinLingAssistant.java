package com.xinling.ai.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

/**
 * XinLing AI 助手接口（LangChain4j AiServices 声明式 AI 服务）
 * 替代手动编排的 RagExecutor，支持 Function Calling / Tool Use
 *
 * @author SuXia
 * @date 2026/05/21
 */
public interface XinLingAssistant {

    /**
     * 同步聊天
     * @param sessionId 会话ID（用于多轮对话记忆）
     * @param userMessage 用户消息
     * @return AI 回复
     */
    String chat(@MemoryId String sessionId, @UserMessage String userMessage);

    /**
     * 流式聊天
     * @param sessionId 会话ID（用于多轮对话记忆）
     * @param userMessage 用户消息
     * @return 流式 TokenStream
     */
    TokenStream streamChat(@MemoryId String sessionId, @UserMessage String userMessage);
}
