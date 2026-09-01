package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付订单实体
 */
@Data
public class PayOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String orderNo;
    private Long userId;
    private Long packageId;
    private String packageName;
    private BigDecimal amount;
    private BigDecimal payAmount;
    private BigDecimal discountAmount;
    private Long couponId;
    private String payType;
    private Integer orderStatus;
    private Date payTime;
    private Date expireTime;
    private String transactionId;
    private BigDecimal refundAmount;
    private Date refundTime;
    private String refundReason;
    private Long distributorId;
    private BigDecimal commissionAmount;
    private Long activityId;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

}
