package com.xinling.ai.mapper;

import com.xinling.ai.domain.config.AiPrompt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI提示词Mapper接口
 *
 * @author SuXia
 */
public interface AiPromptMapper {

    /**
     * 查询AI提示词列表
     */
    List<AiPrompt> selectAiPromptList(AiPrompt aiPrompt);

    /**
     * 根据ID查询AI提示词
     */
    AiPrompt selectAiPromptById(Long promptId);

    /**
     * 根据会话配置ID查询关联的提示词列表
     */
    List<AiPrompt> selectPromptsByConfigId(@Param("configId") Long configId);

    /**
     * 根据会话配置ID和模型ID查询可用的提示词列表
     * 过滤规则：status='0'(启用) AND (model_id IS NULL OR model_id = #{modelId})
     */
    List<AiPrompt> selectEnabledPromptsByConfigId(
            @Param("configId") Long configId,
            @Param("modelId") Long modelId);

    /**
     * 新增AI提示词
     */
    int insertAiPrompt(AiPrompt aiPrompt);

    /**
     * 修改AI提示词
     */
    int updateAiPrompt(AiPrompt aiPrompt);

    /**
     * 删除AI提示词
     */
    int deleteAiPromptById(Long promptId);

    /**
     * 批量删除AI提示词
     */
    int deleteAiPromptByIds(Long[] promptIds);
}
