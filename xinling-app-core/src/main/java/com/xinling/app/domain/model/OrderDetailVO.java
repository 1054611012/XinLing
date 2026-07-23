package com.xinling.app.domain.model;

import com.xinling.app.domain.entity.OrderRefund;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单详情VO
 */
public class OrderDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;
    private Long packageId;
    private String packageName;
    private BigDecimal amount;
    private BigDecimal payAmount;
    private BigDecimal discountAmount;
    private String payType;
    private Integer orderStatus;
    private Date payTime;
    private Date expireTime;
    private Date createTime;
    private BigDecimal refundAmount;
    private String refundReason;
    private List<OrderRefund> refundRecords;

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Long getPackageId() { return packageId; }
    public void setPackageId(Long packageId) { this.packageId = packageId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getPayAmount() { return payAmount; }
    public void setPayAmount(BigDecimal payAmount) { this.payAmount = payAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }

    public Integer getOrderStatus() { return orderStatus; }
    public void setOrderStatus(Integer orderStatus) { this.orderStatus = orderStatus; }

    public Date getPayTime() { return payTime; }
    public void setPayTime(Date payTime) { this.payTime = payTime; }

    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }

    public List<OrderRefund> getRefundRecords() { return refundRecords; }
    public void setRefundRecords(List<OrderRefund> refundRecords) { this.refundRecords = refundRecords; }
}
