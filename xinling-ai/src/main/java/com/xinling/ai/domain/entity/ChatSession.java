package com.xinling.ai.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话实体
 *
 * @author SuXia
 * @date 2025/12/30
 */
@Data
public class ChatSession {
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 会话标题
     */
    private String title;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public ChatSession() {
    }

    public ChatSession(String sessionId, String title, Long userId, String userName) {
        this.sessionId = sessionId;
        this.title = title;
        this.userId = userId;
        this.userName = userName;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}