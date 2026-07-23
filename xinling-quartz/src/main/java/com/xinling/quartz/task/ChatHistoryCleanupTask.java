package com.xinling.quartz.task;

import com.xinling.common.constant.RedisKeys;
import com.xinling.common.core.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * 聊天历史记录清理任务
 *
 * @author SuXia
 * @date 2025/12/30
 */
@Slf4j
@Component("chatHistoryCleanupTask")
public class ChatHistoryCleanupTask {

    @Autowired
    private RedisCache redisCache;

    private static final String CHAT_SESSION_PREFIX = "chat:session:";

    /**
     * 每天凌晨2点清理过期的聊天记录
     */
//     @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredChatHistory() {
        log.info("开始清理过期的聊天记录");

        try {
            // 1. 清理过期的会话历史记录
            Collection<String> historyKeys = redisCache.keys(RedisKeys.CHAT_HISTORY + "*");
            int historyCleaned = 0;
            for (String key : historyKeys) {
                if (!redisCache.hasKey(key)) {
                    redisCache.deleteObject(key);
                    historyCleaned++;
                    log.debug("清理过期历史记录: {}", key);
                }
            }

            // 2. 清理过期的会话信息
            Collection<String> sessionKeys = redisCache.keys(RedisKeys.CHAT_USER_SESSIONS + "*");
            int sessionCleaned = 0;
            for (String key : sessionKeys) {
                if (!redisCache.hasKey(key)) {
                    redisCache.deleteObject(key);
                    sessionCleaned++;
                    log.debug("清理过期会话信息: {}", key);
                }
            }

            // 3. 清理用户会话列表中的无效引用
            Collection<String> userSessionKeys = redisCache.keys(RedisKeys.CHAT_USER_SESSIONS + "*");
            int userSessionCleaned = 0;
            for (String key : userSessionKeys) {
                try {
                    // 尝试读取 List，如果不是 List，则捕获异常跳过
                    List<String> sessionIds;
                    try {
                        sessionIds = redisCache.getCacheList(key);
                    } catch (Exception e) {
                        log.warn("跳过非List类型的用户会话 key: {}, 异常: {}", key, e.getMessage());
                        continue;
                    }

                    if (sessionIds != null && !sessionIds.isEmpty()) {
                        List<String> validSessionIds = new ArrayList<>();
                        for (String sessionId : sessionIds) {
                            String sessionKey = CHAT_SESSION_PREFIX + sessionId;
                            if (redisCache.hasKey(sessionKey)) {
                                validSessionIds.add(sessionId);
                            } else {
                                userSessionCleaned++;
                                log.debug("清理用户会话列表中的无效引用: {}", sessionId);
                            }
                        }

                        // 重新保存有效的会话ID列表
                        if (!validSessionIds.equals(sessionIds)) {
                            redisCache.deleteObject(key);
                            if (!validSessionIds.isEmpty()) {
                                redisCache.setCacheList(key, validSessionIds);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("清理用户会话列表时出错，key: {}", key, e);
                }
            }

            log.info("聊天记录清理完成，清理历史记录: {}, 会话信息: {}, 用户会话引用: {}",
                    historyCleaned, sessionCleaned, userSessionCleaned);
        } catch (Exception e) {
            log.error("清理过期聊天记录时出错", e);
        }
    }
}
