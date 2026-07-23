package com.xinling.admin.controller.file;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import com.xinling.common.utils.poi.ExcelUtil;
import com.xinling.system.domain.entity.file.FileStorageConfig;
import com.xinling.system.service.IFileStorageConfigService;

/**
 * 文件存储配置Controller
 *
 * @author xinling
 */
@RestController
@RequestMapping("/file/config")
@Tag(name = "文件存储配置", description = "文件存储配置")
public class FileStorageConfigController extends BaseController
{
    @Autowired
    private IFileStorageConfigService fileStorageConfigService;

    /**
     * 查询文件存储配置列表
     */
    @PreAuthorize("@ss.hasPermi('file:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(FileStorageConfig config)
    {
        startPage();
        List<FileStorageConfig> list = fileStorageConfigService.selectFileStorageConfigList(config);
        return getDataTable(list);
    }

    /**
     * 导出文件存储配置列表
     */
    @PreAuthorize("@ss.hasPermi('file:config:export')")
    @Log(title = "文件存储配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FileStorageConfig config)
    {
        List<FileStorageConfig> list = fileStorageConfigService.selectFileStorageConfigList(config);
        ExcelUtil<FileStorageConfig> util = new ExcelUtil<FileStorageConfig>(FileStorageConfig.class);
        util.exportExcel(response, list, "文件存储配置数据");
    }

    /**
     * 获取文件存储配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('file:config:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(fileStorageConfigService.selectFileStorageConfigById(id));
    }

    /**
     * 获取主配置信息
     */
    @GetMapping(value = "/master")
    public AjaxResult getMasterConfig()
    {
        return success(fileStorageConfigService.selectMasterConfig());
    }

    /**
     * 新增文件存储配置
     */
    @PreAuthorize("@ss.hasPermi('file:config:add')")
    @Log(title = "文件存储配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FileStorageConfig config)
    {
        if ("1".equals(config.getIsMaster()))
        {
            FileStorageConfig masterConfig = fileStorageConfigService.selectMasterConfig();
            if (masterConfig != null)
            {
                return error("只能存在一个主配置，请先取消当前主配置");
            }
        }
        config.setCreateBy(getUsername());
        return toAjax(fileStorageConfigService.insertFileStorageConfig(config));
    }

    /**
     * 修改文件存储配置
     */
    @PreAuthorize("@ss.hasPermi('file:config:edit')")
    @Log(title = "文件存储配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FileStorageConfig config)
    {
        if ("1".equals(config.getIsMaster()))
        {
            FileStorageConfig masterConfig = fileStorageConfigService.selectMasterConfig();
            if (masterConfig != null && !masterConfig.getId().equals(config.getId()))
            {
                return error("只能存在一个主配置，请先取消当前主配置");
            }
        }
        config.setUpdateBy(getUsername());
        return toAjax(fileStorageConfigService.updateFileStorageConfig(config));
    }

    /**
     * 删除文件存储配置
     */
    @PreAuthorize("@ss.hasPermi('file:config:remove')")
    @Log(title = "文件存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(fileStorageConfigService.deleteFileStorageConfigByIds(ids));
    }

    /**
     * 设置主配置
     */
    @PreAuthorize("@ss.hasPermi('file:config:edit')")
    @Log(title = "文件存储配置", businessType = BusinessType.UPDATE)
    @PutMapping("/setMaster/{id}")
    public AjaxResult setMaster(@PathVariable Long id)
    {
        return toAjax(fileStorageConfigService.setMasterConfig(id));
    }

    /**
     * 测试配置连接
     */
    @PreAuthorize("@ss.hasPermi('file:config:query')")
    @PostMapping("/test/{id}")
    public AjaxResult testConnection(@PathVariable Long id)
    {
        boolean result = fileStorageConfigService.testConnection(id);
        return result ? success("连接测试成功") : error("连接测试失败");
    }
}
