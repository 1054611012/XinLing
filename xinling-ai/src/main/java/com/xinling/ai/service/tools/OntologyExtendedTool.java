package com.xinling.ai.service.tools;

import com.xinling.ai.domain.ontology.*;
import com.xinling.ai.service.OntologyService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 本体扩展查询工具 — LLM 可调用以查询属性、实例、规则、行为等扩展本体信息
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Slf4j
@Component
public class OntologyExtendedTool {

    @Autowired
    private OntologyService ontologyService;

    @Tool("查询指定概念的所有属性定义。用于获取概念具有哪些属性和特征描述")
    public String queryConceptProperties(@P("概念名称") String conceptName) {
        log.info("OntologyExtendedTool - queryConceptProperties: {}", conceptName);
        try {
            OntologyConcept concept = ontologyService.findConceptByName(conceptName);
            if (concept == null) {
                return "未找到概念: " + conceptName;
            }
            List<OntologyProperty> properties = ontologyService.getPropertiesByConceptId(concept.getConceptId());
            if (properties.isEmpty()) {
                return "概念 [" + conceptName + "] 没有定义属性";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("【").append(conceptName).append(" 的属性定义】\n");
            for (OntologyProperty p : properties) {
                sb.append("- ").append(p.getPropertyName())
                  .append(" (").append(p.getPropertyCode()).append(")")
                  .append(" 类型: ").append(p.getPropertyType())
                  .append(p.getRequired().equals("1") ? " [必填]" : " [可选]")
                  .append(": ").append(p.getDescription() != null ? p.getDescription() : "")
                  .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("查询概念属性失败: {}", conceptName, e);
            return "查询概念属性出错: " + e.getMessage();
        }
    }

    @Tool("查询指定概念的所有实例。用于获取具有具体属性值的实际对象列表")
    public String queryInstancesByConcept(@P("概念名称") String conceptName) {
        log.info("OntologyExtendedTool - queryInstancesByConcept: {}", conceptName);
        try {
            OntologyConcept concept = ontologyService.findConceptByName(conceptName);
            if (concept == null) {
                return "未找到概念: " + conceptName;
            }
            List<OntologyInstance> instances = ontologyService.getInstancesByConceptId(concept.getConceptId());
            if (instances.isEmpty()) {
                return "概念 [" + conceptName + "] 没有实例";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("【").append(conceptName).append(" 的实例列表】\n");
            for (OntologyInstance inst : instances) {
                sb.append("- ").append(inst.getInstanceName())
                  .append(" (").append(inst.getInstanceCode()).append(")")
                  .append(": ").append(inst.getDescription() != null ? inst.getDescription() : "")
                  .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("查询实例失败: {}", conceptName, e);
            return "查询实例出错: " + e.getMessage();
        }
    }

    @Tool("查询指定实例的所有属性值。用于获取某个具体实例的完整数据")
    public String queryInstanceValues(@P("实例编码") String instanceCode) {
        log.info("OntologyExtendedTool - queryInstanceValues: {}", instanceCode);
        try {
            OntologyInstance instance = ontologyService.findInstanceByCode(instanceCode);
            if (instance == null) {
                return "未找到实例: " + instanceCode;
            }
            List<OntologyInstanceValue> values = ontologyService.getValuesByInstanceId(instance.getInstanceId());
            if (values.isEmpty()) {
                return "实例 [" + instanceCode + "] 没有设置属性值";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("【实例 ").append(instance.getInstanceName()).append(" 的属性值】\n");
            for (OntologyInstanceValue v : values) {
                sb.append("- ").append(v.getPropertyName() != null ? v.getPropertyName() : "属性#" + v.getPropertyId())
                  .append(": ").append(v.getPropertyValue()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("查询实例属性值失败: {}", instanceCode, e);
            return "查询实例属性值出错: " + e.getMessage();
        }
    }

    @Tool("查询与指定概念关联的业务规则。用于了解概念的业务规则和约束条件")
    public String queryRulesForConcept(@P("概念名称或编码") String conceptIdentifier) {
        log.info("OntologyExtendedTool - queryRulesForConcept: {}", conceptIdentifier);
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
                sb.append("- ").append(r.getRuleName())
                  .append(" (").append(r.getRuleCode()).append(")")
                  .append(" [优先级:").append(r.getPriority()).append("]")
                  .append(" ").append("1".equals(r.getEnabled()) ? "[启用]" : "[禁用]")
                  .append(": ").append(r.getDescription() != null ? r.getDescription() : "")
                  .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("查询规则失败: {}", conceptIdentifier, e);
            return "查询规则出错: " + e.getMessage();
        }
    }

    @Tool("查询与指定概念关联的可执行行为（API、工具函数等）。用于了解概念支持哪些操作")
    public String queryActionsForConcept(@P("概念名称或编码") String conceptIdentifier) {
        log.info("OntologyExtendedTool - queryActionsForConcept: {}", conceptIdentifier);
        try {
            OntologyConcept concept = ontologyService.findConceptByName(conceptIdentifier);
            if (concept == null) {
                concept = ontologyService.findConceptByCode(conceptIdentifier);
            }
            if (concept == null) {
                return "未找到概念: " + conceptIdentifier;
            }
            List<OntologyAction> actions = ontologyService.getActionsByConceptId(concept.getConceptId());
            if (actions.isEmpty()) {
                return "概念 [" + concept.getConceptName() + "] 没有关联的行为";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("【").append(concept.getConceptName()).append(" 的行为定义】\n");
            for (OntologyAction a : actions) {
                sb.append("- ").append(a.getActionName())
                  .append(" (").append(a.getActionCode()).append(")")
                  .append(" [类型:").append(a.getActionType()).append("]")
                  .append(" 目标: ").append(a.getTarget())
                  .append(": ").append(a.getDescription() != null ? a.getDescription() : "")
                  .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("查询行为失败: {}", conceptIdentifier, e);
            return "查询行为出错: " + e.getMessage();
        }
    }

}
