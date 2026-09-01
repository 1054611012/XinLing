package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户优惠券实体
 */
@Data
public class UserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long couponId;
    private String orderNo;
    private Date getTime;
    private Date useTime;
    private Integer status;
    private Date createTime;

}
