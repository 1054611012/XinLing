package com.xinling.ai.domain.vo;

import lombok.Data;

/**
 * 聊天响应对象
 * 
 * @author SuXia
 * @date 2025/12/29
 */
@Data
public class ChatResponse {

    /**
     * 响应内容
     */
    private String content;

    /**
     * 是否结束
     */
    private Boolean finish = false;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 错误信息（如果有）
     */
    private String error;

    public static ChatResponse of(String content, String sessionId) {
        ChatResponse response = new ChatResponse();
        response.setContent(content);
        response.setSessionId(sessionId);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static ChatResponse finish(String sessionId) {
        ChatResponse response = new ChatResponse();
        response.setFinish(true);
        response.setSessionId(sessionId);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static ChatResponse error(String error, String sessionId) {
        ChatResponse response = new ChatResponse();
        response.setError(error);
        response.setSessionId(sessionId);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}