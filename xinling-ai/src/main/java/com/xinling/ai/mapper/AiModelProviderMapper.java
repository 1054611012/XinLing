package com.xinling.ai.mapper;

import com.xinling.ai.domain.config.AiModelProvider;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI模型提供商Mapper接口
 *
 * @author SuXia
 * @date 2025/01/22
 */
public interface AiModelProviderMapper {
    /**
     * 查询AI模型提供商列表
     */
    List<AiModelProvider> selectAiModelProviderList(AiModelProvider aiModelProvider);

    /**
     * 根据ID查询AI模型提供商
     */
    AiModelProvider selectAiModelProviderById(Long providerId);

    /**
     * 根据编码查询AI模型提供商
     */
    AiModelProvider selectAiModelProviderByCode(String providerCode);

    /**
     * 新增AI模型提供商
     */
    int insertAiModelProvider(AiModelProvider aiModelProvider);

    /**
     * 修改AI模型提供商
     */
    int updateAiModelProvider(AiModelProvider aiModelProvider);

    /**
     * 删除AI模型提供商
     */
    int deleteAiModelProviderById(Long providerId);

    /**
     * 批量删除AI模型提供商
     */
    int deleteAiModelProviderByIds(Long[] providerIds);

    /**
     * 校验提供商编码是否唯一
     */
    AiModelProvider checkProviderCodeUnique(@Param("providerCode") String providerCode, @Param("providerId") Long providerId);
}
