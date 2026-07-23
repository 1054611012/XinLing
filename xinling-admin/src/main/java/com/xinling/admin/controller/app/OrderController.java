package com.xinling.admin.controller.app;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xinling.app.domain.entity.OrderRefund;
import com.xinling.app.domain.entity.PayOrder;
import com.xinling.app.service.IOrderService;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import com.xinling.common.utils.poi.ExcelUtil;

/**
 * 订单管理
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/order")
@Tag(name = "订单管理", description = "订单管理")
public class OrderController extends BaseController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 查询订单列表
     */
    @PreAuthorize("@ss.hasPermi('app:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(PayOrder order) {
        startPage();
        List<PayOrder> list = orderService.selectOrderList(order);
        return getDataTable(list);
    }

    /**
     * 获取订单详情
     */
    @PreAuthorize("@ss.hasPermi('app:order:query')")
    @GetMapping("/detail/{orderNo}")
    public AjaxResult getDetail(@PathVariable String orderNo) {
        return success(orderService.selectOrderByOrderNo(orderNo));
    }

    /**
     * 取消订单
     */
    @PreAuthorize("@ss.hasPermi('app:order:edit')")
    @Log(title = "订单管理", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel/{orderNo}")
    public AjaxResult cancel(@PathVariable String orderNo) {
        orderService.cancelOrder(orderNo);
        return success();
    }

    /**
     * 审核退款
     */
    @PreAuthorize("@ss.hasPermi('app:order:auditRefund')")
    @Log(title = "订单管理", businessType = BusinessType.UPDATE)
    @PostMapping("/auditRefund/{orderNo}")
    public AjaxResult auditRefund(@PathVariable String orderNo, @RequestBody OrderRefund refund) {
        refund.setAuditUserId(getUserId());
        orderService.auditRefund(orderNo, refund.getRefundStatus(), refund.getAuditRemark(), getUserId());
        return success();
    }

    /**
     * 导出订单列表
     */
    @PreAuthorize("@ss.hasPermi('app:order:export')")
    @Log(title = "订单管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public AjaxResult export(PayOrder order) {
        List<PayOrder> list = orderService.selectOrderList(order);
        ExcelUtil<PayOrder> util = new ExcelUtil<PayOrder>(PayOrder.class);
        return util.exportExcel(list, "订单数据");
    }
}
