package com.xinling.system.service.file.strategy;

import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import com.xinling.system.domain.entity.file.FileStorageConfig;

/**
 * MinIO存储策略
 *
 * @author xinling
 */
@Component("minioStrategy")
public class MinioStorageStrategy implements FileStorageStrategy
{
    @Override
    public String upload(MultipartFile file, String path, FileStorageConfig config) throws Exception
    {
        MinioClient client = MinioClient.builder()
                .endpoint(config.getEndpoint())
                .credentials(config.getAccessKeyId(), config.getAccessKeySecret())
                .build();

        byte[] data = IOUtils.toByteArray(file.getInputStream());
        client.putObject(PutObjectArgs.builder()
                .bucket(config.getBucketName())
                .object(path)
                .stream(new ByteArrayInputStream(data), data.length, -1)
                .contentType(file.getContentType())
                .build());

        return getFileUrl(path, config);
    }

    @Override
    public byte[] download(String filePath, FileStorageConfig config) throws Exception
    {
        MinioClient client = MinioClient.builder()
                .endpoint(config.getEndpoint())
                .credentials(config.getAccessKeyId(), config.getAccessKeySecret())
                .build();

        try (var stream = client.getObject(GetObjectArgs.builder()
                .bucket(config.getBucketName())
                .object(filePath)
                .build()))
        {
            return IOUtils.toByteArray(stream);
        }
    }

    @Override
    public boolean delete(String filePath, FileStorageConfig config) throws Exception
    {
        MinioClient client = MinioClient.builder()
                .endpoint(config.getEndpoint())
                .credentials(config.getAccessKeyId(), config.getAccessKeySecret())
                .build();

        client.removeObject(RemoveObjectArgs.builder()
                .bucket(config.getBucketName())
                .object(filePath)
                .build());
        return true;
    }

    @Override
    public String getFileUrl(String filePath, FileStorageConfig config)
    {
        String domain = (config.getCustomDomain() != null && !config.getCustomDomain().isEmpty())
                ? config.getCustomDomain()
                : config.getEndpoint();
        return domain + "/" + config.getBucketName() + "/" + filePath;
    }

    @Override
    public boolean testConnection(FileStorageConfig config)
    {
        try
        {
            MinioClient client = MinioClient.builder()
                    .endpoint(config.getEndpoint())
                    .credentials(config.getAccessKeyId(), config.getAccessKeySecret())
                    .build();

            return client.bucketExists(BucketExistsArgs.builder()
                    .bucket(config.getBucketName())
                    .build());
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
