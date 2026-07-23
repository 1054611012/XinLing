package com.xinling.app.controller;

import com.xinling.app.domain.entity.Coupon;
import com.xinling.app.domain.entity.UserCoupon;
import com.xinling.app.service.IPayService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/pay")
public class AppPayController {

    private final IPayService payService;

    public AppPayController(IPayService payService) {
        this.payService = payService;
    }

    /**
     * 创建支付订单
     */
    @PostMapping("/createOrder")
    public R<?> createOrder(@RequestParam Long packageId,
                            @RequestParam(required = false) Long couponId) {
        Long userId = AppContextUtil.getUserId();
        String orderNo = payService.createOrder(userId, packageId, couponId);
        return R.ok(orderNo, "订单创建成功");
    }

    /**
     * 获取支付链接
     */
    @PostMapping("/getPayUrl")
    public R<?> getPayUrl(@RequestParam String orderNo,
                          @RequestParam String payType) {
        String payUrl = payService.getPayUrl(orderNo, payType);
        Map<String, String> result = new HashMap<>();
        result.put("payUrl", payUrl);
        result.put("orderNo", orderNo);
        return R.ok(result);
    }

    /**
     * 查询支付状态
     */
    @PostMapping("/queryOrderStatus/{orderNo}")
    public R<Map<String, Object>> queryOrderStatus(@PathVariable String orderNo) {
        Map<String, Object> status = payService.queryOrderStatus(orderNo);
        return R.ok(status);
    }

    /**
     * 支付宝回调
     */
    @PostMapping("/alipay/callback")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        return payService.handleAlipayCallback(params);
    }

    /**
     * 微信支付回调
     */
    @PostMapping("/wechat/callback")
    public String wechatCallback(@RequestParam Map<String, String> params) {
        return payService.handleWechatCallback(params);
    }

    /**
     * 苹果支付回调
     */
    @PostMapping("/apple/callback")
    public String appleCallback(@RequestParam Map<String, String> params) {
        return payService.handleAppleCallback(params);
    }

    /**
     * 获取可用优惠券列表
     */
    @GetMapping({"/coupon/list", "/couponList"})
    public R<List<Coupon>> couponList() {
        List<Coupon> list = payService.getCouponList();
        return R.ok(list);
    }

    /**
     * 领取优惠券
     */
    @PostMapping({"/coupon/receive/{couponId}", "/receiveCoupon/{couponId}"})
    public R<UserCoupon> receiveCoupon(@PathVariable Long couponId) {
        Long userId = AppContextUtil.getUserId();
        UserCoupon userCoupon = payService.receiveCoupon(userId, couponId);
        return R.ok(userCoupon);
    }
}
