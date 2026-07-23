package com.xinling.system.mapper.file;

import java.util.List;
import com.xinling.system.domain.entity.file.FileStorageConfig;

/**
 * 文件存储配置Mapper接口
 *
 * @author xinling
 */
public interface FileStorageConfigMapper
{
    /**
     * 查询文件存储配置
     */
    FileStorageConfig selectFileStorageConfigById(Long id);

    /**
     * 查询文件存储配置列表
     */
    List<FileStorageConfig> selectFileStorageConfigList(FileStorageConfig config);

    /**
     * 查询主配置
     */
    FileStorageConfig selectMasterConfig();

    /**
     * 新增文件存储配置
     */
    int insertFileStorageConfig(FileStorageConfig config);

    /**
     * 修改文件存储配置
     */
    int updateFileStorageConfig(FileStorageConfig config);

    /**
     * 删除文件存储配置
     */
    int deleteFileStorageConfigById(Long id);

    /**
     * 批量删除文件存储配置
     */
    int deleteFileStorageConfigByIds(Long[] ids);

    /**
     * 清除主配置标记
     */
    int clearMasterConfig();

    /**
     * 设置主配置
     */
    int setMasterConfig(Long id);
}