package com.xinling.system.service;

import java.util.List;
import com.xinling.system.domain.SysActivityLog;

/**
 * 系统活动日志Service接口
 *
 * @author xinling
 * @date 2025-04-10
 */
public interface ISysActivityLogService
{
    /**
     * 查询活动日志
     *
     * @param activityId 活动ID
     * @return 活动日志
     */
    public SysActivityLog selectActivityLogById(Long activityId);

    /**
     * 查询活动日志列表
     *
     * @param activityLog 活动日志
     * @return 活动日志集合
     */
    public List<SysActivityLog> selectActivityLogList(SysActivityLog activityLog);

    /**
     * 查询近期活动(带缓存)
     *
     * @param limit 数量限制,默认10条
     * @return 活动日志集合
     */
    public List<SysActivityLog> selectRecentActivities(int limit);

    /**
     * 新增活动日志
     *
     * @param activityLog 活动日志
     * @return 结果
     */
    public int insertActivityLog(SysActivityLog activityLog);

    /**
     * 修改活动日志
     *
     * @param activityLog 活动日志
     * @return 结果
     */
    public int updateActivityLog(SysActivityLog activityLog);

    /**
     * 批量删除活动日志
     *
     * @param activityIds 需要删除的活动ID
     * @return 结果
     */
    public int deleteActivityLogByIds(Long[] activityIds);

    /**
     * 删除活动日志信息
     *
     * @param activityId 活动ID
     * @return 结果
     */
    public int deleteActivityLogById(Long activityId);
}
