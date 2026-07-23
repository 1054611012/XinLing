package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员赠送记录实体
 */
public class VipGiftRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long ruleId;
    private Long userId;
    private String userNickname;
    private String grantType;
    private Integer vipDays;
    private String reason;
    private Long operatorId;
    private String operatorName;
    private Date expireTime;
    private Integer status;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
    public String getGrantType() { return grantType; }
    public void setGrantType(String grantType) { this.grantType = grantType; }
    public Integer getVipDays() { return vipDays; }
    public void setVipDays(Integer vipDays) { this.vipDays = vipDays; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
