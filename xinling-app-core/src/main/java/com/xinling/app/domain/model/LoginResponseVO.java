package com.xinling.app.domain.model;

/**
 * 登录响应
 */
public class LoginResponseVO {

    private String token;
    private AppUserInfoVO userInfo;

    public LoginResponseVO() {}

    public LoginResponseVO(String token, AppUserInfoVO userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public AppUserInfoVO getUserInfo() { return userInfo; }
    public void setUserInfo(AppUserInfoVO userInfo) { this.userInfo = userInfo; }
}