package com.xinling.app.service;

import com.xinling.app.domain.entity.CommissionRecord;
import com.xinling.app.domain.entity.DistributionSettings;
import com.xinling.app.domain.entity.Distributor;
import com.xinling.app.domain.entity.PayOrder;
import com.xinling.app.domain.entity.WithdrawApply;
import com.xinling.app.domain.model.CommissionOverviewVO;
import com.xinling.app.domain.model.DistributorInfoVO;
import com.xinling.app.domain.model.TeamStatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * 分销服务
 */
public interface IDistributionService {

    /**
     * 申请成为分销员
     */
    void applyDistributor(Long userId, String realName, String alipayAccount, String wechatAccount);

    /**
     * 获取分销员信息
     */
    DistributorInfoVO getDistributorInfo(Long userId);

    /**
     * 更新分销员信息
     */
    void updateDistributorInfo(Long userId, String realName, String alipayAccount, String wechatAccount);

    /**
     * 生成推广码
     */
    String getPromotionCode(Long userId);

    /**
     * 获取直推团队
     */
    List<Map<String, Object>> getTeamDirect(Long userId);

    /**
     * 获取间推团队
     */
    List<Map<String, Object>> getTeamIndirect(Long userId);

    /**
     * 获取团队统计
     */
    TeamStatisticsVO getTeamStatistics(Long userId);

    /**
     * 获取佣金概览
     */
    CommissionOverviewVO getCommissionOverview(Long userId);

    /**
     * 获取佣金明细
     */
    List<CommissionRecord> getCommissionDetail(Long userId, int page, int size);

    /**
     * 申请提现
     */
    void applyWithdraw(Long userId, java.math.BigDecimal amount, String payType);

    /**
     * 获取提现列表
     */
    List<WithdrawApply> getWithdrawList(Long userId);

    /**
     * 获取提现详情
     */
    WithdrawApply getWithdrawDetail(Long id);

    // ========== 管理后台方法 ==========

    List<Distributor> selectDistributorList(Distributor distributor);

    Distributor selectDistributorById(Long id);

    int auditDistributor(Long id, Integer status, String remark, Long auditUserId);

    List<PayOrder> selectDistributionOrderList(PayOrder payOrder);

    List<CommissionRecord> selectCommissionList(CommissionRecord record);

    List<WithdrawApply> selectWithdrawList(WithdrawApply apply);

    int auditWithdraw(Long id, Integer status, String remark, Long auditUserId);

    int updateSettings(DistributionSettings settings);
}
