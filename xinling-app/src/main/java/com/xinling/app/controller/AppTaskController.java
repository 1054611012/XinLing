package com.xinling.app.controller;

import com.xinling.app.service.IGrowthService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 任务相关接口 —— mobile-web 前端使用 /api/app/task/ 前缀
 */
@RestController
@RequestMapping("/api/app/task")
public class AppTaskController {

    private final IGrowthService growthService;

    public AppTaskController(IGrowthService growthService) {
        this.growthService = growthService;
    }

    @GetMapping("/daily")
    public R<List<Map<String, Object>>> daily() {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> tasks = growthService.getDailyTasks(userId);
        return R.ok(tasks);
    }

    @PostMapping("/claimReward/{taskId}")
    public R<?> claimReward(@PathVariable Long taskId) {
        Long userId = AppContextUtil.getUserId();
        growthService.claimTaskReward(userId, taskId);
        return R.ok("领取成功");
    }
}
