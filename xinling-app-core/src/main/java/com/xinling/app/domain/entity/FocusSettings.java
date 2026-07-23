package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 专注设置实体
 */
public class FocusSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Integer strictMode;
    private Integer appBlock;
    private String allowedApps;
    private Integer notificationBlock;
    private Integer aiEncouragement;
    private Integer encouragementInterval;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getStrictMode() { return strictMode; }
    public void setStrictMode(Integer strictMode) { this.strictMode = strictMode; }

    public Integer getAppBlock() { return appBlock; }
    public void setAppBlock(Integer appBlock) { this.appBlock = appBlock; }

    public String getAllowedApps() { return allowedApps; }
    public void setAllowedApps(String allowedApps) { this.allowedApps = allowedApps; }

    public Integer getNotificationBlock() { return notificationBlock; }
    public void setNotificationBlock(Integer notificationBlock) { this.notificationBlock = notificationBlock; }

    public Integer getAiEncouragement() { return aiEncouragement; }
    public void setAiEncouragement(Integer aiEncouragement) { this.aiEncouragement = aiEncouragement; }

    public Integer getEncouragementInterval() { return encouragementInterval; }
    public void setEncouragementInterval(Integer encouragementInterval) { this.encouragementInterval = encouragementInterval; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
