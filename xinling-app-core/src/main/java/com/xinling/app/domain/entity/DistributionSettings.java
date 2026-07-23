package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销设置实体
 */
public class DistributionSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private BigDecimal firstLevelRate;
    private BigDecimal secondLevelRate;
    private BigDecimal teamRewardRate;
    private BigDecimal minWithdrawAmount;
    private BigDecimal withdrawFeeRate;
    private BigDecimal minWithdrawFee;
    private Integer settleDays;
    private Integer autoAudit;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getFirstLevelRate() { return firstLevelRate; }
    public void setFirstLevelRate(BigDecimal firstLevelRate) { this.firstLevelRate = firstLevelRate; }

    public BigDecimal getSecondLevelRate() { return secondLevelRate; }
    public void setSecondLevelRate(BigDecimal secondLevelRate) { this.secondLevelRate = secondLevelRate; }

    public BigDecimal getTeamRewardRate() { return teamRewardRate; }
    public void setTeamRewardRate(BigDecimal teamRewardRate) { this.teamRewardRate = teamRewardRate; }

    public BigDecimal getMinWithdrawAmount() { return minWithdrawAmount; }
    public void setMinWithdrawAmount(BigDecimal minWithdrawAmount) { this.minWithdrawAmount = minWithdrawAmount; }

    public BigDecimal getWithdrawFeeRate() { return withdrawFeeRate; }
    public void setWithdrawFeeRate(BigDecimal withdrawFeeRate) { this.withdrawFeeRate = withdrawFeeRate; }

    public BigDecimal getMinWithdrawFee() { return minWithdrawFee; }
    public void setMinWithdrawFee(BigDecimal minWithdrawFee) { this.minWithdrawFee = minWithdrawFee; }

    public Integer getSettleDays() { return settleDays; }
    public void setSettleDays(Integer settleDays) { this.settleDays = settleDays; }

    public Integer getAutoAudit() { return autoAudit; }
    public void setAutoAudit(Integer autoAudit) { this.autoAudit = autoAudit; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
