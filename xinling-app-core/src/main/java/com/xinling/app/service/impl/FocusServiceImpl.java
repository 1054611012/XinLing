package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.FocusRecord;
import com.xinling.app.domain.entity.FocusSettings;
import com.xinling.app.mapper.FocusRecordMapper;
import com.xinling.app.mapper.FocusSettingsMapper;
import com.xinling.app.service.IFocusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 专注服务实现
 */
@Service
public class FocusServiceImpl implements IFocusService {

    private static final Logger log = LoggerFactory.getLogger(FocusServiceImpl.class);

    private final FocusRecordMapper focusRecordMapper;
    private final FocusSettingsMapper focusSettingsMapper;

    public FocusServiceImpl(FocusRecordMapper focusRecordMapper,
                            FocusSettingsMapper focusSettingsMapper) {
        this.focusRecordMapper = focusRecordMapper;
        this.focusSettingsMapper = focusSettingsMapper;
    }

    @Override
    @Transactional
    public FocusRecord start(Long userId, String mode, String tag, Long audioMixId) {
        // 检查是否有进行中的专注
        FocusRecord current = focusRecordMapper.selectCurrentByUserId(userId);
        if (current != null) {
            throw new RuntimeException("当前已有进行中的专注，请先结束");
        }

        FocusRecord record = new FocusRecord();
        record.setUserId(userId);
        record.setStartTime(new Date());
        record.setDuration(0);
        record.setStatus(0);
        record.setMode(mode != null ? mode : "tomato");
        record.setTag(tag);
        record.setInterruptCount(0);
        record.setAudioMixId(audioMixId);
        focusRecordMapper.insert(record);

        log.info("开始专注: userId={}, mode={}, tag={}", userId, mode, tag);
        return record;
    }

    @Override
    @Transactional
    public FocusRecord pause(Long userId) {
        FocusRecord record = focusRecordMapper.selectCurrentByUserId(userId);
        if (record == null) {
            throw new RuntimeException("没有进行中的专注");
        }

        // 计算当前段已专注的分钟数
        long elapsed = (System.currentTimeMillis() - record.getStartTime().getTime()) / 60000;
        if (elapsed > 0) {
            record.setDuration(record.getDuration() + (int) elapsed);
        }

        // 重置start_time作为检查点，避免暂停期间的时间被计入
        record.setStartTime(new Date());
        record.setInterruptCount(record.getInterruptCount() + 1);
        focusRecordMapper.updateById(record);

        log.info("暂停专注: id={}, duration={}", record.getId(), record.getDuration());
        return record;
    }

    @Override
    @Transactional
    public FocusRecord resume(Long userId) {
        FocusRecord record = focusRecordMapper.selectCurrentByUserId(userId);
        if (record == null) {
            throw new RuntimeException("没有进行中的专注");
        }

        // 重置start_time为当前时间，继续计时
        record.setStartTime(new Date());
        focusRecordMapper.updateById(record);

        log.info("恢复专注: id={}", record.getId());
        return record;
    }

    @Override
    @Transactional
    public FocusRecord end(Long userId, String note) {
        FocusRecord record = focusRecordMapper.selectCurrentByUserId(userId);
        if (record == null) {
            throw new RuntimeException("没有进行中的专注");
        }

        // 计算最后一段的专注分钟数
        long elapsed = (System.currentTimeMillis() - record.getStartTime().getTime()) / 60000;
        record.setDuration(record.getDuration() + (int) elapsed);
        record.setEndTime(new Date());
        record.setStatus(1);
        record.setNote(note);
        focusRecordMapper.updateById(record);

        log.info("完成专注: id={}, duration={}", record.getId(), record.getDuration());
        return record;
    }

    @Override
    @Transactional
    public FocusRecord interrupt(Long userId) {
        FocusRecord record = focusRecordMapper.selectCurrentByUserId(userId);
        if (record == null) {
            throw new RuntimeException("没有进行中的专注");
        }

        // 计算最后一段的专注分钟数
        long elapsed = (System.currentTimeMillis() - record.getStartTime().getTime()) / 60000;
        record.setDuration(record.getDuration() + (int) elapsed);
        record.setEndTime(new Date());
        record.setStatus(2);
        focusRecordMapper.updateById(record);

        log.info("中断专注: id={}, duration={}", record.getId(), record.getDuration());
        return record;
    }

    @Override
    public List<FocusRecord> getRecords(Long userId) {
        return focusRecordMapper.selectByUserId(userId);
    }

    @Override
    public FocusRecord getRecord(Long id) {
        FocusRecord record = focusRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("专注记录不存在");
        }
        return record;
    }

    @Override
    public FocusSettings getSettings(Long userId) {
        FocusSettings settings = focusSettingsMapper.selectByUserId(userId);
        if (settings == null) {
            // 创建默认设置
            settings = new FocusSettings();
            settings.setUserId(userId);
            settings.setStrictMode(0);
            settings.setAppBlock(0);
            settings.setAllowedApps("[]");
            settings.setNotificationBlock(1);
            settings.setAiEncouragement(1);
            settings.setEncouragementInterval(30);
            focusSettingsMapper.insert(settings);
        }
        return settings;
    }

    @Override
    @Transactional
    public void updateSettings(Long userId, FocusSettings settings) {
        FocusSettings existing = focusSettingsMapper.selectByUserId(userId);
        settings.setUserId(userId);
        if (existing == null) {
            focusSettingsMapper.insert(settings);
        } else {
            settings.setId(existing.getId());
            focusSettingsMapper.updateByUserId(settings);
        }
    }

    @Override
    public List<FocusRecord> selectFocusList(FocusRecord focusRecord) {
        return focusRecordMapper.selectList(
                focusRecord.getUserId(), null, null);
    }

    @Override
    public FocusRecord selectById(Long id) {
        return focusRecordMapper.selectById(id);
    }
}