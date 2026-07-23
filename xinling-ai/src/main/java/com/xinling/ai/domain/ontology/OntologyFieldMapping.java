package com.xinling.ai.domain.ontology;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段映射对象 ai_ontology_field_mapping
 * 将概念属性映射到业务表的具体字段，实现属性级的精细映射
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OntologyFieldMapping extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 字段映射ID */
    private Long fieldMappingId;

    /** 映射ID（关联ai_ontology_mapping） */
    private Long mappingId;

    /** 属性编码（关联ai_ontology_property.property_code） */
    @Excel(name = "属性编码")
    private String propertyCode;

    /** 业务表字段名 */
    @Excel(name = "业务字段")
    private String columnName;

    /** 默认值 */
    private String defaultValue;
}
