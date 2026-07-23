package com.xinling.common.utils;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 活动日志记录工具类
 * 提供统一的活动记录方法,避免代码重复
 * 
 * 使用示例:
 * <pre>
 * // 1. 简单记录
 * ActivityLogUtils.recordUserActivity("新用户注册", "张三刚刚注册", userId);
 * 
 * // 2. 自定义图标
 * ActivityLogUtils.recordSystemWarning("CPU告警", "CPU使用率90%");
 * 
 * // 3. 完整自定义
 * ActivityLogUtils.record("order", "el-icon-shopping-cart", "新订单", "订单创建", orderId);
 * </pre>
 *
 * @author xinling
 * @date 2025-04-10
 */
@Component
public class ActivityLogUtils
{
    private static ISysActivityLogServiceStatic activityLogService;

    @Autowired
    public void setActivityLogService(ISysActivityLogServiceStatic service)
    {
        ActivityLogUtils.activityLogService = service;
    }

    /**
     * 记录活动(最通用方法)
     *
     * @param type 活动类型: user/order/system/message/custom
     * @param icon 图标: el-icon-user, el-icon-warning等
     * @param title 标题
     * @param description 描述
     * @param businessId 关联业务ID(可为null)
     */
    public static void record(String type, String icon, String title, String description, Long businessId)
    {
        if (activityLogService == null)
        {
            return;
        }

        try
        {
            activityLogService.insertActivityLogAsync(type, icon, title, description, businessId, new Date());
        }
        catch (Exception e)
        {
            // 静默失败,不影响主业务
            e.printStackTrace();
        }
    }

    // ==================== 用户相关活动 ====================

    /**
     * 记录用户活动
     */
    public static void recordUserActivity(String title, String description, Long userId)
    {
        record("user", "el-icon-user", title, description, userId);
    }

    /**
     * 记录用户注册
     */
    public static void recordUserRegister(String username, Long userId)
    {
        recordUserActivity("新用户注册", username + "刚刚注册成为新用户", userId);
    }

    /**
     * 记录用户登录
     */
    public static void recordUserLogin(String username, Long userId)
    {
        recordUserActivity("用户登录", username + "登录系统", userId);
    }

    // ==================== 系统相关活动 ====================

    /**
     * 记录系统警告
     */
    public static void recordSystemWarning(String title, String description)
    {
        record("system", "el-icon-warning", title, description, null);
    }

    /**
     * 记录CPU告警
     */
    public static void recordCpuWarning(double usage)
    {
        recordSystemWarning("系统警告", 
            String.format("服务器CPU使用率超过80%%，当前使用率：%.2f%%", usage));
    }

    /**
     * 记录内存告警
     */
    public static void recordMemoryWarning(double usage)
    {
        recordSystemWarning("系统警告",
            String.format("服务器内存使用率超过85%%，当前使用率：%.2f%%", usage));
    }

    /**
     * 记录磁盘告警
     */
    public static void recordDiskWarning(String diskName, double usage)
    {
        recordSystemWarning("磁盘空间警告",
            String.format("磁盘分区 %s 使用率超过90%%，当前使用率：%.2f%%", diskName, usage));
    }

    // ==================== 消息相关活动 ====================

    /**
     * 记录消息通知
     */
    public static void recordMessageNotification(String title, String description, Long businessId)
    {
        record("message", "el-icon-message", title, description, businessId);
    }

    /**
     * 记录用户收到消息
     */
    public static void recordUserMessages(Long userId, int count)
    {
        recordMessageNotification("消息通知", 
            String.format("您收到了%d条新消息", count), userId);
    }

    /**
     * 记录系统通知
     */
    public static void recordSystemNotice(String content)
    {
        recordMessageNotification("系统通知", content, null);
    }

    // ==================== 订单相关活动 ====================

    /**
     * 记录订单活动
     */
    public static void recordOrderActivity(String title, String description, Long orderId)
    {
        record("order", "el-icon-shopping-cart-full", title, description, orderId);
    }

    /**
     * 记录新订单
     */
    public static void recordNewOrder(String orderNo, Long orderId)
    {
        recordOrderActivity("新订单生成", 
            String.format("订单%s已创建", orderNo), orderId);
    }

    /**
     * 记录订单状态变更
     */
    public static void recordOrderStatusChange(String orderNo, String status, Long orderId)
    {
        recordOrderActivity("订单状态更新",
            String.format("订单%s状态更新为：%s", orderNo, status), orderId);
    }

    // ==================== 自定义活动 ====================

    /**
     * 记录自定义活动(简化版,使用默认图标)
     */
    public static void recordCustom(String title, String description, Long businessId)
    {
        record("custom", "el-icon-info", title, description, businessId);
    }

    /**
     * 静态内部接口 - 用于解决Spring注入问题
     */
    public interface ISysActivityLogServiceStatic
    {
        void insertActivityLogAsync(String type, String icon, String title, 
                                   String description, Long businessId, Date activityTime);
    }
}
