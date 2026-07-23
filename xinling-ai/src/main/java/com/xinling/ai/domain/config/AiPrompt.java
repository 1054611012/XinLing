package com.xinling.ai.domain.config;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI提示词对象 ai_prompt
 *
 * @author SuXia
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiPrompt extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 提示词ID */
    private Long promptId;

    /** 提示词名称 */
    @Excel(name = "提示词名称")
    private String promptName;

    /** 提示词内容 */
    private String content;

    /** 适配模型ID（NULL=通用） */
    @Excel(name = "适配模型ID")
    private Long modelId;

    /** 适配模型名称（关联查询） */
    @Excel(name = "适配模型")
    private String modelName;

    /** 适配模型编码（关联查询） */
    private String modelCode;

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
