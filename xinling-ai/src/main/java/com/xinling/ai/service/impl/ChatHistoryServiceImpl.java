package com.xinling.ai.service.impl;

import com.xinling.ai.domain.entity.ChatMessage;
import com.xinling.ai.domain.entity.ChatSession;
import com.xinling.common.constant.RedisKeys;
import com.xinling.mq.dto.ChatMessageDto;
import com.xinling.ai.service.ChatHistoryService;
import com.xinling.common.core.redis.RedisCache;
import com.xinling.mq.service.MqProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 聊天历史记录服务实现类（安全版，支持用户映射）
 */
@Slf4j
@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private MqProviderService mqProviderService;

    @Value("${xinling.rabbitmq.chat-routing-key:chat.routing.key}")
    private String chatRoutingKey;

    private static final int HISTORY_EXPIRE_DAYS = 30;

    /*===================== Redis 类型安全工具方法 =====================*/

    public <T> List<T> safeGetCacheList(String key, Class<T> clazz) {
        try {
            if (redisCache.redisTemplate.hasKey(key)) {
                String type = redisCache.redisTemplate.type(key).code();
                if (!"list".equalsIgnoreCase(type)) {
                    redisCache.deleteObject(key);
                }
            }
            List<T> list = redisCache.getCacheList(key);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            log.error("安全获取 List 失败，key: {}", key, e);
            return new ArrayList<>();
        }
    }

    public void safeSetCacheList(String key, List<?> list) {
        try {
            redisCache.setCacheList(key, list);
            redisCache.expire(key, HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("安全设置 List 失败，key: {}", key, e);
        }
    }

    /*===================== 消息历史操作 =====================*/

    @Override
    public void saveMessage(String sessionId, Long userId, String userName, ChatMessage message) {
        try {
            String key = RedisKeys.CHAT_HISTORY + sessionId;
            redisCache.redisTemplate.opsForList().rightPush(key, message);
            redisCache.expire(key, HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);

            log.debug("保存消息到会话: {}, 内容: {}", sessionId, message.getContent());

            // 直接使用传入的用户信息发送到RabbitMQ，避免调用可能耗时的getSession方法
            ChatMessageDto dto = new ChatMessageDto(sessionId, message.getRole(),
                    message.getContent(), userId, userName);
            mqProviderService.sendChatMessage(dto);
            log.debug("发送消息到 {} 队列，会话ID: {}, 用户ID: {}", mqProviderService.getMqType(), sessionId, userId);
        } catch (Exception e) {
            log.error("保存消息到 Redis 失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<ChatMessage> getHistoryMessages(String sessionId) {
        String key = RedisKeys.CHAT_HISTORY + sessionId;
        List<ChatMessage> messages = safeGetCacheList(key, ChatMessage.class);
        redisCache.expire(key, HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);
        return messages;
    }

    /*===================== 会话操作 =====================*/

    @Override
    public void saveSession(ChatSession session) {
        try {
            String titleKey = RedisKeys.CHAT_TITLE + session.getSessionId();
            String title = session.getTitle();
            if (title == null || title.isEmpty()) {
                List<ChatMessage> messages = getHistoryMessages(session.getSessionId());
                if (!messages.isEmpty()) {
                    title = messages.get(0).getContent();
                }
            }
            if (title != null && !title.isEmpty()) {
                redisCache.setCacheObject(titleKey, title, HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);
            }

            // 用户会话列表
            String userSessionsKey = RedisKeys.CHAT_USER_SESSIONS + session.getUserId();
            List<String> sessionIds = safeGetCacheList(userSessionsKey, String.class);
            sessionIds.remove(session.getSessionId());
            sessionIds.add(0, session.getSessionId());
            safeSetCacheList(userSessionsKey, sessionIds);

            // 映射 sessionId -> userId
            String sessionUserKey = RedisKeys.SESSION_USER_PREFIX + session.getSessionId();
            redisCache.setCacheObject(sessionUserKey, session.getUserId(), HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);

            log.debug("保存会话: {}, 用户ID: {}, 标题: {}", session.getSessionId(), session.getUserId(), title);
        } catch (Exception e) {
            log.error("保存会话到 Redis 失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<ChatSession> getUserSessions(Long userId) {
        String userSessionsKey = RedisKeys.CHAT_USER_SESSIONS + userId;
        List<String> sessionIds = safeGetCacheList(userSessionsKey, String.class);
        List<ChatSession> sessions = new ArrayList<>();
        for (String sessionId : sessionIds) {
            String title = redisCache.getCacheObject(RedisKeys.CHAT_TITLE + sessionId);
            ChatSession session = new ChatSession();
            session.setSessionId(sessionId);
            session.setUserId(userId);
            session.setTitle(title);
            sessions.add(session);
        }
        return sessions;
    }

    @Override
    public ChatSession getSession(String sessionId) {
        try {
            String sessionUserKey = RedisKeys.SESSION_USER_PREFIX + sessionId;
            Long userId = redisCache.getCacheObject(sessionUserKey);
            if (userId == null) return null;

            String title = redisCache.getCacheObject(RedisKeys.CHAT_TITLE + sessionId);
            ChatSession session = new ChatSession();
            session.setSessionId(sessionId);
            session.setUserId(userId);
            session.setTitle(title);
            return session;
        } catch (Exception e) {
            log.error("从 Redis 获取会话失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void deleteSession(String sessionId) {
        ChatSession session = getSession(sessionId);
        if (session == null) {
            log.warn("尝试删除不存在的会话: {}", sessionId);
            return;
        }

        redisCache.deleteObject(RedisKeys.CHAT_HISTORY + sessionId);
        redisCache.deleteObject(RedisKeys.CHAT_TITLE + sessionId);
        redisCache.deleteObject(RedisKeys.SESSION_USER_PREFIX + sessionId);

        String userSessionsKey = RedisKeys.CHAT_USER_SESSIONS + session.getUserId();
        List<String> sessionIds = safeGetCacheList(userSessionsKey, String.class);
        sessionIds.remove(sessionId);
        safeSetCacheList(userSessionsKey, sessionIds);

        log.debug("删除会话: {}, 用户ID: {}", sessionId, session.getUserId());
    }

    @Override
    public void updateSessionTitle(String sessionId, String title) {
        ChatSession session = getSession(sessionId);
        if (session != null) {
            redisCache.setCacheObject(RedisKeys.CHAT_TITLE + sessionId, title, HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);
            saveSession(session);
            log.debug("更新会话标题: {}, 用户ID: {}, 新标题: {}", sessionId, session.getUserId(), title);
        }
    }

    @Override
    public boolean isSessionBelongsToUser(String sessionId, Long userId) {
        String sessionUserKey = RedisKeys.SESSION_USER_PREFIX + sessionId;
        Long storedUserId = redisCache.getCacheObject(sessionUserKey);
        return storedUserId != null && storedUserId.equals(userId);
    }
}
