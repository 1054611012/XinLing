package com.xinling.app.controller;

import com.xinling.app.domain.entity.MallGoods;
import com.xinling.app.domain.entity.UserGrowth;
import com.xinling.app.service.IGrowthService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/growth")
public class AppGrowthController {

    private final IGrowthService growthService;

    public AppGrowthController(IGrowthService growthService) {
        this.growthService = growthService;
    }

    /**
     * 获取成长信息
     */
    @GetMapping("/info")
    public R<UserGrowth> info() {
        Long userId = AppContextUtil.getUserId();
        UserGrowth growth = growthService.getGrowthInfo(userId);
        return R.ok(growth);
    }

    /**
     * 获取成就列表
     */
    @GetMapping("/achievements")
    public R<List<Map<String, Object>>> achievements() {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> list = growthService.getAchievementList(userId);
        return R.ok(list);
    }

    /**
     * 获取每日任务
     */
    @GetMapping("/dailyTasks")
    public R<List<Map<String, Object>>> dailyTasks() {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> tasks = growthService.getDailyTasks(userId);
        return R.ok(tasks);
    }

    /**
     * 领取任务奖励
     */
    @PostMapping("/claimTaskReward/{taskId}")
    public R<?> claimTaskReward(@PathVariable Long taskId) {
        Long userId = AppContextUtil.getUserId();
        growthService.claimTaskReward(userId, taskId);
        return R.ok("领取成功");
    }

    /**
     * 积分商城商品列表
     */
    @GetMapping("/mallGoods")
    public R<List<MallGoods>> mallGoods() {
        List<MallGoods> list = growthService.getMallGoods();
        return R.ok(list);
    }

    /**
     * 积分兑换商品
     */
    @PostMapping("/exchangeGoods/{goodsId}")
    public R<?> exchangeGoods(@PathVariable Long goodsId) {
        Long userId = AppContextUtil.getUserId();
        growthService.exchangeGoods(userId, goodsId);
        return R.ok("兑换成功");
    }
}
