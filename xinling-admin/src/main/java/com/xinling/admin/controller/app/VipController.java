package com.xinling.admin.controller.app;

import java.util.List;

import com.xinling.app.domain.model.UserVipVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xinling.app.domain.entity.UserVip;
import com.xinling.app.domain.entity.VipPackage;
import com.xinling.app.service.IVipService;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;

/**
 * 会员管理
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/vip")
@Tag(name = "会员管理", description = "会员管理")
public class VipController extends BaseController {

    private final IVipService vipService;

    public VipController(IVipService vipService) {
        this.vipService = vipService;
    }

    /**
     * 查询套餐列表
     */
    @PreAuthorize("@ss.hasPermi('app:vip:list')")
    @GetMapping("/package/list")
    public TableDataInfo packageList(VipPackage vipPackage) {
        startPage();
        List<VipPackage> list = vipService.selectPackageList(vipPackage);
        return getDataTable(list);
    }

    /**
     * 新增套餐
     */
    @PreAuthorize("@ss.hasPermi('app:vip:add')")
    @Log(title = "会员套餐管理", businessType = BusinessType.INSERT)
    @PostMapping("/package/create")
    public AjaxResult createPackage(@RequestBody VipPackage vipPackage) {
        return toAjax(vipService.insertPackage(vipPackage));
    }

    /**
     * 修改套餐
     */
    @PreAuthorize("@ss.hasPermi('app:vip:edit')")
    @Log(title = "会员套餐管理", businessType = BusinessType.UPDATE)
    @PostMapping("/package/update")
    public AjaxResult updatePackage(@RequestBody VipPackage vipPackage) {
        return toAjax(vipService.updatePackage(vipPackage));
    }

    /**
     * 套餐上下线
     */
    @PreAuthorize("@ss.hasPermi('app:vip:edit')")
    @Log(title = "会员套餐管理", businessType = BusinessType.UPDATE)
    @PostMapping("/package/status")
    public AjaxResult changePackageStatus(@RequestBody VipPackage vipPackage) {
        return toAjax(vipService.updatePackageStatus(vipPackage.getId(), vipPackage.getStatus()));
    }

    /**
     * 查询用户会员列表
     */
    @PreAuthorize("@ss.hasPermi('app:vip:list')")
    @GetMapping("/user/list")
    public TableDataInfo userVipList(UserVip userVip) {
        startPage();
        List<UserVipVO> list = vipService.selectUserVipList(userVip);
        return getDataTable(list);
    }
}
