package com.xinling.admin.controller.app;

import com.xinling.app.domain.entity.Coupon;
import com.xinling.app.mapper.CouponMapper;
import com.xinling.app.mapper.UserCouponMapper;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券管理控制器
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/coupon")
@Tag(name = "优惠券管理", description = "优惠券管理接口")
public class CouponController extends BaseController {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    public CouponController(CouponMapper couponMapper, UserCouponMapper userCouponMapper) {
        this.couponMapper = couponMapper;
        this.userCouponMapper = userCouponMapper;
    }

    /**
     * 查询优惠券列表
     */
    @PreAuthorize("@ss.hasPermi('app:coupon:list')")
    @GetMapping("/list")
    public TableDataInfo list(Coupon coupon) {
        startPage();
        List<Coupon> list = couponMapper.selectList(coupon.getStatus());
        return getDataTable(list);
    }

    /**
     * 新增优惠券
     */
    @PreAuthorize("@ss.hasPermi('app:coupon:create')")
    @Log(title = "优惠券管理", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestBody Coupon coupon) {
        coupon.setCreateTime(new Date());
        coupon.setUpdateTime(new Date());
        return toAjax(couponMapper.insert(coupon));
    }

    /**
     * 修改优惠券
     */
    @PreAuthorize("@ss.hasPermi('app:coupon:update')")
    @Log(title = "优惠券管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update/{id}")
    public AjaxResult update(@PathVariable Long id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        coupon.setUpdateTime(new Date());
        return toAjax(couponMapper.updateById(coupon));
    }

    /**
     * 发放优惠券（占位，具体发放逻辑需实现）
     */
    @PreAuthorize("@ss.hasPermi('app:coupon:grant')")
    @Log(title = "优惠券管理", businessType = BusinessType.GRANT)
    @PostMapping("/grant/{id}")
    public AjaxResult grant(@PathVariable Long id, @RequestBody List<Long> userIds) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            return error("优惠券不存在");
        }
        // TODO: 批量发放优惠券给指定用户
        return success("发放任务已创建，共 " + (userIds != null ? userIds.size() : 0) + " 个用户");
    }

    /**
     * 优惠券使用统计
     */
    @PreAuthorize("@ss.hasPermi('app:coupon:statistics')")
    @GetMapping("/statistics/{id}")
    public AjaxResult statistics(@PathVariable Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            return error("优惠券不存在");
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("coupon", coupon);
        stats.put("totalCount", coupon.getTotalCount());
        stats.put("usedCount", coupon.getUsedCount());
        return success(stats);
    }
}
