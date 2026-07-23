package com.xinling.admin.controller.task;

import org.springframework.stereotype.Component;

import com.xinling.common.utils.ActivityLogUtils;
import com.xinling.framework.web.domain.Server;

/**
 * 系统监控告警定时任务
 *
 * 配置说明:
 * 登录后台 → 系统监控 → 定时任务 → 新增任务
 * @author xinling
 * @date 2025-04-10
 */
@Component("systemMonitorTask")
public class SystemMonitorTask
{
    /**
     * 检查CPU使用率并记录告警
     * 建议配置:每5分钟执行一次
     */
    public void checkCpuUsage()
    {
        try
        {
            Server server = new Server();
            server.copyTo();

            double cpuUsage = server.getCpu().getTotal();

            // CPU使用率超过80%时记录告警
            if (cpuUsage > 80)
            {
                ActivityLogUtils.recordCpuWarning(cpuUsage);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 检查内存使用率并记录告警
     * 建议配置:每5分钟执行一次
     */
    public void checkMemoryUsage()
    {
        try
        {
            Server server = new Server();
            server.copyTo();

            double memoryUsage = server.getMem().getUsage();

            // 内存使用率超过85%时记录告警
            if (memoryUsage > 85)
            {
                ActivityLogUtils.recordMemoryWarning(memoryUsage);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 检查磁盘使用率并记录告警
     * 建议配置:每30分钟执行一次
     */
    public void checkDiskUsage()
    {
        try
        {
            Server server = new Server();
            server.copyTo();

            // 检查每个文件系统的使用率
            server.getSysFiles().forEach(sysFile -> {
                double diskUsage = sysFile.getUsage();

                // 磁盘使用率超过90%时记录告警
                if (diskUsage > 90)
                {
                    ActivityLogUtils.recordDiskWarning(sysFile.getDirName(), diskUsage);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 综合检查所有系统资源
     * 建议配置:每10分钟执行一次
     */
    public void checkAllResources()
    {
        checkCpuUsage();
        checkMemoryUsage();
        checkDiskUsage();
    }
}
