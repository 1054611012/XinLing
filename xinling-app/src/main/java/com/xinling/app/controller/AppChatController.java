package com.xinling.app.controller;

import com.xinling.ai.domain.chat.ChatMessageRecord;
import com.xinling.ai.domain.chat.ChatSession;
import com.xinling.ai.domain.config.AiSessionConfig;
import com.xinling.ai.domain.vo.ChatRequest;
import com.xinling.ai.service.ChatHistoryService;
import com.xinling.ai.service.IAiSessionConfigService;
import com.xinling.ai.service.ai.XinLingAssistant;
import com.xinling.ai.service.memory.ChatMemoryProviderService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 移动端聊天控制器
 * 使用 XinLingAssistant（LangChain4j AiServices）统一处理
 *
 * @author SuXia
 */
@RestController
@RequestMapping("/api/app/chat")
public class AppChatController {

    private final XinLingAssistant xinLingAssistant;
    private final ChatHistoryService chatHistoryService;
    private final ChatMemoryProviderService chatMemoryProviderService;
    private final IAiSessionConfigService aiSessionConfigService;

    public AppChatController(XinLingAssistant xinLingAssistant,
                             ChatHistoryService chatHistoryService,
                             ChatMemoryProviderService chatMemoryProviderService,
                             IAiSessionConfigService aiSessionConfigService) {
        this.xinLingAssistant = xinLingAssistant;
        this.chatHistoryService = chatHistoryService;
        this.chatMemoryProviderService = chatMemoryProviderService;
        this.aiSessionConfigService = aiSessionConfigService;
    }

    /**
     * 发送消息（同步）
     * 自动使用移动端会话配置（config_key='mobile'），注入对应提示词
     */
    @PostMapping("/send")
    public R<?> send(@RequestBody ChatRequest request) {
        Long userId = AppContextUtil.getUserId();
        String nickName = AppContextUtil.getNickname();
        boolean isNewSession = false;

        // 如果无sessionId，创建一个新会话并绑定移动端配置
        if (request.getSessionId() == null || request.getSessionId().trim().isEmpty()) {
            String sessionId = UUID.randomUUID().toString();
            request.setSessionId(sessionId);
            isNewSession = true;

            // 查找移动端配置
            AiSessionConfig mobileConfig = aiSessionConfigService.selectByConfigKey("mobile");
            if (mobileConfig != null) {
                // 保存 sessionId → configId 映射到Redis（ChatMemoryProvider注入prompts时使用）
                chatHistoryService.saveSessionConfigMapping(sessionId, mobileConfig.getConfigId());
            }

            // 保存会话信息
            chatHistoryService.saveSession(new ChatSession(sessionId, null, userId, nickName));
        }

        // 保存用户消息到 Redis
        chatHistoryService.saveMessage(request.getSessionId(), userId, nickName,
                new ChatMessageRecord("user", request.getPrompt()));

        // 使用 XinLingAssistant 进行同步聊天（自动注入提示词）
        String response = xinLingAssistant.chat(request.getSessionId(), request.getPrompt());

        // 保存 AI 回复到 Redis
        chatHistoryService.saveMessage(request.getSessionId(), userId, nickName,
                new ChatMessageRecord("assistant", response));

        // 新会话：用第一条用户消息自动生成标题（取前20个字符）
        if (isNewSession) {
            String title = request.getPrompt().length() > 20
                    ? request.getPrompt().substring(0, 20) + "…"
                    : request.getPrompt();
            chatHistoryService.updateSessionTitle(request.getSessionId(), title);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", request.getSessionId());
        result.put("content", response);
        return R.ok(result);
    }

    /**
     * 获取会话历史消息
     */
    @GetMapping("/history")
    public R<List<ChatMessageRecord>> history(@RequestParam String sessionId) {
        List<ChatMessageRecord> messages = chatHistoryService.getHistoryMessages(sessionId);
        return R.ok(messages);
    }

    /**
     * 获取用户的会话列表（分页）
     */
    @GetMapping("/sessions")
    public R<?> sessions(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "20") int size) {
        Long userId = AppContextUtil.getUserId();
        ChatHistoryService.SessionPageResult result = chatHistoryService.getUserSessions(userId, page, size);
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getList());
        data.put("total", result.getTotal());
        return R.ok(data);
    }

    /**
     * 获取会话详情
     */
    @GetMapping("/session/{sessionId}")
    public R<ChatSession> session(@PathVariable String sessionId) {
        ChatSession session = chatHistoryService.getSession(sessionId);
        return R.ok(session);
    }

    /**
     * 更新会话标题
     */
    @PutMapping("/session/{sessionId}/title")
    public R<?> updateTitle(@PathVariable String sessionId, @RequestParam String title) {
        Long userId = AppContextUtil.getUserId();
        boolean belongs = chatHistoryService.isSessionBelongsToUser(sessionId, userId);
        if (!belongs) {
            return R.fail("无权操作该会话");
        }
        chatHistoryService.updateSessionTitle(sessionId, title);
        return R.ok();
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/session/{sessionId}")
    public R<?> deleteSession(@PathVariable String sessionId) {
        Long userId = AppContextUtil.getUserId();
        boolean belongs = chatHistoryService.isSessionBelongsToUser(sessionId, userId);
        if (!belongs) {
            return R.fail("无权操作该会话");
        }
        chatHistoryService.deleteSession(sessionId);
        return R.ok();
    }

    /**
     * 清除会话记忆
     */
    @PostMapping("/session/{sessionId}/clear")
    public R<?> clearSession(@PathVariable String sessionId) {
        chatMemoryProviderService.clearSession(sessionId);
        return R.ok();
    }
}
