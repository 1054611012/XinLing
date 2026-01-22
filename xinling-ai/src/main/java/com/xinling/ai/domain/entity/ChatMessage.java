package com.xinling.ai.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息实体
 *
 * @author SuXia
 * @date 2025/12/30
 */
@Data
public class ChatMessage {
    /**
     * 消息角色: user, assistant
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public ChatMessage() {
    }

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
        this.createTime = LocalDateTime.now();
    }

    public ChatMessage(String role, String content, LocalDateTime createTime) {
        this.role = role;
        this.content = content;
        this.createTime = createTime;
    }
}