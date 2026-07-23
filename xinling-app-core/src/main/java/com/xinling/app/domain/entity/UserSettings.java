package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户设置
 */
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getDefaultFocusTime() { return defaultFocusTime; }
    public void setDefaultFocusTime(Integer defaultFocusTime) { this.defaultFocusTime = defaultFocusTime; }

    public Integer getDefaultBreakTime() { return defaultBreakTime; }
    public void setDefaultBreakTime(Integer defaultBreakTime) { this.defaultBreakTime = defaultBreakTime; }

    public Long getDefaultAudioId() { return defaultAudioId; }
    public void setDefaultAudioId(Long defaultAudioId) { this.defaultAudioId = defaultAudioId; }

    public Integer getDarkMode() { return darkMode; }
    public void setDarkMode(Integer darkMode) { this.darkMode = darkMode; }

    public Integer getNotification() { return notification; }
    public void setNotification(Integer notification) { this.notification = notification; }

    public Integer getVolume() { return volume; }
    public void setVolume(Integer volume) { this.volume = volume; }

    public String getAiVoiceId() { return aiVoiceId; }
    public void setAiVoiceId(String aiVoiceId) { this.aiVoiceId = aiVoiceId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}