package com.xinling.app.wechat;

/**
 * 微信用户档案（从微信接口解析并归一化后的结果）
 */
public class WechatUserProfile {

    /** 普通用户标识（同一公众号下唯一；不同公众号下不同） */
    private String openid;

    /** 开放平台下统一用户标识（需公众号/应用绑定到同一微信开放平台才返回） */
    private String unionid;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatar;

    /** 性别 0-未知 1-男 2-女 */
    private Integer gender = 0;

    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }

    public String getUnionid() { return unionid; }
    public void setUnionid(String unionid) { this.unionid = unionid; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
}
