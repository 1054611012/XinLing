package com.xinling.app.controller;

import com.xinling.app.domain.entity.AutoRenew;
import com.xinling.app.domain.entity.VipPackage;
import com.xinling.app.domain.entity.UserVip;
import com.xinling.app.service.IAutoRenewService;
import com.xinling.app.service.IVipService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/vip")
public class AppVipController {

    private final IVipService vipService;
    private final IAutoRenewService autoRenewService;

    public AppVipController(IVipService vipService,
                            IAutoRenewService autoRenewService) {
        this.vipService = vipService;
        this.autoRenewService = autoRenewService;
    }

    /**
     * VIP套餐列表
     */
    @GetMapping("/packages")
    public R<List<VipPackage>> packages() {
        List<VipPackage> list = vipService.getPackages();
        return R.ok(list);
    }

    /**
     * 获取用户VIP信息
     */
    @GetMapping("/info")
    public R<UserVip> info() {
        Long userId = AppContextUtil.getUserId();
        UserVip vipInfo = vipService.getUserVipInfo(userId);
        return R.ok(vipInfo);
    }

    /**
     * 取消自动续费
     */
    @PostMapping("/cancelAutoRenew")
    public R<?> cancelAutoRenew() {
        Long userId = AppContextUtil.getUserId();
        vipService.cancelAutoRenew(userId);
        return R.ok();
    }

    /**
     * 获取自动续费状态
     */
    @GetMapping("/autoRenewStatus")
    public R<AutoRenew> autoRenewStatus() {
        Long userId = AppContextUtil.getUserId();
        AutoRenew status = autoRenewService.getAutoRenewStatus(userId);
        return R.ok(status);
    }

    /**
     * 开启自动续费
     */
    @PostMapping("/openAutoRenew")
    public R<?> openAutoRenew(@RequestParam Long packageId,
                              @RequestParam String payType) {
        Long userId = AppContextUtil.getUserId();
        autoRenewService.openAutoRenew(userId, packageId, payType);
        return R.ok();
    }

    /**
     * 关闭自动续费
     */
    @PostMapping("/closeAutoRenew")
    public R<?> closeAutoRenew() {
        Long userId = AppContextUtil.getUserId();
        autoRenewService.closeAutoRenew(userId);
        return R.ok();
    }
}
