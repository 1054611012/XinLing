package com.xinling.ai.domain.ontology;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行为对象 ai_ontology_action
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OntologyAction extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 行为ID */
    private Long actionId;

    /** 行为名称 */
    @Excel(name = "行为名称")
    private String actionName;

    /** 行为编码 */
    @Excel(name = "行为编码")
    private String actionCode;

    /** 所属概念ID */
    private Long conceptId;

    /** 所属概念名称（关联查询） */
    @Excel(name = "所属概念")
    private String conceptName;

    /** 行为类型（TOOL/API/PROMPT） */
    @Excel(name = "行为类型", readConverterExp = "TOOL=工具,API=接口,PROMPT=提示词")
    private String actionType;

    /** 目标（方法名/API地址/Prompt Key） */
    private String target;

    /** 参数（JSON格式） */
    private String parameters;

    /** 行为描述 */
    @Excel(name = "行为描述")
    private String description;

    /** 状态（0启用 1禁用） */
    @Excel(name = "状态", readConverterExp = "0=启用,1=禁用")
    private String status;
}
