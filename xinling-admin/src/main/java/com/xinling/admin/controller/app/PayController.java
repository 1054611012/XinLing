package com.xinling.admin.controller.app;

import com.xinling.app.domain.entity.OrderRefund;
import com.xinling.app.domain.entity.PayConfig;
import com.xinling.app.domain.entity.PayOrder;
import com.xinling.app.domain.entity.PayTransaction;
import com.xinling.app.mapper.OrderRefundMapper;
import com.xinling.app.mapper.PayConfigMapper;
import com.xinling.app.mapper.PayOrderMapper;
import com.xinling.app.mapper.PayTransactionMapper;
import com.xinling.app.service.IPayService;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 支付管理控制器
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/pay")
@Tag(name = "支付管理", description = "支付管理")
public class PayController extends BaseController {

    private final IPayService payService;
    private final PayConfigMapper payConfigMapper;
    private final PayTransactionMapper payTransactionMapper;
    private final PayOrderMapper payOrderMapper;
    private final OrderRefundMapper orderRefundMapper;

    public PayController(IPayService payService,
                               PayConfigMapper payConfigMapper,
                               PayTransactionMapper payTransactionMapper,
                               PayOrderMapper payOrderMapper,
                               OrderRefundMapper orderRefundMapper) {
        this.payService = payService;
        this.payConfigMapper = payConfigMapper;
        this.payTransactionMapper = payTransactionMapper;
        this.payOrderMapper = payOrderMapper;
        this.orderRefundMapper = orderRefundMapper;
    }

    /**
     * 查询支付配置列表
     */
    @PreAuthorize("@ss.hasPermi('app:pay:config:list')")
    @GetMapping("/config")
    public TableDataInfo configList() {
        startPage();
        List<PayConfig> list = payConfigMapper.selectList();
        return getDataTable(list);
    }

    /**
     * 更新支付配置
     */
    @PreAuthorize("@ss.hasPermi('app:pay:config:update')")
    @Log(title = "支付管理", businessType = BusinessType.UPDATE)
    @PostMapping("/config/update")
    public AjaxResult updateConfig(@RequestBody PayConfig payConfig) {
        payConfig.setUpdateTime(new Date());
        return toAjax(payConfigMapper.updateById(payConfig));
    }

    /**
     * 查询交易记录列表
     */
    @PreAuthorize("@ss.hasPermi('app:pay:transaction:list')")
    @GetMapping("/transaction/list")
    public TableDataInfo transactionList(PayTransaction payTransaction) {
        startPage();
        List<PayTransaction> list = payTransactionMapper.selectList(null, payTransaction.getPayType(), null, null);
        return getDataTable(list);
    }

    /**
     * 审核退款
     */
    @PreAuthorize("@ss.hasPermi('app:pay:refund:audit')")
    @Log(title = "支付管理", businessType = BusinessType.UPDATE)
    @PostMapping("/refund/audit")
    public AjaxResult auditRefund(@RequestBody OrderRefund orderRefund) {
        OrderRefund refund = orderRefundMapper.selectById(orderRefund.getId());
        if (refund == null) {
            return error("退款记录不存在");
        }
        refund.setRefundStatus(orderRefund.getRefundStatus());
        refund.setAuditRemark(orderRefund.getAuditRemark());
        refund.setAuditUserId(getUserId());
        refund.setAuditTime(new Date());
        return toAjax(orderRefundMapper.updateById(refund));
    }
}
