package com.xinling.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinling.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 系统活动日志对象 sys_activity_log
 *
 * @author xinling
 * @date 2025-04-10
 */
public class SysActivityLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 活动ID */
    private Long activityId;

    /** 活动类型：user-用户相关 order-订单相关 system-系统相关 message-消息相关 */
    private String activityType;

    /** 活动图标 */
    private String icon;

    /** 活动标题 */
    private String title;

    /** 活动描述 */
    private String description;

    /** 关联业务ID(如用户ID、订单ID等) */
    private Long businessId;

    /** 活动时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date activityTime;

    /** 状态：0-正常 1-已读 2-已归档 */
    private String status;

    public Long getActivityId()
    {
        return activityId;
    }

    public void setActivityId(Long activityId)
    {
        this.activityId = activityId;
    }

    public String getActivityType()
    {
        return activityType;
    }

    public void setActivityType(String activityType)
    {
        this.activityType = activityType;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getBusinessId()
    {
        return businessId;
    }

    public void setBusinessId(Long businessId)
    {
        this.businessId = businessId;
    }

    public Date getActivityTime()
    {
        return activityTime;
    }

    public void setActivityTime(Date activityTime)
    {
        this.activityTime = activityTime;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("activityId", getActivityId())
            .append("activityType", getActivityType())
            .append("icon", getIcon())
            .append("title", getTitle())
            .append("description", getDescription())
            .append("businessId", getBusinessId())
            .append("activityTime", getActivityTime())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
