package com.xinling.app.controller;

import com.xinling.app.service.IStatisticsService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/statistics")
public class AppStatisticsController {

    private final IStatisticsService statisticsService;

    public AppStatisticsController(IStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * 专注统计
     */
    @GetMapping("/focus")
    public R<List<Map<String, Object>>> focus(@RequestParam(defaultValue = "week") String period) {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> stats = statisticsService.getFocusStats(userId, period);
        return R.ok(stats);
    }

    /**
     * 睡眠统计
     */
    @GetMapping("/sleep")
    public R<List<Map<String, Object>>> sleep(@RequestParam(defaultValue = "week") String period) {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> stats = statisticsService.getSleepStats(userId, period);
        return R.ok(stats);
    }

    /**
     * 情绪统计
     */
    @GetMapping("/emotion")
    public R<List<Map<String, Object>>> emotion(@RequestParam(defaultValue = "week") String period) {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> stats = statisticsService.getEmotionStats(userId, period);
        return R.ok(stats);
    }

    /**
     * AI交互统计
     */
    @GetMapping("/ai")
    public R<List<Map<String, Object>>> ai(@RequestParam(defaultValue = "week") String period) {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> stats = statisticsService.getAiStats(userId, period);
        return R.ok(stats);
    }

    /**
     * 用户统计总览
     */
    @GetMapping("/overview")
    public R<Map<String, Object>> overview(@RequestParam(defaultValue = "week") String period) {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> focus = statisticsService.getFocusStats(userId, period);
        List<Map<String, Object>> sleep = statisticsService.getSleepStats(userId, period);
        List<Map<String, Object>> emotion = statisticsService.getEmotionStats(userId, period);
        List<Map<String, Object>> ai = statisticsService.getAiStats(userId, period);
        Map<String, Object> result = new HashMap<>();
        result.put("focus", focus);
        result.put("sleep", sleep);
        result.put("emotion", emotion);
        result.put("ai", ai);
        return R.ok(result);
    }
}
