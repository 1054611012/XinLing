package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 佣金记录实体
 */
@Data
public class CommissionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long distributorId;
    private String orderNo;
    private Long userId;
    private Integer type;
    private BigDecimal amount;
    private BigDecimal orderAmount;
    private BigDecimal rate;
    private Integer status;
    private Date settleTime;
    private Date createTime;
    private Date updateTime;

}
