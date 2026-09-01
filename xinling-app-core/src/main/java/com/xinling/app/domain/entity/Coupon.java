package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券实体
 */
@Data
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String type;
    private BigDecimal value;
    private BigDecimal conditionAmount;
    private Date startTime;
    private Date endTime;
    private Integer totalCount;
    private Integer usedCount;
    private Integer status;
    private Date createTime;
    private Date updateTime;

}
