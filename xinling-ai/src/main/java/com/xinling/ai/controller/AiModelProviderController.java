package com.xinling.ai.controller;

import com.xinling.ai.domain.config.AiModelProvider;
import com.xinling.ai.service.IAiModelProviderService;
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
 * AI模型提供商Controller
 *
 * @author SuXia
 * @date 2025/01/22
 */
@RestController
@RequestMapping("/ai/provider")
public class AiModelProviderController extends BaseController {

    @Autowired
    private IAiModelProviderService aiModelProviderService;

    /**
     * 查询AI模型提供商列表
     */
    @PreAuthorize("@ss.hasPermi('ai:provider:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiModelProvider aiModelProvider) {
        startPage();
        List<AiModelProvider> list = aiModelProviderService.selectAiModelProviderList(aiModelProvider);
        return getDataTable(list);
    }

    /**
     * 导出AI模型提供商列表
     */
    @PreAuthorize("@ss.hasPermi('ai:provider:export')")
    @Log(title = "AI模型提供商", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiModelProvider aiModelProvider) {
        List<AiModelProvider> list = aiModelProviderService.selectAiModelProviderList(aiModelProvider);
        ExcelUtil<AiModelProvider> util = new ExcelUtil<>(AiModelProvider.class);
        util.exportExcel(response, list, "AI模型提供商数据");
    }

    /**
     * 获取AI模型提供商详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:provider:query')")
    @GetMapping(value = "/{providerId}")
    public AjaxResult getInfo(@PathVariable("providerId") Long providerId) {
        return success(aiModelProviderService.selectAiModelProviderById(providerId));
    }

    /**
     * 新增AI模型提供商
     */
    @PreAuthorize("@ss.hasPermi('ai:provider:add')")
    @Log(title = "AI模型提供商", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiModelProvider aiModelProvider) {
        if (!aiModelProviderService.checkProviderCodeUnique(aiModelProvider)) {
            return error("新增AI模型提供商'" + aiModelProvider.getProviderName() + "'失败，提供商编码已存在");
        }
        aiModelProvider.setCreateBy(getUsername());
        return toAjax(aiModelProviderService.insertAiModelProvider(aiModelProvider));
    }

    /**
     * 修改AI模型提供商
     */
    @PreAuthorize("@ss.hasPermi('ai:provider:edit')")
    @Log(title = "AI模型提供商", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiModelProvider aiModelProvider) {
        if (!aiModelProviderService.checkProviderCodeUnique(aiModelProvider)) {
            return error("修改AI模型提供商'" + aiModelProvider.getProviderName() + "'失败，提供商编码已存在");
        }
        aiModelProvider.setUpdateBy(getUsername());
        return toAjax(aiModelProviderService.updateAiModelProvider(aiModelProvider));
    }

    /**
     * 删除AI模型提供商
     */
    @PreAuthorize("@ss.hasPermi('ai:provider:remove')")
    @Log(title = "AI模型提供商", businessType = BusinessType.DELETE)
    @DeleteMapping("/{providerIds}")
    public AjaxResult remove(@PathVariable Long[] providerIds) {
        return toAjax(aiModelProviderService.deleteAiModelProviderByIds(providerIds));
    }

    /**
     * 获取所有启用的提供商
     */
    @GetMapping("/enabled")
    public AjaxResult getEnabledProviders() {
        return success(aiModelProviderService.selectEnabledProviders());
    }
}
