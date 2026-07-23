package com.xinling.ai.mapper;

import com.xinling.ai.domain.ontology.OntologyRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 业务规则Mapper接口
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Mapper
public interface OntologyRuleMapper {

    List<OntologyRule> selectOntologyRuleList(OntologyRule rule);

    OntologyRule selectOntologyRuleById(Long ruleId);

    List<OntologyRule> selectRulesByConceptId(Long conceptId);

    List<OntologyRule> selectAllEnabledRules();

    int insertOntologyRule(OntologyRule rule);

    int updateOntologyRule(OntologyRule rule);

    int deleteOntologyRuleById(Long ruleId);

    int deleteOntologyRuleByIds(Long[] ruleIds);

    OntologyRule checkRuleCodeUnique(@Param("ruleCode") String ruleCode, @Param("ruleId") Long ruleId);
}
