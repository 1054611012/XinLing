package com.xinling.app.mapper;

import com.xinling.app.domain.entity.UserAchievement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户成就 Mapper
 */
public interface UserAchievementMapper {

    List<UserAchievement> selectByUserId(@Param("userId") Long userId);

    UserAchievement selectByUserAndAchievement(@Param("userId") Long userId, @Param("achievementId") Long achievementId);

    int insert(UserAchievement userAchievement);

    int deleteByUserIdAndAchievement(@Param("userId") Long userId, @Param("achievementId") Long achievementId);
}
