package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 退款记录实体
 */
@Data
public class OrderRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal refundAmount;
    private String refundReason;
    private Integer refundStatus;
    private Long auditUserId;
    private Date auditTime;
    private String auditRemark;
    private String transactionId;
    private Date createTime;
    private Date updateTime;

}
