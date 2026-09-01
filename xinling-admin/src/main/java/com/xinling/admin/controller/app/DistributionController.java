package com.xinling.admin.controller.app;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xinling.app.domain.entity.CommissionRecord;
import com.xinling.app.domain.entity.DistributionSettings;
import com.xinling.app.domain.entity.Distributor;
import com.xinling.app.domain.entity.PayOrder;
import com.xinling.app.domain.entity.WithdrawApply;
import com.xinling.app.service.IDistributionService;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;

/**
 * 分销管理
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/distribution")
@Tag(name = "分销管理", description = "分销管理接口")
public class DistributionController extends BaseController {

    private final IDistributionService distributionService;

    public DistributionController(IDistributionService distributionService) {
        this.distributionService = distributionService;
    }

    /**
     * 查询分销员列表
     */
    @PreAuthorize("@ss.hasPermi('app:distribution:list')")
    @GetMapping("/list")
    public TableDataInfo list(Distributor distributor) {
        startPage();
        List<Distributor> list = distributionService.selectDistributorList(distributor);
        return getDataTable(list);
    }

    /**
     * 获取分销员详情
     */
    @PreAuthorize("@ss.hasPermi('app:distribution:query')")
    @GetMapping("/detail/{id}")
    public AjaxResult getDetail(@PathVariable Long id) {
        return success(distributionService.selectDistributorById(id));
    }

    /**
     * 审核分销员
     */
    @PreAuthorize("@ss.hasPermi('app:distribution:audit')")
    @Log(title = "分销管理", businessType = BusinessType.UPDATE)
    @PostMapping("/audit/{id}")
    public AjaxResult audit(@PathVariable Long id, @RequestBody Distributor distributor) {
        distributionService.auditDistributor(id, distributor.getStatus(), distributor.getAuditRemark(), getUserId());
        return success();
    }

    /**
     * 查询分销订单列表
     */
    @PreAuthorize("@ss.hasPermi('app:distribution:list')")
    @GetMapping("/order/list")
    public TableDataInfo orderList(PayOrder order) {
        startPage();
        List<PayOrder> list = distributionService.selectDistributionOrderList(order);
        return getDataTable(list);
    }

    /**
     * 查询佣金记录列表
     */
    @PreAuthorize("@ss.hasPermi('app:distribution:list')")
    @GetMapping("/commission/list")
    public TableDataInfo commissionList(CommissionRecord record) {
        startPage();
        List<CommissionRecord> list = distributionService.selectCommissionList(record);
        return getDataTable(list);
    }

    /**
     * 查询提现申请列表
     */
    @PreAuthorize("@ss.hasPermi('app:distribution:list')")
    @GetMapping("/withdraw/list")
    public TableDataInfo withdrawList(WithdrawApply withdraw) {
        startPage();
        List<WithdrawApply> list = distributionService.selectWithdrawList(withdraw);
        return getDataTable(list);
    }

    /**
     * 审核提现
     */
    @PreAuthorize("@ss.hasPermi('app:distribution:audit')")
    @Log(title = "分销管理", businessType = BusinessType.UPDATE)
    @PostMapping("/withdraw/audit/{id}")
    public AjaxResult auditWithdraw(@PathVariable Long id, @RequestBody WithdrawApply withdraw) {
        distributionService.auditWithdraw(id, withdraw.getStatus(), withdraw.getAuditRemark(), getUserId());
        return success();
    }

    /**
     * 更新分销设置3
     */
    @PreAuthorize("@ss.hasPermi('app:distribution:edit')")
    @Log(title = "分销管理", businessType = BusinessType.UPDATE)
    @PostMapping("/settings/update")
    public AjaxResult updateSettings(@RequestBody DistributionSettings settings) {
        distributionService.updateSettings(settings);
        return success();
    }
}
