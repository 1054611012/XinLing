package com.xinling.ai.service.impl;

import com.xinling.ai.domain.chat.ChatMessageRecord;
import com.xinling.ai.domain.chat.ChatSession;
import com.xinling.common.constant.RedisKeys;
import com.xinling.mq.dto.ChatMessageDto;
import com.xinling.ai.service.ChatHistoryService;
import com.xinling.common.core.redis.RedisCache;
import com.xinling.mq.service.MqProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xinling.common.constant.RedisKeys.SESSION_CONFIG_PREFIX;

/**
 * 聊天历史记录服务实现
 * - 会话列表：ZSet（按 updateTime 降序），支持分页
 * - 会话元数据：Hash（chat:session:{sessionId}）
 * - 消息历史：List（chat:history:{sessionId}），全量加载
 *
 * @author SuXia
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

    private static final int EXPIRE_DAYS = 30;

    /** Hash 字段名 */
    private static final String F_TITLE = "title";
    private static final String F_USER_ID = "userId";
    private static final String F_CREATE_TIME = "createTime";
    private static final String F_UPDATE_TIME = "updateTime";
    private static final String F_MESSAGE_COUNT = "messageCount";

    /* ===================== 消息历史 ===================== */

    @Override
    public void saveMessage(String sessionId, Long userId, String userName, ChatMessageRecord message) {
        try {
            String key = RedisKeys.CHAT_HISTORY + sessionId;
            redisCache.redisTemplate.opsForList().rightPush(key, message);
            redisCache.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);

            updateSessionMeta(sessionId, userId);

            ChatMessageDto dto = new ChatMessageDto(sessionId, message.getRole(),
                    message.getContent(), userId, userName);
            mqProviderService.sendChatMessage(dto, dto.getSequenceNumber());
        } catch (Exception e) {
            log.error("保存消息失败: sessionId={}", sessionId, e);
        }
    }

    @Override
    public List<ChatMessageRecord> getHistoryMessages(String sessionId) {
        String key = RedisKeys.CHAT_HISTORY + sessionId;
        try {
            if (Boolean.TRUE.equals(redisCache.redisTemplate.hasKey(key))) {
                List<ChatMessageRecord> list = redisCache.getCacheList(key);
                redisCache.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);
                return list != null ? list : new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("获取消息历史失败: sessionId={}", sessionId, e);
        }
        return new ArrayList<>();
    }

    /* ===================== 会话操作 ===================== */

    private String hashKey(String sessionId) {
        return RedisKeys.CHAT_SESSION_PREFIX + sessionId;
    }

    @Override
    public void saveSession(ChatSession session) {
        try {
            Map<String, Object> meta = new LinkedHashMap<>();
            String now = nowStr();
            meta.put(F_TITLE, session.getTitle() != null ? session.getTitle() : "");
            meta.put(F_USER_ID, String.valueOf(session.getUserId()));
            meta.put(F_CREATE_TIME, now);
            meta.put(F_UPDATE_TIME, now);
            meta.put(F_MESSAGE_COUNT, "0");

            String hk = hashKey(session.getSessionId());
            redisCache.redisTemplate.opsForHash().putAll(hk, meta);
            redisCache.expire(hk, EXPIRE_DAYS, TimeUnit.DAYS);

            addToUserZSet(session.getUserId(), session.getSessionId(), now);
        } catch (Exception e) {
            log.error("保存会话失败: sessionId={}", session.getSessionId(), e);
        }
    }

    @Override
    public SessionPageResult getUserSessions(Long userId, int page, int size) {
        String key = RedisKeys.CHAT_USER_SESSIONS + userId;
        Long total = redisCache.redisTemplate.opsForZSet().size(key);
        if (total == null || total == 0) {
            return new SessionPageResult(new ArrayList<>(), 0);
        }

        long start = (long) (page - 1) * size;
        long end = start + size - 1;
        Set<String> ids = redisCache.redisTemplate.opsForZSet()
                .reverseRange(key, start, end);

        if (ids == null || ids.isEmpty()) {
            return new SessionPageResult(new ArrayList<>(), total);
        }

        List<ChatSession> sessions = ids.stream()
                .map(this::buildFromHash)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new SessionPageResult(sessions, total);
    }

    @Override
    public ChatSession getSession(String sessionId) {
        return buildFromHash(sessionId);
    }

    @Override
    public void deleteSession(String sessionId) {
        try {
            String hk = hashKey(sessionId);
            Object uid = redisCache.redisTemplate.opsForHash().get(hk, F_USER_ID);

            redisCache.deleteObject(RedisKeys.CHAT_HISTORY + sessionId);
            redisCache.deleteObject(RedisKeys.SESSION_CONFIG_PREFIX + sessionId);
            redisCache.deleteObject(hk);

            if (uid != null) {
                redisCache.redisTemplate.opsForZSet().remove(
                        RedisKeys.CHAT_USER_SESSIONS + Long.valueOf(uid.toString()), sessionId);
            }
        } catch (Exception e) {
            log.error("删除会话失败: sessionId={}", sessionId, e);
        }
    }

    @Override
    public void updateSessionTitle(String sessionId, String title) {
        try {
            String hk = hashKey(sessionId);
            redisCache.redisTemplate.opsForHash().put(hk, F_TITLE, title);
            redisCache.expire(hk, EXPIRE_DAYS, TimeUnit.DAYS);

            String now = nowStr();
            redisCache.redisTemplate.opsForHash().put(hk, F_UPDATE_TIME, now);

            Object uid = redisCache.redisTemplate.opsForHash().get(hk, F_USER_ID);
            if (uid != null) {
                addToUserZSet(Long.valueOf(uid.toString()), sessionId, now);
            }
        } catch (Exception e) {
            log.error("更新标题失败: sessionId={}", sessionId, e);
        }
    }

    @Override
    public boolean isSessionBelongsToUser(String sessionId, Long userId) {
        try {
            Double score = redisCache.redisTemplate.opsForZSet()
                    .score(RedisKeys.CHAT_USER_SESSIONS + userId, sessionId);
            return score != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void saveSessionConfigMapping(String sessionId, Long configId) {
        try {
            redisCache.setCacheObject(SESSION_CONFIG_PREFIX + sessionId, configId, EXPIRE_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("保存配置映射失败: sessionId={}", sessionId, e);
        }
    }

    @Override
    public Long getSessionConfigId(String sessionId) {
        try {
            return redisCache.getCacheObject(SESSION_CONFIG_PREFIX + sessionId);
        } catch (Exception e) {
            return null;
        }
    }

    /* ===================== 内部方法 ===================== */

    private ChatSession buildFromHash(String sessionId) {
        try {
            Map<Object, Object> entries = redisCache.redisTemplate.opsForHash().entries(hashKey(sessionId));
            if (entries == null || entries.isEmpty()) {
                return null;
            }
            ChatSession s = new ChatSession();
            s.setSessionId(sessionId);
            s.setTitle(str(entries, F_TITLE));
            s.setUserId(lng(entries, F_USER_ID));
            if (entries.containsKey(F_CREATE_TIME)) {
                s.setCreateTime(LocalDateTime.parse(str(entries, F_CREATE_TIME), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            if (entries.containsKey(F_UPDATE_TIME)) {
                s.setUpdateTime(LocalDateTime.parse(str(entries, F_UPDATE_TIME), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            return s;
        } catch (Exception e) {
            log.warn("从 Hash 构建会话失败: sessionId={}", sessionId, e);
            return null;
        }
    }

    private void addToUserZSet(Long userId, String sessionId, String timeStr) {
        try {
            double score = LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            redisCache.zAdd(RedisKeys.CHAT_USER_SESSIONS + userId, sessionId, score, EXPIRE_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("ZSet 添加失败: sessionId={}", sessionId, e);
        }
    }

    private void updateSessionMeta(String sessionId, Long userId) {
        try {
            String hk = hashKey(sessionId);
            String now = nowStr();
            redisCache.redisTemplate.opsForHash().put(hk, F_UPDATE_TIME, now);
            redisCache.redisTemplate.opsForHash().increment(hk, F_MESSAGE_COUNT, 1);
            redisCache.expire(hk, EXPIRE_DAYS, TimeUnit.DAYS);
            addToUserZSet(userId, sessionId, now);
        } catch (Exception e) {
            log.warn("更新会话元数据失败: sessionId={}", sessionId, e);
        }
    }

    private static String nowStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private static String str(Map<Object, Object> m, String k) {
        Object v = m.get(k);
        return v != null ? v.toString() : null;
    }

    private static Long lng(Map<Object, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) return null;
        try { return Long.valueOf(v.toString()); } catch (NumberFormatException e) { return null; }
    }
}
