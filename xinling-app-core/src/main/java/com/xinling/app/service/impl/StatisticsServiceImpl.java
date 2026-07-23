package com.xinling.app.service.impl;

import com.xinling.app.mapper.StatisticsMapper;
import com.xinling.app.service.IStatisticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 数据统计服务实现
 */
@Service
public class StatisticsServiceImpl implements IStatisticsService {

    private final StatisticsMapper statisticsMapper;

    public StatisticsServiceImpl(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    @Override
    public List<Map<String, Object>> getFocusStats(Long userId, String period) {
        String[] range = getDateRange(period);
        String dateFormat = getDateFormat(period);
        return statisticsMapper.selectFocusStats(userId, range[0], range[1], dateFormat);
    }

    @Override
    public List<Map<String, Object>> getSleepStats(Long userId, String period) {
        String[] range = getDateRange(period);
        String dateFormat = getDateFormat(period);
        return statisticsMapper.selectSleepStats(userId, range[0], range[1], dateFormat);
    }

    @Override
    public List<Map<String, Object>> getEmotionStats(Long userId, String period) {
        String[] range = getDateRange(period);
        return statisticsMapper.selectEmotionStats(userId, range[0], range[1]);
    }

    @Override
    public List<Map<String, Object>> getAiStats(Long userId, String period) {
        String[] range = getDateRange(period);
        String dateFormat = getDateFormat(period);
        return statisticsMapper.selectAiStats(userId, range[0], range[1], dateFormat);
    }

    @Override
    public Map<String, Object> getDistributionStats(Long userId, String period) {
        String[] range = getDateRange(period);
        List<Map<String, Object>> focusDist = statisticsMapper.selectFocusDistribution(userId, range[0], range[1]);
        List<Map<String, Object>> sleepDist = statisticsMapper.selectSleepDistribution(userId, range[0], range[1]);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("focusDistribution", focusDist);
        result.put("sleepDistribution", sleepDist);
        return result;
    }

    // ========== 私有方法 ==========

    /**
     * 根据period获取日期范围和SQL日期格式
     */
    private String[] getDateRange(String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        if ("week".equalsIgnoreCase(period)) {
            startDate = endDate.minusDays(7);
        } else if ("month".equalsIgnoreCase(period)) {
            startDate = endDate.minusMonths(1);
        } else if ("year".equalsIgnoreCase(period)) {
            startDate = endDate.minusYears(1);
        } else {
            startDate = endDate.minusDays(7);
        }

        return new String[]{
                startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59"))
        };
    }

    /**
     * 获取日期分组格式
     */
    private String getDateFormat(String period) {
        if ("week".equalsIgnoreCase(period)) {
            return "%Y-%m-%d";
        } else if ("month".equalsIgnoreCase(period)) {
            return "%Y-%m-%d";
        } else if ("year".equalsIgnoreCase(period)) {
            return "%Y-%m";
        }
        return "%Y-%m-%d";
    }
}
