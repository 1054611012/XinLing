package com.xinling.mq.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息数据传输对象
 *
 * @author SuXia
 * @date 2026/01/07
 */
@Data
public class ChatMessageDto {
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 消息角色: user, assistant
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
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
     * 消息序列号，用于保证顺序消费
     */
    private Long sequenceNumber;

    public ChatMessageDto() {
    }

    public ChatMessageDto(String sessionId, String role, String content, Long userId, String userName) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
        this.createTime = LocalDateTime.now();
        this.sequenceNumber = System.currentTimeMillis(); // 使用时间戳作为序列号
    }

    public ChatMessageDto(String sessionId, String role, String content, Long userId, String userName, Long sequenceNumber) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
        this.createTime = LocalDateTime.now();
        this.sequenceNumber = sequenceNumber != null ? sequenceNumber : System.currentTimeMillis();
    }
}