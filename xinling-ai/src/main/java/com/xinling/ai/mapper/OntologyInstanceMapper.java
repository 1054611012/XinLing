package com.xinling.ai.mapper;

import com.xinling.ai.domain.ontology.OntologyInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 本体实例Mapper接口
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Mapper
public interface OntologyInstanceMapper {

    List<OntologyInstance> selectOntologyInstanceList(OntologyInstance instance);

    OntologyInstance selectOntologyInstanceById(Long instanceId);

    List<OntologyInstance> selectInstancesByConceptId(Long conceptId);

    int insertOntologyInstance(OntologyInstance instance);

    int updateOntologyInstance(OntologyInstance instance);

    int deleteOntologyInstanceById(Long instanceId);

    int deleteOntologyInstanceByIds(Long[] instanceIds);

    OntologyInstance checkInstanceCodeUnique(@Param("instanceCode") String instanceCode, @Param("instanceId") Long instanceId);

    OntologyInstance selectOntologyInstanceByCode(@Param("instanceCode") String instanceCode);
}
