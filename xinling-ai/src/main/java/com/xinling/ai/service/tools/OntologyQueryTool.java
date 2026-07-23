package com.xinling.ai.service.tools;

import com.xinling.ai.domain.ontology.OntologyConcept;
import com.xinling.ai.domain.ontology.OntologyFieldMapping;
import com.xinling.ai.domain.ontology.OntologyMapping;
import com.xinling.ai.domain.ontology.OntologyProperty;
import com.xinling.ai.domain.ontology.OntologyRelation;
import com.xinling.ai.service.OntologyService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 本体查询工具 — LLM 可调用以查询本体知识库
 *
 * @author SuXia
 * @date 2026/07/06
 */
@Slf4j
@Component
public class OntologyQueryTool {

    @Autowired
    private OntologyService ontologyService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Tool("查询本体知识库中的概念定义。用于回答关于概念含义、定义、描述等问题")
    public String queryConceptDefinition(@P("概念名称") String conceptName) {
        log.info("OntologyQueryTool - queryConceptDefinition: {}", conceptName);
        try {
            OntologyConcept concept = ontologyService.findConceptByName(conceptName);
            if (concept == null) {
                return "未找到概念: " + conceptName;
            }
            return String.format("【%s】%s", concept.getConceptName(), concept.getDescription());
        } catch (Exception e) {
            log.error("查询概念定义失败: {}", conceptName, e);
            return "查询概念定义出错: " + e.getMessage();
        }
    }

    @Tool("查找与指定概念相关的其他概念和关系。用于回答概念之间的关联、分类体系等问题")
    public String findRelatedConcepts(@P("概念名称") String conceptName) {
        log.info("OntologyQueryTool - findRelatedConcepts: {}", conceptName);
        try {
            OntologyConcept concept = ontologyService.findConceptByName(conceptName);
            if (concept == null) {
                return "未找到概念: " + conceptName;
            }

            List<OntologyConcept> related = ontologyService.findRelatedConcepts(concept.getConceptId());
            if (related.isEmpty()) {
                return "没有找到与 " + conceptName + " 相关的概念";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("【").append(conceptName).append(" 的相关概念】\n");
            for (OntologyConcept item : related) {
                sb.append("- ").append(item.getConceptName()).append(": ").append(item.getDescription()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("查找相关概念失败: {}", conceptName, e);
            return "查找相关概念出错: " + e.getMessage();
        }
    }

    @Tool("查询两个概念之间的关系。用于回答概念之间的具体关系类型")
    public String queryRelationBetween(@P("源概念名称") String sourceName, @P("目标概念名称") String targetName) {
        log.info("OntologyQueryTool - queryRelationBetween: {} -> {}", sourceName, targetName);
        try {
            OntologyConcept source = ontologyService.findConceptByName(sourceName);
            OntologyConcept target = ontologyService.findConceptByName(targetName);

            if (source == null) {
                return "未找到源概念: " + sourceName;
            }
            if (target == null) {
                return "未找到目标概念: " + targetName;
            }

            OntologyRelation relation = ontologyService.findRelationBetween(source.getConceptId(), target.getConceptId());
            if (relation == null) {
                return sourceName + " 和 " + targetName + " 之间没有直接关系";
            }

            return String.format("【%s -[%s]-> %s】%s",
                    sourceName, relation.getRelationType(), targetName, relation.getDescription());
        } catch (Exception e) {
            log.error("查询概念关系失败: {} -> {}", sourceName, targetName, e);
            return "查询概念关系出错: " + e.getMessage();
        }
    }

    @Tool("基于本体知识库进行推理问答。用于回答需要知识推理的复杂问题")
    public String ontologyReasoning(@P("问题") String query) {
        log.info("OntologyQueryTool - ontologyReasoning: {}", query);
        try {
            return ontologyService.reason(query);
        } catch (Exception e) {
            log.error("本体推理失败: {}", query, e);
            return "本体推理出错: " + e.getMessage();
        }
    }

    @Tool("获取本体知识库中的所有概念列表。用于回答涉及多个概念的问题")
    public String getAllConcepts() {
        log.info("OntologyQueryTool - getAllConcepts");
        try {
            List<OntologyConcept> concepts = ontologyService.listConcepts(new OntologyConcept());
            if (concepts.isEmpty()) {
                return "本体知识库为空";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("【本体概念列表】\n");
            for (OntologyConcept concept : concepts) {
                sb.append("- ").append(concept.getConceptName()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("获取概念列表失败", e);
            return "获取概念列表出错: " + e.getMessage();
        }
    }

    @Tool("查询指定概念的子概念。用于回答分类层级相关的问题")
    public String queryChildConcepts(@P("父概念名称") String parentName) {
        log.info("OntologyQueryTool - queryChildConcepts: {}", parentName);
        try {
            OntologyConcept parent = ontologyService.findConceptByName(parentName);
            if (parent == null) {
                return "未找到概念: " + parentName;
            }

            List<OntologyConcept> children = ontologyService.getChildConcepts(parent.getConceptId());
            if (children.isEmpty()) {
                return parentName + " 没有子概念";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("【").append(parentName).append(" 的子概念】\n");
            for (OntologyConcept child : children) {
                sb.append("- ").append(child.getConceptName()).append(": ").append(child.getDescription()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("查询子概念失败: {}", parentName, e);
            return "查询子概念出错: " + e.getMessage();
        }
    }

    @Tool("根据概念编码查询关联的业务数据记录。用于获取与概念相关的实际数据，如音频、资源等")
    public String queryRelatedData(@P("概念编码") String conceptCode) {
        log.info("OntologyQueryTool - queryRelatedData: {}", conceptCode);
        try {
            List<OntologyMapping> mappings = ontologyService.getMappingsByConceptCode(conceptCode);
            if (mappings.isEmpty()) {
                return "概念编码 [" + conceptCode + "] 没有关联的业务数据";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("【概念 ").append(conceptCode).append(" 关联的业务数据】\n");

            // 按表分组，查询每张表的实际数据
            Map<String, List<Long>> groupedByTable = new HashMap<>();
            for (OntologyMapping mapping : mappings) {
                groupedByTable.computeIfAbsent(mapping.getTableName(), k -> new ArrayList<>()).add(mapping.getRecordId());
            }

            // 获取字段映射信息（用于精确查询业务字段）
            List<OntologyFieldMapping> fieldMappings = null;
            if (!mappings.isEmpty()) {
                fieldMappings = ontologyService.getFieldMappingsByMappingId(mappings.get(0).getMappingId());
            }

            for (Map.Entry<String, List<Long>> entry : groupedByTable.entrySet()) {
                String tableName = entry.getKey();
                List<Long> ids = entry.getValue();

                // 如果有字段映射，只查映射字段；否则查所有字段
                String columns;
                if (fieldMappings != null && !fieldMappings.isEmpty()) {
                    columns = fieldMappings.stream()
                            .map(OntologyFieldMapping::getColumnName)
                            .distinct()
                            .collect(Collectors.joining(", "));
                } else {
                    columns = "*";
                }

                // 分批查询（一次最多100个ID）
                sb.append("--- ").append(tableName).append(" ---\n");
                for (int i = 0; i < ids.size(); i += 100) {
                    List<Long> batch = ids.subList(i, Math.min(i + 100, ids.size()));
                    String placeholders = batch.stream().map(id -> "?").collect(Collectors.joining(","));
                    String sql = "SELECT " + columns + " FROM " + tableName + " WHERE id IN (" + placeholders + ") LIMIT 100";

                    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, batch.toArray());
                    for (Map<String, Object> row : rows) {
                        String rowStr = row.entrySet().stream()
                                .map(e -> e.getKey() + "=" + e.getValue())
                                .collect(Collectors.joining(", "));
                        sb.append("- ").append(rowStr).append("\n");
                    }
                }
            }

            return sb.toString();
        } catch (Exception e) {
            log.error("查询关联数据失败: {}", conceptCode, e);
            return "查询关联数据出错: " + e.getMessage();
        }
    }

    /**
     * 根据概念名称查询关联的业务数据（LLM更易使用）
     */
    @Tool("根据概念名称查询关联的业务数据。例如输入'白噪音'获取所有白噪音数据")
    public String queryBusinessDataByConcept(@P("概念名称") String conceptName) {
        log.info("OntologyQueryTool - queryBusinessDataByConcept: {}", conceptName);
        try {
            OntologyConcept concept = ontologyService.findConceptByName(conceptName);
            if (concept == null) {
                return "未找到概念: " + conceptName;
            }
            return queryRelatedData(concept.getConceptCode());
        } catch (Exception e) {
            log.error("查询业务数据失败: {}", conceptName, e);
            return "查询业务数据出错: " + e.getMessage();
        }
    }
}