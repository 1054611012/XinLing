package com.xinling.admin.controller.system;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xinling.common.annotation.Anonymous;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import com.xinling.system.domain.SysActivityLog;
import com.xinling.system.service.ISysActivityLogService;

/**
 * 系统活动日志Controller
 *
 * @author xinling
 * @date 2025-04-10
 */
@RestController
@RequestMapping("/system/activity")
@Tag(name = "系统活动日志", description = "系统活动日志")
public class SysActivityLogController extends BaseController
{
    @Autowired
    private ISysActivityLogService activityLogService;

    /**
     * 查询近期活动列表(前端调用,无需权限)
     */
    @Anonymous
    @GetMapping("/recent")
    public AjaxResult recentActivities(@RequestParam(defaultValue = "5") int limit)
    {
        List<SysActivityLog> list = activityLogService.selectRecentActivities(limit);
        return success(list);
    }

    /**
     * 查询活动日志列表(管理后台,需要权限)
     */
    @PreAuthorize("@ss.hasPermi('system:activity:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysActivityLog activityLog)
    {
        startPage();
        List<SysActivityLog> list = activityLogService.selectActivityLogList(activityLog);
        return getDataTable(list);
    }

    /**
     * 根据活动ID获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:activity:query')")
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable Long activityId)
    {
        return success(activityLogService.selectActivityLogById(activityId));
    }

    /**
     * 新增活动日志
     */
    @PreAuthorize("@ss.hasPermi('system:activity:add')")
    @Log(title = "活动日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysActivityLog activityLog)
    {
        return toAjax(activityLogService.insertActivityLog(activityLog));
    }

    /**
     * 修改活动日志
     */
    @PreAuthorize("@ss.hasPermi('system:activity:edit')")
    @Log(title = "活动日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysActivityLog activityLog)
    {
        return toAjax(activityLogService.updateActivityLog(activityLog));
    }

    /**
     * 删除活动日志
     */
    @PreAuthorize("@ss.hasPermi('system:activity:remove')")
    @Log(title = "活动日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Long[] activityIds)
    {
        return toAjax(activityLogService.deleteActivityLogByIds(activityIds));
    }
}
