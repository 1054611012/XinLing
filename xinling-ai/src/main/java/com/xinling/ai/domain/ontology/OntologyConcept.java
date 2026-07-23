package com.xinling.ai.domain.ontology;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 本体概念对象 ai_ontology_concept
 *
 * @author SuXia
 * @date 2026/07/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OntologyConcept extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 概念ID */
    private Long conceptId;

    /** 概念名称 */
    @Excel(name = "概念名称")
    private String conceptName;

    /** 概念编码 */
    @Excel(name = "概念编码")
    private String conceptCode;

    /** 概念描述 */
    @Excel(name = "概念描述")
    private String description;

    /** 父概念ID */
    @Excel(name = "父概念ID")
    private Long parentId;

    /** 父概念名称（关联查询） */
    private String parentName;

    /** 类别 */
    @Excel(name = "类别")
    private String category;

    /** 状态（0启用 1禁用） */
    @Excel(name = "状态", readConverterExp = "0=启用,1=禁用")
    private String status;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;
}