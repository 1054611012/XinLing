package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 睡眠记录实体
 */
public class SleepRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Date startTime;
    private Date endTime;
    private Integer duration;
    private Integer sleepScore;
    private Integer deepSleepMinutes;
    private Integer lightSleepMinutes;
    private Integer remSleepMinutes;
    private Integer interruptCount;
    private Integer snoringCount;
    private Long audioMixId;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getSleepScore() { return sleepScore; }
    public void setSleepScore(Integer sleepScore) { this.sleepScore = sleepScore; }

    public Integer getDeepSleepMinutes() { return deepSleepMinutes; }
    public void setDeepSleepMinutes(Integer deepSleepMinutes) { this.deepSleepMinutes = deepSleepMinutes; }

    public Integer getLightSleepMinutes() { return lightSleepMinutes; }
    public void setLightSleepMinutes(Integer lightSleepMinutes) { this.lightSleepMinutes = lightSleepMinutes; }

    public Integer getRemSleepMinutes() { return remSleepMinutes; }
    public void setRemSleepMinutes(Integer remSleepMinutes) { this.remSleepMinutes = remSleepMinutes; }

    public Integer getInterruptCount() { return interruptCount; }
    public void setInterruptCount(Integer interruptCount) { this.interruptCount = interruptCount; }

    public Integer getSnoringCount() { return snoringCount; }
    public void setSnoringCount(Integer snoringCount) { this.snoringCount = snoringCount; }

    public Long getAudioMixId() { return audioMixId; }
    public void setAudioMixId(Long audioMixId) { this.audioMixId = audioMixId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
