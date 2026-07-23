package com.xinling.app.domain.model;

import jakarta.validation.constraints.NotBlank;

/**
 * 第三方登录请求
 */
public class ThirdLoginBody {

    @NotBlank(message = "第三方平台不能为空")
    private String platform; // wechat / qq / apple

    @NotBlank(message = "授权码不能为空")
    private String code;

    private String deviceId;
    private String deviceName;
    private String deviceType;
    private Long inviterId;

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

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