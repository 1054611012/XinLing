package com.xinling.app.service;

import com.xinling.app.domain.entity.OrderRefund;
import com.xinling.app.domain.entity.PayOrder;
import com.xinling.app.domain.model.OrderDetailVO;

import java.util.List;
import java.util.Map;

/**
 * 订单服务
 */
public interface IOrderService {

    /**
     * 创建订单
     */
    String createOrder(Long userId, Long packageId);

    /**
     * 创建订单（含优惠券）
     */
    String createOrder(Long userId, Long packageId, Long couponId);

    /**
     * 查询订单列表
     */
    Map<String, Object> listOrders(Long userId, String status, int page, int size);

    /**
     * 查询订单详情
     */
    OrderDetailVO getOrderDetail(String orderNo);

    /**
     * 取消订单
     */
    void cancelOrder(String orderNo);

    /**
     * 申请退款
     */
    void applyRefund(String orderNo, String reason);

    /**
     * 查询退款记录
     */
    List<OrderRefund> getRefundRecords(String orderNo);

    /**
     * 根据退款ID查询退款记录
     */
    OrderRefund getRefundById(Long refundId);

    // ========== 管理后台方法 ==========

    List<PayOrder> selectOrderList(PayOrder payOrder);

    PayOrder selectOrderByOrderNo(String orderNo);

    int auditRefund(String orderNo, Integer auditStatus, String auditRemark, Long auditUserId);
}
