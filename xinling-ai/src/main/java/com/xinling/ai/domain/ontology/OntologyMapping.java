package com.xinling.ai.domain.ontology;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 本体映射对象 ai_ontology_mapping
 *
 * @author SuXia
 * @date 2026/07/08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OntologyMapping extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long mappingId;

    /** 概念编码（业务标识，修改频率低） */
    @Excel(name = "概念编码")
    private String conceptCode;

    /** 概念ID（外键，优先使用） */
    private Long conceptId;

    /** 概念名称（关联查询） */
    @Excel(name = "概念名称")
    private String conceptName;

    /** 业务表名 */
    @Excel(name = "业务表")
    private String tableName;

    /** 业务记录ID */
    @Excel(name = "业务记录ID")
    private Long recordId;

    /** 映射类型（PRIMARY主映射/TAG标签映射） */
    @Excel(name = "映射类型")
    private String mappingType;
}
