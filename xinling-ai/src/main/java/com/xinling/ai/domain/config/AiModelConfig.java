package com.xinling.ai.domain.config;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * AI模型配置对象 ai_model_config
 *
 * @author SuXia
 * @date 2025/01/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiModelConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 模型配置ID */
    private Long modelId;

    /** 提供商ID */
    @Excel(name = "提供商ID")
    private Long providerId;

    /** 提供商名称（关联查询） */
    private String providerName;

    /** 模型名称 */
    @Excel(name = "模型名称")
    private String modelName;

    /** 模型编码 */
    @Excel(name = "模型编码")
    private String modelCode;

    /** 模型类型：chat=对话模型, embedding=嵌入模型, image=图像模型 */
    @Excel(name = "模型类型", readConverterExp = "chat=对话模型,embedding=嵌入模型,image=图像模型")
    private String modelType;

    /** API密钥 */
    private String apiKey;

    /** 上下文窗口大小 */
    @Excel(name = "上下文窗口")
    private Integer contextWindow;

    /** 最大输出token数 */
    @Excel(name = "最大Token数")
    private Integer maxTokens;

    /** 温度参数（0-2） */
    @Excel(name = "温度参数")
    private BigDecimal temperature;

    /** Top-P参数（0-1） */
    @Excel(name = "Top-P参数")
    private BigDecimal topP;

    /** 超时时间（秒） */
    @Excel(name = "超时时间")
    private Integer timeoutSeconds;

    /** 是否默认模型（0否 1是） */
    @Excel(name = "是否默认", readConverterExp = "0=否,1=是")
    private String isDefault;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 排序顺序 */
    @Excel(name = "排序顺序")
    private Integer sortOrder;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;
}
