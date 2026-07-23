package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.UserDevice;
import com.xinling.app.mapper.UserDeviceMapper;
import com.xinling.app.service.IUserDeviceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户设备服务实现
 */
@Service
public class UserDeviceServiceImpl implements IUserDeviceService {

    private final UserDeviceMapper userDeviceMapper;

    public UserDeviceServiceImpl(UserDeviceMapper userDeviceMapper) {
        this.userDeviceMapper = userDeviceMapper;
    }

    @Override
    public List<UserDevice> getUserDevices(Long userId) {
        return userDeviceMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void recordDevice(Long userId, String deviceId, String deviceName,
                             String deviceType, String ip) {
        if (deviceId == null || deviceId.isEmpty()) return;

        UserDevice existing = userDeviceMapper.selectByDeviceId(deviceId);
        Date now = new Date();

        if (existing != null) {
            existing.setLastActiveTime(now);
            existing.setIpAddress(ip);
            userDeviceMapper.updateById(existing);
        } else {
            UserDevice device = new UserDevice();
            device.setUserId(userId);
            device.setDeviceId(deviceId);
            device.setDeviceName(deviceName);
            device.setDeviceType(deviceType);
            device.setLoginTime(now);
            device.setLastActiveTime(now);
            device.setIpAddress(ip);
            userDeviceMapper.insert(device);
        }
    }

    @Override
    @Transactional
    public void logoutDevice(Long userId, Long deviceId) {
        UserDevice device = userDeviceMapper.selectByUserId(userId)
                .stream()
                .filter(d -> d.getId().equals(deviceId))
                .findFirst()
                .orElse(null);
        if (device == null) return;
        userDeviceMapper.deleteById(deviceId);
    }

    @Override
    @Transactional
    public void removeDevice(Long userId, Long deviceId) {
        logoutDevice(userId, deviceId);
    }
}
