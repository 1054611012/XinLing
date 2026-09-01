package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录设备
 */
@Data
public class UserDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private Date loginTime;
    private Date lastActiveTime;
    private String ipAddress;
    private Date createTime;

}