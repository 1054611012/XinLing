package com.xinling.app.controller;

import com.xinling.app.service.IStatisticsService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/rank")
public class AppRankController {

    private final IStatisticsService statisticsService;

    public AppRankController(IStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * 专注排行榜
     */
    @GetMapping("/focus")
    public R<List<Map<String, Object>>> focusRank(@RequestParam(defaultValue = "week") String period) {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> stats = statisticsService.getFocusStats(userId, period);
        return R.ok(stats);
    }

    /**
     * 睡眠排行榜
     */
    @GetMapping("/sleep")
    public R<List<Map<String, Object>>> sleepRank(@RequestParam(defaultValue = "week") String period) {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> stats = statisticsService.getSleepStats(userId, period);
        return R.ok(stats);
    }

    /**
     * 情绪排行榜
     */
    @GetMapping("/emotion")
    public R<List<Map<String, Object>>> emotionRank(@RequestParam(defaultValue = "week") String period) {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> stats = statisticsService.getEmotionStats(userId, period);
        return R.ok(stats);
    }

    /**
     * 综合排行榜
     */
    @GetMapping("/all")
    public R<Map<String, Object>> allRank(@RequestParam(defaultValue = "week") String period) {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> focus = statisticsService.getFocusStats(userId, period);
        List<Map<String, Object>> sleep = statisticsService.getSleepStats(userId, period);
        List<Map<String, Object>> emotion = statisticsService.getEmotionStats(userId, period);
        Map<String, Object> result = new HashMap<>();
        result.put("focus", focus);
        result.put("sleep", sleep);
        result.put("emotion", emotion);
        return R.ok(result);
    }
}
