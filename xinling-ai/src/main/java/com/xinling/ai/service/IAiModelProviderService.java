package com.xinling.ai.service;

import com.xinling.ai.domain.config.AiModelProvider;

import java.util.List;

/**
 * AI模型提供商Service接口
 *
 * @author SuXia
 * @date 2025/01/22
 */
public interface IAiModelProviderService {
    /**
     * 查询AI模型提供商列表
     */
    List<AiModelProvider> selectAiModelProviderList(AiModelProvider aiModelProvider);

    /**
     * 根据ID查询AI模型提供商
     */
    AiModelProvider selectAiModelProviderById(Long providerId);

    /**
     * 新增AI模型提供商
     */
    int insertAiModelProvider(AiModelProvider aiModelProvider);

    /**
     * 修改AI模型提供商
     */
    int updateAiModelProvider(AiModelProvider aiModelProvider);

    /**
     * 批量删除AI模型提供商
     */
    int deleteAiModelProviderByIds(Long[] providerIds);

    /**
     * 删除AI模型提供商信息
     */
    int deleteAiModelProviderById(Long providerId);

    /**
     * 校验提供商编码是否唯一
     */
    boolean checkProviderCodeUnique(AiModelProvider aiModelProvider);

    /**
     * 获取所有启用的提供商
     */
    List<AiModelProvider> selectEnabledProviders();
}
