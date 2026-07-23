package com.xinling.app.service;

import com.xinling.app.domain.entity.UserDevice;

import java.util.List;

/**
 * 用户设备服务
 */
public interface IUserDeviceService {

    /**
     * 获取用户的设备列表
     */
    List<UserDevice> getUserDevices(Long userId);

    /**
     * 记录或更新设备登录
     */
    void recordDevice(Long userId, String deviceId, String deviceName,
                      String deviceType, String ip);

    /**
     * 登出指定设备
     */
    void logoutDevice(Long userId, Long deviceId);

    /**
     * 移除用户的某个设备
     */
    void removeDevice(Long userId, Long deviceId);
}
