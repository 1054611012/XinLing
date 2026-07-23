package com.xinling.ai.domain.ontology;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务规则对象 ai_ontology_rule
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OntologyRule extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long ruleId;

    /** 规则名称 */
    @Excel(name = "规则名称")
    private String ruleName;

    /** 规则编码 */
    @Excel(name = "规则编码")
    private String ruleCode;

    /** 所属概念ID */
    private Long conceptId;

    /** 所属概念名称（关联查询） */
    @Excel(name = "所属概念")
    private String conceptName;

    /** 条件（JSON格式） */
    private String condition;

    /** 动作（JSON格式） */
    private String action;

    /** 优先级 */
    @Excel(name = "优先级")
    private Integer priority;

    /** 是否启用（0禁用 1启用） */
    @Excel(name = "是否启用", readConverterExp = "0=禁用,1=启用")
    private String enabled;

    /** 规则描述 */
    @Excel(name = "规则描述")
    private String description;
}
