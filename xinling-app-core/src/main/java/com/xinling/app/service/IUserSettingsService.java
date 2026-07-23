package com.xinling.app.service;

import com.xinling.app.domain.entity.UserSettings;

/**
 * 用户设置服务
 */
public interface IUserSettingsService {

    /**
     * 获取用户设置（没有则创建默认）
     */
    UserSettings getOrCreate(Long userId);

    /**
     * 更新用户设置
     */
    void update(Long userId, UserSettings settings);
}
