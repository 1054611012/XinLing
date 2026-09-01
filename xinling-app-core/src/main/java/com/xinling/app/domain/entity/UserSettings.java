package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户设置
 */
@Data
public class UserSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Integer defaultFocusTime;
    private Integer defaultBreakTime;
    private Long defaultAudioId;
    private Integer darkMode;
    private Integer notification;
    private Integer volume;
    private String aiVoiceId;
    private Date createTime;
    private Date updateTime;

}