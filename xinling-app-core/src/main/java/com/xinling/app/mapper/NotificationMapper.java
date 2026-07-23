package com.xinling.app.mapper;

import com.xinling.app.domain.entity.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知消息 Mapper
 */
public interface NotificationMapper {

    List<Notification> selectByUserId(@Param("userId") Long userId);

    Notification selectById(@Param("id") Long id);

    int insert(Notification notification);

    int markRead(@Param("id") Long id);

    int markAllReadByUserId(@Param("userId") Long userId);

    int deleteById(@Param("id") Long id);

    int countUnread(@Param("userId") Long userId);
}
