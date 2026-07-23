package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户成长实体
 */
public class UserGrowth implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Integer level;
    private Integer exp;
    private Integer points;
    private Integer continuousFocusDays;
    private Integer continuousSleepDays;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public Integer getExp() { return exp; }
    public void setExp(Integer exp) { this.exp = exp; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Integer getContinuousFocusDays() { return continuousFocusDays; }
    public void setContinuousFocusDays(Integer continuousFocusDays) { this.continuousFocusDays = continuousFocusDays; }

    public Integer getContinuousSleepDays() { return continuousSleepDays; }
    public void setContinuousSleepDays(Integer continuousSleepDays) { this.continuousSleepDays = continuousSleepDays; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
