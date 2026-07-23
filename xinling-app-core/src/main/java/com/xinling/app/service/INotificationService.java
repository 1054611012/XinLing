package com.xinling.app.service;

import com.xinling.app.domain.entity.Notification;

import java.util.List;

/**
 * 通知消息服务
 */
public interface INotificationService {

    /**
     * 获取用户通知列表
     */
    List<Notification> listNotifications(Long userId);

    /**
     * 标记为已读
     */
    void markRead(Long notificationId);

    /**
     * 全部标记为已读
     */
    void markAllRead(Long userId);

    /**
     * 删除通知
     */
    void deleteNotification(Long userId, Long id);

    /**
     * 获取未读通知数量
     */
    int countUnread(Long userId);
}
