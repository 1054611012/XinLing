package com.xinling.ai.service;

import com.xinling.ai.domain.entity.ChatMessage;
import com.xinling.ai.domain.entity.ChatSession;

import java.util.List;

/**
 * 聊天历史记录服务
 *
 * @author SuXia
 * @date 2025/12/30
 */
public interface ChatHistoryService {
    
    /**
     * 保存聊天消息到指定会话
     *
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param userName 用户名
     * @param message 消息内容
     */
    void saveMessage(String sessionId, Long userId, String userName, ChatMessage message);
    
    /**
     * 获取会话历史消息
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<ChatMessage> getHistoryMessages(String sessionId);
    
    /**
     * 保存会话标题
     *
     * @param session 会话信息
     */
    void saveSession(ChatSession session);
    
    /**
     * 获取用户的所有会话标题
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ChatSession> getUserSessions(Long userId);
    
    /**
     * 获取会话标题
     *
     * @param sessionId 会话ID
     * @return 会话信息
     */
    ChatSession getSession(String sessionId);
    
    /**
     * 删除会话及历史记录
     *
     * @param sessionId 会话ID
     */
    void deleteSession(String sessionId);
    
    /**
     * 更新会话标题
     *
     * @param sessionId 会话ID
     * @param title 新标题
     */
    void updateSessionTitle(String sessionId, String title);
    
    /**
     * 验证会话是否属于指定用户
     * 
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 是否属于该用户
     */
    boolean isSessionBelongsToUser(String sessionId, Long userId);
}