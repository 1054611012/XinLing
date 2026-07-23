package com.xinling.app.domain.model;

import java.math.BigDecimal;

/**
 * 佣金概览VO
 */
public class CommissionOverviewVO {

    private BigDecimal totalCommission;
    private BigDecimal availableCommission;
    private BigDecimal frozenCommission;
    private BigDecimal totalWithdraw;
    private Integer totalOrders;
    private Integer totalFans;
    private Integer todayOrders;
    private BigDecimal todayCommission;
    private Integer pendingSettleCount;
    private BigDecimal pendingSettleAmount;

    public BigDecimal getTotalCommission() { return totalCommission; }
    public void setTotalCommission(BigDecimal totalCommission) { this.totalCommission = totalCommission; }

    public BigDecimal getAvailableCommission() { return availableCommission; }
    public void setAvailableCommission(BigDecimal availableCommission) { this.availableCommission = availableCommission; }

    public BigDecimal getFrozenCommission() { return frozenCommission; }
    public void setFrozenCommission(BigDecimal frozenCommission) { this.frozenCommission = frozenCommission; }

    public BigDecimal getTotalWithdraw() { return totalWithdraw; }
    public void setTotalWithdraw(BigDecimal totalWithdraw) { this.totalWithdraw = totalWithdraw; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public Integer getTotalFans() { return totalFans; }
    public void setTotalFans(Integer totalFans) { this.totalFans = totalFans; }

    public Integer getTodayOrders() { return todayOrders; }
    public void setTodayOrders(Integer todayOrders) { this.todayOrders = todayOrders; }

    public BigDecimal getTodayCommission() { return todayCommission; }
    public void setTodayCommission(BigDecimal todayCommission) { this.todayCommission = todayCommission; }

    public Integer getPendingSettleCount() { return pendingSettleCount; }
    public void setPendingSettleCount(Integer pendingSettleCount) { this.pendingSettleCount = pendingSettleCount; }

    public BigDecimal getPendingSettleAmount() { return pendingSettleAmount; }
    public void setPendingSettleAmount(BigDecimal pendingSettleAmount) { this.pendingSettleAmount = pendingSettleAmount; }
}
