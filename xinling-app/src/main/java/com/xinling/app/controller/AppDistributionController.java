package com.xinling.app.controller;

import com.xinling.app.domain.model.CommissionOverviewVO;
import com.xinling.app.domain.model.DistributorInfoVO;
import com.xinling.app.domain.model.TeamStatisticsVO;
import com.xinling.app.service.IDistributionService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/distribution")
public class AppDistributionController {

    private final IDistributionService distributionService;

    public AppDistributionController(IDistributionService distributionService) {
        this.distributionService = distributionService;
    }

    /**
     * 申请成为推广员
     */
    @PostMapping("/apply")
    public R<?> apply(@RequestParam String realName,
                      @RequestParam(required = false) String alipayAccount,
                      @RequestParam(required = false) String wechatAccount) {
        Long userId = AppContextUtil.getUserId();
        distributionService.applyDistributor(userId, realName, alipayAccount, wechatAccount);
        return R.ok("申请已提交");
    }

    /**
     * 获取推广员信息
     */
    @GetMapping("/info")
    public R<DistributorInfoVO> info() {
        Long userId = AppContextUtil.getUserId();
        DistributorInfoVO info = distributionService.getDistributorInfo(userId);
        return R.ok(info);
    }

    /**
     * 更新推广员信息
     */
    @PutMapping("/updateInfo")
    public R<?> updateInfo(@RequestParam(required = false) String realName,
                           @RequestParam(required = false) String alipayAccount,
                           @RequestParam(required = false) String wechatAccount) {
        Long userId = AppContextUtil.getUserId();
        distributionService.updateDistributorInfo(userId, realName, alipayAccount, wechatAccount);
        return R.ok();
    }

    /**
     * 生成推广码
     */
    @GetMapping("/promotionCode")
    public R<?> promotionCode() {
        Long userId = AppContextUtil.getUserId();
        String code = distributionService.getPromotionCode(userId);
        return R.ok(code);
    }

    /**
     * 直接团队
     */
    @GetMapping("/teamDirect")
    public R<?> teamDirect() {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> list = distributionService.getTeamDirect(userId);
        return R.ok(list);
    }

    /**
     * 间接团队
     */
    @GetMapping("/teamIndirect")
    public R<?> teamIndirect() {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> list = distributionService.getTeamIndirect(userId);
        return R.ok(list);
    }

    /**
     * 团队统计
     */
    @GetMapping("/teamStatistics")
    public R<TeamStatisticsVO> teamStatistics() {
        Long userId = AppContextUtil.getUserId();
        TeamStatisticsVO stats = distributionService.getTeamStatistics(userId);
        return R.ok(stats);
    }

    /**
     * 佣金概览
     */
    @GetMapping("/commissionOverview")
    public R<CommissionOverviewVO> commissionOverview() {
        Long userId = AppContextUtil.getUserId();
        CommissionOverviewVO overview = distributionService.getCommissionOverview(userId);
        return R.ok(overview);
    }

    /**
     * 佣金明细
     */
    @GetMapping("/commissionDetail")
    public R<Map<String, Object>> commissionDetail(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        Long userId = AppContextUtil.getUserId();
        List<?> list = distributionService.getCommissionDetail(userId, page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    /**
     * 申请提现
     */
    @PostMapping("/applyWithdraw")
    public R<?> applyWithdraw(@RequestParam BigDecimal amount,
                              @RequestParam String payType) {
        Long userId = AppContextUtil.getUserId();
        distributionService.applyWithdraw(userId, amount, payType);
        return R.ok("提现申请已提交");
    }

    /**
     * 提现记录
     */
    @GetMapping("/withdrawList")
    public R<?> withdrawList() {
        Long userId = AppContextUtil.getUserId();
        List<?> list = distributionService.getWithdrawList(userId);
        return R.ok(list);
    }

    /**
     * 提现详情
     */
    @GetMapping("/withdrawDetail/{id}")
    public R<?> withdrawDetail(@PathVariable Long id) {
        Object detail = distributionService.getWithdrawDetail(id);
        return R.ok(detail);
    }
}
