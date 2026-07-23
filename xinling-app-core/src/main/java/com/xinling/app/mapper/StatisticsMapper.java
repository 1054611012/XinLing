package com.xinling.app.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 数据统计 Mapper
 */
public interface StatisticsMapper {

    /**
     * 专注统计（按天聚合）
     */
    List<Map<String, Object>> selectFocusStats(@Param("userId") Long userId,
                                               @Param("startTime") String startTime,
                                               @Param("endTime") String endTime,
                                               @Param("dateFormat") String dateFormat);

    /**
     * 睡眠统计（按天聚合）
     */
    List<Map<String, Object>> selectSleepStats(@Param("userId") Long userId,
                                               @Param("startTime") String startTime,
                                               @Param("endTime") String endTime,
                                               @Param("dateFormat") String dateFormat);

    /**
     * 情绪统计（按天聚合）
     */
    List<Map<String, Object>> selectEmotionStats(@Param("userId") Long userId,
                                                 @Param("startTime") String startTime,
                                                 @Param("endTime") String endTime);

    /**
     * AI交互统计（按天聚合）
     */
    List<Map<String, Object>> selectAiStats(@Param("userId") Long userId,
                                            @Param("startTime") String startTime,
                                            @Param("endTime") String endTime,
                                            @Param("dateFormat") String dateFormat);

    /**
     * 专注模式分布统计
     */
    List<Map<String, Object>> selectFocusDistribution(@Param("userId") Long userId,
                                                       @Param("startTime") String startTime,
                                                       @Param("endTime") String endTime);

    /**
     * 睡眠评分分布统计
     */
    List<Map<String, Object>> selectSleepDistribution(@Param("userId") Long userId,
                                                       @Param("startTime") String startTime,
                                                       @Param("endTime") String endTime);
}
