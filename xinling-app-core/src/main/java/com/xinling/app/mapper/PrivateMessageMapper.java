package com.xinling.app.mapper;

import com.xinling.app.domain.entity.PrivateMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 私信 Mapper
 */
public interface PrivateMessageMapper {

    List<PrivateMessage> selectByUserId(@Param("userId") Long userId);

    int insert(PrivateMessage privateMessage);

    int markAsRead(@Param("userId") Long userId, @Param("fromUserId") Long fromUserId);
}
