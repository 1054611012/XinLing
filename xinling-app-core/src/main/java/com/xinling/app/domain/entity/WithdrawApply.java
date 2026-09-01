package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现申请实体
 */
@Data
public class WithdrawApply implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long distributorId;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal actualAmount;
    private String payType;
    private String account;
    private String realName;
    private Integer status;
    private Long auditUserId;
    private Date auditTime;
    private String auditRemark;
    private Date payTime;
    private String transactionId;
    private Date createTime;
    private Date updateTime;

}
