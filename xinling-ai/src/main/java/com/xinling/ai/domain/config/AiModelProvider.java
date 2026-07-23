package com.xinling.ai.domain.config;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI模型提供商对象 ai_model_provider
 *
 * @author SuXia
 * @date 2025/01/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiModelProvider extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 提供商ID */
    private Long providerId;

    /** 提供商名称 */
    @Excel(name = "提供商名称")
    private String providerName;

    /** 提供商编码 */
    @Excel(name = "提供商编码")
    private String providerCode;

    /** 提供商类型：local=本地模型, cloud=云端模型 */
    @Excel(name = "提供商类型", readConverterExp = "local=本地模型,cloud=云端模型")
    private String providerType;

    /** API基础地址 */
    @Excel(name = "API基础地址")
    private String apiBaseUrl;

    /** 排序顺序 */
    @Excel(name = "排序顺序")
    private Integer sortOrder;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;
}
