package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户优惠券实体
 */
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Date getGetTime() { return getTime; }
    public void setGetTime(Date getTime) { this.getTime = getTime; }

    public Date getUseTime() { return useTime; }
    public void setUseTime(Date useTime) { this.useTime = useTime; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
