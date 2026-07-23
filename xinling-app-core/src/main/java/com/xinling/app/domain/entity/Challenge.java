package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 挑战活动实体
 */
public class Challenge implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String description;
    private String cover;
    private String type;
    private Integer duration;
    private Integer conditionValue;
    private Integer pointsReward;
    private Integer vipDaysReward;
    private Long badgeId;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getConditionValue() { return conditionValue; }
    public void setConditionValue(Integer conditionValue) { this.conditionValue = conditionValue; }

    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }

    public Integer getVipDaysReward() { return vipDaysReward; }
    public void setVipDaysReward(Integer vipDaysReward) { this.vipDaysReward = vipDaysReward; }

    public Long getBadgeId() { return badgeId; }
    public void setBadgeId(Long badgeId) { this.badgeId = badgeId; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
