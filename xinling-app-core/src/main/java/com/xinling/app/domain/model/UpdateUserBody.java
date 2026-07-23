package com.xinling.app.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新用户信息请求
 */
public class UpdateUserBody {

    @Size(max = 50, message = "昵称不能超过50个字符")
    private String nickname;

    private Integer gender;

    private String birthday;

    @Size(max = 200, message = "简介不能超过200个字符")
    private String intro;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }
}