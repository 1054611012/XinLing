package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * APP用户实体类
 * 用于存储APP用户的基本信息，包括用户身份认证、个人资料、VIP状态等
 */
@Data
public class AppUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像路径
     */
    private String avatar;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 密码哈希值
     */
    private String passwordHash;

    /**
     * 性别（0-未知，1-男，2-女）
     */
    private Integer gender;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * VIP状态（0-非VIP，1-VIP）
     */
    private Integer vipStatus;

    /**
     * VIP到期时间
     */
    private Date vipEndTime;

    /**
     * 邀请人ID
     */
    private Long inviterId;

    /**
     * 微信 OpenID（与 appid 一一对应）
     */
    private String wxOpenid;

    /**
     * 微信 UnionID（同一微信开放平台下统一）
     */
    private String wxUnionid;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 注册IP
     */
    private String registerIp;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Integer isDeleted;

}
