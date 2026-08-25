package com.xinling.app.wechat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信登录配置
 * 对应 application.yml 中 xinling.wechat.* 节点
 */
@Component
@ConfigurationProperties(prefix = "xinling.wechat")
public class WechatProperties {

    /** 公众号 AppID 或 微信开放平台·网站应用 AppID */
    private String appid = "";

    /** AppSecret */
    private String secret = "";

    /**
     * 授权作用域：
     * snsapi_base   —— 静默授权，仅拿到 openid（无法获取昵称/头像）
     * snsapi_userinfo —— 弹授权页，可拿到昵称/头像/性别
     */
    private String scope = "snsapi_userinfo";

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
