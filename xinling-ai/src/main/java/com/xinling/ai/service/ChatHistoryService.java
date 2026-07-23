package com.xinling.ai.service;

import com.xinling.ai.domain.chat.ChatMessageRecord;
import com.xinling.ai.domain.chat.ChatSession;

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
     */
    void saveMessage(String sessionId, Long userId, String userName, ChatMessageRecord message);

    /**
     * 获取会话历史消息（全量）
     */
    List<ChatMessageRecord> getHistoryMessages(String sessionId);

    /**
     * 保存会话信息
     */
    void saveSession(ChatSession session);

    /**
     * 获取用户的所有会话（分页），按最后更新时间降序
     *
     * @param userId 用户ID
     * @param page 页码，从1开始
     * @param size 每页条数
     * @return 分页结果，包含会话列表和总数
     */
    SessionPageResult getUserSessions(Long userId, int page, int size);

    /**
     * 获取会话信息
     */
    ChatSession getSession(String sessionId);

    /**
     * 删除会话及历史记录
     */
    void deleteSession(String sessionId);

    /**
     * 更新会话标题
     */
    void updateSessionTitle(String sessionId, String title);

    /**
     * 验证会话是否属于指定用户
     */
    boolean isSessionBelongsToUser(String sessionId, Long userId);

    /**
     * 保存会话与配置的映射关系
     */
    void saveSessionConfigMapping(String sessionId, Long configId);

    /**
     * 获取会话关联的配置ID
     */
    Long getSessionConfigId(String sessionId);

    /**
     * 会话分页结果
     */
    class SessionPageResult {
        private List<ChatSession> list;
        private long total;

        public SessionPageResult() {}

        public SessionPageResult(List<ChatSession> list, long total) {
            this.list = list;
            this.total = total;
        }

        public List<ChatSession> getList() { return list; }
        public void setList(List<ChatSession> list) { this.list = list; }
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
    }
}