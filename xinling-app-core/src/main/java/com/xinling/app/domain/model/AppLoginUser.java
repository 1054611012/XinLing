package com.xinling.app.domain.model;

import java.io.Serializable;
import java.util.Date;

/**
 * APP登录用户信息
 * 用于存储用户登录后的会话信息，存入Redis进行会话管理
 */
public class AppLoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * Token标识（UUID格式）
     */
    private String token;

    /**
     * 登录时间戳（毫秒）
     */
    private Long loginTime;

    /**
     * 过期时间戳（毫秒）
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getLoginTime() { return loginTime; }
    public void setLoginTime(Long loginTime) { this.loginTime = loginTime; }

    public Long getExpireTime() { return expireTime; }
    public void setExpireTime(Long expireTime) { this.expireTime = expireTime; }

    public String getIpaddr() { return ipaddr; }
    public void setIpaddr(String ipaddr) { this.ipaddr = ipaddr; }
}
