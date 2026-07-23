package com.xinling.app.domain.model;

/**
 * 用户信息VO（脱敏返回给客户端）
 */
public class AppUserInfoVO {

    private Long id;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Integer gender;
    private String birthday;
    private Integer vipStatus;
    private Long inviterId;

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

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public Integer getVipStatus() { return vipStatus; }
    public void setVipStatus(Integer vipStatus) { this.vipStatus = vipStatus; }

    public Long getInviterId() { return inviterId; }
    public void setInviterId(Long inviterId) { this.inviterId = inviterId; }
}