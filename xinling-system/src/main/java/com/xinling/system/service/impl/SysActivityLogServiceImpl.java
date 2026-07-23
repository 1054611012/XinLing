package com.xinling.system.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.xinling.common.constant.CacheConstants;
import com.xinling.common.core.redis.RedisCache;
import com.xinling.common.utils.ActivityLogUtils;
import com.xinling.common.utils.DateUtils;
import com.xinling.system.domain.SysActivityLog;
import com.xinling.system.mapper.SysActivityLogMapper;
import com.xinling.system.service.ISysActivityLogService;

/**
 * 系统活动日志Service业务层处理
 *
 * @author xinling
 * @date 2025-04-10
 */
@Service
public class SysActivityLogServiceImpl implements ISysActivityLogService, ActivityLogUtils.ISysActivityLogServiceStatic
{
    @Autowired
    private SysActivityLogMapper activityLogMapper;

    @Autowired
    private RedisCache redisCache;

    private static final String RECENT_ACTIVITY_KEY = CacheConstants.SYS_CONFIG_KEY + "recentActivities";

    /**
     * 查询活动日志
     *
     * @param activityId 活动ID
     * @return 活动日志
     */
    @Override
    public SysActivityLog selectActivityLogById(Long activityId)
    {
        return activityLogMapper.selectActivityLogById(activityId);
    }

    /**
     * 查询活动日志列表
     *
     * @param activityLog 活动日志
     * @return 活动日志
     */
    @Override
    public List<SysActivityLog> selectActivityLogList(SysActivityLog activityLog)
    {
        return activityLogMapper.selectActivityLogList(activityLog);
    }

    /**
     * 查询近期活动(带缓存优化)
     *
     * @param limit 数量限制
     * @return 活动日志集合
     */
    @Override
    public List<SysActivityLog> selectRecentActivities(int limit)
    {
        // 构建缓存key,不同limit使用不同缓存
        String cacheKey = RECENT_ACTIVITY_KEY + ":" + limit;
        
        // 先从Redis中获取
        List<SysActivityLog> cachedActivities = redisCache.getCacheObject(cacheKey);
        if (cachedActivities != null && !cachedActivities.isEmpty())
        {
            return cachedActivities;
        }

        // 如果Redis中没有,则查询数据库并存入Redis
        List<SysActivityLog> activities = activityLogMapper.selectRecentActivityLogs(limit);
        
        // 缓存5分钟,活动数据实时性要求不高
        redisCache.setCacheObject(cacheKey, activities, 5, TimeUnit.MINUTES);
        
        return activities;
    }

    /**
     * 新增活动日志
     *
     * @param activityLog 活动日志
     * @return 结果
     */
    @Override
    public int insertActivityLog(SysActivityLog activityLog)
    {
        activityLog.setCreateTime(DateUtils.getNowDate());
        activityLog.setStatus("0"); // 默认正常状态
        int result = activityLogMapper.insertActivityLog(activityLog);
        
        // 新增后清除缓存,保证数据一致性
        clearActivityCache();
        
        return result;
    }

    /**
     * 修改活动日志
     *
     * @param activityLog 活动日志
     * @return 结果
     */
    @Override
    public int updateActivityLog(SysActivityLog activityLog)
    {
        activityLog.setUpdateTime(DateUtils.getNowDate());
        int result = activityLogMapper.updateActivityLog(activityLog);
        
        // 修改后清除缓存
        clearActivityCache();
        
        return result;
    }

    /**
     * 批量删除活动日志
     *
     * @param activityIds 需要删除的活动ID
     * @return 结果
     */
    @Override
    public int deleteActivityLogByIds(Long[] activityIds)
    {
        int result = activityLogMapper.deleteActivityLogByIds(activityIds);
        
        // 删除后清除缓存
        clearActivityCache();
        
        return result;
    }

    /**
     * 删除活动日志信息
     *
     * @param activityId 活动ID
     * @return 结果
     */
    @Override
    public int deleteActivityLogById(Long activityId)
    {
        int result = activityLogMapper.deleteActivityLogById(activityId);
        
        // 删除后清除缓存
        clearActivityCache();
        
        return result;
    }

    /**
     * 清除活动缓存
     */
    private void clearActivityCache()
    {
        // 清除所有limit的缓存
        redisCache.deleteObject(RECENT_ACTIVITY_KEY + ":*");
    }

    /**
     * 异步插入活动日志(供工具类调用)
     * 使用@Async注解实现异步执行,不阻塞主线程
     */
    @Override
    @Async
    public void insertActivityLogAsync(String type, String icon, String title, 
                                      String description, Long businessId, Date activityTime)
    {
        try
        {
            SysActivityLog activityLog = new SysActivityLog();
            activityLog.setActivityType(type);
            activityLog.setIcon(icon);
            activityLog.setTitle(title);
            activityLog.setDescription(description);
            activityLog.setBusinessId(businessId);
            activityLog.setActivityTime(activityTime);
            activityLog.setStatus("0");
            activityLog.setCreateTime(DateUtils.getNowDate());
            
            activityLogMapper.insertActivityLog(activityLog);
            
            // 清除缓存
            clearActivityCache();
        }
        catch (Exception e)
        {
            // 静默失败,不影响主业务
            e.printStackTrace();
        }
    }
}
