package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.Notification;
import com.xinling.app.mapper.NotificationMapper;
import com.xinling.app.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通知消息服务实现
 */
@Service
public class NotificationServiceImpl implements INotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Override
    public List<Notification> listNotifications(Long userId) {
        return notificationMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void markRead(Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new RuntimeException("通知不存在");
        }
        notificationMapper.markRead(notificationId);
    }

    @Override
    @Transactional
    public void markAllRead(Long userId) {
        notificationMapper.markAllReadByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long userId, Long id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new RuntimeException("通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该通知");
        }
        notificationMapper.deleteById(id);
        log.info("用户删除通知: userId={}, notificationId={}", userId, id);
    }

    @Override
    public int countUnread(Long userId) {
        return notificationMapper.countUnread(userId);
    }
}
