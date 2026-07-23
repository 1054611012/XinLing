package com.xinling.app.domain.model;

import jakarta.validation.constraints.NotBlank;

/**
 * 发送验证码请求
 */
public class SendCodeBody {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    /** 场景: login-登录 register-注册 bind-绑定 */
    private String scene;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getScene() { return scene; }
    public void setScene(String scene) { this.scene = scene; }
}
