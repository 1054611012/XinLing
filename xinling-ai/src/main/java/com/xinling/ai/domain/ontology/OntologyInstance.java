package com.xinling.ai.domain.ontology;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 本体实例对象 ai_ontology_instance
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OntologyInstance extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 实例ID */
    private Long instanceId;

    /** 实例名称 */
    @Excel(name = "实例名称")
    private String instanceName;

    /** 实例编码 */
    @Excel(name = "实例编码")
    private String instanceCode;

    /** 所属概念ID */
    private Long conceptId;

    /** 所属概念名称（关联查询） */
    @Excel(name = "所属概念")
    private String conceptName;

    /** 实例描述 */
    @Excel(name = "实例描述")
    private String description;

    /** 状态（0启用 1禁用） */
    @Excel(name = "状态", readConverterExp = "0=启用,1=禁用")
    private String status;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;
}
