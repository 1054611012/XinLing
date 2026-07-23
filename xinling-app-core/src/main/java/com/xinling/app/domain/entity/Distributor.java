package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销员实体
 */
public class Distributor implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Integer level;
    private String realName;
    private String phone;
    private String alipayAccount;
    private String wechatAccount;
    private BigDecimal totalCommission;
    private BigDecimal availableCommission;
    private BigDecimal frozenCommission;
    private BigDecimal totalWithdraw;
    private Integer totalFans;
    private Integer totalOrders;
    private Integer status;
    private Date applyTime;
    private Date auditTime;
    private String auditRemark;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAlipayAccount() { return alipayAccount; }
    public void setAlipayAccount(String alipayAccount) { this.alipayAccount = alipayAccount; }

    public String getWechatAccount() { return wechatAccount; }
    public void setWechatAccount(String wechatAccount) { this.wechatAccount = wechatAccount; }

    public BigDecimal getTotalCommission() { return totalCommission; }
    public void setTotalCommission(BigDecimal totalCommission) { this.totalCommission = totalCommission; }

    public BigDecimal getAvailableCommission() { return availableCommission; }
    public void setAvailableCommission(BigDecimal availableCommission) { this.availableCommission = availableCommission; }

    public BigDecimal getFrozenCommission() { return frozenCommission; }
    public void setFrozenCommission(BigDecimal frozenCommission) { this.frozenCommission = frozenCommission; }

    public BigDecimal getTotalWithdraw() { return totalWithdraw; }
    public void setTotalWithdraw(BigDecimal totalWithdraw) { this.totalWithdraw = totalWithdraw; }

    public Integer getTotalFans() { return totalFans; }
    public void setTotalFans(Integer totalFans) { this.totalFans = totalFans; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getApplyTime() { return applyTime; }
    public void setApplyTime(Date applyTime) { this.applyTime = applyTime; }

    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }

    public String getAuditRemark() { return auditRemark; }
    public void setAuditRemark(String auditRemark) { this.auditRemark = auditRemark; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
