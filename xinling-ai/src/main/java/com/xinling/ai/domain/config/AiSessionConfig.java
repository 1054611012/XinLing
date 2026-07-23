package com.xinling.ai.domain.config;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * AI会话配置对象 ai_session_config
 *
 * @author SuXia
 * @date 2025/01/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiSessionConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long configId;

    /** 配置名称 */
    @Excel(name = "配置名称")
    private String configName;

    /** 配置标识键（platform/mobile/ontology/nl2sql） */
    @Excel(name = "配置标识")
    private String configKey;

    /** 对话模型ID */
    @Excel(name = "对话模型ID")
    private Long chatModelId;

    /** 对话模型名称（关联查询） */
    private String chatModelName;

    /** 嵌入模型ID */
    @Excel(name = "嵌入模型ID")
    private Long embeddingModelId;

    /** 嵌入模型名称（关联查询） */
    private String embeddingModelName;

    /** 最大历史消息数 */
    @Excel(name = "最大历史消息数")
    private Integer maxHistoryMessages;

    /** 是否启用RAG（0否 1是） */
    @Excel(name = "启用RAG", readConverterExp = "0=否,1=是")
    private String enableRag;

    /** RAG检索结果数量 */
    @Excel(name = "RAG检索数量")
    private Integer ragMaxResults;

    /** RAG最小相似度 */
    @Excel(name = "RAG最小相似度")
    private BigDecimal ragMinScore;

    /** 是否默认配置（0否 1是） */
    @Excel(name = "是否默认", readConverterExp = "0=否,1=是")
    private String isDefault;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 关联的提示词列表（非DB字段，仅用于展示和传递） */
    private List<AiPrompt> prompts;

    /** 关联的提示词ID列表（前端提交用） */
    private Long[] promptIds;
}
