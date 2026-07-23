package com.xinling.ai.mapper;

import com.xinling.ai.domain.ontology.OntologyRelation;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 本体关系Mapper接口
 *
 * @author SuXia
 * @date 2026/07/06
 */
public interface OntologyRelationMapper {
    /**
     * 查询本体关系列表
     */
    List<OntologyRelation> selectOntologyRelationList(OntologyRelation ontologyRelation);

    /**
     * 根据ID查询本体关系
     */
    OntologyRelation selectOntologyRelationById(Long relationId);

    /**
     * 根据源概念ID查询关系
     */
    List<OntologyRelation> selectRelationsBySource(Long sourceConceptId);

    /**
     * 根据目标概念ID查询关系
     */
    List<OntologyRelation> selectRelationsByTarget(Long targetConceptId);

    /**
     * 查询与指定概念相关的所有关系
     */
    List<OntologyRelation> selectRelationsByConcept(Long conceptId);

    /**
     * 查询指定两个概念之间的关系
     */
    OntologyRelation selectRelationBetween(@Param("sourceId") Long sourceId, @Param("targetId") Long targetId);

    /**
     * 新增本体关系
     */
    int insertOntologyRelation(OntologyRelation ontologyRelation);

    /**
     * 修改本体关系
     */
    int updateOntologyRelation(OntologyRelation ontologyRelation);

    /**
     * 删除本体关系
     */
    int deleteOntologyRelationById(Long relationId);

    /**
     * 批量删除本体关系
     */
    int deleteOntologyRelationByIds(Long[] relationIds);

    /**
     * 根据概念ID删除相关关系
     */
    int deleteRelationsByConceptId(Long conceptId);
}
