package com.xinling.ai.mapper;

import com.xinling.ai.domain.ontology.OntologyFieldMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字段映射Mapper接口
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Mapper
public interface OntologyFieldMappingMapper {

    List<OntologyFieldMapping> selectOntologyFieldMappingList(OntologyFieldMapping fieldMapping);

    OntologyFieldMapping selectOntologyFieldMappingById(Long fieldMappingId);

    List<OntologyFieldMapping> selectFieldMappingsByMappingId(Long mappingId);

    List<OntologyFieldMapping> selectFieldMappingsByPropertyCode(String propertyCode);

    int insertOntologyFieldMapping(OntologyFieldMapping fieldMapping);

    int updateOntologyFieldMapping(OntologyFieldMapping fieldMapping);

    int deleteOntologyFieldMappingById(Long fieldMappingId);

    int deleteOntologyFieldMappingByIds(Long[] fieldMappingIds);

    int deleteFieldMappingsByMappingId(Long mappingId);
}
