package com.xinling.app.mapper;

import com.xinling.app.domain.entity.UserActivity;
import org.apache.ibatis.annotations.Param;

/**
 * 用户活动参与 Mapper
 */
public interface UserActivityMapper {

    UserActivity selectByUserAndActivity(@Param("userId") Long userId,
                                          @Param("activityId") Long activityId);

    int insert(UserActivity userActivity);
}
