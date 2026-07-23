package com.xinling.admin.controller.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.system.service.IFileRecordService;

/**
 * 文件统计Controller
 *
 * @author xinling
 */
@RestController
@RequestMapping("/file/stat")
@Tag(name = "文件统计", description = "文件统计")
public class FileStatController extends BaseController {

    @Autowired
    private IFileRecordService fileRecordService;

    /**
     * 获取文件统计概览
     */
    @PreAuthorize("@ss.hasPermi('file:stat:overview')")
    @GetMapping("/overview")
    @Operation(summary = "获取文件统计概览" , description = "获取文件统计概览")
    public AjaxResult getFileStatistics() {
        return success(fileRecordService.getFileStatistics());
    }

    /**
     * 获取存储类型分布
     */
    @PreAuthorize("@ss.hasPermi('file:stat:storageType')")
    @GetMapping("/storageType")
    @Operation(summary = "获取存储类型分布" , description = "获取存储类型分布")
    public AjaxResult getStorageTypeDistribution() {
        return success(fileRecordService.getStorageTypeDistribution());
    }

    /**
     * 获取文件类型分布
     */
    @PreAuthorize("@ss.hasPermi('file:stat:fileType')")
    @GetMapping("/fileType")
    @Operation(summary = "获取文件类型分布" , description = "获取文件类型分布")
    public AjaxResult getFileTypeDistribution() {
        return success(fileRecordService.getFileTypeDistribution());
    }

    /**
     * 获取上传趋势
     */
    @PreAuthorize("@ss.hasPermi('file:stat:uploadTrend')")
    @GetMapping("/uploadTrend")
    @Operation(summary = "获取上传趋势" , description = "获取上传趋势")
    public AjaxResult getUploadTrend(
            @RequestParam(value = "days", defaultValue = "7") Integer days,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr) {
        return success(fileRecordService.getUploadTrend(days, startDateStr, endDateStr));
    }

    /**
     * 获取存储趋势
     */
    @PreAuthorize("@ss.hasPermi('file:stat:storageTrend')")
    @GetMapping("/storageTrend")
    @Operation(summary = "获取存储趋势" , description = "获取存储趋势")
    public AjaxResult getStorageTrend(
            @RequestParam(value = "days", defaultValue = "7") Integer days,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr) {
        return success(fileRecordService.getStorageTrend(days, startDateStr, endDateStr));
    }
}
