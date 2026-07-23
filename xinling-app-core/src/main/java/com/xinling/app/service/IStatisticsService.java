package com.xinling.app.service;

import java.util.List;
import java.util.Map;

/**
 * 数据统计服务
 */
public interface IStatisticsService {

    /**
     * 专注统计
     */
    List<Map<String, Object>> getFocusStats(Long userId, String period);

    /**
     * 睡眠统计
     */
    List<Map<String, Object>> getSleepStats(Long userId, String period);

    /**
     * 情绪统计
     */
    List<Map<String, Object>> getEmotionStats(Long userId, String period);

    /**
     * AI交互统计
     */
    List<Map<String, Object>> getAiStats(Long userId, String period);

    /**
     * 分布统计
     */
    Map<String, Object> getDistributionStats(Long userId, String period);
}
