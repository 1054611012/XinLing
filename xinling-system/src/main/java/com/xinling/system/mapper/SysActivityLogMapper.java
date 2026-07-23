package com.xinling.system.mapper;

import java.util.List;
import com.xinling.system.domain.SysActivityLog;

/**
 * 系统活动日志Mapper接口
 *
 * @author xinling
 * @date 2025-04-10
 */
public interface SysActivityLogMapper
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
     * 查询最新N条活动日志
     *
     * @param limit 数量限制
     * @return 活动日志集合
     */
    public List<SysActivityLog> selectRecentActivityLogs(int limit);

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
     * 删除活动日志
     *
     * @param activityId 活动ID
     * @return 结果
     */
    public int deleteActivityLogById(Long activityId);

    /**
     * 批量删除活动日志
     *
     * @param activityIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteActivityLogByIds(Long[] activityIds);
}
