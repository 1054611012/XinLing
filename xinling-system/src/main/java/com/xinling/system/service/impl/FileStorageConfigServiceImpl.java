package com.xinling.system.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xinling.common.exception.ServiceException;
import com.xinling.system.domain.entity.file.FileStorageConfig;
import com.xinling.system.mapper.file.FileStorageConfigMapper;
import com.xinling.system.service.IFileStorageConfigService;
import com.xinling.system.service.file.strategy.FileStorageStrategy;

/**
 * 文件存储配置Service实现
 *
 * @author xinling
 */
@Service
public class FileStorageConfigServiceImpl implements IFileStorageConfigService
{
    @Autowired
    private FileStorageConfigMapper configMapper;

    @Autowired
    private Map<String, FileStorageStrategy> strategyMap;

    @Override
    public FileStorageConfig selectFileStorageConfigById(Long id)
    {
        return configMapper.selectFileStorageConfigById(id);
    }

    @Override
    public List<FileStorageConfig> selectFileStorageConfigList(FileStorageConfig config)
    {
        return configMapper.selectFileStorageConfigList(config);
    }

    @Override
    public FileStorageConfig selectMasterConfig()
    {
        return configMapper.selectMasterConfig();
    }

    @Override
    @Transactional
    public int insertFileStorageConfig(FileStorageConfig config)
    {
        if ("1".equals(config.getIsMaster()))
        {
            configMapper.clearMasterConfig();
        }
        return configMapper.insertFileStorageConfig(config);
    }

    @Override
    @Transactional
    public int updateFileStorageConfig(FileStorageConfig config)
    {
        if ("1".equals(config.getIsMaster()))
        {
            configMapper.clearMasterConfig();
        }
        return configMapper.updateFileStorageConfig(config);
    }

    @Override
    @Transactional
    public int deleteFileStorageConfigByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            FileStorageConfig config = configMapper.selectFileStorageConfigById(id);
            if (config == null)
            {
                throw new ServiceException("配置不存在: " + id);
            }
            if ("1".equals(config.getIsMaster()))
            {
                throw new ServiceException("不能删除主配置");
            }
        }
        return configMapper.deleteFileStorageConfigByIds(ids);
    }

    @Override
    @Transactional
    public int setMasterConfig(Long id)
    {
        FileStorageConfig config = configMapper.selectFileStorageConfigById(id);
        if (config == null)
        {
            throw new ServiceException("配置不存在: " + id);
        }
        if ("1".equals(config.getStatus()))
        {
            throw new ServiceException("停用的配置不能设置为主配置");
        }

        configMapper.clearMasterConfig();
        return configMapper.setMasterConfig(id);
    }

    @Override
    public boolean testConnection(Long id)
    {
        FileStorageConfig config = configMapper.selectFileStorageConfigById(id);
        if (config == null)
        {
            throw new ServiceException("配置不存在: " + id);
        }

        if ("local".equals(config.getStorageType()))
        {
            FileStorageStrategy strategy = strategyMap.get("localStrategy");
            return strategy != null && strategy.testConnection(config);
        }

        FileStorageStrategy strategy = strategyMap.get(config.getStorageType() + "Strategy");
        if (strategy == null)
        {
            throw new ServiceException("不支持的存储类型: " + config.getStorageType());
        }
        return strategy.testConnection(config);
    }
}
