package com.xinling.app.domain.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 佣金概览VO
 */
@Data
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

}
