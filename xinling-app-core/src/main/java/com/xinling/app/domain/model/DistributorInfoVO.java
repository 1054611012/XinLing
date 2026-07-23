package com.xinling.app.domain.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销员信息VO
 */
public class DistributorInfoVO {

    private Long id;
    private Long userId;
    private Integer level;
    private String levelName;
    private String realName;
    private String phone;
    private String alipayAccount;
    private String wechatAccount;
    private Integer status;
    private String statusName;
    private Date applyTime;
    private Date auditTime;
    private String auditRemark;

    // 统计信息
    private BigDecimal totalCommission;
    private BigDecimal availableCommission;
    private BigDecimal frozenCommission;
    private BigDecimal totalWithdraw;
    private Integer totalFans;
    private Integer totalOrders;
    private String promotionCode;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAlipayAccount() { return alipayAccount; }
    public void setAlipayAccount(String alipayAccount) { this.alipayAccount = alipayAccount; }

    public String getWechatAccount() { return wechatAccount; }
    public void setWechatAccount(String wechatAccount) { this.wechatAccount = wechatAccount; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public Date getApplyTime() { return applyTime; }
    public void setApplyTime(Date applyTime) { this.applyTime = applyTime; }

    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }

    public String getAuditRemark() { return auditRemark; }
    public void setAuditRemark(String auditRemark) { this.auditRemark = auditRemark; }

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

    public String getPromotionCode() { return promotionCode; }
    public void setPromotionCode(String promotionCode) { this.promotionCode = promotionCode; }
}
