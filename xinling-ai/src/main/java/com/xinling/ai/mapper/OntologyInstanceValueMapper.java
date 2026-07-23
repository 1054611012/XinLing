package com.xinling.ai.mapper;

import com.xinling.ai.domain.ontology.OntologyInstanceValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实例属性值Mapper接口
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Mapper
public interface OntologyInstanceValueMapper {

    List<OntologyInstanceValue> selectOntologyInstanceValueList(OntologyInstanceValue value);

    OntologyInstanceValue selectOntologyInstanceValueById(Long valueId);

    List<OntologyInstanceValue> selectValuesByInstanceId(Long instanceId);

    int insertOntologyInstanceValue(OntologyInstanceValue value);

    int updateOntologyInstanceValue(OntologyInstanceValue value);

    int deleteOntologyInstanceValueById(Long valueId);

    int deleteOntologyInstanceValueByIds(Long[] valueIds);

    int deleteValuesByInstanceId(Long instanceId);
}
