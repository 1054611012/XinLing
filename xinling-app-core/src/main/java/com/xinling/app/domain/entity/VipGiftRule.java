package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员赠送规则实体
 */
public class VipGiftRule implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String ruleName;
    private String ruleType;
    private String conditionValue;
    private Integer vipDays;
    private Integer autoGrant;
    private Integer totalLimit;
    private Integer grantedCount;
    private Integer dailyLimit;
    private Integer status;
    private String description;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }
    public String getConditionValue() { return conditionValue; }
    public void setConditionValue(String conditionValue) { this.conditionValue = conditionValue; }
    public Integer getVipDays() { return vipDays; }
    public void setVipDays(Integer vipDays) { this.vipDays = vipDays; }
    public Integer getAutoGrant() { return autoGrant; }
    public void setAutoGrant(Integer autoGrant) { this.autoGrant = autoGrant; }
    public Integer getTotalLimit() { return totalLimit; }
    public void setTotalLimit(Integer totalLimit) { this.totalLimit = totalLimit; }
    public Integer getGrantedCount() { return grantedCount; }
    public void setGrantedCount(Integer grantedCount) { this.grantedCount = grantedCount; }
    public Integer getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(Integer dailyLimit) { this.dailyLimit = dailyLimit; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
