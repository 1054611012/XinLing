package com.xinling.app.controller;

import com.xinling.app.domain.entity.FocusRecord;
import com.xinling.app.domain.entity.FocusSettings;
import com.xinling.app.service.IFocusService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/focus")
public class AppFocusController {

    private final IFocusService focusService;

    public AppFocusController(IFocusService focusService) {
        this.focusService = focusService;
    }

    /**
     * 开始专注
     */
    @PostMapping("/start")
    public R<FocusRecord> start(@RequestParam(defaultValue = "focus") String mode,
                                @RequestParam(required = false) String tag,
                                @RequestParam(required = false) Long audioMixId) {
        Long userId = AppContextUtil.getUserId();
        FocusRecord record = focusService.start(userId, mode, tag, audioMixId);
        return R.ok(record);
    }

    /**
     * 暂停专注
     */
    @PostMapping("/pause")
    public R<FocusRecord> pause() {
        Long userId = AppContextUtil.getUserId();
        FocusRecord record = focusService.pause(userId);
        return R.ok(record);
    }

    /**
     * 恢复专注
     */
    @PostMapping("/resume")
    public R<FocusRecord> resume() {
        Long userId = AppContextUtil.getUserId();
        FocusRecord record = focusService.resume(userId);
        return R.ok(record);
    }

    /**
     * 结束专注
     */
    @PostMapping("/end")
    public R<FocusRecord> end(@RequestParam(required = false) String note) {
        Long userId = AppContextUtil.getUserId();
        FocusRecord record = focusService.end(userId, note);
        return R.ok(record);
    }

    /**
     * 中断专注
     */
    @PostMapping("/interrupt")
    public R<FocusRecord> interrupt() {
        Long userId = AppContextUtil.getUserId();
        FocusRecord record = focusService.interrupt(userId);
        return R.ok(record);
    }

    /**
     * 专注记录列表
     */
    @GetMapping("/records")
    public R<List<FocusRecord>> records() {
        Long userId = AppContextUtil.getUserId();
        List<FocusRecord> records = focusService.getRecords(userId);
        return R.ok(records);
    }

    /**
     * 专注记录详情
     */
    @GetMapping("/record/{id}")
    public R<FocusRecord> record(@PathVariable Long id) {
        FocusRecord record = focusService.getRecord(id);
        return R.ok(record);
    }

    /**
     * 获取专注设置
     */
    @GetMapping("/settings")
    public R<FocusSettings> settings() {
        Long userId = AppContextUtil.getUserId();
        FocusSettings settings = focusService.getSettings(userId);
        return R.ok(settings);
    }

    /**
     * 更新专注设置
     */
    @PostMapping("/settings/update")
    public R<?> updateSettings(@RequestBody FocusSettings settings) {
        Long userId = AppContextUtil.getUserId();
        focusService.updateSettings(userId, settings);
        return R.ok();
    }
}
