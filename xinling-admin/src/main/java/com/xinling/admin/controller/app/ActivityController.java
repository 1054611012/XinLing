package com.xinling.admin.controller.app;

import com.xinling.app.domain.entity.Activity;
import com.xinling.app.mapper.ActivityMapper;
import com.xinling.common.annotation.Log;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 活动管理控制器
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/activity")
@Tag(name = "活动管理", description = "活动管理")
public class ActivityController extends BaseController {

    private final ActivityMapper activityMapper;

    public ActivityController(ActivityMapper activityMapper) {
        this.activityMapper = activityMapper;
    }

    /**
     * 查询活动列表
     */
    @PreAuthorize("@ss.hasPermi('app:activity:list')")
    @GetMapping("/list")
    public TableDataInfo list(Activity activity) {
        startPage();
        List<Activity> list = activityMapper.selectList(activity.getTitle(), activity.getType(), activity.getStatus());
        return getDataTable(list);
    }

    /**
     * 新增活动
     */
    @PreAuthorize("@ss.hasPermi('app:activity:create')")
    @Log(title = "活动管理", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestBody Activity activity) {
        activity.setCreateTime(new Date());
        activity.setUpdateTime(new Date());
        return toAjax(activityMapper.insert(activity));
    }

    /**
     * 修改活动
     */
    @PreAuthorize("@ss.hasPermi('app:activity:update')")
    @Log(title = "活动管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update/{id}")
    public AjaxResult update(@PathVariable Long id, @RequestBody Activity activity) {
        activity.setId(id);
        activity.setUpdateTime(new Date());
        return toAjax(activityMapper.updateById(activity));
    }

    /**
     * 发布活动
     */
    @PreAuthorize("@ss.hasPermi('app:activity:online')")
    @Log(title = "活动管理", businessType = BusinessType.UPDATE)
    @PostMapping("/online/{id}")
    public AjaxResult online(@PathVariable Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            return error("活动不存在");
        }
        activity.setStatus(1);
        activity.setUpdateTime(new Date());
        return toAjax(activityMapper.updateById(activity));
    }

    /**
     * 下架活动
     */
    @PreAuthorize("@ss.hasPermi('app:activity:offline')")
    @Log(title = "活动管理", businessType = BusinessType.UPDATE)
    @PostMapping("/offline/{id}")
    public AjaxResult offline(@PathVariable Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            return error("活动不存在");
        }
        activity.setStatus(0);
        activity.setUpdateTime(new Date());
        return toAjax(activityMapper.updateById(activity));
    }

    /**
     * 活动统计
     */
    @PreAuthorize("@ss.hasPermi('app:activity:statistics')")
    @GetMapping("/statistics/{id}")
    public AjaxResult statistics(@PathVariable Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            return error("活动不存在");
        }
        return success(activity);
    }
}
