package com.xinling.ai.controller;

import com.xinling.ai.service.AiModelSwitchService;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AI模型管理API控制器
 * 提供模型切换、配置管理等运行时功能
 *
 * @author SuXia
 * @date 2025/01/22
 */
@RestController
@RequestMapping("/ai/management")
public class AiManagementController extends BaseController {
    
    @Autowired
    private AiModelSwitchService aiModelSwitchService;

    /**
     * 切换到指定模型
     */
    @PreAuthorize("@ss.hasPermi('ai:model:edit')")
    @Log(title = "切换AI模型", businessType = BusinessType.UPDATE)
    @PutMapping("/switchModel/{modelId}")
    public AjaxResult switchModel(@PathVariable Long modelId) {
        boolean success = aiModelSwitchService.switchToModel(modelId);
        if (success) {
            return success("模型切换成功");
        } else {
            return error("模型切换失败，请检查模型配置");
        }
    }

    /**
     * 切换到指定会话配置
     */
    @PreAuthorize("@ss.hasPermi('ai:session:edit')")
    @Log(title = "切换会话配置", businessType = BusinessType.UPDATE)
    @PutMapping("/switchSession/{configId}")
    public AjaxResult switchSession(@PathVariable Long configId) {
        boolean success = aiModelSwitchService.switchToSessionConfig(configId);
        if (success) {
            return success("会话配置切换成功");
        } else {
            return error("会话配置切换失败，请检查配置");
        }
    }

    /**
     * 使用默认配置
     */
    @PutMapping("/useDefault")
    public AjaxResult useDefaultConfig() {
        boolean success = aiModelSwitchService.useDefaultConfig();
        if (success) {
            return success("已切换到默认配置");
        } else {
            return error("切换默认配置失败");
        }
    }

    /**
     * 刷新所有模型缓存
     */
    @PreAuthorize("@ss.hasPermi('ai:model:edit')")
    @Log(title = "刷新AI模型缓存", businessType = BusinessType.UPDATE)
    @PostMapping("/refresh")
    public AjaxResult refreshModels() {
        aiModelSwitchService.refreshAllModels();
        return success("模型缓存已刷新");
    }

    /**
     * 获取当前使用的模型信息
     */
    @GetMapping("/currentModel")
    public AjaxResult getCurrentModel() {
        // 这里可以返回当前正在使用的模型信息
        return success("当前使用默认模型配置");
    }
}
