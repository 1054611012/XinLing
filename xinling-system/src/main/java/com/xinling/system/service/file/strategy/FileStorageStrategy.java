package com.xinling.system.service.file.strategy;

import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;
import com.xinling.system.domain.entity.file.FileStorageConfig;

/**
 * 文件存储策略接口
 *
 * @author xinling
 */
public interface FileStorageStrategy
{
    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @param path 存储路径
     * @param config 存储配置
     * @return 文件访问URL
     */
    String upload(MultipartFile file, String path, FileStorageConfig config) throws Exception;

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @param config 存储配置
     * @return 文件字节流
     */
    byte[] download(String filePath, FileStorageConfig config) throws Exception;

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @param config 存储配置
     * @return 是否成功
     */
    boolean delete(String filePath, FileStorageConfig config) throws Exception;

    /**
     * 获取文件访问URL
     *
     * @param filePath 文件路径
     * @param config 存储配置
     * @return 文件访问URL
     */
    String getFileUrl(String filePath, FileStorageConfig config);

    /**
     * 测试连接
     *
     * @param config 存储配置
     * @return 是否连接成功
     */
    boolean testConnection(FileStorageConfig config);
}
