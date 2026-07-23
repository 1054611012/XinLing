package com.xinling.common.core.domain.model;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * APP用户管理 - 后台展示VO
 * <p>
 * 脱敏处理敏感字段，附带统计数据，专用于管理后台展示。
 * 位于 common 模块以便 xinling-app 和 xinling-admin 共享。
 *
 * @author xinling
 */
public class AppUserAdminVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @Excel(name = "用户编号")
    private Long id;

    /** 昵称 */
    @Excel(name = "昵称")
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phone;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 性别(0未知,1男,2女) */
    @Excel(name = "性别", readConverterExp = "0=未知,1=男,2=女")
    private Integer gender;

    /** 状态(0正常,1禁用,2冻结) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=禁用,2=冻结")
    private Integer status;

    /** VIP状态(0普通,1VIP,2终身VIP) */
    @Excel(name = "VIP等级", readConverterExp = "0=普通,1=VIP,2=终身VIP")
    private Integer vipStatus;

    /** VIP到期时间 */
    @Excel(name = "VIP到期时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date vipEndTime;

    /** 邀请人ID */
    private Long inviterId;

    /** 邀请人昵称 */
    @Excel(name = "邀请人")
    private String inviterNickname;

    /** 最后登录IP */
    private String lastLoginIp;

    /** 最后登录时间 */
    @Excel(name = "最后登录", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    /** 注册IP */
    private String registerIp;

    /** 注册时间 */
    @Excel(name = "注册时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 是否删除(0正常,1删除) */
    private Integer isDeleted;

    // ========== 统计数据 ==========

    /** 动态数 */
    @Excel(name = "动态数")
    private Integer momentCount;

    /** 评论数 */
    @Excel(name = "评论数")
    private Integer commentCount;

    // ========== getters / setters ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getVipStatus() { return vipStatus; }
    public void setVipStatus(Integer vipStatus) { this.vipStatus = vipStatus; }

    public Date getVipEndTime() { return vipEndTime; }
    public void setVipEndTime(Date vipEndTime) { this.vipEndTime = vipEndTime; }

    public Long getInviterId() { return inviterId; }
    public void setInviterId(Long inviterId) { this.inviterId = inviterId; }

    public String getInviterNickname() { return inviterNickname; }
    public void setInviterNickname(String inviterNickname) { this.inviterNickname = inviterNickname; }

    public String getLastLoginIp() { return lastLoginIp; }
    public void setLastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; }

    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }

    public String getRegisterIp() { return registerIp; }
    public void setRegisterIp(String registerIp) { this.registerIp = registerIp; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public Integer getMomentCount() { return momentCount; }
    public void setMomentCount(Integer momentCount) { this.momentCount = momentCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    @Override
    public String toString() {
        return "AppUserAdminVO{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                ", vipStatus=" + vipStatus +
                ", createTime=" + createTime +
                '}';
    }
}