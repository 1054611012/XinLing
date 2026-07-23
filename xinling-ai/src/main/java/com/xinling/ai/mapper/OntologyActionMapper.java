package com.xinling.ai.mapper;

import com.xinling.ai.domain.ontology.OntologyAction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 行为Mapper接口
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Mapper
public interface OntologyActionMapper {

    List<OntologyAction> selectOntologyActionList(OntologyAction action);

    OntologyAction selectOntologyActionById(Long actionId);

    List<OntologyAction> selectActionsByConceptId(Long conceptId);

    int insertOntologyAction(OntologyAction action);

    int updateOntologyAction(OntologyAction action);

    int deleteOntologyActionById(Long actionId);

    int deleteOntologyActionByIds(Long[] actionIds);

    OntologyAction checkActionCodeUnique(@Param("actionCode") String actionCode, @Param("actionId") Long actionId);
}
