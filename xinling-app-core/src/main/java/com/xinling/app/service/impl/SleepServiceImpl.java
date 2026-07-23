package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.SleepDiary;
import com.xinling.app.domain.entity.SleepRecord;
import com.xinling.app.domain.model.SleepSettingsVO;
import com.xinling.app.mapper.SleepDiaryMapper;
import com.xinling.app.mapper.SleepRecordMapper;
import com.xinling.app.service.ISleepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 睡眠服务实现
 */
@Service
public class SleepServiceImpl implements ISleepService {

    private static final Logger log = LoggerFactory.getLogger(SleepServiceImpl.class);

    private final SleepRecordMapper sleepRecordMapper;
    private final SleepDiaryMapper sleepDiaryMapper;

    public SleepServiceImpl(SleepRecordMapper sleepRecordMapper,
                            SleepDiaryMapper sleepDiaryMapper) {
        this.sleepRecordMapper = sleepRecordMapper;
        this.sleepDiaryMapper = sleepDiaryMapper;
    }

    @Override
    @Transactional
    public SleepRecord start(Long userId, Long audioMixId) {
        // 检查是否有未结束的睡眠记录
        SleepRecord current = sleepRecordMapper.selectCurrentByUserId(userId);
        if (current != null) {
            throw new RuntimeException("当前已有进行中的睡眠记录，请先起床");
        }

        SleepRecord record = new SleepRecord();
        record.setUserId(userId);
        record.setStartTime(new Date());
        record.setAudioMixId(audioMixId);
        sleepRecordMapper.insert(record);

        log.info("开始睡眠: userId={}", userId);
        return record;
    }

    @Override
    @Transactional
    public SleepRecord end(Long userId, Integer interruptCount, Integer snoringCount) {
        SleepRecord record = sleepRecordMapper.selectCurrentByUserId(userId);
        if (record == null) {
            throw new RuntimeException("没有进行中的睡眠记录");
        }

        Date now = new Date();
        long elapsedMinutes = (now.getTime() - record.getStartTime().getTime()) / 60000;
        int duration = (int) Math.max(1, elapsedMinutes);

        record.setEndTime(now);
        record.setDuration(duration);
        record.setInterruptCount(interruptCount != null ? interruptCount : 0);
        record.setSnoringCount(snoringCount != null ? snoringCount : 0);

        // 模拟估算睡眠阶段
        int deepSleepMinutes = (int) (duration * 0.25);
        int remSleepMinutes = (int) (duration * 0.20);
        int lightSleepMinutes = duration - deepSleepMinutes - remSleepMinutes;
        record.setDeepSleepMinutes(deepSleepMinutes);
        record.setLightSleepMinutes(lightSleepMinutes);
        record.setRemSleepMinutes(remSleepMinutes);

        // 根据时长计算睡眠评分
        int score = calculateSleepScore(duration);
        record.setSleepScore(score);

        sleepRecordMapper.updateById(record);

        // 自动创建当天的睡眠日记
        SleepDiary diary = new SleepDiary();
        diary.setUserId(userId);
        diary.setDate(new java.sql.Date(now.getTime()));
        sleepDiaryMapper.upsert(diary);

        log.info("结束睡眠: id={}, duration={}, score={}", record.getId(), duration, score);
        return record;
    }

    @Override
    @Transactional
    public SleepRecord setWakeUp(Long userId, Integer interruptCount, Integer snoringCount) {
        return end(userId, interruptCount, snoringCount);
    }

    @Override
    public List<SleepRecord> getRecords(Long userId) {
        return sleepRecordMapper.selectByUserId(userId);
    }

    @Override
    public SleepRecord getRecord(Long id) {
        SleepRecord record = sleepRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("睡眠记录不存在");
        }
        return record;
    }

    @Override
    public Map<String, Object> getReport(Long userId, String dateStr) {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            throw new RuntimeException("日期格式错误，应为yyyy-MM-dd");
        }

        SleepRecord record = sleepRecordMapper.selectByUserIdAndDate(userId, date);
        SleepDiary diary = sleepDiaryMapper.selectByUserIdAndDate(userId, date);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("date", dateStr);

        if (record != null) {
            report.put("sleepRecord", record);
        } else {
            report.put("sleepRecord", null);
        }

        if (diary != null) {
            report.put("sleepDiary", diary);
        } else {
            report.put("sleepDiary", null);
        }

        // 近7天趋势（简化）
        List<Map<String, Object>> weeklyTrend = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
        } catch (Exception e) {
            cal.setTime(new Date());
        }
        cal.add(Calendar.DAY_OF_MONTH, -6);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 7; i++) {
            Date d = new java.sql.Date(cal.getTimeInMillis());
            SleepRecord r = sleepRecordMapper.selectByUserIdAndDate(userId, d);
            Map<String, Object> dayData = new LinkedHashMap<>();
            dayData.put("date", sdf.format(cal.getTime()));
            dayData.put("duration", r != null ? r.getDuration() : 0);
            dayData.put("sleepScore", r != null ? r.getSleepScore() : 0);
            weeklyTrend.add(dayData);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        report.put("weeklyTrend", weeklyTrend);

        return report;
    }

    @Override
    @Transactional
    public void updateDiary(Long userId, SleepDiary diary) {
        diary.setUserId(userId);
        if (diary.getDate() == null) {
            diary.setDate(new java.sql.Date(System.currentTimeMillis()));
        }
        sleepDiaryMapper.upsert(diary);
    }

    @Override
    public SleepSettingsVO getSettings(Long userId) {
        // 返回默认设置，后续可扩展持久化
        SleepSettingsVO settings = new SleepSettingsVO();
        settings.setTargetBedtime("23:00");
        settings.setTargetWakeUpTime("07:00");
        settings.setSmartAlarm(1);
        settings.setSmartAlarmAdvance(30);
        settings.setWindDownReminder(1);
        settings.setWindDownTime("22:30");
        settings.setSleepReminder(1);
        settings.setSleepReminderTime("22:50");
        settings.setSleepSoundEnabled(1);
        settings.setDefaultAudioId(null);
        return settings;
    }

    @Override
    @Transactional
    public void updateSettings(Long userId, SleepSettingsVO settings) {
        // 暂不持久化，后续可扩展
        log.info("更新睡眠设置: userId={}", userId);
    }

    /**
     * 根据睡眠时长计算评分
     */
    private int calculateSleepScore(int durationMinutes) {
        if (durationMinutes >= 420 && durationMinutes <= 540) {
            // 7-9小时：最佳
            return 85 + (int) (Math.random() * 15);
        } else if (durationMinutes >= 360 && durationMinutes < 420) {
            // 6-7小时：良好
            return 70 + (int) (Math.random() * 15);
        } else if (durationMinutes >= 540 && durationMinutes <= 600) {
            // 9-10小时：良好
            return 65 + (int) (Math.random() * 15);
        } else if (durationMinutes >= 180 && durationMinutes < 360) {
            // 3-6小时：一般
            return 50 + (int) (Math.random() * 20);
        } else {
            // 不足3小时或超过10小时：较差
            return 30 + (int) (Math.random() * 20);
        }
    }
}
