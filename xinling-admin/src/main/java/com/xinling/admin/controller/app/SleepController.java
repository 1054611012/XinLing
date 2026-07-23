package com.xinling.admin.controller.app;

import com.xinling.app.domain.entity.SleepRecord;
import com.xinling.app.mapper.SleepRecordMapper;
import com.xinling.app.service.ISleepService;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 睡眠记录管理控制器
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/sleep")
@Tag(name = "睡眠记录管理", description = "睡眠记录管理")
public class SleepController extends BaseController {

    private final ISleepService sleepService;
    private final SleepRecordMapper sleepRecordMapper;

    public SleepController(ISleepService sleepService, SleepRecordMapper sleepRecordMapper) {
        this.sleepService = sleepService;
        this.sleepRecordMapper = sleepRecordMapper;
    }

    /**
     * 查询睡眠记录列表
     */
    @PreAuthorize("@ss.hasPermi('app:sleep:list')")
    @GetMapping("/list")
    public TableDataInfo list(SleepRecord sleepRecord) {
        startPage();
        List<SleepRecord> list = sleepRecordMapper.selectList(null, null, null);
        return getDataTable(list);
    }

    /**
     * 获取睡眠记录详情
     */
    @PreAuthorize("@ss.hasPermi('app:sleep:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable Long id) {
        SleepRecord record = sleepRecordMapper.selectById(id);
        if (record == null) {
            return error("睡眠记录不存在");
        }
        return success(record);
    }
}
