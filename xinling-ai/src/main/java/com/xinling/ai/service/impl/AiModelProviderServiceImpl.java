package com.xinling.ai.service.impl;

import com.xinling.ai.domain.config.AiModelProvider;
import com.xinling.ai.mapper.AiModelProviderMapper;
import com.xinling.ai.service.IAiModelProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI模型提供商Service业务层处理
 *
 * @author SuXia
 * @date 2025/01/22
 */
@Slf4j
@Service
public class AiModelProviderServiceImpl implements IAiModelProviderService {
    
    @Autowired
    private AiModelProviderMapper aiModelProviderMapper;

    @Override
    public List<AiModelProvider> selectAiModelProviderList(AiModelProvider aiModelProvider) {
        return aiModelProviderMapper.selectAiModelProviderList(aiModelProvider);
    }

    @Override
    public AiModelProvider selectAiModelProviderById(Long providerId) {
        return aiModelProviderMapper.selectAiModelProviderById(providerId);
    }

    @Override
    public int insertAiModelProvider(AiModelProvider aiModelProvider) {
        return aiModelProviderMapper.insertAiModelProvider(aiModelProvider);
    }

    @Override
    public int updateAiModelProvider(AiModelProvider aiModelProvider) {
        return aiModelProviderMapper.updateAiModelProvider(aiModelProvider);
    }

    @Override
    public int deleteAiModelProviderByIds(Long[] providerIds) {
        return aiModelProviderMapper.deleteAiModelProviderByIds(providerIds);
    }

    @Override
    public int deleteAiModelProviderById(Long providerId) {
        return aiModelProviderMapper.deleteAiModelProviderById(providerId);
    }

    @Override
    public boolean checkProviderCodeUnique(AiModelProvider aiModelProvider) {
        Long providerId = aiModelProvider.getProviderId() == null ? -1L : aiModelProvider.getProviderId();
        AiModelProvider info = aiModelProviderMapper.checkProviderCodeUnique(aiModelProvider.getProviderCode(), providerId);
        return info == null;
    }

    @Override
    public List<AiModelProvider> selectEnabledProviders() {
        AiModelProvider query = new AiModelProvider();
        query.setStatus("0");
        return aiModelProviderMapper.selectAiModelProviderList(query);
    }
}
