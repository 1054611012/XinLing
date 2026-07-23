package com.xinling.app.controller;

import com.xinling.app.domain.entity.Activity;
import com.xinling.app.service.IActivityService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/activity")
public class AppActivityController {

    private final IActivityService activityService;

    public AppActivityController(IActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * 活动列表
     */
    @GetMapping("/list")
    public R<List<Activity>> list() {
        List<Activity> list = activityService.listActivities();
        return R.ok(list);
    }

    /**
     * 活动详情
     */
    @GetMapping("/detail/{id}")
    public R<Activity> detail(@PathVariable Long id) {
        Activity activity = activityService.getActivityDetail(id);
        return R.ok(activity);
    }

    /**
     * 参与活动
     */
    @PostMapping("/join/{activityId}")
    public R<?> join(@PathVariable Long activityId) {
        Long userId = AppContextUtil.getUserId();
        activityService.joinActivity(userId, activityId);
        return R.ok("参与成功");
    }
}
