package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员套餐实体
 */
@Data
public class VipPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer days;
    private String type;
    private Integer status;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;

}
