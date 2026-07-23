package com.xinling.admin.controller.app;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xinling.app.domain.entity.FocusRecord;
import com.xinling.app.service.IFocusService;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;

/**
 * 专注记录管理
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/focus")
@Tag(name = "专注记录管理", description = "专注记录管理接口")
public class FocusController extends BaseController {

    private final IFocusService focusService;

    public FocusController(IFocusService focusService) {
        this.focusService = focusService;
    }

    /**
     * 查询专注记录列表
     */
    @PreAuthorize("@ss.hasPermi('app:focus:list')")
    @GetMapping("/list")
    @Operation(summary = "查询专注记录列表", description = "查询专注记录列表")
    public TableDataInfo list(FocusRecord record) {
        startPage();
        List<FocusRecord> list = focusService.selectFocusList(record);
        return getDataTable(list);
    }

    /**
     * 获取专注记录详情
     */
    @PreAuthorize("@ss.hasPermi('app:focus:query')")
    @GetMapping("/{id}")
    @Operation(summary = "获取专注记录详情", description = "获取专注记录详情")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(focusService.selectById(id));
    }
}
