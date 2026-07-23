package com.xinling.ai.controller;

import com.xinling.ai.domain.config.AiSessionConfig;
import com.xinling.ai.service.IAiSessionConfigService;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import com.xinling.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * AI会话配置Controller
 *
 * @author SuXia
 * @date 2025/01/22
 */
@RestController
@RequestMapping("/ai/session")
public class AiSessionConfigController extends BaseController {

    @Autowired
    private IAiSessionConfigService aiSessionConfigService;

    /**
     * 查询AI会话配置列表
     */
    @PreAuthorize("@ss.hasPermi('ai:session:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiSessionConfig aiSessionConfig) {
        startPage();
        List<AiSessionConfig> list = aiSessionConfigService.selectAiSessionConfigList(aiSessionConfig);
        return getDataTable(list);
    }

    /**
     * 导出AI会话配置列表
     */
    @PreAuthorize("@ss.hasPermi('ai:session:export')")
    @Log(title = "AI会话配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiSessionConfig aiSessionConfig) {
        List<AiSessionConfig> list = aiSessionConfigService.selectAiSessionConfigList(aiSessionConfig);
        ExcelUtil<AiSessionConfig> util = new ExcelUtil<>(AiSessionConfig.class);
        util.exportExcel(response, list, "AI会话配置数据");
    }

    /**
     * 获取AI会话配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:session:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable("configId") Long configId) {
        return success(aiSessionConfigService.selectAiSessionConfigById(configId));
    }

    /**
     * 新增AI会话配置
     */
    @PreAuthorize("@ss.hasPermi('ai:session:add')")
    @Log(title = "AI会话配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiSessionConfig aiSessionConfig) {
        aiSessionConfig.setCreateBy(getUsername());
        return toAjax(aiSessionConfigService.insertAiSessionConfig(aiSessionConfig));
    }

    /**
     * 修改AI会话配置
     */
    @PreAuthorize("@ss.hasPermi('ai:session:edit')")
    @Log(title = "AI会话配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiSessionConfig aiSessionConfig) {
        aiSessionConfig.setUpdateBy(getUsername());
        return toAjax(aiSessionConfigService.updateAiSessionConfig(aiSessionConfig));
    }

    /**
     * 删除AI会话配置
     */
    @PreAuthorize("@ss.hasPermi('ai:session:remove')")
    @Log(title = "AI会话配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds) {
        return toAjax(aiSessionConfigService.deleteAiSessionConfigByIds(configIds));
    }

    /**
     * 设置默认配置
     */
    @PreAuthorize("@ss.hasPermi('ai:session:edit')")
    @Log(title = "设置默认配置", businessType = BusinessType.UPDATE)
    @PutMapping("/setDefault/{configId}")
    public AjaxResult setDefault(@PathVariable Long configId) {
        return toAjax(aiSessionConfigService.setDefaultConfig(configId));
    }

    /**
     * 获取默认会话配置
     */
    @GetMapping("/default")
    public AjaxResult getDefaultConfig() {
        return success(aiSessionConfigService.selectDefaultSessionConfig());
    }
}
