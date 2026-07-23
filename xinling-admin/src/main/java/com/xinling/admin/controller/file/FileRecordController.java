package com.xinling.admin.controller.file;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import com.xinling.common.utils.poi.ExcelUtil;
import com.xinling.system.domain.entity.file.FileRecord;
import com.xinling.system.service.IFileRecordService;

/**
 * 文件记录Controller
 *
 * @author xinling
 */
@RestController
@RequestMapping("/file/record")
@Tag(name = "文件记录", description = "文件记录")
public class FileRecordController extends BaseController
{
    @Autowired
    private IFileRecordService fileRecordService;

    /**
     * 查询文件记录列表
     */
    @PreAuthorize("@ss.hasPermi('file:record:list')")
    @GetMapping("/list")
    @Operation(summary = "查询文件记录列表" , description = "查询文件记录列表")
    public TableDataInfo list(FileRecord record)
    {
        startPage();
        List<FileRecord> list = fileRecordService.selectFileRecordList(record);
        return getDataTable(list);
    }

    /**
     * 导出文件记录列表
     */
    @PreAuthorize("@ss.hasPermi('file:record:export')")
    @Log(title = "文件记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FileRecord record)
    {
        List<FileRecord> list = fileRecordService.selectFileRecordList(record);
        ExcelUtil<FileRecord> util = new ExcelUtil<FileRecord>(FileRecord.class);
        util.exportExcel(response, list, "文件记录数据");
    }

    /**
     * 获取文件记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('file:record:query')")
    @GetMapping(value = "/{fileId}")
    public AjaxResult getInfo(@PathVariable("fileId") Long fileId)
    {
        return success(fileRecordService.selectFileRecordById(fileId));
    }

    /**
     * 上传文件
     */
    @PreAuthorize("@ss.hasPermi('file:record:add')")
    @Log(title = "文件上传", businessType = BusinessType.INSERT)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件" , description = "上传文件")
    public AjaxResult upload(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "businessType", required = false) String businessType,
                             @RequestParam(value = "businessId", required = false) Long businessId)
    {
        try
        {
            FileRecord record = fileRecordService.uploadFile(file, businessType, businessId);
            return success(record);
        }
        catch (Exception e)
        {
            return error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传文件
     */
    @PreAuthorize("@ss.hasPermi('file:record:add')")
    @Log(title = "文件批量上传", businessType = BusinessType.INSERT)
    @PostMapping(value = "/uploadBatch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "批量上传文件" , description = "批量上传文件")
    public AjaxResult uploadBatch(@RequestParam("files") MultipartFile[] files,
                                   @RequestParam(value = "businessType", required = false) String businessType,
                                   @RequestParam(value = "businessId", required = false) Long businessId)
    {
        try
        {
            List<FileRecord> records = fileRecordService.uploadFiles(files, businessType, businessId);
            return success(records);
        }
        catch (Exception e)
        {
            return error("文件批量上传失败：" + e.getMessage());
        }
    }

    /**
     * 下载文件
     */
    @PreAuthorize("@ss.hasPermi('file:record:download')")
    @Log(title = "文件下载", businessType = BusinessType.OTHER)
    @GetMapping("/download/{fileId}")
    @Operation(summary = "下载文件" , description = "下载文件")
    public void download(@PathVariable("fileId") Long fileId, HttpServletResponse response)
    {
        fileRecordService.downloadFile(fileId, response);
    }

    /**
     * 修改文件记录
     */
    @PreAuthorize("@ss.hasPermi('file:record:edit')")
    @Log(title = "文件记录", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改文件记录" , description = "修改文件记录")
    public AjaxResult edit(@Validated @RequestBody FileRecord record)
    {
        record.setUpdateBy(getUsername());
        return toAjax(fileRecordService.updateFileRecord(record));
    }

    /**
     * 删除文件记录
     */
    @PreAuthorize("@ss.hasPermi('file:record:remove')")
    @Log(title = "文件记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fileIds}")
    @Operation(summary = "删除文件记录" , description = "删除文件记录")
    public AjaxResult remove(@PathVariable Long[] fileIds)
    {
        return toAjax(fileRecordService.deleteFileRecordByIds(fileIds));
    }

    /**
     * 获取文件统计信息
     */
    @PreAuthorize("@ss.hasPermi('file:record:list')")
    @GetMapping("/statistics")
    @Operation(summary = "获取文件统计信息" , description = "获取文件统计信息")
    public AjaxResult getStatistics()
    {
        return success(fileRecordService.getFileStatistics());
    }

    /**
     * 根据业务类型查询文件列表
     */
    @PreAuthorize("@ss.hasPermi('file:record:list')")
    @GetMapping("/business/{businessType}/{businessId}")
    @Operation(summary = "根据业务类型查询文件列表" , description = "根据业务类型查询文件列表")
    public TableDataInfo listByBusiness(@PathVariable String businessType, @PathVariable Long businessId)
    {
        startPage();
        List<FileRecord> list = fileRecordService.selectFileRecordByBusiness(businessType, businessId);
        return getDataTable(list);
    }
}
