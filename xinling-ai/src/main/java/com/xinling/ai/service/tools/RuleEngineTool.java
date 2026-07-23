package com.xinling.ai.service.tools;

import com.xinling.ai.domain.ontology.OntologyConcept;
import com.xinling.ai.domain.ontology.OntologyRule;
import com.xinling.ai.service.OntologyService;
import com.xinling.ai.service.RuleEngineService;
import com.xinling.ai.service.RuleEngineService.RuleEvalResult;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 规则引擎工具 — LLM 可调用以评估业务规则
 *
 * @author SuXia
 * @date 2026/07/20
 */
@Slf4j
@Component
public class RuleEngineTool {

    @Autowired
    private RuleEngineService ruleEngineService;

    @Autowired
    private OntologyService ontologyService;

    @Tool("查询指定概念适用的业务规则列表。用于了解某个业务概念有哪些约束和条件")
    public String queryRulesForConcept(@P("概念名称或编码") String conceptIdentifier) {
        log.info("RuleEngineTool - queryRulesForConcept: {}", conceptIdentifier);
        try {
            OntologyConcept concept = ontologyService.findConceptByName(conceptIdentifier);
            if (concept == null) {
                concept = ontologyService.findConceptByCode(conceptIdentifier);
            }
            if (concept == null) {
                return "未找到概念: " + conceptIdentifier;
            }
            List<OntologyRule> rules = ontologyService.getRulesByConceptId(concept.getConceptId());
            if (rules.isEmpty()) {
                return "概念 [" + concept.getConceptName() + "] 没有关联的业务规则";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("【").append(concept.getConceptName()).append(" 的业务规则】\n");
            for (OntologyRule r : rules) {
                sb.append("- ").append(r.getRuleName()).append(" (").append(r.getRuleCode()).append(")");
                if (!"1".equals(r.getEnabled())) {
                    sb.append(" [已禁用]");
                }
                sb.append(" [优先级:").append(r.getPriority()).append("]");
                sb.append(": ").append(r.getDescription()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("查询规则失败: {}", conceptIdentifier, e);
            return "查询规则出错: " + e.getMessage();
        }
    }

    @Tool("评估指定概念的业务规则，给出给定条件下的匹配结果。用于回答'如果...会怎样'类问题")
    public String evaluateRulesForConcept(
            @P("概念名称或编码") String conceptIdentifier,
            @P("JSON格式的上下文数据，如{\"duration_minutes\": 3}") String contextJson) {
        log.info("RuleEngineTool - evaluateRulesForConcept: {}, context: {}", conceptIdentifier, contextJson);
        try {
            OntologyConcept concept = ontologyService.findConceptByName(conceptIdentifier);
            if (concept == null) {
                concept = ontologyService.findConceptByCode(conceptIdentifier);
            }
            if (concept == null) {
                return "未找到概念: " + conceptIdentifier;
            }

            Map<String, Object> context = parseContext(contextJson);
            List<RuleEvalResult> results = ruleEngineService.evaluateRulesForConcept(concept.getConceptId(), context);

            StringBuilder sb = new StringBuilder();
            sb.append("【").append(concept.getConceptName()).append(" 规则评估结果】\n");
            if (results.isEmpty()) {
                sb.append("没有匹配的规则（或规则已禁用）\n");
            } else {
                for (RuleEvalResult result : results) {
                    sb.append("- ").append(result.getRule().getRuleName()).append(": ");
                    sb.append(result.isMatched() ? "✅ 触发" : "❌ 未触发");
                    if (result.getActionMessage() != null) {
                        sb.append(" → ").append(result.getActionMessage());
                    }
                    sb.append("\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("规则评估失败: {}", conceptIdentifier, e);
            return "规则评估出错: " + e.getMessage();
        }
    }

    private Map<String, Object> parseContext(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("解析上下文JSON失败: {}, 使用空上下文", json);
            return Collections.emptyMap();
        }
    }
}
