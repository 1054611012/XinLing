package com.xinling.ai.service;

import com.xinling.ai.domain.config.AiPrompt;
import com.xinling.ai.domain.config.AiSessionConfig;
import com.xinling.ai.domain.ontology.OntologyAction;
import com.xinling.ai.domain.ontology.OntologyConcept;
import com.xinling.ai.domain.ontology.OntologyFieldMapping;
import com.xinling.ai.domain.ontology.OntologyInstance;
import com.xinling.ai.domain.ontology.OntologyInstanceValue;
import com.xinling.ai.domain.ontology.OntologyMapping;
import com.xinling.ai.domain.ontology.OntologyProperty;
import com.xinling.ai.domain.ontology.OntologyRelation;
import com.xinling.ai.domain.ontology.OntologyRule;
import com.xinling.ai.mapper.OntologyActionMapper;
import com.xinling.ai.mapper.OntologyConceptMapper;
import com.xinling.ai.mapper.OntologyFieldMappingMapper;
import com.xinling.ai.mapper.OntologyInstanceMapper;
import com.xinling.ai.mapper.OntologyInstanceValueMapper;
import com.xinling.ai.mapper.OntologyMappingMapper;
import com.xinling.ai.mapper.OntologyPropertyMapper;
import com.xinling.ai.mapper.OntologyRelationMapper;
import com.xinling.ai.mapper.OntologyRuleMapper;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * 本体服务
 * 提供本体知识管理、推理和问答能力
 * 提示词从 ai_prompt 表加载（按 session config = ontology 关联）
 *
 * @author SuXia
 */
@Slf4j
@Service
public class OntologyService {

    @Autowired
    private ChatModel chatLanguageModel;

    @Autowired
    private OntologyConceptMapper conceptMapper;

    @Autowired
    private OntologyRelationMapper relationMapper;

    @Autowired
    private OntologyMappingMapper mappingMapper;

    @Autowired
    private OntologyPropertyMapper propertyMapper;

    @Autowired
    private OntologyInstanceMapper instanceMapper;

    @Autowired
    private OntologyInstanceValueMapper instanceValueMapper;

    @Autowired
    private OntologyRuleMapper ruleMapper;

    @Autowired
    private OntologyActionMapper actionMapper;

    @Autowired
    private OntologyFieldMappingMapper fieldMappingMapper;

    @Autowired
    private IAiSessionConfigService aiSessionConfigService;

    @Autowired
    private IAiPromptService aiPromptService;

    /**
     * 添加概念
     */
    public void addConcept(OntologyConcept concept) {
        conceptMapper.insertOntologyConcept(concept);
        log.info("本体概念已添加: {}", concept.getConceptName());
    }

    /**
     * 更新概念
     */
    public void updateConcept(OntologyConcept concept) {
        conceptMapper.updateOntologyConcept(concept);
        log.info("本体概念已更新: {}", concept.getConceptName());
    }

    /**
     * 删除概念（级联删除相关关系）
     */
    public void deleteConcept(Long conceptId) {
        relationMapper.deleteRelationsByConceptId(conceptId);
        conceptMapper.deleteOntologyConceptById(conceptId);
        log.info("本体概念已删除: {}", conceptId);
    }

    /**
     * 获取概念详情
     */
    public OntologyConcept getConcept(Long conceptId) {
        return conceptMapper.selectOntologyConceptById(conceptId);
    }

    /**
     * 获取概念列表
     */
    public List<OntologyConcept> listConcepts(OntologyConcept concept) {
        return conceptMapper.selectOntologyConceptList(concept);
    }

    /**
     * 添加关系
     */
    public void addRelation(OntologyRelation relation) {
        relationMapper.insertOntologyRelation(relation);
        log.info("本体关系已添加: {} -> {}", relation.getSourceConceptId(), relation.getTargetConceptId());
    }

    /**
     * 更新关系
     */
    public void updateRelation(OntologyRelation relation) {
        relationMapper.updateOntologyRelation(relation);
        log.info("本体关系已更新: {}", relation.getRelationId());
    }

    /**
     * 删除关系
     */
    public void deleteRelation(Long relationId) {
        relationMapper.deleteOntologyRelationById(relationId);
        log.info("本体关系已删除: {}", relationId);
    }

    /**
     * 获取关系列表
     */
    public List<OntologyRelation> listRelations(OntologyRelation relation) {
        return relationMapper.selectOntologyRelationList(relation);
    }

    /**
     * 查询与指定概念相关的所有概念
     */
    public List<OntologyConcept> findRelatedConcepts(Long conceptId) {
        List<OntologyRelation> relations = relationMapper.selectRelationsByConcept(conceptId);
        Set<Long> relatedIds = new HashSet<>();
        for (OntologyRelation relation : relations) {
            if (!relation.getSourceConceptId().equals(conceptId)) {
                relatedIds.add(relation.getSourceConceptId());
            }
            if (!relation.getTargetConceptId().equals(conceptId)) {
                relatedIds.add(relation.getTargetConceptId());
            }
        }
        List<OntologyConcept> result = new ArrayList<>();
        for (Long id : relatedIds) {
            OntologyConcept concept = conceptMapper.selectOntologyConceptById(id);
            if (concept != null) {
                result.add(concept);
            }
        }
        return result;
    }

    /**
     * 查询指定两个概念之间的关系
     */
    public OntologyRelation findRelationBetween(Long sourceId, Long targetId) {
        return relationMapper.selectRelationBetween(sourceId, targetId);
    }

    /**
     * 获取本体知识图谱文本表示
     */
    public String getOntologyText() {
        List<OntologyConcept> concepts = conceptMapper.selectAllEnabledConcepts();
        List<OntologyRelation> relations = relationMapper.selectOntologyRelationList(new OntologyRelation());

        StringBuilder sb = new StringBuilder();
        sb.append("【概念列表】\n");
        for (OntologyConcept concept : concepts) {
            sb.append("- ").append(concept.getConceptName())
              .append(" (").append(concept.getConceptCode()).append(")")
              .append(": ").append(concept.getDescription()).append("\n");
        }
        sb.append("\n【关系列表】\n");
        for (OntologyRelation relation : relations) {
            sb.append("- ").append(relation.getSourceConceptName())
              .append(" -[").append(relation.getRelationType()).append("]-> ")
              .append(relation.getTargetConceptName())
              .append(": ").append(relation.getDescription()).append("\n");
        }
        return sb.toString();
    }

    /**
     * 本体推理问答
     * 提示词从 ai_prompt 表加载（关联 ontology 会话配置）
     */
    public String reason(String query) {
        try {
            String ontologyText = getOntologyText();
            String systemPrompt = loadSystemPromptFromDb(ontologyText);

            SystemMessage systemMessage = new SystemMessage(systemPrompt);
            UserMessage userMessage = new UserMessage(query);

            ChatResponse response = chatLanguageModel.chat(List.of(systemMessage, userMessage));
            return response.aiMessage().text();
        } catch (Exception e) {
            log.error("本体推理失败", e);
            return "本体推理时发生错误：" + e.getMessage();
        }
    }

    /**
     * 本体增强RAG查询
     */
    public String ragQuery(String query) {
        return reason(query);
    }

    /**
     * 从数据库加载本体系统提示词（通过 ai_prompt 表 + Redis 缓存）
     */
    private String loadSystemPromptFromDb(String ontologyText) {
        AiSessionConfig ontologyConfig = aiSessionConfigService.selectByConfigKey("ontology");
        if (ontologyConfig != null) {
            List<AiPrompt> prompts = aiPromptService.selectEnabledPromptsByConfigId(
                    ontologyConfig.getConfigId(), ontologyConfig.getChatModelId());
            if (prompts != null && !prompts.isEmpty()) {
                String template = prompts.get(0).getContent();
                return template.replace("{ontology}", ontologyText);
            }
        }
        throw new RuntimeException("未找到本体推理提示词，请检查 ai_prompt 表是否有 ontology 配置的关联提示词");
    }

    /**
     * 根据名称查找概念（精确匹配，失败则模糊搜索返回第一个）
     */
    public OntologyConcept findConceptByName(String name) {
        OntologyConcept exact = conceptMapper.selectOntologyConceptByName(name);
        if (exact != null) {
            return exact;
        }
        // 模糊匹配：按名称、编码、描述搜索，返回最匹配的
        List<OntologyConcept> fuzzyResults = conceptMapper.searchConceptsByKeyword(name);
        if (!fuzzyResults.isEmpty()) {
            log.info("概念精确匹配「{}」未找到，模糊匹配命中「{}」", name, fuzzyResults.get(0).getConceptName());
            return fuzzyResults.get(0);
        }
        return null;
    }

    /**
     * 根据编码查找概念
     */
    public OntologyConcept findConceptByCode(String code) {
        return conceptMapper.selectOntologyConceptByCode(code);
    }

    /**
     * 模糊搜索概念
     */
    public List<OntologyConcept> searchConceptsByKeyword(String keyword) {
        return conceptMapper.searchConceptsByKeyword(keyword);
    }

    /**
     * 获取所有启用的概念
     */
    public List<OntologyConcept> getAllEnabledConcepts() {
        return conceptMapper.selectAllEnabledConcepts();
    }

    /**
     * 校验概念编码是否唯一
     */
    public OntologyConcept checkConceptCodeUnique(String conceptCode, Long conceptId) {
        return conceptMapper.checkConceptCodeUnique(conceptCode, conceptId);
    }

    /**
     * 校验概念名称是否唯一
     */
    public OntologyConcept checkConceptNameUnique(String conceptName, Long conceptId) {
        return conceptMapper.checkConceptNameUnique(conceptName, conceptId);
    }

    /**
     * 根据ID获取关系
     */
    public OntologyRelation getRelationById(Long relationId) {
        return relationMapper.selectOntologyRelationById(relationId);
    }

    /**
     * 批量删除关系
     */
    public int deleteRelationsByIds(Long[] relationIds) {
        return relationMapper.deleteOntologyRelationByIds(relationIds);
    }

    /**
     * 查询与指定概念相关的所有关系
     */
    public List<OntologyRelation> getRelationsByConceptId(Long conceptId) {
        return relationMapper.selectRelationsByConcept(conceptId);
    }

    /**
     * 获取概念的子概念列表
     */
    public List<OntologyConcept> getChildConcepts(Long parentId) {
        return conceptMapper.selectChildConcepts(parentId);
    }

    /**
     * 添加映射（自动填充 conceptId 和 mappingType）
     */
    public void addMapping(OntologyMapping mapping) {
        // 如果只传了 conceptCode 没传 conceptId，自动补齐
        if (mapping.getConceptId() == null && mapping.getConceptCode() != null) {
            OntologyConcept concept = conceptMapper.selectOntologyConceptByCode(mapping.getConceptCode());
            if (concept != null) {
                mapping.setConceptId(concept.getConceptId());
            }
        }
        // 如果只传了 conceptId 没传 conceptCode，自动补齐
        if (mapping.getConceptCode() == null && mapping.getConceptId() != null) {
            OntologyConcept concept = conceptMapper.selectOntologyConceptById(mapping.getConceptId());
            if (concept != null) {
                mapping.setConceptCode(concept.getConceptCode());
            }
        }
        if (mapping.getMappingType() == null) {
            mapping.setMappingType("PRIMARY");
        }
        mappingMapper.insertOntologyMapping(mapping);
        log.info("本体映射已添加: {} -> {}.{}", mapping.getConceptCode(), mapping.getTableName(), mapping.getRecordId());
    }

    /**
     * 删除映射
     */
    public void deleteMapping(Long mappingId) {
        mappingMapper.deleteOntologyMappingById(mappingId);
        log.info("本体映射已删除: {}", mappingId);
    }

    /**
     * 删除指定概念的所有映射（按编码）
     */
    public void deleteMappingsByConceptCode(String conceptCode) {
        mappingMapper.deleteMappingsByConceptCode(conceptCode);
        log.info("本体映射已删除(按概念编码): {}", conceptCode);
    }

    /**
     * 删除指定概念的所有映射（按ID）
     */
    public void deleteMappingsByConceptId(Long conceptId) {
        mappingMapper.deleteMappingsByConceptId(conceptId);
        log.info("本体映射已删除(按概念ID): {}", conceptId);
    }

    /**
     * 删除指定业务记录的所有映射
     */
    public void deleteMappingsByTableRecord(String tableName, Long recordId) {
        mappingMapper.deleteMappingsByTableRecord(tableName, recordId);
        log.info("本体映射已删除(按记录): {}.{}", tableName, recordId);
    }

    /**
     * 获取映射列表
     */
    public List<OntologyMapping> listMappings(OntologyMapping mapping) {
        return mappingMapper.selectOntologyMappingList(mapping);
    }

    /**
     * 根据概念编码获取映射
     */
    public List<OntologyMapping> getMappingsByConceptCode(String conceptCode) {
        return mappingMapper.selectMappingsByConceptCode(conceptCode);
    }

    /**
     * 根据概念ID获取映射
     */
    public List<OntologyMapping> getMappingsByConceptId(Long conceptId) {
        return mappingMapper.selectMappingsByConceptId(conceptId);
    }

    /**
     * 根据业务表记录获取映射
     */
    public List<OntologyMapping> getMappingsByTableRecord(String tableName, Long recordId) {
        return mappingMapper.selectMappingsByTableRecord(tableName, recordId);
    }

    // ==================== Property ====================

    public List<OntologyProperty> listProperties(OntologyProperty property) {
        return propertyMapper.selectOntologyPropertyList(property);
    }

    public OntologyProperty getProperty(Long propertyId) {
        return propertyMapper.selectOntologyPropertyById(propertyId);
    }

    public void addProperty(OntologyProperty property) {
        propertyMapper.insertOntologyProperty(property);
        log.info("本体属性已添加: {}", property.getPropertyName());
    }

    public void updateProperty(OntologyProperty property) {
        propertyMapper.updateOntologyProperty(property);
        log.info("本体属性已更新: {}", property.getPropertyName());
    }

    public void deleteProperty(Long propertyId) {
        propertyMapper.deleteOntologyPropertyById(propertyId);
        log.info("本体属性已删除: {}", propertyId);
    }

    public void deletePropertiesByIds(Long[] propertyIds) {
        propertyMapper.deleteOntologyPropertyByIds(propertyIds);
        log.info("本体属性已批量删除: {}", (Object) propertyIds);
    }

    public List<OntologyProperty> getPropertiesByConceptId(Long conceptId) {
        return propertyMapper.selectPropertiesByConceptId(conceptId);
    }

    public OntologyProperty checkPropertyCodeUnique(String propertyCode, Long propertyId) {
        return propertyMapper.checkPropertyCodeUnique(propertyCode, propertyId);
    }

    // ==================== Instance ====================

    public List<OntologyInstance> listInstances(OntologyInstance instance) {
        return instanceMapper.selectOntologyInstanceList(instance);
    }

    public OntologyInstance getInstance(Long instanceId) {
        return instanceMapper.selectOntologyInstanceById(instanceId);
    }

    public void addInstance(OntologyInstance instance) {
        instanceMapper.insertOntologyInstance(instance);
        log.info("本体实例已添加: {}", instance.getInstanceName());
    }

    public void updateInstance(OntologyInstance instance) {
        instanceMapper.updateOntologyInstance(instance);
        log.info("本体实例已更新: {}", instance.getInstanceName());
    }

    public void deleteInstance(Long instanceId) {
        instanceMapper.deleteOntologyInstanceById(instanceId);
        log.info("本体实例已删除: {}", instanceId);
    }

    public void deleteInstancesByIds(Long[] instanceIds) {
        instanceMapper.deleteOntologyInstanceByIds(instanceIds);
        log.info("本体实例已批量删除: {}", (Object) instanceIds);
    }

    public List<OntologyInstance> getInstancesByConceptId(Long conceptId) {
        return instanceMapper.selectInstancesByConceptId(conceptId);
    }

    public OntologyInstance checkInstanceCodeUnique(String instanceCode, Long instanceId) {
        return instanceMapper.checkInstanceCodeUnique(instanceCode, instanceId);
    }

    /**
     * 根据实例编码精确查找实例
     */
    public OntologyInstance findInstanceByCode(String instanceCode) {
        return instanceMapper.selectOntologyInstanceByCode(instanceCode);
    }

    // ==================== Instance Value ====================

    public List<OntologyInstanceValue> listInstanceValues(OntologyInstanceValue value) {
        return instanceValueMapper.selectOntologyInstanceValueList(value);
    }

    public OntologyInstanceValue getInstanceValue(Long valueId) {
        return instanceValueMapper.selectOntologyInstanceValueById(valueId);
    }

    public void addInstanceValue(OntologyInstanceValue value) {
        instanceValueMapper.insertOntologyInstanceValue(value);
        log.info("实例属性值已添加: instanceId={}, propertyId={}", value.getInstanceId(), value.getPropertyId());
    }

    public void updateInstanceValue(OntologyInstanceValue value) {
        instanceValueMapper.updateOntologyInstanceValue(value);
        log.info("实例属性值已更新: {}", value.getValueId());
    }

    public void deleteInstanceValue(Long valueId) {
        instanceValueMapper.deleteOntologyInstanceValueById(valueId);
        log.info("实例属性值已删除: {}", valueId);
    }

    public void deleteInstanceValuesByIds(Long[] valueIds) {
        instanceValueMapper.deleteOntologyInstanceValueByIds(valueIds);
        log.info("实例属性值已批量删除");
    }

    public List<OntologyInstanceValue> getValuesByInstanceId(Long instanceId) {
        return instanceValueMapper.selectValuesByInstanceId(instanceId);
    }

    public void deleteValuesByInstanceId(Long instanceId) {
        instanceValueMapper.deleteValuesByInstanceId(instanceId);
        log.info("实例属性值已清空: instanceId={}", instanceId);
    }

    // ==================== Rule ====================

    public List<OntologyRule> listRules(OntologyRule rule) {
        return ruleMapper.selectOntologyRuleList(rule);
    }

    public OntologyRule getRule(Long ruleId) {
        return ruleMapper.selectOntologyRuleById(ruleId);
    }

    public void addRule(OntologyRule rule) {
        ruleMapper.insertOntologyRule(rule);
        log.info("业务规则已添加: {}", rule.getRuleName());
    }

    public void updateRule(OntologyRule rule) {
        ruleMapper.updateOntologyRule(rule);
        log.info("业务规则已更新: {}", rule.getRuleName());
    }

    public void deleteRule(Long ruleId) {
        ruleMapper.deleteOntologyRuleById(ruleId);
        log.info("业务规则已删除: {}", ruleId);
    }

    public void deleteRulesByIds(Long[] ruleIds) {
        ruleMapper.deleteOntologyRuleByIds(ruleIds);
        log.info("业务规则已批量删除");
    }

    public List<OntologyRule> getRulesByConceptId(Long conceptId) {
        return ruleMapper.selectRulesByConceptId(conceptId);
    }

    public List<OntologyRule> getAllEnabledRules() {
        return ruleMapper.selectAllEnabledRules();
    }

    public OntologyRule checkRuleCodeUnique(String ruleCode, Long ruleId) {
        return ruleMapper.checkRuleCodeUnique(ruleCode, ruleId);
    }

    // ==================== Action ====================

    public List<OntologyAction> listActions(OntologyAction action) {
        return actionMapper.selectOntologyActionList(action);
    }

    public OntologyAction getAction(Long actionId) {
        return actionMapper.selectOntologyActionById(actionId);
    }

    public void addAction(OntologyAction action) {
        actionMapper.insertOntologyAction(action);
        log.info("行为已添加: {}", action.getActionName());
    }

    public void updateAction(OntologyAction action) {
        actionMapper.updateOntologyAction(action);
        log.info("行为已更新: {}", action.getActionName());
    }

    public void deleteAction(Long actionId) {
        actionMapper.deleteOntologyActionById(actionId);
        log.info("行为已删除: {}", actionId);
    }

    public void deleteActionsByIds(Long[] actionIds) {
        actionMapper.deleteOntologyActionByIds(actionIds);
        log.info("行为已批量删除");
    }

    public List<OntologyAction> getActionsByConceptId(Long conceptId) {
        return actionMapper.selectActionsByConceptId(conceptId);
    }

    public OntologyAction checkActionCodeUnique(String actionCode, Long actionId) {
        return actionMapper.checkActionCodeUnique(actionCode, actionId);
    }

    // ==================== Field Mapping ====================

    public List<OntologyFieldMapping> listFieldMappings(OntologyFieldMapping fieldMapping) {
        return fieldMappingMapper.selectOntologyFieldMappingList(fieldMapping);
    }

    public OntologyFieldMapping getFieldMapping(Long fieldMappingId) {
        return fieldMappingMapper.selectOntologyFieldMappingById(fieldMappingId);
    }

    public void addFieldMapping(OntologyFieldMapping fieldMapping) {
        fieldMappingMapper.insertOntologyFieldMapping(fieldMapping);
        log.info("字段映射已添加: propertyCode={}, column={}", fieldMapping.getPropertyCode(), fieldMapping.getColumnName());
    }

    public void updateFieldMapping(OntologyFieldMapping fieldMapping) {
        fieldMappingMapper.updateOntologyFieldMapping(fieldMapping);
        log.info("字段映射已更新: {}", fieldMapping.getFieldMappingId());
    }

    public void deleteFieldMapping(Long fieldMappingId) {
        fieldMappingMapper.deleteOntologyFieldMappingById(fieldMappingId);
        log.info("字段映射已删除: {}", fieldMappingId);
    }

    public void deleteFieldMappingsByIds(Long[] fieldMappingIds) {
        fieldMappingMapper.deleteOntologyFieldMappingByIds(fieldMappingIds);
        log.info("字段映射已批量删除");
    }

    public List<OntologyFieldMapping> getFieldMappingsByMappingId(Long mappingId) {
        return fieldMappingMapper.selectFieldMappingsByMappingId(mappingId);
    }

    public List<OntologyFieldMapping> getFieldMappingsByPropertyCode(String propertyCode) {
        return fieldMappingMapper.selectFieldMappingsByPropertyCode(propertyCode);
    }

    public void deleteFieldMappingsByMappingId(Long mappingId) {
        fieldMappingMapper.deleteFieldMappingsByMappingId(mappingId);
        log.info("字段映射已清空: mappingId={}", mappingId);
    }
}
