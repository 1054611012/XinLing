package com.xinling.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xinling.ai.domain.ontology.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 本体导入导出服务
 * 支持将整个本体图谱（概念+关系+属性+实例+规则+行为+映射）序列化为 JSON
 *
 * @author SuXia
 * @date 2026/07/20
 */
@Slf4j
@Service
public class OntologyExportService {

    @Autowired
    private OntologyService ontologyService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * 导出完整本体图谱为 JSON 字符串
     */
    public String exportFullOntology() {
        try {
            OntologySnapshot snapshot = new OntologySnapshot();

            // 概念
            snapshot.setConcepts(ontologyService.listConcepts(new OntologyConcept()));

            // 关系
            snapshot.setRelations(ontologyService.listRelations(new OntologyRelation()));

            // 属性
            snapshot.setProperties(ontologyService.listProperties(new OntologyProperty()));

            // 实例
            snapshot.setInstances(ontologyService.listInstances(new OntologyInstance()));

            // 实例属性值
            snapshot.setInstanceValues(ontologyService.listInstanceValues(new OntologyInstanceValue()));

            // 规则
            snapshot.setRules(ontologyService.listRules(new OntologyRule()));

            // 行为
            snapshot.setActions(ontologyService.listActions(new OntologyAction()));

            // 映射
            snapshot.setMappings(ontologyService.listMappings(new OntologyMapping()));

            // 字段映射
            snapshot.setFieldMappings(ontologyService.listFieldMappings(new OntologyFieldMapping()));

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(snapshot);
            log.info("本体导出完成: {} 概念, {} 关系, {} 属性, {} 实例, {} 规则, {} 行为",
                    snapshot.getConcepts().size(), snapshot.getRelations().size(),
                    snapshot.getProperties().size(), snapshot.getInstances().size(),
                    snapshot.getRules().size(), snapshot.getActions().size());
            return json;
        } catch (Exception e) {
            log.error("本体导出失败", e);
            throw new RuntimeException("本体导出失败: " + e.getMessage(), e);
        }
    }

    /**
     * 导入完整本体图谱（覆盖模式：先清空再导入）
     */
    @Transactional(rollbackFor = Exception.class)
    public String importFullOntology(String json) {
        try {
            OntologySnapshot snapshot = objectMapper.readValue(json, OntologySnapshot.class);

            // 按依赖顺序重建（先有概念，再有关系和属性，然后实例，最后规则/行为/映射）
            // 先全部删除（按依赖倒序）
            // 映射 → 字段映射 → 实例值 → 行为 → 规则 → 实例 → 属性 → 关系 → 概念
            if (snapshot.getFieldMappings() != null) {
                for (OntologyFieldMapping fm : snapshot.getFieldMappings()) {
                    ontologyService.deleteFieldMapping(fm.getFieldMappingId());
                }
            }
            if (snapshot.getInstanceValues() != null) {
                for (OntologyInstanceValue iv : snapshot.getInstanceValues()) {
                    ontologyService.deleteInstanceValue(iv.getValueId());
                }
            }
            if (snapshot.getActions() != null) {
                for (OntologyAction a : snapshot.getActions()) {
                    ontologyService.deleteAction(a.getActionId());
                }
            }
            if (snapshot.getRules() != null) {
                for (OntologyRule r : snapshot.getRules()) {
                    ontologyService.deleteRule(r.getRuleId());
                }
            }
            if (snapshot.getInstances() != null) {
                for (OntologyInstance i : snapshot.getInstances()) {
                    ontologyService.deleteInstance(i.getInstanceId());
                }
            }
            if (snapshot.getProperties() != null) {
                for (OntologyProperty p : snapshot.getProperties()) {
                    ontologyService.deleteProperty(p.getPropertyId());
                }
            }
            if (snapshot.getRelations() != null) {
                for (OntologyRelation r : snapshot.getRelations()) {
                    ontologyService.deleteRelation(r.getRelationId());
                }
            }
            if (snapshot.getConcepts() != null) {
                for (OntologyConcept c : snapshot.getConcepts()) {
                    ontologyService.deleteConcept(c.getConceptId());
                }
            }
            if (snapshot.getMappings() != null) {
                for (OntologyMapping m : snapshot.getMappings()) {
                    ontologyService.deleteMapping(m.getMappingId());
                }
            }

            // 按依赖正序重建
            if (snapshot.getConcepts() != null) {
                for (OntologyConcept c : snapshot.getConcepts()) {
                    ontologyService.addConcept(c);
                }
            }
            if (snapshot.getRelations() != null) {
                for (OntologyRelation r : snapshot.getRelations()) {
                    ontologyService.addRelation(r);
                }
            }
            if (snapshot.getProperties() != null) {
                for (OntologyProperty p : snapshot.getProperties()) {
                    ontologyService.addProperty(p);
                }
            }
            if (snapshot.getInstances() != null) {
                for (OntologyInstance i : snapshot.getInstances()) {
                    ontologyService.addInstance(i);
                }
            }
            if (snapshot.getInstanceValues() != null) {
                for (OntologyInstanceValue iv : snapshot.getInstanceValues()) {
                    ontologyService.addInstanceValue(iv);
                }
            }
            if (snapshot.getRules() != null) {
                for (OntologyRule r : snapshot.getRules()) {
                    ontologyService.addRule(r);
                }
            }
            if (snapshot.getActions() != null) {
                for (OntologyAction a : snapshot.getActions()) {
                    ontologyService.addAction(a);
                }
            }
            if (snapshot.getMappings() != null) {
                for (OntologyMapping m : snapshot.getMappings()) {
                    ontologyService.addMapping(m);
                }
            }
            if (snapshot.getFieldMappings() != null) {
                for (OntologyFieldMapping fm : snapshot.getFieldMappings()) {
                    ontologyService.addFieldMapping(fm);
                }
            }

            log.info("本体导入完成: {} 概念, {} 关系, {} 属性, {} 实例, {} 规则, {} 行为",
                    snapshot.getConcepts() != null ? snapshot.getConcepts().size() : 0,
                    snapshot.getRelations() != null ? snapshot.getRelations().size() : 0,
                    snapshot.getProperties() != null ? snapshot.getProperties().size() : 0,
                    snapshot.getInstances() != null ? snapshot.getInstances().size() : 0,
                    snapshot.getRules() != null ? snapshot.getRules().size() : 0,
                    snapshot.getActions() != null ? snapshot.getActions().size() : 0);

            return "导入成功: " +
                    (snapshot.getConcepts() != null ? snapshot.getConcepts().size() : 0) + " 概念, " +
                    (snapshot.getRelations() != null ? snapshot.getRelations().size() : 0) + " 关系, " +
                    (snapshot.getProperties() != null ? snapshot.getProperties().size() : 0) + " 属性, " +
                    (snapshot.getInstances() != null ? snapshot.getInstances().size() : 0) + " 实例, " +
                    (snapshot.getRules() != null ? snapshot.getRules().size() : 0) + " 规则, " +
                    (snapshot.getActions() != null ? snapshot.getActions().size() : 0) + " 行为";

        } catch (Exception e) {
            log.error("本体导入失败", e);
            throw new RuntimeException("本体导入失败: " + e.getMessage(), e);
        }
    }

    /**
     * 本体快照 — 完整本体图谱的 JSON 结构
     */
    @Data
    public static class OntologySnapshot {
        private List<OntologyConcept> concepts = new ArrayList<>();
        private List<OntologyRelation> relations = new ArrayList<>();
        private List<OntologyProperty> properties = new ArrayList<>();
        private List<OntologyInstance> instances = new ArrayList<>();
        private List<OntologyInstanceValue> instanceValues = new ArrayList<>();
        private List<OntologyRule> rules = new ArrayList<>();
        private List<OntologyAction> actions = new ArrayList<>();
        private List<OntologyMapping> mappings = new ArrayList<>();
        private List<OntologyFieldMapping> fieldMappings = new ArrayList<>();
    }
}
