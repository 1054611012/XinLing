package com.xinling.ai.domain.ontology;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 本体关系对象 ai_ontology_relation
 *
 * @author SuXia
 * @date 2026/07/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OntologyRelation extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 关系ID */
    private Long relationId;

    /** 源概念ID */
    @Excel(name = "源概念ID")
    private Long sourceConceptId;

    /** 源概念名称（关联查询） */
    private String sourceConceptName;

    /** 目标概念ID */
    @Excel(name = "目标概念ID")
    private Long targetConceptId;

    /** 目标概念名称（关联查询） */
    private String targetConceptName;

    /** 关系类型 */
    @Excel(name = "关系类型")
    private String relationType;

    /** 关系描述 */
    @Excel(name = "关系描述")
    private String description;
}