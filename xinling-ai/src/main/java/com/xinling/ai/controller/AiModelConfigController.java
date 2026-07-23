package com.xinling.ai.controller;

import com.xinling.ai.domain.config.AiModelConfig;
import com.xinling.ai.service.IAiModelConfigService;
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
 * AI模型配置Controller
 *
 * @author SuXia
 * @date 2025/01/22
 */
@RestController
@RequestMapping("/ai/model")
public class AiModelConfigController extends BaseController {

    @Autowired
    private IAiModelConfigService aiModelConfigService;

    /**
     * 查询AI模型配置列表
     */
    @PreAuthorize("@ss.hasPermi('ai:model:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiModelConfig aiModelConfig) {
        startPage();
        List<AiModelConfig> list = aiModelConfigService.selectAiModelConfigList(aiModelConfig);
        return getDataTable(list);
    }

    /**
     * 导出AI模型配置列表
     */
    @PreAuthorize("@ss.hasPermi('ai:model:export')")
    @Log(title = "AI模型配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiModelConfig aiModelConfig) {
        List<AiModelConfig> list = aiModelConfigService.selectAiModelConfigList(aiModelConfig);
        ExcelUtil<AiModelConfig> util = new ExcelUtil<>(AiModelConfig.class);
        util.exportExcel(response, list, "AI模型配置数据");
    }

    /**
     * 获取AI模型配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:model:query')")
    @GetMapping(value = "/{modelId}")
    public AjaxResult getInfo(@PathVariable("modelId") Long modelId) {
        return success(aiModelConfigService.selectAiModelConfigById(modelId));
    }

    /**
     * 新增AI模型配置
     */
    @PreAuthorize("@ss.hasPermi('ai:model:add')")
    @Log(title = "AI模型配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiModelConfig aiModelConfig) {
        if (!aiModelConfigService.checkModelCodeUnique(aiModelConfig)) {
            return error("新增AI模型配置'" + aiModelConfig.getModelName() + "'失败，模型编码已存在");
        }
        aiModelConfig.setCreateBy(getUsername());
        return toAjax(aiModelConfigService.insertAiModelConfig(aiModelConfig));
    }

    /**
     * 修改AI模型配置
     */
    @PreAuthorize("@ss.hasPermi('ai:model:edit')")
    @Log(title = "AI模型配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiModelConfig aiModelConfig) {
        if (!aiModelConfigService.checkModelCodeUnique(aiModelConfig)) {
            return error("修改AI模型配置'" + aiModelConfig.getModelName() + "'失败，模型编码已存在");
        }
        aiModelConfig.setUpdateBy(getUsername());
        return toAjax(aiModelConfigService.updateAiModelConfig(aiModelConfig));
    }

    /**
     * 删除AI模型配置
     */
    @PreAuthorize("@ss.hasPermi('ai:model:remove')")
    @Log(title = "AI模型配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{modelIds}")
    public AjaxResult remove(@PathVariable Long[] modelIds) {
        return toAjax(aiModelConfigService.deleteAiModelConfigByIds(modelIds));
    }

    /**
     * 设置默认模型
     */
    @PreAuthorize("@ss.hasPermi('ai:model:edit')")
    @Log(title = "设置默认模型", businessType = BusinessType.UPDATE)
    @PutMapping("/setDefault/{modelId}")
    public AjaxResult setDefault(@PathVariable Long modelId) {
        return toAjax(aiModelConfigService.setDefaultModel(modelId));
    }

    /**
     * 取消默认模型
     */
    @PreAuthorize("@ss.hasPermi('ai:model:edit')")
    @Log(title = "取消默认模型", businessType = BusinessType.UPDATE)
    @PutMapping("/cancelDefault/{modelId}")
    public AjaxResult cancelDefault(@PathVariable Long modelId) {
        return toAjax(aiModelConfigService.cancelDefaultModel(modelId));
    }

    /**
     * 获取所有启用的对话模型
     */
    @GetMapping("/chatModels")
    public AjaxResult getChatModels() {
        return success(aiModelConfigService.selectEnabledChatModels());
    }

    /**
     * 获取所有启用的嵌入模型
     */
    @GetMapping("/embeddingModels")
    public AjaxResult getEmbeddingModels() {
        return success(aiModelConfigService.selectEnabledEmbeddingModels());
    }

    /**
     * 测试模型连接是否可用
     * 会对模型发送测试请求，验证模型是否能正常响应
     */
    @PreAuthorize("@ss.hasPermi('ai:model:edit')")
    @Log(title = "测试模型连接", businessType = BusinessType.OTHER)
    @PostMapping("/test/{modelId}")
    public AjaxResult testModel(@PathVariable Long modelId) {
        String result = aiModelConfigService.testModel(modelId);
        if (result.startsWith("测试成功")) {
            return success(result);
        } else {
            return error(result);
        }
    }
}
