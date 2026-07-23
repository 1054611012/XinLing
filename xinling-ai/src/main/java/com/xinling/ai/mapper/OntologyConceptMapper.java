package com.xinling.ai.mapper;

import com.xinling.ai.domain.ontology.OntologyConcept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 本体概念Mapper接口
 *
 * @author SuXia
 * @date 2026/07/06
 */
public interface OntologyConceptMapper {
    /**
     * 查询本体概念列表
     */
    List<OntologyConcept> selectOntologyConceptList(OntologyConcept ontologyConcept);

    /**
     * 根据ID查询本体概念
     */
    OntologyConcept selectOntologyConceptById(Long conceptId);

    /**
     * 根据编码查询本体概念
     */
    OntologyConcept selectOntologyConceptByCode(String conceptCode);

    /**
     * 根据名称查询本体概念
     */
    OntologyConcept selectOntologyConceptByName(String conceptName);

    /**
     * 查询子概念列表
     */
    List<OntologyConcept> selectChildConcepts(Long parentId);

    /**
     * 查询所有启用的概念
     */
    List<OntologyConcept> selectAllEnabledConcepts();

    /**
     * 新增本体概念
     */
    int insertOntologyConcept(OntologyConcept ontologyConcept);

    /**
     * 修改本体概念
     */
    int updateOntologyConcept(OntologyConcept ontologyConcept);

    /**
     * 删除本体概念
     */
    int deleteOntologyConceptById(Long conceptId);

    /**
     * 批量删除本体概念
     */
    int deleteOntologyConceptByIds(Long[] conceptIds);

    /**
     * 校验概念编码是否唯一
     */
    OntologyConcept checkConceptCodeUnique(@Param("conceptCode") String conceptCode, @Param("conceptId") Long conceptId);

    /**
     * 校验概念名称是否唯一
     */
    OntologyConcept checkConceptNameUnique(@Param("conceptName") String conceptName, @Param("conceptId") Long conceptId);

    /**
     * 模糊搜索概念（按名称、编码、描述匹配）
     */
    List<OntologyConcept> searchConceptsByKeyword(@Param("keyword") String keyword);
}