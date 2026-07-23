package com.xinling.app.service;

import com.xinling.app.domain.entity.Coupon;
import com.xinling.app.domain.entity.UserCoupon;
import com.xinling.app.domain.model.OrderDetailVO;

import java.util.List;
import java.util.Map;

/**
 * 支付服务
 */
public interface IPayService {

    /**
     * 创建支付订单
     */
    String createOrder(Long userId, Long packageId, Long couponId);

    /**
     * 获取支付链接
     */
    String getPayUrl(String orderNo, String payType);

    /**
     * 查询订单支付状态
     */
    Map<String, Object> queryOrderStatus(String orderNo);

    /**
     * 处理支付宝回调
     */
    String handleAlipayCallback(Map<String, String> params);

    /**
     * 处理微信回调
     */
    String handleWechatCallback(Map<String, String> params);

    /**
     * 处理苹果回调
     */
    String handleAppleCallback(Map<String, String> params);

    /**
     * 获取可用优惠券列表
     */
    List<Coupon> getCouponList();

    /**
     * 领取优惠券
     */
    UserCoupon receiveCoupon(Long userId, Long couponId);
}
