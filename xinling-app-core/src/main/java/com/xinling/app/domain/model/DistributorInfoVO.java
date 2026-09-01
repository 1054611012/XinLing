package com.xinling.app.domain.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销员信息VO
 */
@Data
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
}
