package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销员实体
 */
@Data
public class Distributor implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Integer level;
    private String realName;
    private String phone;
    private String alipayAccount;
    private String wechatAccount;
    private BigDecimal totalCommission;
    private BigDecimal availableCommission;
    private BigDecimal frozenCommission;
    private BigDecimal totalWithdraw;
    private Integer totalFans;
    private Integer totalOrders;
    private Integer status;
    private Date applyTime;
    private Date auditTime;
    private String auditRemark;
    private Date createTime;
    private Date updateTime;

}
