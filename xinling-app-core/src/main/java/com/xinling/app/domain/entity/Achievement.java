package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 成就实体类
 * 用于存储用户成就信息，包括成就名称、描述、图标、解锁条件及奖励积分
 */
public class Achievement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 成就名称
     */
    private String name;

    /**
     * 成就描述
     */
    private String description;

    /**
     * 成就图标路径
     */
    private String icon;

    /**
     * 条件类型（如：答题数、分享次数等）
     */
    private String conditionType;

    /**
     * 条件值（达到该值可解锁成就）
     */
    private Integer conditionValue;

    /**
     * 奖励积分
     */
    private Integer pointsReward;

    /**
     * 创建时间
     */
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getConditionType() { return conditionType; }
    public void setConditionType(String conditionType) { this.conditionType = conditionType; }

    public Integer getConditionValue() { return conditionValue; }
    public void setConditionValue(Integer conditionValue) { this.conditionValue = conditionValue; }

    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
