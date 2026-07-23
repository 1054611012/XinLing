package com.xinling.system.mapper.file;

import java.util.List;
import com.xinling.system.domain.entity.file.FileRecord;

/**
 * 文件记录Mapper接口
 *
 * @author xinling
 */
public interface FileRecordMapper
{
    /**
     * 查询文件记录
     */
    FileRecord selectFileRecordById(Long fileId);

    /**
     * 根据UUID查询文件记录
     */
    FileRecord selectFileRecordByUuid(String fileUuid);

    /**
     * 根据文件Hash查询文件记录（用于去重）
     */
    List<FileRecord> selectFileRecordByHash(String fileHash);

    /**
     * 查询文件记录列表
     */
    List<FileRecord> selectFileRecordList(FileRecord record);

    /**
     * 新增文件记录
     */
    int insertFileRecord(FileRecord record);

    /**
     * 修改文件记录
     */
    int updateFileRecord(FileRecord record);

    /**
     * 软删除文件记录
     */
    int deleteFileRecordById(Long fileId);

    /**
     * 批量软删除文件记录
     */
    int deleteFileRecordByIds(Long[] fileIds);

    /**
     * 根据业务类型查询文件列表
     */
    List<FileRecord> selectFileRecordByBusiness(FileRecord record);

    /**
     * 获取文件统计信息
     */
    List<FileRecord> selectFileStatistics(FileRecord record);

    /**
     * 更新下载次数
     */
    int incrementDownloadCount(Long fileId);

    /**
     * 增加引用次数
     */
    int incrementReferenceCount(Long fileId);

    /**
     * 减少引用次数
     */
    int decrementReferenceCount(Long fileId);

    /**
     * 查询过期文件
     */
    List<FileRecord> selectExpiredFiles();
}
