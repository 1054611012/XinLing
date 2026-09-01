package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易记录实体
 */
@Data
public class PayTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String orderNo;
    private String transactionId;
    private String payType;
    private BigDecimal amount;
    private Integer status;
    private Date payTime;
    private String rawData;
    private Date createTime;
    private Date updateTime;

}
