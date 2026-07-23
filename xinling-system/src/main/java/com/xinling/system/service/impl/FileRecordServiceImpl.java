package com.xinling.system.service.impl;

import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.xinling.common.exception.ServiceException;
import com.xinling.common.utils.DateUtils;
import com.xinling.common.utils.ip.IpUtils;
import com.xinling.common.utils.SecurityUtils;
import com.xinling.common.utils.StringUtils;
import com.xinling.common.utils.uuid.IdUtils;
import com.xinling.system.domain.entity.file.FileRecord;
import com.xinling.system.domain.entity.file.FileStorageConfig;
import com.xinling.system.mapper.file.FileRecordMapper;
import com.xinling.system.mapper.file.FileStorageConfigMapper;
import com.xinling.system.service.IFileRecordService;
import com.xinling.system.service.file.strategy.FileStorageStrategy;

/**
 * 文件记录Service实现
 *
 * @author xinling
 */
@Service
public class FileRecordServiceImpl implements IFileRecordService
{
    @Autowired
    private FileStorageConfigMapper configMapper;

    @Autowired
    private FileRecordMapper fileRecordMapper;

    @Autowired
    private Map<String, FileStorageStrategy> strategyMap;

    @Override
    public FileRecord selectFileRecordById(Long fileId)
    {
        return fileRecordMapper.selectFileRecordById(fileId);
    }

    @Override
    public List<FileRecord> selectFileRecordList(FileRecord record)
    {
        return fileRecordMapper.selectFileRecordList(record);
    }

    @Override
    @Transactional
    public FileRecord uploadFile(MultipartFile file, String businessType, Long businessId)
    {
        if (file == null || file.isEmpty())
        {
            throw new ServiceException("上传文件不能为空");
        }

        FileStorageConfig config = configMapper.selectMasterConfig();
        if (config == null)
        {
            throw new ServiceException("未配置文件存储主配置，请先在【存储配置】中设置主配置");
        }

        if ("1".equals(config.getStatus()))
        {
            throw new ServiceException("当前主存储配置已停用");
        }

        validateFile(file, config);

        FileStorageStrategy strategy = strategyMap.get(config.getStorageType() + "Strategy");
        if (strategy == null)
        {
            throw new ServiceException("不支持的存储类型: " + config.getStorageType());
        }

        try
        {
            String fileName = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(fileName);
            String uuid = IdUtils.fastSimpleUUID();
            String path = (config.getBasePath() != null ? config.getBasePath() : "")
                    + DateUtils.datePath() + "/" + uuid + "." + extension;

            String fileUrl = strategy.upload(file, path, config);

            FileRecord record = new FileRecord();
            record.setFileUuid(uuid);
            record.setConfigId(config.getId());
            record.setFileName(fileName);
            record.setStoredName(uuid + "." + extension);
            record.setFilePath(path);
            record.setFileUrl(fileUrl);
            record.setFileSize(file.getSize());
            record.setFileType(file.getContentType());
            record.setFileExtension(extension);
            record.setFileHash(calculateHash(file));
            record.setStorageType(config.getStorageType());
            record.setBucketName(config.getBucketName());
            record.setObjectKey(path);
            record.setBusinessType(businessType);
            record.setBusinessId(businessId);
            record.setSourceType("UPLOAD");
            record.setIsPublic(0);
            record.setAccessLevel("PRIVATE");
            record.setUploaderId(SecurityUtils.getUserId());
            record.setUploaderName(SecurityUtils.getUsername());
            record.setUploaderIp(IpUtils.getIpAddr());
            record.setDownloadCount(0);
            record.setReferenceCount(0);
            record.setStatus("0");
            record.setTenantId(0L);
            record.setCreateTime(new Date());

            fileRecordMapper.insertFileRecord(record);
            return record;
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            ServiceException se = new ServiceException("文件上传失败: " + e.getMessage());
            se.initCause(e);
            throw se;
        }
    }

    @Override
    @Transactional
    public List<FileRecord> uploadFiles(MultipartFile[] files, String businessType, Long businessId)
    {
        List<FileRecord> records = new ArrayList<>();
        for (MultipartFile file : files)
        {
            records.add(uploadFile(file, businessType, businessId));
        }
        return records;
    }

    @Override
    public void downloadFile(Long fileId, HttpServletResponse response)
    {
        FileRecord record = fileRecordMapper.selectFileRecordById(fileId);
        if (record == null)
        {
            throw new ServiceException("文件记录不存在: " + fileId);
        }

        FileStorageConfig config = configMapper.selectFileStorageConfigById(record.getConfigId());
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        FileStorageStrategy strategy = strategyMap.get(record.getStorageType() + "Strategy");
        if (strategy == null)
        {
            // 本地文件下载兼容
            strategy = strategyMap.get("localStrategy");
        }

        if (strategy == null)
        {
            throw new ServiceException("不支持的存储类型: " + record.getStorageType());
        }

        try
        {
            byte[] data = strategy.download(record.getFilePath(), config);
            if (data == null)
            {
                // 提供更具体的错误信息
                throw new ServiceException("物理文件不存在或已被删除: " + record.getFilePath() + " (ID: " + fileId + ")");
            }

            response.setContentType(record.getFileType() != null
                    ? record.getFileType() : "application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + record.getFileName() + "\"");
            response.setContentLength(data.length);

            try (OutputStream os = response.getOutputStream())
            {
                os.write(data);
                os.flush();
            }

            fileRecordMapper.incrementDownloadCount(fileId);
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            ServiceException se = new ServiceException("文件下载失败: " + e.getMessage());
            se.initCause(e);
            throw se;
        }
    }

    @Override
    public int updateFileRecord(FileRecord record)
    {
        record.setUpdateTime(new Date());
        return fileRecordMapper.updateFileRecord(record);
    }

    @Override
    @Transactional
    public int deleteFileRecordByIds(Long[] fileIds)
    {
        int count = 0;
        for (Long fileId : fileIds)
        {
            FileRecord record = fileRecordMapper.selectFileRecordById(fileId);
            if (record == null || "1".equals(record.getStatus()))
            {
                continue;
            }

            // 尝试删除物理文件
            try
            {
                FileStorageConfig config = configMapper.selectFileStorageConfigById(record.getConfigId());
                if (config != null)
                {
                    FileStorageStrategy strategy = strategyMap.get(record.getStorageType() + "Strategy");
                    if (strategy != null)
                    {
                        strategy.delete(record.getFilePath(), config);
                    }
                }
            }
            catch (Exception e)
            {
                // 文件删除失败不影响记录删除
            }

            // 软删除：标记状态为已删除，记录删除人和删除时间
            record.setStatus("1");
            record.setDeleteBy(SecurityUtils.getUserId());
            record.setDeleteTime(new Date());
            fileRecordMapper.updateFileRecord(record);
            count++;
        }
        return count;
    }

    @Override
    public Map<String, Object> getFileStatistics()
    {
        Map<String, Object> result = new HashMap<>();

        FileRecord query = new FileRecord();
        query.setStatus("0");
        List<FileRecord> list = fileRecordMapper.selectFileRecordList(query);

        // 按存储类型分组统计
        Map<String, List<FileRecord>> groupedByType = list.stream()
                .collect(Collectors.groupingBy(r -> r.getStorageType() != null
                        ? r.getStorageType() : "unknown"));

        List<Map<String, Object>> details = new ArrayList<>();
        long totalSize = 0;
        int totalCount = 0;
        int totalDownloads = 0;

        for (Map.Entry<String, List<FileRecord>> entry : groupedByType.entrySet())
        {
            Map<String, Object> detail = new HashMap<>();
            long size = entry.getValue().stream().mapToLong(r -> r.getFileSize() != null ? r.getFileSize() : 0).sum();
            int downloads = entry.getValue().stream().mapToInt(r -> r.getDownloadCount() != null ? r.getDownloadCount() : 0).sum();
            detail.put("storageType", entry.getKey()); // 存储类型
            detail.put("fileCount", entry.getValue().size()); // 文件数量
            detail.put("totalSize", size); // 文件大小
            detail.put("totalDownloads", downloads); // 下载次数
            details.add(detail);

            totalSize += size;
            totalCount += entry.getValue().size();
            totalDownloads += downloads;
        }

        // 计算今日上传数量
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date todayStart = today.getTime();

        long todayUploads = list.stream()
                .filter(r -> r.getCreateTime() != null && r.getCreateTime().after(todayStart))
                .count();

        result.put("totalCount", totalCount); // 总文件数量
        result.put("totalSize", totalSize); //  总文件大小
        result.put("totalDownloads", totalDownloads); // 总下载次数
        result.put("details", details); // 上传统计详情
        result.put("todayUploads", todayUploads); // 今日上传数量
        return result;
    }

    @Override
    public Map<String, Object> getStorageTypeDistribution() {
        FileRecord query = new FileRecord();
        query.setStatus("0");
        List<FileRecord> allRecords = fileRecordMapper.selectFileRecordList(query);

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> distribution = new ArrayList<>();

        // 按存储类型分组
        Map<String, List<FileRecord>> groupedByStorageType = allRecords.stream()
                .collect(Collectors.groupingBy(
                    r -> r.getStorageType() != null ? r.getStorageType() : "unknown"
                ));

        for (Map.Entry<String, List<FileRecord>> entry : groupedByStorageType.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("count", entry.getValue().size());
            item.put("size", entry.getValue().stream()
                    .filter(r -> r.getFileSize() != null)
                    .mapToLong(r -> r.getFileSize())
                    .sum());
            distribution.add(item);
        }

        result.put("distribution", distribution);
        return result;
    }

    @Override
    public Map<String, Object> getFileTypeDistribution() {
        FileRecord query = new FileRecord();
        query.setStatus("0");
        List<FileRecord> allRecords = fileRecordMapper.selectFileRecordList(query);

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> distribution = new ArrayList<>();

        // 按文件扩展名分组
        Map<String, List<FileRecord>> groupedByFileType = allRecords.stream()
                .collect(Collectors.groupingBy(
                    r -> r.getFileExtension() != null ? r.getFileExtension() : "unknown"
                ));

        // 取前10个最常见的文件类型
        List<Map.Entry<String, List<FileRecord>>> sortedEntries = groupedByFileType.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .limit(10)
                .collect(Collectors.toList());

        for (Map.Entry<String, List<FileRecord>> entry : sortedEntries) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("count", entry.getValue().size());
            item.put("size", entry.getValue().stream()
                    .filter(r -> r.getFileSize() != null)
                    .mapToLong(r -> r.getFileSize())
                    .sum());
            distribution.add(item);
        }

        result.put("distribution", distribution);
        return result;
    }

    @Override
    public Map<String, Object> getUploadTrend(int days, String startDateStr, String endDateStr) {
        FileRecord query = new FileRecord();
        query.setStatus("0");
        if (startDateStr != null && endDateStr != null) {
            query.getParams().put("beginTime", startDateStr);
            query.getParams().put("endTime", endDateStr);
        }
        List<FileRecord> allRecords = fileRecordMapper.selectFileRecordList(query);

        // 生成日期范围
        List<Date> dateRange = generateDateRange(days, startDateStr, endDateStr);
        Map<String, Long> dailyUploadCounts = new HashMap<>();
        Map<String, Long> dailyStorageSizes = new HashMap<>();

        // 初始化所有日期的计数为0
        for (Date date : dateRange) {
            String dateStr = new java.sql.Date(date.getTime()).toString();
            dailyUploadCounts.put(dateStr, 0L);
            dailyStorageSizes.put(dateStr, 0L);
        }

        // 统计每天的数据
        for (FileRecord record : allRecords) {
            if (record.getCreateTime() != null) {
                String recordDate = new java.sql.Date(record.getCreateTime().getTime()).toString();

                if (dailyUploadCounts.containsKey(recordDate)) {
                    dailyUploadCounts.put(recordDate, dailyUploadCounts.get(recordDate) + 1);
                    if (record.getFileSize() != null) {
                        dailyStorageSizes.put(recordDate,
                            dailyStorageSizes.get(recordDate) + record.getFileSize());
                    }
                }
            }
        }

        // 构建时间序列数据
        List<String> dates = dateRange.stream()
                .map(date -> new java.sql.Date(date.getTime()).toString())
                .collect(Collectors.toList());

        List<Long> counts = dates.stream()
                .map(date -> dailyUploadCounts.get(date))
                .collect(Collectors.toList());

        List<Long> sizes = dates.stream()
                .map(date -> dailyStorageSizes.get(date))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("uploadCounts", counts);
        result.put("storageSizes", sizes);

        return result;
    }

    @Override
    public Map<String, Object> getStorageTrend(int days, String startDateStr, String endDateStr) {
        FileRecord query = new FileRecord();
        query.setStatus("0");
        if (startDateStr != null && endDateStr != null) {
            query.getParams().put("beginTime", startDateStr);
            query.getParams().put("endTime", endDateStr);
        }
        List<FileRecord> allRecords = fileRecordMapper.selectFileRecordList(query);

        // 按时间排序文件记录
        List<FileRecord> sortedRecords = allRecords.stream()
                .filter(r -> r.getCreateTime() != null && r.getFileSize() != null)
                .sorted(Comparator.comparing(FileRecord::getCreateTime))
                .collect(Collectors.toList());

        // 生成日期范围
        List<Date> dateRange = generateDateRange(days, startDateStr, endDateStr);
        Map<String, Long> cumulativeSizes = new HashMap<>();

        // 初始化所有日期的累计存储为0
        for (Date date : dateRange) {
            String dateStr = new java.sql.Date(date.getTime()).toString();
            cumulativeSizes.put(dateStr, 0L);
        }

        // 计算每天的累计存储
        long currentTotalSize = 0;
        int recordIndex = 0;

        for (Date date : dateRange) {
            String dateStr = new java.sql.Date(date.getTime()).toString();

            // 累加到当前日期的所有文件大小
            while (recordIndex < sortedRecords.size()) {
                FileRecord record = sortedRecords.get(recordIndex);
                String recordDate = new java.sql.Date(record.getCreateTime().getTime()).toString();

                if (recordDate.compareTo(dateStr) <= 0) {
                    currentTotalSize += record.getFileSize();
                    recordIndex++;
                } else {
                    break;
                }
            }

            cumulativeSizes.put(dateStr, currentTotalSize);
        }

        // 构建时间序列数据
        List<String> dates = dateRange.stream()
                .map(date -> new java.sql.Date(date.getTime()).toString())
                .collect(Collectors.toList());

        List<Long> sizes = dates.stream()
                .map(date -> cumulativeSizes.get(date))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("cumulativeSizes", sizes);

        return result;
    }

    /**
     * 生成日期范围
     */
    private List<Date> generateDateRange(int days, String startDateStr, String endDateStr) {
        List<Date> dateRange = new ArrayList<>();

        Date startDate, endDate;
        if (startDateStr != null && endDateStr != null) {
            startDate = java.sql.Date.valueOf(startDateStr);
            endDate = java.sql.Date.valueOf(endDateStr);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, -days + 1);
            startDate = cal.getTime();
            endDate = new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        while (cal.getTime().compareTo(endDate) <= 0) {
            dateRange.add(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dateRange;
    }

    @Override
    public List<FileRecord> selectFileRecordByBusiness(String businessType, Long businessId)
    {
        FileRecord record = new FileRecord();
        record.setBusinessType(businessType);
        record.setBusinessId(businessId);
        return fileRecordMapper.selectFileRecordByBusiness(record);
    }

    private void validateFile(MultipartFile file, FileStorageConfig config)
    {
        // 检查文件大小
        if (config.getMaxFileSize() != null && file.getSize() > config.getMaxFileSize())
        {
            throw new ServiceException("文件大小超过限制: " + (config.getMaxFileSize() / 1024 / 1024) + " MB");
        }

        // 检查文件扩展名
        if (StringUtils.isNotEmpty(config.getAllowedExtensions()))
        {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String[] allowedExts = config.getAllowedExtensions().split(",");
            boolean found = false;
            for (String ext : allowedExts)
            {
                if (ext.trim().equalsIgnoreCase(extension))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                throw new ServiceException("不支持的文件类型: " + extension + "，允许的类型: " + config.getAllowedExtensions());
            }
        }
    }

    private String calculateHash(MultipartFile file)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(file.getBytes());
            return Hex.encodeHexString(digest);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
