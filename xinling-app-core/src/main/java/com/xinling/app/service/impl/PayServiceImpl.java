package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.*;
import com.xinling.app.mapper.*;
import com.xinling.app.service.IPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 支付服务实现
 */
@Service
public class PayServiceImpl implements IPayService {

    private static final Logger log = LoggerFactory.getLogger(PayServiceImpl.class);

    private final PayOrderMapper payOrderMapper;
    private final PayConfigMapper payConfigMapper;
    private final PayTransactionMapper payTransactionMapper;
    private final VipPackageMapper vipPackageMapper;
    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    public PayServiceImpl(PayOrderMapper payOrderMapper,
                           PayConfigMapper payConfigMapper,
                           PayTransactionMapper payTransactionMapper,
                           VipPackageMapper vipPackageMapper,
                           CouponMapper couponMapper,
                           UserCouponMapper userCouponMapper) {
        this.payOrderMapper = payOrderMapper;
        this.payConfigMapper = payConfigMapper;
        this.payTransactionMapper = payTransactionMapper;
        this.vipPackageMapper = vipPackageMapper;
        this.couponMapper = couponMapper;
        this.userCouponMapper = userCouponMapper;
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
        BigDecimal payAmount = vipPackage.getPrice();
        BigDecimal discountAmount = BigDecimal.ZERO;

        // 处理优惠券
        if (couponId != null && couponId > 0) {
            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon != null && coupon.getStatus() == 1) {
                // 检查用户是否已领取此优惠券且未使用
                UserCoupon userCoupon = userCouponMapper.selectByUserIdAndCouponId(userId, couponId);
                if (userCoupon != null && userCoupon.getStatus() == 0) {
                    // 检查满减条件
                    if (coupon.getConditionAmount() == null
                            || vipPackage.getPrice().compareTo(coupon.getConditionAmount()) >= 0) {
                        discountAmount = coupon.getValue();
                        payAmount = vipPackage.getPrice().subtract(discountAmount);
                        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
                            payAmount = BigDecimal.ZERO;
                        }
                        // 标记优惠券已使用
                        userCouponMapper.markUsed(userCoupon.getId(), orderNo);
                        // 增加优惠券使用计数
                        couponMapper.incrementUsedCount(couponId);
                    }
                }
            }
        }

        PayOrder payOrder = new PayOrder();
        payOrder.setOrderNo(orderNo);
        payOrder.setUserId(userId);
        payOrder.setPackageId(packageId);
        payOrder.setPackageName(vipPackage.getName());
        payOrder.setAmount(vipPackage.getPrice());
        payOrder.setPayAmount(payAmount);
        payOrder.setDiscountAmount(discountAmount);
        payOrder.setCouponId(couponId != null ? couponId : 0L);
        payOrder.setOrderStatus(0);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        payOrder.setExpireTime(calendar.getTime());

        payOrderMapper.insert(payOrder);
        log.info("创建支付订单成功，orderNo: {}, userId: {}, amount: {}", orderNo, userId, payAmount);

        return orderNo;
    }

    @Override
    public String getPayUrl(String orderNo, String payType) {
        PayOrder order = payOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != 0) {
            throw new RuntimeException("订单状态异常");
        }

        // TODO: 调用第三方支付SDK获取支付链接
        // 根据payType选择支付渠道（alipay/wechat/apple）
        // 构建支付参数并返回支付URL
        log.info("获取支付链接，orderNo: {}, payType: {}", orderNo, payType);

        // 模拟返回支付链接
        return "https://pay.example.com/pay?orderNo=" + orderNo + "&type=" + payType;
    }

    @Override
    public Map<String, Object> queryOrderStatus(String orderNo) {
        PayOrder order = payOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("orderStatus", order.getOrderStatus());
        result.put("payAmount", order.getPayAmount());
        result.put("payType", order.getPayType());
        result.put("payTime", order.getPayTime());
        return result;
    }

    @Override
    @Transactional
    public String handleAlipayCallback(Map<String, String> params) {
        // TODO: 验证支付宝回调签名
        // 1. 验证签名
        // 2. 验证订单号是否存在
        // 3. 验证金额是否一致
        // 4. 更新订单状态
        // 5. 记录交易记录
        log.info("收到支付宝回调，params: {}", params);
        return "success";
    }

    @Override
    @Transactional
    public String handleWechatCallback(Map<String, String> params) {
        // TODO: 验证微信回调签名
        log.info("收到微信回调，params: {}", params);
        return "success";
    }

    @Override
    @Transactional
    public String handleAppleCallback(Map<String, String> params) {
        // TODO: 验证苹果内购收据
        log.info("收到苹果回调，params: {}", params);
        return "success";
    }

    @Override
    public List<Coupon> getCouponList() {
        return couponMapper.selectUsableList();
    }

    @Override
    @Transactional
    public UserCoupon receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        if (coupon.getStatus() != 1) {
            throw new RuntimeException("优惠券已下架");
        }
        if (coupon.getTotalCount() > 0 && coupon.getUsedCount() >= coupon.getTotalCount()) {
            throw new RuntimeException("优惠券已领完");
        }

        // 检查是否已领取
        int count = userCouponMapper.countByUserIdAndCouponId(userId, couponId);
        if (count > 0) {
            throw new RuntimeException("您已领取过该优惠券");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setGetTime(new Date());
        userCoupon.setStatus(0); // 未使用
        userCouponMapper.insert(userCoupon);

        log.info("用户领取优惠券成功，userId: {}, couponId: {}", userId, couponId);
        return userCoupon;
    }
}
