package com.xinling.admin.controller.app;

import com.xinling.app.domain.entity.AppUser;
import com.xinling.app.service.IAppUserService;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.domain.model.AppUserAdminVO;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import com.xinling.common.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * APP用户管理Controller
 * <p>
 * 管理后台 - 通过 IAppUserService 管理 APP 端用户。
 * 不直接操作数据库，未来 IAppUserService 可转为 Feign 接口。
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/user")
@Tag(name = "APP用户管理", description = "APP用户管理接口")
public class AppUserController extends BaseController {

    private final IAppUserService appUserService;

    public AppUserController(IAppUserService appUserService) {
        this.appUserService = appUserService;
    }

    // ==================== 查询 ====================

    /**
     * 查询用户列表
     */
    @PreAuthorize("@ss.hasPermi('app:user:list')")
    @GetMapping("/list")
    @Operation(summary = "查询用户列表", description = "查询用户列表")
   public TableDataInfo list(@RequestParam(required = false) String nickname,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) Integer status,
                               @RequestParam(required = false) Integer vipStatus,
                               @RequestParam(required = false) String beginTime,
                               @RequestParam(required = false) String endTime) {
        startPage();
        List<AppUserAdminVO> list = appUserService.selectAdminUserList(
                nickname, phone, status, vipStatus, beginTime, endTime);
        return getDataTable(list);
    }

    /**
     * 获取用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('app:user:query')")
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详细信息", description = "获取用户详细信息")
    public AjaxResult getInfo(@PathVariable Long id) {
        AppUserAdminVO user = appUserService.selectAdminUserDetail(id);
        if (user == null) {
            return error("用户不存在");
        }
        return success(user);
    }

    /**
     * 获取用户总数
     */
    @PreAuthorize("@ss.hasPermi('app:user:list')")
    @GetMapping("/count")
    @Operation(summary = "获取用户总数", description = "获取用户总数")
    public AjaxResult count() {
        return success(appUserService.countAll());
    }

    // ==================== 编辑 ====================

    /**
     * 修改用户信息
     */
    @PreAuthorize("@ss.hasPermi('app:user:edit')")
    @Log(title = "APP用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改用户信息", description = "修改用户信息")
    public AjaxResult edit(@RequestBody AppUserAdminVO user) {
        if (user.getId() == null) {
            return error("用户ID不能为空");
        }
        // 通过 AppUser 实体更新（仅更新允许的字段）
        AppUser update = new AppUser();
        update.setId(user.getId());
        update.setNickname(user.getNickname());
        update.setEmail(user.getEmail());
        update.setGender(user.getGender());
        update.setStatus(user.getStatus());
        return toAjax(appUserService.updateById(update));
    }

    /**
     * 修改用户状态（启用/禁用）
     */
    @PreAuthorize("@ss.hasPermi('app:user:edit')")
    @Log(title = "APP用户管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改用户状态", description = "修改用户状态")
    @PutMapping("/status")
    public AjaxResult changeStatus(@RequestParam Long userId, @RequestParam Integer status) {
        return toAjax(appUserService.updateUserStatus(userId, status));
    }

    // ==================== VIP 管理 ====================

    /**
     * 设置 VIP
     */
    @PreAuthorize("@ss.hasPermi('app:user:edit')")
    @Log(title = "APP用户管理-VIP", businessType = BusinessType.UPDATE)
    @Operation(summary = "设置 VIP", description = "设置 VIP")
    @PutMapping("/vip")
    public AjaxResult setVip(@RequestBody AppUserAdminVO user) {
        if (user.getId() == null || user.getVipStatus() == null) {
            return error("参数不完整");
        }
        return toAjax(appUserService.updateUserVip(user.getId(), user.getVipStatus(), user.getVipEndTime()));
    }

    /**
     * 延长 VIP
     */
    @PreAuthorize("@ss.hasPermi('app:user:edit')")
    @Log(title = "APP用户管理-VIP", businessType = BusinessType.UPDATE)
    @Operation(summary = "延长 VIP", description = "延长 VIP")
    @PostMapping("/vip/extend")
    public AjaxResult extendVip(@RequestParam Long userId, @RequestParam int days) {
        return toAjax(appUserService.extendUserVip(userId, days));
    }

    // ==================== 删除 ====================

    /**
     * 删除用户（软删除）
     */
    @PreAuthorize("@ss.hasPermi('app:user:remove')")
    @Log(title = "APP用户管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除用户", description = "删除用户")
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(appUserService.deleteByAdmin(id));
    }

    /**
     * 批量删除用户
     */
    @PreAuthorize("@ss.hasPermi('app:user:remove')")
    @Log(title = "APP用户管理", businessType = BusinessType.DELETE)
    @Operation(summary = "批量删除用户", description = "批量删除用户")
    @DeleteMapping("/batch/{ids}")
    public AjaxResult batchRemove(@PathVariable Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += appUserService.deleteByAdmin(id);
        }
        return toAjax(rows);
    }

    // ==================== 导出 ====================

    /**
     * 导出用户列表
     */
    @PreAuthorize("@ss.hasPermi('app:user:export')")
    @Log(title = "APP用户管理", businessType = BusinessType.EXPORT)
    @Operation(summary = "导出用户列表", description = "导出用户列表")
    @PostMapping("/export")
    public void export(HttpServletResponse response,
                        @RequestParam(required = false) String nickname,
                        @RequestParam(required = false) String phone,
                        @RequestParam(required = false) Integer status,
                        @RequestParam(required = false) Integer vipStatus,
                        @RequestParam(required = false) String beginTime,
                        @RequestParam(required = false) String endTime) {
        List<AppUserAdminVO> list = appUserService.selectAdminUserList(
                nickname, phone, status, vipStatus, beginTime, endTime);
        ExcelUtil<AppUserAdminVO> util = new ExcelUtil<>(AppUserAdminVO.class);
        util.exportExcel(response, list, "APP用户数据");
    }
}
