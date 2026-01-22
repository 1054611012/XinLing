package com.xinling.ai.domain.vo;

import lombok.Data;

/**
 * 聊天请求对象
 * 
 * @author SuXia
 * @date 2025/12/29
 */
@Data
public class ChatRequest {

    /**
     * 会话ID，用于多轮对话上下文管理
     */
    private String sessionId;

    /**
     * 用户输入的提示词
     */
    private String prompt;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 是否启用RAG检索
     */
    private Boolean enableRag = true;

    /**
     * 是否启用流式响应
     */
    private Boolean stream = true;
}