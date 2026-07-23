package com.xinling.system.service;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinling.common.utils.DateUtils;
import com.xinling.system.domain.SysActivityLog;

/**
 * 消息通知服务
 * 用于统一记录各种消息通知活动
 *
 * @author xinling
 * @date 2025-04-10
 */
@Service
public class MessageNotificationService
{
    @Autowired
    private ISysActivityLogService activityLogService;

    /**
     * 记录消息通知活动
     *
     * @param title 通知标题
     * @param description 通知描述
     * @param businessId 关联业务ID(可选)
     */
    public void recordMessageNotification(String title, String description, Long businessId)
    {
        try
        {
            SysActivityLog activityLog = new SysActivityLog();
            activityLog.setActivityType("message");
            activityLog.setIcon("el-icon-message");
            activityLog.setTitle(title);
            activityLog.setDescription(description);
            activityLog.setBusinessId(businessId);
            activityLog.setActivityTime(new Date());
            
            activityLogService.insertActivityLog(activityLog);
        }
        catch (Exception e)
        {
            // 记录失败不影响主业务流程
            e.printStackTrace();
        }
    }

    /**
     * 记录系统通知
     *
     * @param content 通知内容
     */
    public void recordSystemNotice(String content)
    {
        recordMessageNotification("系统通知", content, null);
    }

    /**
     * 记录用户消息
     *
     * @param userId 用户ID
     * @param messageCount 消息数量
     */
    public void recordUserMessages(Long userId, int messageCount)
    {
        String description = String.format("您收到了%d条新消息", messageCount);
        recordMessageNotification("消息通知", description, userId);
    }

    /**
     * 记录订单通知
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param status 订单状态
     */
    public void recordOrderNotification(Long orderId, String orderNo, String status)
    {
        String description = String.format("订单%s状态更新为：%s", orderNo, status);
        recordMessageNotification("订单通知", description, orderId);
    }

    /**
     * 记录审批通知
     *
     * @param businessId 业务ID
     * @param approver 审批人
     * @param result 审批结果
     */
    public void recordApprovalNotification(Long businessId, String approver, String result)
    {
        String description = String.format("%s已%s您的申请", approver, result);
        recordMessageNotification("审批通知", description, businessId);
    }
}
