package com.xinling.system.service.file.strategy;

import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.xinling.system.domain.entity.file.FileStorageConfig;

/**
 * 腾讯云COS存储策略
 *
 * @author xinling
 */
@Component("tencent-cosStrategy")
public class TencentCosStorageStrategy implements FileStorageStrategy
{
    private COSClient createClient(FileStorageConfig config)
    {
        COSCredentials cred = new BasicCOSCredentials(config.getAccessKeyId(), config.getAccessKeySecret());
        Region region = new Region(config.getRegion() != null ? config.getRegion() : "ap-guangzhou");
        ClientConfig clientConfig = new ClientConfig(region);
        return new COSClient(cred, clientConfig);
    }

    @Override
    public String upload(MultipartFile file, String path, FileStorageConfig config) throws Exception
    {
        COSClient client = createClient(config);
        try
        {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            byte[] data = IOUtils.toByteArray(file.getInputStream());
            PutObjectRequest request = new PutObjectRequest(config.getBucketName(), path,
                    new ByteArrayInputStream(data), metadata);
            client.putObject(request);

            return getFileUrl(path, config);
        }
        finally
        {
            client.shutdown();
        }
    }

    @Override
    public byte[] download(String filePath, FileStorageConfig config) throws Exception
    {
        COSClient client = createClient(config);
        try
        {
            COSObject object = client.getObject(config.getBucketName(), filePath);
            return IOUtils.toByteArray(object.getObjectContent());
        }
        finally
        {
            client.shutdown();
        }
    }

    @Override
    public boolean delete(String filePath, FileStorageConfig config)
    {
        COSClient client = createClient(config);
        try
        {
            client.deleteObject(config.getBucketName(), filePath);
            return true;
        }
        finally
        {
            client.shutdown();
        }
    }

    @Override
    public String getFileUrl(String filePath, FileStorageConfig config)
    {
        String domain = (config.getCustomDomain() != null && !config.getCustomDomain().isEmpty())
                ? config.getCustomDomain()
                : String.format("https://%s.cos.%s.myqcloud.com", config.getBucketName(),
                        config.getRegion() != null ? config.getRegion() : "ap-guangzhou");
        return domain + "/" + filePath;
    }

    @Override
    public boolean testConnection(FileStorageConfig config)
    {
        try
        {
            COSClient client = createClient(config);
            boolean exists = client.doesBucketExist(config.getBucketName());
            client.shutdown();
            return exists;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
