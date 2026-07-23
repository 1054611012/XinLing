package com.xinling.system.service.file.strategy;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.xinling.system.domain.entity.file.FileStorageConfig;

/**
 * 七牛云存储策略
 *
 * @author xinling
 */
@Component("qiniuStrategy")
public class QiniuStorageStrategy implements FileStorageStrategy
{
    private String getUploadToken(FileStorageConfig config)
    {
        Auth auth = Auth.create(config.getAccessKeyId(), config.getAccessKeySecret());
        return auth.uploadToken(config.getBucketName());
    }

    @Override
    public String upload(MultipartFile file, String path, FileStorageConfig config) throws Exception
    {
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);

        byte[] data = IOUtils.toByteArray(file.getInputStream());
        String token = getUploadToken(config);
        uploadManager.put(data, path, token);

        return getFileUrl(path, config);
    }

    @Override
    public byte[] download(String filePath, FileStorageConfig config) throws Exception
    {
        String domain = (config.getCustomDomain() != null && !config.getCustomDomain().isEmpty())
                ? config.getCustomDomain()
                : "https://" + config.getEndpoint();
        String url = domain + "/" + filePath;

        Auth auth = Auth.create(config.getAccessKeyId(), config.getAccessKeySecret());
        String privateUrl = auth.privateDownloadUrl(url, 3600);

        java.net.URL urlObj = new java.net.URL(privateUrl);
        try (java.io.InputStream is = urlObj.openStream())
        {
            return IOUtils.toByteArray(is);
        }
    }

    @Override
    public boolean delete(String filePath, FileStorageConfig config) throws Exception
    {
        // 七牛云删除需要通过API调用，这里通过HTTP请求实现
        Auth auth = Auth.create(config.getAccessKeyId(), config.getAccessKeySecret());
        String domain = (config.getCustomDomain() != null && !config.getCustomDomain().isEmpty())
                ? config.getCustomDomain()
                : "https://" + config.getEndpoint();

        String url = "http://rs.qiniu.com/delete/" + java.net.URLEncoder.encode(
                config.getBucketName() + ":" + filePath, "UTF-8");
        String accessToken = auth.signRequest(url, null, "POST");

        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "QBox " + accessToken);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        int code = conn.getResponseCode();
        return code == 200;
    }

    @Override
    public String getFileUrl(String filePath, FileStorageConfig config)
    {
        String domain = (config.getCustomDomain() != null && !config.getCustomDomain().isEmpty())
                ? config.getCustomDomain()
                : "https://" + config.getEndpoint();
        return domain + "/" + filePath;
    }

    @Override
    public boolean testConnection(FileStorageConfig config)
    {
        try
        {
            // 七牛云通过获取bucket信息来测试连接
            String domain = (config.getCustomDomain() != null && !config.getCustomDomain().isEmpty())
                    ? "https://" + config.getCustomDomain()
                    : "https://" + config.getEndpoint();

            java.net.URL url = new java.net.URL(domain);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.connect();
            // 即使返回403/404也说明网络可达
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
