package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销设置实体
 */
@Data
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

}
