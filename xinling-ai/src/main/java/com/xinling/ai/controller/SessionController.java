package com.xinling.ai.controller;

import com.xinling.ai.domain.chat.ChatMessageRecord;
import com.xinling.ai.service.ChatHistoryService;
import java.util.List;
import com.xinling.ai.service.memory.ChatMemoryProviderService;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.domain.model.LoginUser;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI 会话管理控制器
 * 提供用户会话的查询、历史记录、删除等功能
 *
 * @author SuXia
 */
@Slf4j
@RestController
@RequestMapping("/ai/sessions")
@Tag(name = "会话管理", description = "AI会话管理接口")
public class SessionController {

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private ChatMemoryProviderService chatMemoryProviderService;

    /**
     * 获取用户会话列表（分页）
     */
    @GetMapping
    public AjaxResult getUserSessions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUser().getUserId();

        ChatHistoryService.SessionPageResult result = chatHistoryService.getUserSessions(userId, page, size);
        return AjaxResult.success(new TableDataInfo(result.getList(), result.getTotal()));
    }

    /**
     * 获取会话历史消息
     */
    @GetMapping("/{sessionId}/history")
    @Operation(summary = "获取会话历史消息", description = "获取会话历史消息")
    public AjaxResult getSessionHistory(@PathVariable String sessionId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (!isSessionBelongsToUser(sessionId, loginUser.getUser().getUserId())) {
            return AjaxResult.error("无权访问该会话");
        }

        List<ChatMessageRecord> history = chatHistoryService.getHistoryMessages(sessionId);
        return AjaxResult.success(history);
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/{sessionId}")
    @Operation(summary = "删除会话", description = "删除会话")
    public AjaxResult deleteSession(@PathVariable String sessionId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUser().getUserId();

        if (!isSessionBelongsToUser(sessionId, userId)) {
            return AjaxResult.error("无权删除该会话");
        }

        chatHistoryService.deleteSession(sessionId);
        return AjaxResult.success("会话已删除");
    }

    /**
     * 清除会话记忆
     */
    @DeleteMapping("/{sessionId}/memory")
    public AjaxResult clearSessionMemory(@PathVariable String sessionId) {
        chatMemoryProviderService.clearSession(sessionId);
        return AjaxResult.success("会话记忆已清除");
    }

    /**
     * 校验会话是否属于当前用户
     */
    private boolean isSessionBelongsToUser(String sessionId, Long userId) {
        return chatHistoryService.isSessionBelongsToUser(sessionId, userId);
    }
}
