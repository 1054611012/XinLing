package com.xinling.ai.controller;

import com.xinling.ai.domain.config.AiPrompt;
import com.xinling.ai.service.IAiPromptService;
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
 * AI提示词Controller
 *
 * @author SuXia
 */
@RestController
@RequestMapping("/ai/prompt")
public class AiPromptController extends BaseController {

    @Autowired
    private IAiPromptService aiPromptService;

    /**
     * 查询AI提示词列表
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiPrompt aiPrompt) {
        startPage();
        List<AiPrompt> list = aiPromptService.selectAiPromptList(aiPrompt);
        return getDataTable(list);
    }

    /**
     * 导出AI提示词列表
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:export')")
    @Log(title = "AI提示词", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiPrompt aiPrompt) {
        List<AiPrompt> list = aiPromptService.selectAiPromptList(aiPrompt);
        ExcelUtil<AiPrompt> util = new ExcelUtil<>(AiPrompt.class);
        util.exportExcel(response, list, "AI提示词数据");
    }

    /**
     * 获取AI提示词详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:query')")
    @GetMapping(value = "/{promptId}")
    public AjaxResult getInfo(@PathVariable("promptId") Long promptId) {
        return success(aiPromptService.selectAiPromptById(promptId));
    }

    /**
     * 新增AI提示词
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:add')")
    @Log(title = "AI提示词", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiPrompt aiPrompt) {
        if (!aiPromptService.checkPromptNameUnique(aiPrompt)) {
            return error("新增提示词'" + aiPrompt.getPromptName() + "'失败，提示词名称已存在");
        }
        aiPrompt.setCreateBy(getUsername());
        return toAjax(aiPromptService.insertAiPrompt(aiPrompt));
    }

    /**
     * 修改AI提示词
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:edit')")
    @Log(title = "AI提示词", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiPrompt aiPrompt) {
        if (!aiPromptService.checkPromptNameUnique(aiPrompt)) {
            return error("修改提示词'" + aiPrompt.getPromptName() + "'失败，提示词名称已存在");
        }
        aiPrompt.setUpdateBy(getUsername());
        return toAjax(aiPromptService.updateAiPrompt(aiPrompt));
    }

    /**
     * 删除AI提示词
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:remove')")
    @Log(title = "AI提示词", businessType = BusinessType.DELETE)
    @DeleteMapping("/{promptIds}")
    public AjaxResult remove(@PathVariable Long[] promptIds) {
        return toAjax(aiPromptService.deleteAiPromptByIds(promptIds));
    }

    /**
     * 获取所有启用的提示词列表（供会话配置页面选择）
     */
    @GetMapping("/enabledList")
    public AjaxResult enabledList() {
        AiPrompt query = new AiPrompt();
        query.setStatus("0");
        List<AiPrompt> list = aiPromptService.selectAiPromptList(query);
        return success(list);
    }

    /**
     * 刷新提示词缓存（修改提示词后，调用此接口立即生效）
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:edit')")
    @Log(title = "刷新提示词缓存", businessType = BusinessType.UPDATE)
    @PostMapping("/refreshCache")
    public AjaxResult refreshCache() {
        aiPromptService.clearAllCache();
        return success("提示词缓存已刷新");
    }
}
