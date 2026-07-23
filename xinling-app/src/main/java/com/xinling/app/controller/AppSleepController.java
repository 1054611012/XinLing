package com.xinling.app.controller;

import com.xinling.app.domain.entity.SleepDiary;
import com.xinling.app.domain.entity.SleepRecord;
import com.xinling.app.domain.model.SleepSettingsVO;
import com.xinling.app.service.ISleepService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/sleep")
public class AppSleepController {

    private final ISleepService sleepService;

    public AppSleepController(ISleepService sleepService) {
        this.sleepService = sleepService;
    }

    /**
     * 开始睡眠
     */
    @PostMapping("/start")
    public R<SleepRecord> start(@RequestParam(required = false) Long audioMixId) {
        Long userId = AppContextUtil.getUserId();
        SleepRecord record = sleepService.start(userId, audioMixId);
        return R.ok(record);
    }

    /**
     * 结束睡眠（起床）
     */
    @PostMapping("/end")
    public R<SleepRecord> end(@RequestParam(defaultValue = "0") Integer interruptCount,
                              @RequestParam(defaultValue = "0") Integer snoringCount) {
        Long userId = AppContextUtil.getUserId();
        SleepRecord record = sleepService.end(userId, interruptCount, snoringCount);
        return R.ok(record);
    }

    /**
     * 设置起床
     */
    @PostMapping("/wakeUp")
    public R<SleepRecord> wakeUp(@RequestParam(defaultValue = "0") Integer interruptCount,
                                 @RequestParam(defaultValue = "0") Integer snoringCount) {
        Long userId = AppContextUtil.getUserId();
        SleepRecord record = sleepService.setWakeUp(userId, interruptCount, snoringCount);
        return R.ok(record);
    }

    /**
     * 睡眠记录列表
     */
    @GetMapping("/records")
    public R<List<SleepRecord>> records() {
        Long userId = AppContextUtil.getUserId();
        List<SleepRecord> records = sleepService.getRecords(userId);
        return R.ok(records);
    }

    /**
     * 睡眠记录详情
     */
    @GetMapping("/record/{id}")
    public R<SleepRecord> record(@PathVariable Long id) {
        SleepRecord record = sleepService.getRecord(id);
        return R.ok(record);
    }

    /**
     * 睡眠报告（指定日期）
     */
    @GetMapping({"/report", "/report/{date}"})
    public R<Map<String, Object>> report(@PathVariable(required = false) String date) {
        Long userId = AppContextUtil.getUserId();
        Map<String, Object> report = sleepService.getReport(userId, date);
        return R.ok(report);
    }

    /**
     * 更新睡眠日记
     */
    @PutMapping("/diary")
    public R<?> updateDiary(@RequestBody SleepDiary diary) {
        Long userId = AppContextUtil.getUserId();
        sleepService.updateDiary(userId, diary);
        return R.ok();
    }

    /**
     * 获取睡眠设置
     */
    @GetMapping("/settings")
    public R<SleepSettingsVO> settings() {
        Long userId = AppContextUtil.getUserId();
        SleepSettingsVO settings = sleepService.getSettings(userId);
        return R.ok(settings);
    }

    /**
     * 更新睡眠设置
     */
    @PutMapping("/settings")
    public R<?> updateSettings(@RequestBody SleepSettingsVO settings) {
        Long userId = AppContextUtil.getUserId();
        sleepService.updateSettings(userId, settings);
        return R.ok();
    }
}
