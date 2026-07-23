package com.xinling.app.controller;

import com.xinling.app.domain.entity.OrderRefund;
import com.xinling.app.domain.model.OrderDetailVO;
import com.xinling.app.service.IOrderService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/order")
public class AppOrderController {

    private final IOrderService orderService;

    public AppOrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public R<?> create(@RequestParam Long packageId,
                       @RequestParam(required = false) Long couponId) {
        Long userId = AppContextUtil.getUserId();
        String orderNo;
        if (couponId != null) {
            orderNo = orderService.createOrder(userId, packageId, couponId);
        } else {
            orderNo = orderService.createOrder(userId, packageId);
        }
        return R.ok(orderNo, "创建成功");
    }

    /**
     * 订单列表
     */
    @GetMapping("/list")
    public R<Map<String, Object>> list(@RequestParam(defaultValue = "all") String status,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        Long userId = AppContextUtil.getUserId();
        Map<String, Object> result = orderService.listOrders(userId, status, page, size);
        return R.ok(result);
    }

    /**
     * 订单详情
     */
    @GetMapping("/detail/{orderNo}")
    public R<OrderDetailVO> detail(@PathVariable String orderNo) {
        OrderDetailVO detail = orderService.getOrderDetail(orderNo);
        return R.ok(detail);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public R<?> cancel(@PathVariable String orderNo) {
        orderService.cancelOrder(orderNo);
        return R.ok();
    }

    /**
     * 申请退款
     */
    @PostMapping("/applyRefund/{orderNo}")
    public R<?> applyRefund(@PathVariable String orderNo,
                            @RequestParam String reason) {
        orderService.applyRefund(orderNo, reason);
        return R.ok("退款申请已提交");
    }

    /**
     * 退款记录
     */
    @GetMapping("/refundRecords/{orderNo}")
    public R<List<OrderRefund>> refundRecords(@PathVariable String orderNo) {
        List<OrderRefund> records = orderService.getRefundRecords(orderNo);
        return R.ok(records);
    }
}
