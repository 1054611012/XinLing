package com.xinling.app.service;

import com.xinling.app.domain.entity.SleepDiary;
import com.xinling.app.domain.entity.SleepRecord;
import com.xinling.app.domain.model.SleepSettingsVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 睡眠服务
 */
public interface ISleepService {

    /**
     * 开始睡眠
     */
    SleepRecord start(Long userId, Long audioMixId);

    /**
     * 结束睡眠（起床）
     */
    SleepRecord end(Long userId, Integer interruptCount, Integer snoringCount);

    /**
     * 设置起床（同结束睡眠，但独立接口）
     */
    SleepRecord setWakeUp(Long userId, Integer interruptCount, Integer snoringCount);

    /**
     * 获取睡眠记录列表
     */
    List<SleepRecord> getRecords(Long userId);

    /**
     * 获取睡眠记录详情
     */
    SleepRecord getRecord(Long id);

    /**
     * 获取指定日期的睡眠报告
     */
    Map<String, Object> getReport(Long userId, String date);

    /**
     * 更新睡眠日记
     */
    void updateDiary(Long userId, SleepDiary diary);

    /**
     * 获取睡眠设置
     */
    SleepSettingsVO getSettings(Long userId);

    /**
     * 更新睡眠设置
     */
    void updateSettings(Long userId, SleepSettingsVO settings);
}
