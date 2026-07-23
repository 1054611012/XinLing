package com.xinling.app.mapper;

import com.xinling.app.domain.entity.UserDevice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 登录设备 Mapper
 */
public interface UserDeviceMapper {

    List<UserDevice> selectByUserId(@Param("userId") Long userId);

    UserDevice selectByDeviceId(@Param("deviceId") String deviceId);

    int insert(UserDevice device);

    int updateById(UserDevice device);

    int deleteById(@Param("id") Long id);

    int deleteByUserId(@Param("userId") Long userId);

    int deleteByDeviceId(@Param("deviceId") String deviceId);
}