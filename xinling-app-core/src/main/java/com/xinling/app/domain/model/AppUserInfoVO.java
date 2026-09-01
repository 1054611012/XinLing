package com.xinling.app.domain.model;

import lombok.Data;

/**
 * 用户信息VO（脱敏返回给客户端）
 */
@Data
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

}