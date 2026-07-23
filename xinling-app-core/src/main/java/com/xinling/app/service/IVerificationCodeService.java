package com.xinling.app.service;

/**
 * 验证码服务
 */
public interface IVerificationCodeService {

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @param scene 场景 login/register/bind
     */
    void sendCode(String phone, String scene);

    /**
     * 校验验证码
     * @param phone 手机号
     * @param code 验证码
     * @return true-验证通过
     */
    boolean verifyCode(String phone, String code);

    /**
     * 删除验证码（验证通过后清除）
     */
    void deleteCode(String phone);
}
