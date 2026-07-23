package com.xinling.ai.mapper;

import com.xinling.ai.domain.ontology.OntologyMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 本体映射Mapper接口
 *
 * @author SuXia
 * @date 2026/07/08
 */
@Mapper
public interface OntologyMappingMapper {
    int insertOntologyMapping(OntologyMapping mapping);

    int deleteOntologyMappingById(Long mappingId);

    int deleteMappingsByConceptCode(String conceptCode);

    int deleteMappingsByTableRecord(@Param("tableName") String tableName, @Param("recordId") Long recordId);

    OntologyMapping selectOntologyMappingById(Long mappingId);

    List<OntologyMapping> selectOntologyMappingList(OntologyMapping mapping);

    List<OntologyMapping> selectMappingsByConceptCode(String conceptCode);

    List<OntologyMapping> selectMappingsByConceptId(Long conceptId);

    int deleteMappingsByConceptId(Long conceptId);

    List<OntologyMapping> selectMappingsByTableRecord(@Param("tableName") String tableName, @Param("recordId") Long recordId);
}
