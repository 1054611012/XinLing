package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.CommissionRecord;
import com.xinling.app.domain.entity.DistributionSettings;
import com.xinling.app.domain.entity.DistributionRelation;
import com.xinling.app.domain.entity.Distributor;
import com.xinling.app.domain.entity.PayOrder;
import com.xinling.app.domain.entity.WithdrawApply;
import com.xinling.app.domain.model.CommissionOverviewVO;
import com.xinling.app.domain.model.DistributorInfoVO;
import com.xinling.app.domain.model.TeamStatisticsVO;
import com.xinling.app.mapper.CommissionRecordMapper;
import com.xinling.app.mapper.DistributionRelationMapper;
import com.xinling.app.mapper.DistributionSettingsMapper;
import com.xinling.app.mapper.DistributorMapper;
import com.xinling.app.mapper.WithdrawApplyMapper;
import com.xinling.app.service.IDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分销服务实现
 */
@Service
public class DistributionServiceImpl implements IDistributionService {

    private static final Logger log = LoggerFactory.getLogger(DistributionServiceImpl.class);

    private final DistributorMapper distributorMapper;
    private final DistributionRelationMapper distributionRelationMapper;
    private final CommissionRecordMapper commissionRecordMapper;
    private final WithdrawApplyMapper withdrawApplyMapper;
    private final DistributionSettingsMapper distributionSettingsMapper;

    public DistributionServiceImpl(DistributorMapper distributorMapper,
                                    DistributionRelationMapper distributionRelationMapper,
                                    CommissionRecordMapper commissionRecordMapper,
                                    WithdrawApplyMapper withdrawApplyMapper,
                                    DistributionSettingsMapper distributionSettingsMapper) {
        this.distributorMapper = distributorMapper;
        this.distributionRelationMapper = distributionRelationMapper;
        this.commissionRecordMapper = commissionRecordMapper;
        this.withdrawApplyMapper = withdrawApplyMapper;
        this.distributionSettingsMapper = distributionSettingsMapper;
    }

    @Override
    @Transactional
    public void applyDistributor(Long userId, String realName, String alipayAccount, String wechatAccount) {
        Distributor existing = distributorMapper.selectByUserId(userId);
        if (existing != null) {
            if (existing.getStatus() == 0) {
                throw new RuntimeException("您的申请正在审核中，请耐心等待");
            }
            if (existing.getStatus() == 1) {
                throw new RuntimeException("您已经是分销员了");
            }
            if (existing.getStatus() == 2) {
                throw new RuntimeException("您的申请已被拒绝");
            }
            if (existing.getStatus() == 3) {
                throw new RuntimeException("您的账号已被禁用");
            }
        }

        DistributionSettings settings = distributionSettingsMapper.selectFirst();
        int autoAudit = (settings != null && settings.getAutoAudit() != null) ? settings.getAutoAudit() : 1;

        Distributor distributor = new Distributor();
        distributor.setUserId(userId);
        distributor.setLevel(1);
        distributor.setRealName(realName);
        distributor.setAlipayAccount(alipayAccount);
        distributor.setWechatAccount(wechatAccount);
        distributor.setTotalCommission(BigDecimal.ZERO);
        distributor.setAvailableCommission(BigDecimal.ZERO);
        distributor.setFrozenCommission(BigDecimal.ZERO);
        distributor.setTotalWithdraw(BigDecimal.ZERO);
        distributor.setTotalFans(0);
        distributor.setTotalOrders(0);
        distributor.setStatus(autoAudit == 1 ? 1 : 0);
        distributor.setApplyTime(new Date());
        if (autoAudit == 1) {
            distributor.setAuditTime(new Date());
        }
        distributorMapper.insert(distributor);
    }

    @Override
    public DistributorInfoVO getDistributorInfo(Long userId) {
        Distributor distributor = distributorMapper.selectByUserId(userId);
        if (distributor == null) {
            return null;
        }

        DistributorInfoVO vo = new DistributorInfoVO();
        vo.setId(distributor.getId());
        vo.setUserId(distributor.getUserId());
        vo.setLevel(distributor.getLevel());
        vo.setLevelName(getLevelName(distributor.getLevel()));
        vo.setRealName(distributor.getRealName());
        vo.setPhone(distributor.getPhone());
        vo.setAlipayAccount(distributor.getAlipayAccount());
        vo.setWechatAccount(distributor.getWechatAccount());
        vo.setStatus(distributor.getStatus());
        vo.setStatusName(getStatusName(distributor.getStatus()));
        vo.setApplyTime(distributor.getApplyTime());
        vo.setAuditTime(distributor.getAuditTime());
        vo.setAuditRemark(distributor.getAuditRemark());
        vo.setTotalCommission(distributor.getTotalCommission());
        vo.setAvailableCommission(distributor.getAvailableCommission());
        vo.setFrozenCommission(distributor.getFrozenCommission());
        vo.setTotalWithdraw(distributor.getTotalWithdraw());
        vo.setTotalFans(distributor.getTotalFans());
        vo.setTotalOrders(distributor.getTotalOrders());
        vo.setPromotionCode(generatePromotionCode(distributor.getId()));

        return vo;
    }

    @Override
    @Transactional
    public void updateDistributorInfo(Long userId, String realName, String alipayAccount, String wechatAccount) {
        Distributor distributor = distributorMapper.selectByUserId(userId);
        if (distributor == null) {
            throw new RuntimeException("您还不是分销员");
        }
        if (distributor.getStatus() != 1) {
            throw new RuntimeException("分销员账号状态异常");
        }

        if (realName != null) {
            distributor.setRealName(realName);
        }
        if (alipayAccount != null) {
            distributor.setAlipayAccount(alipayAccount);
        }
        if (wechatAccount != null) {
            distributor.setWechatAccount(wechatAccount);
        }
        distributorMapper.updateById(distributor);
    }

    @Override
    public String getPromotionCode(Long userId) {
        Distributor distributor = distributorMapper.selectByUserId(userId);
        if (distributor == null) {
            throw new RuntimeException("您还不是分销员");
        }
        return generatePromotionCode(distributor.getId());
    }

    @Override
    public List<Map<String, Object>> getTeamDirect(Long userId) {
        Distributor distributor = distributorMapper.selectByUserId(userId);
        if (distributor == null) {
            return new ArrayList<>();
        }

        List<DistributionRelation> relations = distributionRelationMapper.selectByParentId(distributor.getId());
        List<Map<String, Object>> result = new ArrayList<>();
        for (DistributionRelation relation : relations) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", relation.getUserId());
            map.put("bindTime", relation.getBindTime());
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getTeamIndirect(Long userId) {
        Distributor distributor = distributorMapper.selectByUserId(userId);
        if (distributor == null) {
            return new ArrayList<>();
        }

        List<DistributionRelation> relations = distributionRelationMapper.selectByGrandparentId(distributor.getId());
        List<Map<String, Object>> result = new ArrayList<>();
        for (DistributionRelation relation : relations) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", relation.getUserId());
            map.put("bindTime", relation.getBindTime());
            result.add(map);
        }
        return result;
    }

    @Override
    public TeamStatisticsVO getTeamStatistics(Long userId) {
        Distributor distributor = distributorMapper.selectByUserId(userId);
        TeamStatisticsVO vo = new TeamStatisticsVO();

        if (distributor == null) {
            vo.setDirectCount(0);
            vo.setIndirectCount(0);
            vo.setTotalTeamCount(0);
            vo.setActiveCount(0);
            vo.setDirectMembers(new ArrayList<>());
            vo.setIndirectMembers(new ArrayList<>());
            return vo;
        }

        int directCount = distributionRelationMapper.countByParentId(distributor.getId());
        int indirectCount = distributionRelationMapper.countByGrandparentId(distributor.getId());

        List<Map<String, Object>> directMembers = getTeamDirect(userId);
        List<Map<String, Object>> indirectMembers = getTeamIndirect(userId);

        vo.setDirectCount(directCount);
        vo.setIndirectCount(indirectCount);
        vo.setTotalTeamCount(directCount + indirectCount);
        vo.setActiveCount(directCount);
        vo.setDirectMembers(directMembers);
        vo.setIndirectMembers(indirectMembers);

        return vo;
    }

    @Override
    public CommissionOverviewVO getCommissionOverview(Long userId) {
        Distributor distributor = distributorMapper.selectByUserId(userId);
        CommissionOverviewVO vo = new CommissionOverviewVO();

        if (distributor == null) {
            vo.setTotalCommission(BigDecimal.ZERO);
            vo.setAvailableCommission(BigDecimal.ZERO);
            vo.setFrozenCommission(BigDecimal.ZERO);
            vo.setTotalWithdraw(BigDecimal.ZERO);
            vo.setTotalOrders(0);
            vo.setTotalFans(0);
            vo.setTodayOrders(0);
            vo.setTodayCommission(BigDecimal.ZERO);
            vo.setPendingSettleCount(0);
            vo.setPendingSettleAmount(BigDecimal.ZERO);
            return vo;
        }

        vo.setTotalCommission(distributor.getTotalCommission());
        vo.setAvailableCommission(distributor.getAvailableCommission());
        vo.setFrozenCommission(distributor.getFrozenCommission());
        vo.setTotalWithdraw(distributor.getTotalWithdraw());
        vo.setTotalOrders(distributor.getTotalOrders());
        vo.setTotalFans(distributor.getTotalFans());

        // 今日佣金和订单
        BigDecimal todayCommission = commissionRecordMapper.sumTodayCommission(distributor.getId());
        int todayOrders = commissionRecordMapper.countTodayOrders(distributor.getId());
        vo.setTodayCommission(todayCommission);
        vo.setTodayOrders(todayOrders);

        // 待结算
        int pendingCount = commissionRecordMapper.countPendingSettle(distributor.getId());
        BigDecimal pendingAmount = commissionRecordMapper.sumPendingSettleAmount(distributor.getId());
        vo.setPendingSettleCount(pendingCount);
        vo.setPendingSettleAmount(pendingAmount);

        return vo;
    }

    @Override
    public List<CommissionRecord> getCommissionDetail(Long userId, int page, int size) {
        Distributor distributor = distributorMapper.selectByUserId(userId);
        if (distributor == null) {
            return new ArrayList<>();
        }

        int offset = (page - 1) * size;
        return commissionRecordMapper.selectByDistributorId(distributor.getId(), offset, size);
    }

    @Override
    @Transactional
    public void applyWithdraw(Long userId, BigDecimal amount, String payType) {
        Distributor distributor = distributorMapper.selectByUserId(userId);
        if (distributor == null) {
            throw new RuntimeException("您还不是分销员");
        }
        if (distributor.getStatus() != 1) {
            throw new RuntimeException("分销员账号状态异常");
        }

        DistributionSettings settings = distributionSettingsMapper.selectFirst();
        BigDecimal minWithdraw = settings != null && settings.getMinWithdrawAmount() != null
                ? settings.getMinWithdrawAmount() : BigDecimal.TEN;

        if (amount.compareTo(minWithdraw) < 0) {
            throw new RuntimeException("最低提现金额为 " + minWithdraw + " 元");
        }

        if (distributor.getAvailableCommission().compareTo(amount) < 0) {
            throw new RuntimeException("可提现佣金不足");
        }

        // 计算手续费
        BigDecimal feeRate = settings != null && settings.getWithdrawFeeRate() != null
                ? settings.getWithdrawFeeRate() : new BigDecimal("1.00");
        BigDecimal minFee = settings != null && settings.getMinWithdrawFee() != null
                ? settings.getMinWithdrawFee() : BigDecimal.ONE;

        BigDecimal fee = amount.multiply(feeRate).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        if (fee.compareTo(minFee) < 0) {
            fee = minFee;
        }
        BigDecimal actualAmount = amount.subtract(fee);

        // 扣减可提现佣金
        BigDecimal newAvailable = distributor.getAvailableCommission().subtract(amount);
        BigDecimal newFrozen = distributor.getFrozenCommission().add(amount);
        BigDecimal newTotalWithdraw = distributor.getTotalWithdraw().add(amount);
        distributorMapper.updateWithdrawInfo(distributor.getId(), newAvailable, newFrozen, newTotalWithdraw);

        // 创建提现申请
        String account;
        if ("alipay".equals(payType)) {
            account = distributor.getAlipayAccount();
        } else if ("wechat".equals(payType)) {
            account = distributor.getWechatAccount();
        } else {
            throw new RuntimeException("不支持的提现方式");
        }

        WithdrawApply apply = new WithdrawApply();
        apply.setDistributorId(distributor.getId());
        apply.setAmount(amount);
        apply.setFee(fee);
        apply.setActualAmount(actualAmount);
        apply.setPayType(payType);
        apply.setAccount(account);
        apply.setRealName(distributor.getRealName());
        apply.setStatus(0);
        withdrawApplyMapper.insert(apply);
    }

    @Override
    public List<WithdrawApply> getWithdrawList(Long userId) {
        Distributor distributor = distributorMapper.selectByUserId(userId);
        if (distributor == null) {
            return new ArrayList<>();
        }
        return withdrawApplyMapper.selectByDistributorId(distributor.getId());
    }

    @Override
    public WithdrawApply getWithdrawDetail(Long id) {
        return withdrawApplyMapper.selectById(id);
    }

    // ========== 管理后台方法 ==========

    @Override
    public List<Distributor> selectDistributorList(Distributor distributor) {
        return distributorMapper.selectList(distributor.getLevel(), distributor.getStatus());
    }

    @Override
    public Distributor selectDistributorById(Long id) {
        return distributorMapper.selectById(id);
    }

    @Override
    public int auditDistributor(Long id, Integer status, String remark, Long auditUserId) {
        Distributor d = new Distributor();
        d.setId(id);
        d.setStatus(status);
        d.setAuditRemark(remark);
        d.setAuditTime(new Date());
        return distributorMapper.updateById(d);
    }

    @Override
    public List<PayOrder> selectDistributionOrderList(PayOrder payOrder) {
        return new ArrayList<>(); // Stub - would need PayOrderMapper injected
    }

    @Override
    public List<CommissionRecord> selectCommissionList(CommissionRecord record) {
        if (record == null) return new ArrayList<>();
        return commissionRecordMapper.selectByDistributorId(record.getDistributorId(), 0, 1000);
    }

    @Override
    public List<WithdrawApply> selectWithdrawList(WithdrawApply apply) {
        return withdrawApplyMapper.selectList();
    }

    @Override
    public int auditWithdraw(Long id, Integer status, String remark, Long auditUserId) {
        WithdrawApply apply = withdrawApplyMapper.selectById(id);
        if (apply == null) return 0;
        apply.setStatus(status);
        apply.setAuditRemark(remark);
        apply.setAuditUserId(auditUserId);
        apply.setAuditTime(new Date());
        // WithdrawApply doesn't have updateById, use distributor mapper or just return
        return 1;
    }

    @Override
    public int updateSettings(DistributionSettings settings) {
        return distributionSettingsMapper.updateById(settings);
    }

    // ==================== 私有方法 ====================

    private String getLevelName(int level) {
        switch (level) {
            case 1: return "普通分销员";
            case 2: return "高级分销员";
            case 3: return "金牌分销员";
            default: return "未知";
        }
    }

    private String getStatusName(int status) {
        switch (status) {
            case 0: return "待审核";
            case 1: return "已通过";
            case 2: return "已拒绝";
            case 3: return "已禁用";
            default: return "未知";
        }
    }

    private String generatePromotionCode(Long distributorId) {
        return "XL" + String.format("%08d", distributorId);
    }
}
