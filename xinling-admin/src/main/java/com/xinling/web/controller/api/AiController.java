package com.xinling.web.controller.api;

import com.xinling.ai.domain.entity.ChatMessage;
import com.xinling.ai.domain.entity.ChatSession;
import com.xinling.ai.domain.vo.ChatRequest;
import com.xinling.ai.domain.vo.ChatResponse;
import com.xinling.ai.enums.RagScene;
import com.xinling.ai.service.*;
import com.xinling.ai.utils.QueryTypeUtils;
import com.xinling.common.constant.RedisKeys;
import com.xinling.common.core.domain.entity.SysUser;
import com.xinling.common.core.domain.model.LoginUser;
import com.xinling.common.utils.SecurityUtils;
import com.xinling.common.core.redis.RedisCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * AI控制器（更新版）
 * 去掉 chat:session:，历史消息和标题分开存储
 */
@Slf4j
@RestController
@RequestMapping("/ai")
@Tag(name = "AI聊天接口", description = "AI控制器")
public class AiController {

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private XinLingAiService xinLingAiService;

    @Autowired
    private DatabaseRagService databaseRagService;

    @Autowired
    private RagExecutor ragExecutor;

    @Autowired
    private DocumentLoaderService documentLoaderService;

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private RedisCache redisCache;

    private static final int HISTORY_EXPIRE_DAYS = 30;


    /**
     * 获取模型列表
     */
    @Operation(summary = "获取模型列表", description = "获取Ollama可用模型列表")
    @GetMapping("/listModels")
    public ResponseEntity<String[]> listModels() {
        try {
            List<String> models = ollamaService.listModels()
                    .take(10) // 限制最多返回10个模型
                    .collectList()
                    .block();

            if (models != null) {
                return ResponseEntity.ok(models.toArray(new String[0]));
            } else {
                return ResponseEntity.ok(new String[]{});
            }
        } catch (Exception e) {
            log.error("获取模型列表失败", e);
            return ResponseEntity.status(500).body(new String[]{});
        }
    }

    /**
     * 智能聊天接口（自动选择场景）
     */
    @Operation(summary = "智能聊天", description = "智能聊天接口，自动选择处理方式")
    @PostMapping(value = "/smartChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> smartChat(@RequestBody ChatRequest request) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();

            // 生成会话ID
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.trim().isEmpty()) {
                sessionId = UUID.randomUUID().toString();
                request.setSessionId(sessionId);
            }
            final String finalSessionId = sessionId;

            // 在Redis中缓存用户消息（仅用于本次会话上下文）
            ChatMessage userMessage = new ChatMessage("user", request.getPrompt(), LocalDateTime.now());
            chatHistoryService.saveMessage(finalSessionId, user.getUserId(), user.getUserName(), userMessage);

            // 如果是新会话，保存标题到 Redis，并将sessionId加入ZSET
            String titleKey = RedisKeys.CHAT_TITLE + finalSessionId;
            if (redisCache.getCacheObject(titleKey) == null) {
                String initialTitle = generateSessionTitle(request.getPrompt());
                redisCache.setCacheObject(titleKey, initialTitle, HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);

                // 使用ZSET存储用户会话sessionId，score为当前时间戳，去重且高并发安全
                String userSessionsKey = RedisKeys.CHAT_USER_SESSIONS + user.getUserId();
                double score = System.currentTimeMillis();
                redisCache.zAdd(userSessionsKey, finalSessionId, score, HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);
            }

            RagScene scene = QueryTypeUtils.detect(request.getPrompt());
            log.info("智能聊天场景：{}，查询内容：{}", scene, request.getPrompt());

            // 获取历史消息
            List<ChatMessage> historyMessages = chatHistoryService.getHistoryMessages(finalSessionId);

            final StringBuilder fullResponse = new StringBuilder();
            final boolean[] isFirstResponse = {true};
            final String[] assistantContent = {null};

            return ragExecutor.stream(request.getPrompt(), scene, historyMessages)
                    .map(content -> {
                        fullResponse.append(content);
                        if (isFirstResponse[0]) {
                            assistantContent[0] = content;
                            isFirstResponse[0] = false;
                        } else {
                            assistantContent[0] += content;
                        }
                        return ChatResponse.of(content, finalSessionId);
                    })
                    .concatWith(Flux.defer(() -> {
                        if (assistantContent[0] != null && !assistantContent[0].isEmpty()) {
                            // 在流式响应结束后，将完整的消息通过MQ异步保存
                            ChatMessage assistantMessage = new ChatMessage("assistant", assistantContent[0], LocalDateTime.now());
                            // 只在Redis中缓存AI消息，数据库持久化通过MQ异步完成
                            chatHistoryService.saveMessage(finalSessionId, user.getUserId(), user.getUserName(), assistantMessage);
                        }

                        updateSessionTitleIfNeeded(finalSessionId, request.getPrompt(), fullResponse.toString());

                        return Flux.just(ChatResponse.finish(finalSessionId));
                    }));

        } catch (Exception e) {
            log.error("智能聊天错误", e);
            return Flux.just(ChatResponse.error("智能聊天服务错误: " + e.getMessage(), request.getSessionId()));
        }
    }

    /**
     * 生成会话标题
     */
    private String generateSessionTitle(String userPrompt) {
        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            return "新对话";
        }
        String title = userPrompt.trim();
        if (title.length() > 20) title = title.substring(0, 20);
        return title;
    }

    /**
     * 更新标题（首次AI回复时）
     */
    private void updateSessionTitleIfNeeded(String sessionId, String userPrompt, String aiResponse) {
        String titleKey = RedisKeys.CHAT_TITLE + sessionId;
        String currentTitle = redisCache.getCacheObject(titleKey);
        if (currentTitle == null) return;

        String initialTitle = generateSessionTitle(userPrompt);
        if (currentTitle.equals(initialTitle)) {
            String newTitle = generateImprovedTitle(userPrompt, aiResponse);
            if (!newTitle.equals(currentTitle)) {
                redisCache.setCacheObject(titleKey, newTitle, HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);
            }
        }
    }

    private String generateImprovedTitle(String userPrompt, String aiResponse) {
        String title = userPrompt.trim();
        if (title.length() > 20) title = title.substring(0, 20);
        return title;
    }

    /**
     * 获取用户会话列表
     */
    @Operation(summary = "获取用户会话列表", description = "获取当前用户的会话标题列表")
    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSession>> getUserSessions() {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();

            String userSessionsKey = RedisKeys.CHAT_USER_SESSIONS + user.getUserId();

            // 检查 ZSet 类型
            if (!redisCache.isZSet(userSessionsKey)) {
                redisCache.deleteObject(userSessionsKey);
            }

            // 获取ZSET中的sessionId，按score降序
            List<RedisCache.ZSetTuple<String>> tuples = redisCache.zRevRangeWithScores(userSessionsKey, 0L, -1L);
            if (tuples == null || tuples.isEmpty()) return ResponseEntity.ok(List.of());

            List<ChatSession> chatSessionList = new ArrayList<>();
            for (RedisCache.ZSetTuple<String> tuple : tuples) {
                String sessionId = tuple.getValue();
                String title = redisCache.getCacheObject("chat:title:" + sessionId);
                chatSessionList.add(new ChatSession(sessionId, title, user.getUserId(), user.getUserName()));
            }

            return ResponseEntity.ok(chatSessionList);

        } catch (Exception e) {
            log.error("获取用户会话列表失败", e);
            return ResponseEntity.status(500).body(List.of());
        }
    }

    /**
     * 获取会话历史记录
     */
    @Operation(summary = "获取会话历史记录", description = "获取指定会话的历史消息记录")
    @GetMapping("/session/{sessionId}/history")
    public ResponseEntity<List<ChatMessage>> getSessionHistory(@PathVariable String sessionId) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();

            if (!isSessionBelongsToUser(sessionId, user.getUserId())) {
                return ResponseEntity.status(403).build();
            }

            List<ChatMessage> history = chatHistoryService.getHistoryMessages(sessionId);
            return ResponseEntity.ok(history);

        } catch (Exception e) {
            log.error("获取会话历史记录失败", e);
            return ResponseEntity.status(500).body(List.of());
        }
    }

    /**
     * 删除会话及历史记录
     */
    @Operation(summary = "删除会话", description = "删除指定会话及历史记录")
    @DeleteMapping("/deleteSession/{sessionId}")
    public ResponseEntity<String> deleteSession(@PathVariable String sessionId) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();

            if (!isSessionBelongsToUser(sessionId, user.getUserId())) {
                return ResponseEntity.status(403).body("无权删除此会话");
            }

            // 删除历史记录和标题
            redisCache.deleteObject("chat:history:" + sessionId);
            redisCache.deleteObject("chat:title:" + sessionId);

            // 从ZSET中删除sessionId
            String userSessionsKey = "chat:user_sessions:" + user.getUserId();
            redisCache.zRemove(userSessionsKey, sessionId);

            return ResponseEntity.ok("会话删除成功");
        } catch (Exception e) {
            log.error("删除会话失败", e);
            return ResponseEntity.status(500).body("删除会话失败: " + e.getMessage());
        }
    }

    /**
     * 校验会话是否属于当前用户
     */
    private boolean isSessionBelongsToUser(String sessionId, Long userId) {
        String userSessionsKey = "chat:user_sessions:" + userId;
        // 使用ZSET判断sessionId是否存在
        return redisCache.zScore(userSessionsKey, sessionId) != null;
    }
}
