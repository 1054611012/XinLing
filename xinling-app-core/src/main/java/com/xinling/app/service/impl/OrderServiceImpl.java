package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.OrderRefund;
import com.xinling.app.domain.entity.PayOrder;
import com.xinling.app.domain.entity.VipPackage;
import com.xinling.app.domain.model.OrderDetailVO;
import com.xinling.app.mapper.OrderRefundMapper;
import com.xinling.app.mapper.PayOrderMapper;
import com.xinling.app.mapper.VipPackageMapper;
import com.xinling.app.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 订单服务实现
 */
@Service
public class OrderServiceImpl implements IOrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final PayOrderMapper payOrderMapper;
    private final OrderRefundMapper orderRefundMapper;
    private final VipPackageMapper vipPackageMapper;

    public OrderServiceImpl(PayOrderMapper payOrderMapper,
                             OrderRefundMapper orderRefundMapper,
                             VipPackageMapper vipPackageMapper) {
        this.payOrderMapper = payOrderMapper;
        this.orderRefundMapper = orderRefundMapper;
        this.vipPackageMapper = vipPackageMapper;
    }

    @Override
    @Transactional
    public String createOrder(Long userId, Long packageId) {
        return createOrder(userId, packageId, null);
    }

    @Override
    @Transactional
    public String createOrder(Long userId, Long packageId, Long couponId) {
        VipPackage vipPackage = vipPackageMapper.selectById(packageId);
        if (vipPackage == null) {
            throw new RuntimeException("套餐不存在");
        }
        if (vipPackage.getStatus() != 1) {
            throw new RuntimeException("套餐已下架");
        }

        String orderNo = "ORD" + System.currentTimeMillis();

        PayOrder payOrder = new PayOrder();
        payOrder.setOrderNo(orderNo);
        payOrder.setUserId(userId);
        payOrder.setPackageId(packageId);
        payOrder.setPackageName(vipPackage.getName());
        payOrder.setAmount(vipPackage.getPrice());
        payOrder.setPayAmount(vipPackage.getPrice());
        payOrder.setDiscountAmount(BigDecimal.ZERO);
        payOrder.setCouponId(0L);
        payOrder.setOrderStatus(0);

        // 设置过期时间（30分钟未支付自动过期）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        payOrder.setExpireTime(calendar.getTime());

        payOrderMapper.insert(payOrder);
        log.info("创建订单成功，orderNo: {}, userId: {}, packageId: {}", orderNo, userId, packageId);

        return orderNo;
    }

    @Override
    public Map<String, Object> listOrders(Long userId, String status, int page, int size) {
        Integer statusInt = null;
        if (!"all".equalsIgnoreCase(status)) {
            try {
                statusInt = Integer.parseInt(status);
            } catch (NumberFormatException e) {
                // ignore, treat as all
            }
        }

        int offset = (page - 1) * size;
        List<PayOrder> list = payOrderMapper.selectByUserId(userId, statusInt, offset, size);
        int total = payOrderMapper.countByUserId(userId, statusInt);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    public OrderDetailVO getOrderDetail(String orderNo) {
        PayOrder order = payOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return null;
        }

        List<OrderRefund> refundRecords = orderRefundMapper.selectByOrderNo(orderNo);

        OrderDetailVO vo = new OrderDetailVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setPackageId(order.getPackageId());
        vo.setPackageName(order.getPackageName());
        vo.setAmount(order.getAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setPayType(order.getPayType());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setPayTime(order.getPayTime());
        vo.setExpireTime(order.getExpireTime());
        vo.setCreateTime(order.getCreateTime());
        vo.setRefundAmount(order.getRefundAmount());
        vo.setRefundReason(order.getRefundReason());
        vo.setRefundRecords(refundRecords);
        return vo;
    }

    @Override
    @Transactional
    public void cancelOrder(String orderNo) {
        PayOrder order = payOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != 0) {
            throw new RuntimeException("当前订单状态不允许取消");
        }
        payOrderMapper.updateStatus(orderNo, 2);
        log.info("订单已取消，orderNo: {}", orderNo);
    }

    @Override
    @Transactional
    public void applyRefund(String orderNo, String reason) {
        PayOrder order = payOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != 1) {
            throw new RuntimeException("当前订单状态不允许退款");
        }

        OrderRefund refund = new OrderRefund();
        refund.setOrderNo(orderNo);
        refund.setUserId(order.getUserId());
        refund.setRefundAmount(order.getPayAmount());
        refund.setRefundReason(reason);
        refund.setRefundStatus(0); // 待审核
        orderRefundMapper.insert(refund);

        log.info("申请退款成功，orderNo: {}, refundId: {}", orderNo, refund.getId());

        // 更新订单状态为退款中
        payOrderMapper.updateStatus(orderNo, 3);
    }

    @Override
    public List<OrderRefund> getRefundRecords(String orderNo) {
        return orderRefundMapper.selectByOrderNo(orderNo);
    }

    @Override
    public OrderRefund getRefundById(Long refundId) {
        return orderRefundMapper.selectById(refundId);
    }

    @Override
    public List<PayOrder> selectOrderList(PayOrder payOrder) {
        return payOrderMapper.selectList(
                payOrder.getOrderNo(), payOrder.getUserId(),
                payOrder.getOrderStatus(), null, null);
    }

    @Override
    public PayOrder selectOrderByOrderNo(String orderNo) {
        return payOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public int auditRefund(String orderNo, Integer auditStatus, String auditRemark, Long auditUserId) {
        List<OrderRefund> refunds = orderRefundMapper.selectByOrderNo(orderNo);
        if (refunds.isEmpty()) return 0;
        OrderRefund refund = refunds.get(0);
        refund.setRefundStatus(auditStatus);
        refund.setAuditRemark(auditRemark);
        refund.setAuditUserId(auditUserId);
        refund.setAuditTime(new Date());
        return orderRefundMapper.updateById(refund);
    }
}
