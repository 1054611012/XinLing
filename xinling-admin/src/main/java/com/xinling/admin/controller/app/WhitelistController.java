package com.xinling.admin.controller.app;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xinling.app.domain.entity.Whitelist;
import com.xinling.app.service.IWhitelistService;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;

/**
 * 白名单管理
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/whitelist")
@Tag(name = "白名单管理", description = "白名单管理")
public class WhitelistController extends BaseController {

    private final IWhitelistService whitelistService;

    public WhitelistController(IWhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    /**
     * 查询白名单列表
     */
    @PreAuthorize("@ss.hasPermi('app:whitelist:list')")
    @GetMapping("/list")
    public TableDataInfo list(Whitelist whitelist) {
        startPage();
        List<Whitelist> list = whitelistService.selectWhitelistList(whitelist);
        return getDataTable(list);
    }

    /**
     * 新增白名单
     */
    @PreAuthorize("@ss.hasPermi('app:whitelist:add')")
    @Log(title = "白名单管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody Whitelist whitelist) {
        return toAjax(whitelistService.insertWhitelist(whitelist));
    }

    /**
     * 删除白名单
     */
    @PreAuthorize("@ss.hasPermi('app:whitelist:remove')")
    @Log(title = "白名单管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(whitelistService.deleteById(id));
    }

    /**
     * 修改白名单状态
     */
    @PreAuthorize("@ss.hasPermi('app:whitelist:edit')")
    @Log(title = "白名单管理", businessType = BusinessType.UPDATE)
    @PostMapping("/updateStatus/{id}")
    public AjaxResult updateStatus(@PathVariable Long id, @RequestBody Whitelist whitelist) {
        return toAjax(whitelistService.updateStatus(id, whitelist.getStatus()));
    }
}
