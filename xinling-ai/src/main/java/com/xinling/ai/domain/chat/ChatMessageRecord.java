package com.xinling.ai.domain.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息记录（纯内存，避免与LangChain4j同名类冲突）
 *
 * @author SuXia
 * @date 2025/12/30
 */
@Data
public class ChatMessageRecord {
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

    public ChatMessageRecord() {
    }

    public ChatMessageRecord(String role, String content) {
        this.role = role;
        this.content = content;
        this.createTime = LocalDateTime.now();
    }

    public ChatMessageRecord(String role, String content, LocalDateTime createTime) {
        this.role = role;
        this.content = content;
        this.createTime = createTime;
    }
}