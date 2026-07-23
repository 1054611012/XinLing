package com.xinling.app.mapper;

import com.xinling.app.domain.entity.UserSettings;
import org.apache.ibatis.annotations.Param;

/**
 * 用户设置 Mapper
 */
public interface UserSettingsMapper {

    UserSettings selectByUserId(@Param("userId") Long userId);

    int insert(UserSettings settings);

    int updateByUserId(UserSettings settings);
}