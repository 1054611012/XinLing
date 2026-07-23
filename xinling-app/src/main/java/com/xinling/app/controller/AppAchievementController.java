package com.xinling.app.controller;

import com.xinling.app.service.IGrowthService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 成就相关接口 —— mobile-web 前端使用 /api/app/achievement/ 前缀
 */
@RestController
@RequestMapping("/api/app/achievement")
public class AppAchievementController {

    private final IGrowthService growthService;

    public AppAchievementController(IGrowthService growthService) {
        this.growthService = growthService;
    }

    @GetMapping("/list")
    public R<List<Map<String, Object>>> list() {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> list = growthService.getAchievementList(userId);
        return R.ok(list);
    }
}
