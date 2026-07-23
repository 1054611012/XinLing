package com.xinling.system.service;

import java.util.List;
import com.xinling.system.domain.entity.file.FileStorageConfig;

/**
 * 文件存储配置Service接口
 *
 * @author xinling
 */
public interface IFileStorageConfigService
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
     * 批量删除文件存储配置
     */
    int deleteFileStorageConfigByIds(Long[] ids);

    /**
     * 设置主配置
     */
    int setMasterConfig(Long id);

    /**
     * 测试配置连接
     */
    boolean testConnection(Long id);
}
