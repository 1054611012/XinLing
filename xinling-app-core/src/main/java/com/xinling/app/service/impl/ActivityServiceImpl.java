package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.Activity;
import com.xinling.app.domain.entity.UserActivity;
import com.xinling.app.mapper.ActivityMapper;
import com.xinling.app.mapper.UserActivityMapper;
import com.xinling.app.service.IActivityService;
import org.slf4j.Logger;
import java.util.Date;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 活动服务实现
 */
@Service
public class ActivityServiceImpl implements IActivityService {

    private static final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

    private final ActivityMapper activityMapper;
    private final UserActivityMapper userActivityMapper;

    public ActivityServiceImpl(ActivityMapper activityMapper,
                                UserActivityMapper userActivityMapper) {
        this.activityMapper = activityMapper;
        this.userActivityMapper = userActivityMapper;
    }

    @Override
    public List<Activity> listActivities() {
        return activityMapper.selectActiveList();
    }

    @Override
    public Activity getActivityDetail(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        return activity;
    }

    @Override
    @Transactional
    public void joinActivity(Long userId, Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (activity.getStatus() != 1) {
            throw new RuntimeException("活动未上线");
        }

        // 检查时间范围
        Date now = new Date();
        if (activity.getStartTime() != null && now.before(activity.getStartTime())) {
            throw new RuntimeException("活动尚未开始");
        }
        if (activity.getEndTime() != null && now.after(activity.getEndTime())) {
            throw new RuntimeException("活动已结束");
        }

        // 检查是否已参与
        UserActivity existing = userActivityMapper.selectByUserAndActivity(userId, activityId);
        if (existing != null) {
            throw new RuntimeException("您已参与该活动");
        }

        // 记录参与
        UserActivity userActivity = new UserActivity();
        userActivity.setUserId(userId);
        userActivity.setActivityId(activityId);
        userActivity.setJoinTime(now);
        userActivityMapper.insert(userActivity);

        // 增加活动参与计数
        activityMapper.incrementJoinCount(activityId);
    }
}
