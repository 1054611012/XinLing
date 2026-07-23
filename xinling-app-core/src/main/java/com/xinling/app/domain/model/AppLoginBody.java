package com.xinling.app.domain.model;

import jakarta.validation.constraints.NotBlank;

/**
 * 手机号验证码登录请求
 */
public class AppLoginBody {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    private String code;

    /** 设备唯一标识（用于设备管理） */
    private String deviceId;

    /** 设备名称 */
    private String deviceName;

    /** 设备类型 Android/iOS/Web */
    private String deviceType;

    /** 邀请人ID（首次注册时绑定） */
    private Long inviterId;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public Long getInviterId() { return inviterId; }
    public void setInviterId(Long inviterId) { this.inviterId = inviterId; }
}
