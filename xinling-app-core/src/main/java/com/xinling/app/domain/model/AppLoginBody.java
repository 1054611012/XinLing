package com.xinling.app.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 手机号验证码登录请求
 */
@Data
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

}
