package com.xinling.app.controller;

import com.xinling.app.domain.entity.Notification;
import com.xinling.app.service.INotificationService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/notification")
public class AppNotificationController {

    private final INotificationService notificationService;

    public AppNotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 通知列表
     */
    @GetMapping("/list")
    public R<Map<String, Object>> list() {
        Long userId = AppContextUtil.getUserId();
        List<Notification> list = notificationService.listNotifications(userId);
        int unreadCount = notificationService.countUnread(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("unreadCount", unreadCount);
        return R.ok(result);
    }

    /**
     * 标记已读
     */
    @PostMapping("/markRead/{id}")
    public R<?> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return R.ok();
    }

    /**
     * 标记已读（兼容旧路径）
     */
    @PutMapping("/read/{notificationId}")
    public R<?> markReadLegacy(@PathVariable Long notificationId) {
        notificationService.markRead(notificationId);
        return R.ok();
    }

    /**
     * 全部标记已读
     */
    @PostMapping("/markAllRead")
    public R<?> markAllRead() {
        Long userId = AppContextUtil.getUserId();
        notificationService.markAllRead(userId);
        return R.ok();
    }

    /**
     * 删除通知
     */
    @PostMapping("/delete/{id}")
    public R<?> delete(@PathVariable Long id) {
        Long userId = AppContextUtil.getUserId();
        notificationService.deleteNotification(userId, id);
        return R.ok();
    }

    /**
     * 未读数量
     */
    @GetMapping("/unreadCount")
    public R<?> unreadCount() {
        Long userId = AppContextUtil.getUserId();
        int count = notificationService.countUnread(userId);
        return R.ok(count);
    }
}
