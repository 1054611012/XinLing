package com.xinling.ai.domain.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 聊天消息数据库实体
 *
 * @author SuXia
 * @date 2026/01/07
 */
@Data
public class ChatMessageEntity {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 消息角色: user, assistant
     */
    @NotBlank(message = "消息角色不能为空")
    private String role;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
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

    public ChatMessageEntity() {
    }

    public ChatMessageEntity(String sessionId, String role, String content, Long userId, String userName, LocalDateTime createTime, Long sequenceNumber) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
        this.createTime = createTime;
        this.sequenceNumber = sequenceNumber;
    }
}
