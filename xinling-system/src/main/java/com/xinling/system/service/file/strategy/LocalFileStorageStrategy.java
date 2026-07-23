package com.xinling.system.service.file.strategy;

import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.xinling.common.config.XinLingConfig;
import com.xinling.common.utils.file.FileUtils;
import com.xinling.system.domain.entity.file.FileStorageConfig;

/**
 * 本地文件存储策略
 *
 * @author xinling
 */
@Component("localStrategy")
public class LocalFileStorageStrategy implements FileStorageStrategy
{
    @Override
    public String upload(MultipartFile file, String path, FileStorageConfig config) throws Exception
    {
        String fullPath = XinLingConfig.getUploadPath() + "/" + path;
        File dest = new File(fullPath);
        if (!dest.getParentFile().exists())
        {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        return getFileUrl(path, config);
    }

    @Override
    public byte[] download(String filePath, FileStorageConfig config) throws Exception
    {
        String localPath = XinLingConfig.getUploadPath() + "/" + filePath;
        File file = new File(localPath);
        if (!file.exists())
        {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file))
        {
            return IOUtils.toByteArray(fis);
        }
    }

    @Override
    public boolean delete(String filePath, FileStorageConfig config) throws Exception
    {
        return FileUtils.deleteFile(XinLingConfig.getUploadPath() + "/" + filePath);
    }

    @Override
    public String getFileUrl(String filePath, FileStorageConfig config)
    {
        String domain = (config.getCustomDomain() != null && !config.getCustomDomain().isEmpty())
                ? config.getCustomDomain()
                : "/uploads";
        if (domain.endsWith("/"))
        {
            domain = domain.substring(0, domain.length() - 1);
        }
        return domain + "/" + filePath;
    }

    @Override
    public boolean testConnection(FileStorageConfig config)
    {
        String uploadPath = XinLingConfig.getUploadPath();
        File dir = new File(uploadPath);
        if (!dir.exists())
        {
            return dir.mkdirs();
        }
        return dir.exists() && dir.isDirectory() && dir.canWrite();
    }
}
