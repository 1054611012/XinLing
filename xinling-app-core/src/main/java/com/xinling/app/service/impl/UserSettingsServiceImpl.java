package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.UserSettings;
import com.xinling.app.mapper.UserSettingsMapper;
import com.xinling.app.service.IUserSettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户设置服务实现
 */
@Service
public class UserSettingsServiceImpl implements IUserSettingsService {

    private final UserSettingsMapper userSettingsMapper;

    public UserSettingsServiceImpl(UserSettingsMapper userSettingsMapper) {
        this.userSettingsMapper = userSettingsMapper;
    }

    @Override
    public UserSettings getOrCreate(Long userId) {
        UserSettings settings = userSettingsMapper.selectByUserId(userId);
        if (settings == null) {
            settings = new UserSettings();
            settings.setUserId(userId);
            settings.setDefaultFocusTime(25);
            settings.setDefaultBreakTime(5);
            settings.setDarkMode(1);
            settings.setNotification(1);
            settings.setVolume(70);
            userSettingsMapper.insert(settings);
        }
        return settings;
    }

    @Override
    @Transactional
    public void update(Long userId, UserSettings settings) {
        settings.setUserId(userId);
        userSettingsMapper.updateByUserId(settings);
    }
}
