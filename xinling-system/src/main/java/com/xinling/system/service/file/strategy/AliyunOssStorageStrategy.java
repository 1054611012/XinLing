package com.xinling.system.service.file.strategy;

import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.xinling.system.domain.entity.file.FileStorageConfig;

/**
 * 阿里云OSS存储策略
 *
 * @author xinling
 */
@Component("aliyun-ossStrategy")
public class AliyunOssStorageStrategy implements FileStorageStrategy
{
    @Override
    public String upload(MultipartFile file, String path, FileStorageConfig config) throws Exception
    {
        OSS ossClient = new OSSClientBuilder().build(config.getEndpoint(),
                config.getAccessKeyId(), config.getAccessKeySecret());

        try
        {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());

            byte[] data = IOUtils.toByteArray(file.getInputStream());
            ossClient.putObject(config.getBucketName(), path,
                    new ByteArrayInputStream(data), metadata);

            return getFileUrl(path, config);
        }
        finally
        {
            ossClient.shutdown();
        }
    }

    @Override
    public byte[] download(String filePath, FileStorageConfig config) throws Exception
    {
        OSS ossClient = new OSSClientBuilder().build(config.getEndpoint(),
                config.getAccessKeyId(), config.getAccessKeySecret());

        try
        {
            OSSObject object = ossClient.getObject(config.getBucketName(), filePath);
            return IOUtils.toByteArray(object.getObjectContent());
        }
        finally
        {
            ossClient.shutdown();
        }
    }

    @Override
    public boolean delete(String filePath, FileStorageConfig config)
    {
        OSS ossClient = new OSSClientBuilder().build(config.getEndpoint(),
                config.getAccessKeyId(), config.getAccessKeySecret());

        try
        {
            ossClient.deleteObject(config.getBucketName(), filePath);
            return true;
        }
        finally
        {
            ossClient.shutdown();
        }
    }

    @Override
    public String getFileUrl(String filePath, FileStorageConfig config)
    {
        String domain = (config.getCustomDomain() != null && !config.getCustomDomain().isEmpty())
                ? config.getCustomDomain()
                : String.format("https://%s.%s", config.getBucketName(), config.getEndpoint());
        return domain + "/" + filePath;
    }

    @Override
    public boolean testConnection(FileStorageConfig config)
    {
        try
        {
            OSS ossClient = new OSSClientBuilder().build(config.getEndpoint(),
                    config.getAccessKeyId(), config.getAccessKeySecret());
            boolean exists = ossClient.doesBucketExist(config.getBucketName());
            ossClient.shutdown();
            return exists;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
