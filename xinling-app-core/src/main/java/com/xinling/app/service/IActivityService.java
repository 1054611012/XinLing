package com.xinling.app.service;

import com.xinling.app.domain.entity.Activity;

import java.util.List;

/**
 * 活动服务
 */
public interface IActivityService {

    /**
     * 获取活动列表（进行中的活动）
     */
    List<Activity> listActivities();

    /**
     * 获取活动详情
     */
    Activity getActivityDetail(Long id);

    /**
     * 参与活动
     */
    void joinActivity(Long userId, Long activityId);
}
