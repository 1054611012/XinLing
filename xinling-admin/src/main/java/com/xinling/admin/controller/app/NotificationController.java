package com.xinling.admin.controller.app;

import com.xinling.app.domain.entity.PushTask;
import com.xinling.app.mapper.PushTaskMapper;
import com.xinling.app.service.INotificationService;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 通知推送管理控制器
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/notification")
@Tag(name = "通知推送管理", description = "通知推送管理")
public class NotificationController extends BaseController {

    private final INotificationService notificationService;
    private final PushTaskMapper pushTaskMapper;

    public NotificationController(INotificationService notificationService, PushTaskMapper pushTaskMapper) {
        this.notificationService = notificationService;
        this.pushTaskMapper = pushTaskMapper;
    }

    /**
     * 创建推送任务
     */
    @PreAuthorize("@ss.hasPermi('app:notification:push')")
    @Log(title = "通知推送", businessType = BusinessType.INSERT)
    @PostMapping("/push")
    public AjaxResult push(@RequestBody PushTask pushTask) {
        pushTask.setStatus(0);
        pushTask.setSuccessCount(0);
        pushTask.setFailCount(0);
        pushTask.setCreateTime(new Date());
        pushTask.setUpdateTime(new Date());
        return toAjax(pushTaskMapper.insert(pushTask));
    }

    /**
     * 查询推送任务列表
     */
    @PreAuthorize("@ss.hasPermi('app:notification:task:list')")
    @GetMapping("/task/list")
    public TableDataInfo taskList() {
        startPage();
        List<PushTask> list = pushTaskMapper.selectList();
        return getDataTable(list);
    }

    /**
     * 重试失败推送任务
     */
    @PreAuthorize("@ss.hasPermi('app:notification:task:retry')")
    @Log(title = "通知推送", businessType = BusinessType.UPDATE)
    @PostMapping("/task/retry/{id}")
    public AjaxResult retryTask(@PathVariable Long id) {
        PushTask pushTask = pushTaskMapper.selectById(id);
        if (pushTask == null) {
            return error("推送任务不存在");
        }
        pushTask.setStatus(0);
        pushTask.setUpdateTime(new Date());
        return toAjax(pushTaskMapper.updateById(pushTask));
    }
}
