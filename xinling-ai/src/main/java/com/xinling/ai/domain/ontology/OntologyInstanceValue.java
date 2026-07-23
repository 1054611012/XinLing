package com.xinling.ai.domain.ontology;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实例属性值对象 ai_ontology_instance_value
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OntologyInstanceValue extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 属性值ID */
    private Long valueId;

    /** 实例ID */
    private Long instanceId;

    /** 属性ID */
    private Long propertyId;

    /** 属性名称（关联查询） */
    @Excel(name = "属性名称")
    private String propertyName;

    /** 属性值 */
    @Excel(name = "属性值")
    private String propertyValue;
}
