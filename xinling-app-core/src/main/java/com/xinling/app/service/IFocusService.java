package com.xinling.app.service;

import com.xinling.app.domain.entity.FocusRecord;
import com.xinling.app.domain.entity.FocusSettings;

import java.util.List;

/**
 * 专注服务
 */
public interface IFocusService {

    /**
     * 开始专注
     */
    FocusRecord start(Long userId, String mode, String tag, Long audioMixId);

    /**
     * 暂停专注
     */
    FocusRecord pause(Long userId);

    /**
     * 恢复专注
     */
    FocusRecord resume(Long userId);

    /**
     * 结束专注
     */
    FocusRecord end(Long userId, String note);

    /**
     * 中断专注
     */
    FocusRecord interrupt(Long userId);

    /**
     * 获取专注记录列表
     */
    List<FocusRecord> getRecords(Long userId);

    /**
     * 获取专注记录详情
     */
    FocusRecord getRecord(Long id);

    /**
     * 获取专注设置
     */
    FocusSettings getSettings(Long userId);

    /**
     * 更新专注设置
     */
    void updateSettings(Long userId, FocusSettings settings);

    // ========== 管理后台方法 ==========

    List<FocusRecord> selectFocusList(FocusRecord focusRecord);

    FocusRecord selectById(Long id);
}
