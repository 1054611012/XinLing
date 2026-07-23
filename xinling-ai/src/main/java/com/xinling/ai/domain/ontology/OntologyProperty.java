package com.xinling.ai.domain.ontology;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 本体属性对象 ai_ontology_property
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OntologyProperty extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 属性ID */
    private Long propertyId;

    /** 属性名称 */
    @Excel(name = "属性名称")
    private String propertyName;

    /** 属性编码 */
    @Excel(name = "属性编码")
    private String propertyCode;

    /** 属性类型（STRING/INTEGER/DOUBLE/BOOLEAN/DATE/ENUM） */
    @Excel(name = "属性类型", readConverterExp = "STRING=字符串,INTEGER=整数,DOUBLE=小数,BOOLEAN=布尔,DATE=日期,ENUM=枚举")
    private String propertyType;

    /** 所属概念ID */
    private Long conceptId;

    /** 所属概念名称（关联查询） */
    @Excel(name = "所属概念")
    private String conceptName;

    /** 是否必填（0否 1是） */
    @Excel(name = "是否必填", readConverterExp = "0=否,1=是")
    private String required;

    /** 默认值 */
    private String defaultValue;

    /** 枚举值列表（JSON格式） */
    private String enumValues;

    /** 属性描述 */
    @Excel(name = "属性描述")
    private String description;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 状态（0启用 1禁用） */
    @Excel(name = "状态", readConverterExp = "0=启用,1=禁用")
    private String status;
}
