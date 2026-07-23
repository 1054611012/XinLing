package com.xinling.ai.mapper;

import com.xinling.ai.domain.ontology.OntologyProperty;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 本体属性Mapper接口
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Mapper
public interface OntologyPropertyMapper {

    List<OntologyProperty> selectOntologyPropertyList(OntologyProperty property);

    OntologyProperty selectOntologyPropertyById(Long propertyId);

    List<OntologyProperty> selectPropertiesByConceptId(Long conceptId);

    int insertOntologyProperty(OntologyProperty property);

    int updateOntologyProperty(OntologyProperty property);

    int deleteOntologyPropertyById(Long propertyId);

    int deleteOntologyPropertyByIds(Long[] propertyIds);

    OntologyProperty checkPropertyCodeUnique(@Param("propertyCode") String propertyCode, @Param("propertyId") Long propertyId);
}
