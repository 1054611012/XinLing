package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 专注记录实体
 */
public class FocusRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Date startTime;
    private Date endTime;
    private Integer duration;
    private Integer status;
    private String mode;
    private String tag;
    private Integer interruptCount;
    private String note;
    private Long audioMixId;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

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

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public Integer getInterruptCount() { return interruptCount; }
    public void setInterruptCount(Integer interruptCount) { this.interruptCount = interruptCount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Long getAudioMixId() { return audioMixId; }
    public void setAudioMixId(Long audioMixId) { this.audioMixId = audioMixId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
