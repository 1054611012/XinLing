package com.xinling.admin.controller.app;

import com.xinling.app.domain.entity.AppUser;
import com.xinling.app.domain.entity.UserVip;
import com.xinling.app.domain.entity.VipGiftRecord;
import com.xinling.app.domain.entity.VipGiftRule;
import com.xinling.app.mapper.AppUserMapper;
import com.xinling.app.mapper.UserVipMapper;
import com.xinling.app.mapper.VipGiftRecordMapper;
import com.xinling.app.mapper.VipGiftRuleMapper;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import com.xinling.common.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员赠送管理控制器
 */
@RestController
@RequestMapping("/app/vip/gift")
@Tag(name = "会员赠送管理", description = "会员赠送管理")
public class VipGiftController extends BaseController {

    private final VipGiftRuleMapper vipGiftRuleMapper;
    private final VipGiftRecordMapper vipGiftRecordMapper;
    private final AppUserMapper appUserMapper;
    private final UserVipMapper userVipMapper;

    public VipGiftController(VipGiftRuleMapper vipGiftRuleMapper,
                                   VipGiftRecordMapper vipGiftRecordMapper,
                                   AppUserMapper appUserMapper,
                                   UserVipMapper userVipMapper) {
        this.vipGiftRuleMapper = vipGiftRuleMapper;
        this.vipGiftRecordMapper = vipGiftRecordMapper;
        this.appUserMapper = appUserMapper;
        this.userVipMapper = userVipMapper;
    }

    // ==================== 赠送规则管理 ====================

    /**
     * 查询规则列表
     */
    @PreAuthorize("@ss.hasPermi('app:vip:gift:list')")
    @GetMapping("/rule/list")
    public TableDataInfo ruleList(VipGiftRule query) {
        startPage();
        List<VipGiftRule> list = vipGiftRuleMapper.selectList(query.getRuleType(), query.getStatus());
        return getDataTable(list);
    }

    /**
     * 查询规则详情
     */
    @PreAuthorize("@ss.hasPermi('app:vip:gift:list')")
    @GetMapping("/rule/{id}")
    public AjaxResult ruleDetail(@PathVariable Long id) {
        return success(vipGiftRuleMapper.selectById(id));
    }

    /**
     * 新增规则
     */
    @PreAuthorize("@ss.hasPermi('app:vip:gift:add')")
    @Log(title = "会员赠送规则", businessType = BusinessType.INSERT)
    @PostMapping("/rule/create")
    public AjaxResult createRule(@RequestBody VipGiftRule rule) {
        if (rule.getVipDays() == null || rule.getVipDays() < 0) {
            return error("请填写有效的赠送天数");
        }
        if (rule.getSortOrder() == null) {
            rule.setSortOrder(0);
        }
        if (rule.getStatus() == null) {
            rule.setStatus(1);
        }
        return toAjax(vipGiftRuleMapper.insert(rule));
    }

    /**
     * 修改规则
     */
    @PreAuthorize("@ss.hasPermi('app:vip:gift:edit')")
    @Log(title = "会员赠送规则", businessType = BusinessType.UPDATE)
    @PostMapping("/rule/update")
    public AjaxResult updateRule(@RequestBody VipGiftRule rule) {
        return toAjax(vipGiftRuleMapper.updateById(rule));
    }

    /**
     * 启用/禁用规则
     */
    @PreAuthorize("@ss.hasPermi('app:vip:gift:edit')")
    @Log(title = "会员赠送规则", businessType = BusinessType.UPDATE)
    @PostMapping("/rule/status")
    public AjaxResult changeRuleStatus(@RequestBody VipGiftRule rule) {
        VipGiftRule update = new VipGiftRule();
        update.setId(rule.getId());
        update.setStatus(rule.getStatus());
        return toAjax(vipGiftRuleMapper.updateById(update));
    }

    /**
     * 删除规则
     */
    @PreAuthorize("@ss.hasPermi('app:vip:gift:remove')")
    @Log(title = "会员赠送规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/rule/{id}")
    public AjaxResult deleteRule(@PathVariable Long id) {
        return toAjax(vipGiftRuleMapper.deleteById(id));
    }

    // ==================== 赠送记录 ====================

    /**
     * 查询赠送记录列表
     */
    @PreAuthorize("@ss.hasPermi('app:vip:gift:list')")
    @GetMapping("/record/list")
    public TableDataInfo recordList(VipGiftRecord query) {
        startPage();
        List<VipGiftRecord> list = vipGiftRecordMapper.selectList(
                query.getRuleId(), query.getUserId(), query.getGrantType());
        return getDataTable(list);
    }

    // ==================== 手动赠送 ====================

    /**
     * 手动赠送会员
     */
    @PreAuthorize("@ss.hasPermi('app:vip:gift:grant')")
    @Log(title = "会员手动赠送", businessType = BusinessType.INSERT)
    @PostMapping("/grant")
    public AjaxResult manualGrant(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Integer days = Integer.valueOf(body.get("vipDays").toString());
        String reason = (String) body.get("reason");

        // 查询用户信息
        AppUser user = appUserMapper.selectById(userId);
        if (user == null) {
            return error("用户不存在");
        }

        // 计算到期时间
        Date expireTime = null;
        if (days > 0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, days);
            expireTime = cal.getTime();
        } else {
            // 0 表示终身
            expireTime = new Date(Long.MAX_VALUE);
        }

        // 创建赠送记录
        VipGiftRecord record = new VipGiftRecord();
        record.setRuleId(null);
        record.setUserId(userId);
        record.setUserNickname(user.getNickname());
        record.setGrantType("manual");
        record.setVipDays(days);
        record.setReason(reason);
        record.setOperatorId(getUserId());
        record.setOperatorName(SecurityUtils.getUsername());
        record.setExpireTime(expireTime);
        record.setStatus(1);
        vipGiftRecordMapper.insert(record);

        // 更新或创建用户会员
        UserVip existing = userVipMapper.selectByUserId(userId);
        if (existing != null) {
            // 累加会员时间
            Calendar cal = Calendar.getInstance();
            cal.setTime(existing.getEndTime() != null && existing.getEndTime().after(new Date())
                    ? existing.getEndTime() : new Date());
            cal.add(Calendar.DAY_OF_YEAR, days);
            existing.setEndTime(cal.getTime());
            existing.setAutoRenew(0);
            existing.setPackageName("管理员赠送");
            userVipMapper.updateById(existing);
        } else {
            // 新增会员记录
            Calendar cal = Calendar.getInstance();
            Date start = new Date();
            Date end;
            if (days > 0) {
                cal.add(Calendar.DAY_OF_YEAR, days);
                end = cal.getTime();
            } else {
                end = expireTime;
            }
            UserVip newVip = new UserVip();
            newVip.setUserId(userId);
            newVip.setPackageName("管理员赠送");
            newVip.setStartTime(start);
            newVip.setEndTime(end);
            newVip.setAutoRenew(0);
            userVipMapper.insert(newVip);
        }

        // 增加规则已发放数量
        Long ruleId = body.get("ruleId") != null ? Long.valueOf(body.get("ruleId").toString()) : null;
        if (ruleId != null) {
            vipGiftRuleMapper.incrementGrantedCount(ruleId);
        }

        return success("赠送成功，用户「" + user.getNickname() + "」获得" + (days > 0 ? days + "天" : "终身") + "会员");
    }

    // ==================== 统计 ====================

    /**
     * 赠送统计数据
     */
    @PreAuthorize("@ss.hasPermi('app:vip:gift:list')")
    @GetMapping("/statistics")
    public AjaxResult statistics() {
        Map<String, Object> stats = new HashMap<>();
        List<VipGiftRule> allRules = vipGiftRuleMapper.selectList(null, null);
        List<VipGiftRecord> allRecords = vipGiftRecordMapper.selectList(null, null, null);

        stats.put("totalRules", allRules.size());
        stats.put("activeRules", allRules.stream().filter(r -> r.getStatus() == 1).count());
        stats.put("totalGrants", allRecords.size());
        stats.put("autoGrants", allRecords.stream().filter(r -> "auto".equals(r.getGrantType())).count());
        stats.put("manualGrants", allRecords.stream().filter(r -> "manual".equals(r.getGrantType())).count());

        return success(stats);
    }
}
