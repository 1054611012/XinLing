package com.xinling.system.service;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import com.xinling.system.domain.entity.file.FileRecord;

/**
 * 文件记录Service接口
 *
 * @author xinling
 */
public interface IFileRecordService
{
    /**
     * 查询文件记录
     */
    FileRecord selectFileRecordById(Long fileId);

    /**
     * 查询文件记录列表
     */
    List<FileRecord> selectFileRecordList(FileRecord record);

    /**
     * 上传文件
     */
    FileRecord uploadFile(MultipartFile file, String businessType, Long businessId);

    /**
     * 批量上传文件
     */
    List<FileRecord> uploadFiles(MultipartFile[] files, String businessType, Long businessId);

    /**
     * 下载文件
     */
    void downloadFile(Long fileId, HttpServletResponse response);

    /**
     * 修改文件记录
     */
    int updateFileRecord(FileRecord record);

    /**
     * 批量删除文件记录
     */
    int deleteFileRecordByIds(Long[] fileIds);

    /**
     * 获取文件统计信息
     */
    Map<String, Object> getFileStatistics();

    /**
     * 获取存储类型分布
     */
    Map<String, Object> getStorageTypeDistribution();

    /**
     * 获取文件类型分布
     */
    Map<String, Object> getFileTypeDistribution();

    /**
     * 获取上传趋势数据
     */
    Map<String, Object> getUploadTrend(int days, String startDate, String endDate);

    /**
     * 获取存储趋势数据
     */
    Map<String, Object> getStorageTrend(int days, String startDate, String endDate);

    /**
     * 根据业务类型查询文件列表
     */
    List<FileRecord> selectFileRecordByBusiness(String businessType, Long businessId);
}
