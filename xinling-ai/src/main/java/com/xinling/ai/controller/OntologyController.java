package com.xinling.ai.controller;

import com.xinling.ai.domain.ontology.OntologyConcept;
import com.xinling.ai.domain.ontology.OntologyRelation;
import com.xinling.ai.domain.ontology.OntologyProperty;
import com.xinling.ai.domain.ontology.OntologyInstance;
import com.xinling.ai.domain.ontology.OntologyInstanceValue;
import com.xinling.ai.domain.ontology.OntologyRule;
import com.xinling.ai.domain.ontology.OntologyAction;
import com.xinling.ai.domain.ontology.OntologyFieldMapping;
import com.xinling.ai.service.OntologyExportService;
import com.xinling.ai.service.OntologyService;
import com.xinling.ai.service.RuleEngineService;
import com.xinling.ai.service.RuleEngineService.RuleEvalResult;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import com.xinling.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 本体管理Controller
 * 提供本体概念和关系的管理接口
 *
 * @author SuXia
 * @date 2026/07/06
 */
@RestController
@RequestMapping("/ai/ontology")
public class OntologyController extends BaseController {

    @Autowired
    private OntologyService ontologyService;

    @Autowired(required = false)
    private RuleEngineService ruleEngineService;

    @Autowired(required = false)
    private OntologyExportService ontologyExportService;

    /**
     * 查询本体概念列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/concept/list")
    public TableDataInfo listConcept(OntologyConcept concept) {
        startPage();
        List<OntologyConcept> list = ontologyService.listConcepts(concept);
        return getDataTable(list);
    }

    /**
     * 导出本体概念列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:export')")
    @Log(title = "本体概念", businessType = BusinessType.EXPORT)
    @PostMapping("/concept/export")
    public void exportConcept(HttpServletResponse response, OntologyConcept concept) {
        List<OntologyConcept> list = ontologyService.listConcepts(concept);
        ExcelUtil<OntologyConcept> util = new ExcelUtil<>(OntologyConcept.class);
        util.exportExcel(response, list, "本体概念数据");
    }

    /**
     * 获取本体概念详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:query')")
    @GetMapping(value = "/concept/{conceptId}")
    public AjaxResult getConceptInfo(@PathVariable("conceptId") Long conceptId) {
        return success(ontologyService.getConcept(conceptId));
    }

    /**
     * 新增本体概念
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:add')")
    @Log(title = "本体概念", businessType = BusinessType.INSERT)
    @PostMapping("/concept")
    public AjaxResult addConcept(@RequestBody OntologyConcept concept) {
        if (ontologyService.checkConceptCodeUnique(concept.getConceptCode(), null) != null) {
            return error("新增概念'" + concept.getConceptName() + "'失败，概念编码已存在");
        }
        if (ontologyService.checkConceptNameUnique(concept.getConceptName(), null) != null) {
            return error("新增概念'" + concept.getConceptName() + "'失败，概念名称已存在");
        }
        concept.setCreateBy(getUsername());
        concept.setStatus("0");
        ontologyService.addConcept(concept);
        return success();
    }

    /**
     * 修改本体概念
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:edit')")
    @Log(title = "本体概念", businessType = BusinessType.UPDATE)
    @PutMapping("/concept")
    public AjaxResult editConcept(@RequestBody OntologyConcept concept) {
        if (ontologyService.checkConceptCodeUnique(concept.getConceptCode(), concept.getConceptId()) != null) {
            return error("修改概念'" + concept.getConceptName() + "'失败，概念编码已存在");
        }
        if (ontologyService.checkConceptNameUnique(concept.getConceptName(), concept.getConceptId()) != null) {
            return error("修改概念'" + concept.getConceptName() + "'失败，概念名称已存在");
        }
        concept.setUpdateBy(getUsername());
        ontologyService.updateConcept(concept);
        return success();
    }

    /**
     * 删除本体概念（级联删除相关关系）
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:remove')")
    @Log(title = "本体概念", businessType = BusinessType.DELETE)
    @DeleteMapping("/concept/{conceptIds}")
    public AjaxResult removeConcept(@PathVariable Long[] conceptIds) {
        for (Long conceptId : conceptIds) {
            ontologyService.deleteConcept(conceptId);
        }
        return success();
    }

    /**
     * 获取所有启用的概念
     */
    @GetMapping("/concept/enabled")
    public AjaxResult getEnabledConcepts() {
        return success(ontologyService.getAllEnabledConcepts());
    }

    /**
     * 查询子概念列表
     */
    @GetMapping("/concept/children/{parentId}")
    public AjaxResult getChildConcepts(@PathVariable Long parentId) {
        return success(ontologyService.getChildConcepts(parentId));
    }

    /**
     * 查询本体关系列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/relation/list")
    public TableDataInfo listRelation(OntologyRelation relation) {
        startPage();
        List<OntologyRelation> list = ontologyService.listRelations(relation);
        return getDataTable(list);
    }

    /**
     * 获取本体关系详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:query')")
    @GetMapping(value = "/relation/{relationId}")
    public AjaxResult getRelationInfo(@PathVariable("relationId") Long relationId) {
        return success(ontologyService.getRelationById(relationId));
    }

    /**
     * 新增本体关系
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:add')")
    @Log(title = "本体关系", businessType = BusinessType.INSERT)
    @PostMapping("/relation")
    public AjaxResult addRelation(@RequestBody OntologyRelation relation) {
        ontologyService.addRelation(relation);
        return success();
    }

    /**
     * 修改本体关系
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:edit')")
    @Log(title = "本体关系", businessType = BusinessType.UPDATE)
    @PutMapping("/relation")
    public AjaxResult editRelation(@RequestBody OntologyRelation relation) {
        ontologyService.updateRelation(relation);
        return success();
    }

    /**
     * 删除本体关系
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:remove')")
    @Log(title = "本体关系", businessType = BusinessType.DELETE)
    @DeleteMapping("/relation/{relationIds}")
    public AjaxResult removeRelation(@PathVariable Long[] relationIds) {
        ontologyService.deleteRelationsByIds(relationIds);
        return success();
    }

    /**
     * 查询与指定概念相关的所有关系
     */
    @GetMapping("/relation/concept/{conceptId}")
    public AjaxResult getRelationsByConcept(@PathVariable Long conceptId) {
        return success(ontologyService.getRelationsByConceptId(conceptId));
    }

    /**
     * 查询指定两个概念之间的关系
     */
    @GetMapping("/relation/between")
    public AjaxResult getRelationBetween(@RequestParam Long sourceId, @RequestParam Long targetId) {
        return success(ontologyService.findRelationBetween(sourceId, targetId));
    }

    /**
     * 本体推理问答
     */
    @PostMapping("/reason")
    public AjaxResult reason(@RequestBody java.util.Map<String, String> request) {
        String query = request.get("query");
        if (query == null || query.isEmpty()) {
            return error("查询内容不能为空");
        }
        String result = ontologyService.reason(query);
        return success(result);
    }

    /**
     * 获取本体知识图谱文本表示
     */
    @GetMapping("/knowledge")
    public AjaxResult getOntologyKnowledge() {
        return success(ontologyService.getOntologyText());
    }

    /**
     * 查询与指定概念相关的概念
     */
    @GetMapping("/related/{conceptId}")
    public AjaxResult getRelatedConcepts(@PathVariable Long conceptId) {
        return success(ontologyService.findRelatedConcepts(conceptId));
    }

    // ==================== Property ====================

    /**
     * 查询本体属性列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/property/list")
    public TableDataInfo listProperty(OntologyProperty property) {
        startPage();
        List<OntologyProperty> list = ontologyService.listProperties(property);
        return getDataTable(list);
    }

    /**
     * 导出本体属性列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:export')")
    @Log(title = "本体属性", businessType = BusinessType.EXPORT)
    @PostMapping("/property/export")
    public void exportProperty(HttpServletResponse response, OntologyProperty property) {
        List<OntologyProperty> list = ontologyService.listProperties(property);
        ExcelUtil<OntologyProperty> util = new ExcelUtil<>(OntologyProperty.class);
        util.exportExcel(response, list, "本体属性数据");
    }

    /**
     * 获取本体属性详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:query')")
    @GetMapping("/property/{propertyId}")
    public AjaxResult getPropertyInfo(@PathVariable Long propertyId) {
        return success(ontologyService.getProperty(propertyId));
    }

    /**
     * 查询指定概念的属性
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/property/concept/{conceptId}")
    public AjaxResult getPropertiesByConcept(@PathVariable Long conceptId) {
        return success(ontologyService.getPropertiesByConceptId(conceptId));
    }

    /**
     * 新增本体属性
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:add')")
    @Log(title = "本体属性", businessType = BusinessType.INSERT)
    @PostMapping("/property")
    public AjaxResult addProperty(@RequestBody OntologyProperty property) {
        if (ontologyService.checkPropertyCodeUnique(property.getPropertyCode(), null) != null) {
            return error("新增属性'" + property.getPropertyName() + "'失败，属性编码已存在");
        }
        property.setCreateBy(getUsername());
        property.setStatus("0");
        ontologyService.addProperty(property);
        return success();
    }

    /**
     * 修改本体属性
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:edit')")
    @Log(title = "本体属性", businessType = BusinessType.UPDATE)
    @PutMapping("/property")
    public AjaxResult editProperty(@RequestBody OntologyProperty property) {
        if (ontologyService.checkPropertyCodeUnique(property.getPropertyCode(), property.getPropertyId()) != null) {
            return error("修改属性'" + property.getPropertyName() + "'失败，属性编码已存在");
        }
        property.setUpdateBy(getUsername());
        ontologyService.updateProperty(property);
        return success();
    }

    /**
     * 删除本体属性
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:remove')")
    @Log(title = "本体属性", businessType = BusinessType.DELETE)
    @DeleteMapping("/property/{propertyIds}")
    public AjaxResult removeProperty(@PathVariable Long[] propertyIds) {
        ontologyService.deletePropertiesByIds(propertyIds);
        return success();
    }

    // ==================== Instance ====================

    /**
     * 查询本体实例列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/instance/list")
    public TableDataInfo listInstance(OntologyInstance instance) {
        startPage();
        List<OntologyInstance> list = ontologyService.listInstances(instance);
        return getDataTable(list);
    }

    /**
     * 导出本体实例列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:export')")
    @Log(title = "本体实例", businessType = BusinessType.EXPORT)
    @PostMapping("/instance/export")
    public void exportInstance(HttpServletResponse response, OntologyInstance instance) {
        List<OntologyInstance> list = ontologyService.listInstances(instance);
        ExcelUtil<OntologyInstance> util = new ExcelUtil<>(OntologyInstance.class);
        util.exportExcel(response, list, "本体实例数据");
    }

    /**
     * 获取本体实例详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:query')")
    @GetMapping("/instance/{instanceId}")
    public AjaxResult getInstanceInfo(@PathVariable Long instanceId) {
        return success(ontologyService.getInstance(instanceId));
    }

    /**
     * 查询指定概念的实例
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/instance/concept/{conceptId}")
    public AjaxResult getInstancesByConcept(@PathVariable Long conceptId) {
        return success(ontologyService.getInstancesByConceptId(conceptId));
    }

    /**
     * 新增本体实例
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:add')")
    @Log(title = "本体实例", businessType = BusinessType.INSERT)
    @PostMapping("/instance")
    public AjaxResult addInstance(@RequestBody OntologyInstance instance) {
        if (ontologyService.checkInstanceCodeUnique(instance.getInstanceCode(), null) != null) {
            return error("新增实例'" + instance.getInstanceName() + "'失败，实例编码已存在");
        }
        instance.setCreateBy(getUsername());
        instance.setStatus("0");
        ontologyService.addInstance(instance);
        return success();
    }

    /**
     * 修改本体实例
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:edit')")
    @Log(title = "本体实例", businessType = BusinessType.UPDATE)
    @PutMapping("/instance")
    public AjaxResult editInstance(@RequestBody OntologyInstance instance) {
        if (ontologyService.checkInstanceCodeUnique(instance.getInstanceCode(), instance.getInstanceId()) != null) {
            return error("修改实例'" + instance.getInstanceName() + "'失败，实例编码已存在");
        }
        instance.setUpdateBy(getUsername());
        ontologyService.updateInstance(instance);
        return success();
    }

    /**
     * 删除本体实例
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:remove')")
    @Log(title = "本体实例", businessType = BusinessType.DELETE)
    @DeleteMapping("/instance/{instanceIds}")
    public AjaxResult removeInstance(@PathVariable Long[] instanceIds) {
        ontologyService.deleteInstancesByIds(instanceIds);
        return success();
    }

    // ==================== Instance Value ====================

    /**
     * 查询实例属性值列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/instance/value/list")
    public TableDataInfo listInstanceValue(OntologyInstanceValue value) {
        startPage();
        List<OntologyInstanceValue> list = ontologyService.listInstanceValues(value);
        return getDataTable(list);
    }

    /**
     * 获取实例属性值详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:query')")
    @GetMapping("/instance/value/{valueId}")
    public AjaxResult getInstanceValueInfo(@PathVariable Long valueId) {
        return success(ontologyService.getInstanceValue(valueId));
    }

    /**
     * 查询指定实例的所有属性值
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/instance/value/by-instance/{instanceId}")
    public AjaxResult getValuesByInstance(@PathVariable Long instanceId) {
        return success(ontologyService.getValuesByInstanceId(instanceId));
    }

    /**
     * 新增实例属性值
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:add')")
    @Log(title = "实例属性值", businessType = BusinessType.INSERT)
    @PostMapping("/instance/value")
    public AjaxResult addInstanceValue(@RequestBody OntologyInstanceValue value) {
        value.setCreateBy(getUsername());
        ontologyService.addInstanceValue(value);
        return success();
    }

    /**
     * 修改实例属性值
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:edit')")
    @Log(title = "实例属性值", businessType = BusinessType.UPDATE)
    @PutMapping("/instance/value")
    public AjaxResult editInstanceValue(@RequestBody OntologyInstanceValue value) {
        value.setUpdateBy(getUsername());
        ontologyService.updateInstanceValue(value);
        return success();
    }

    /**
     * 删除实例属性值
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:remove')")
    @Log(title = "实例属性值", businessType = BusinessType.DELETE)
    @DeleteMapping("/instance/value/{valueIds}")
    public AjaxResult removeInstanceValue(@PathVariable Long[] valueIds) {
        ontologyService.deleteInstanceValuesByIds(valueIds);
        return success();
    }

    // ==================== Rule ====================

    /**
     * 查询业务规则列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/rule/list")
    public TableDataInfo listRule(OntologyRule rule) {
        startPage();
        List<OntologyRule> list = ontologyService.listRules(rule);
        return getDataTable(list);
    }

    /**
     * 导出业务规则列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:export')")
    @Log(title = "业务规则", businessType = BusinessType.EXPORT)
    @PostMapping("/rule/export")
    public void exportRule(HttpServletResponse response, OntologyRule rule) {
        List<OntologyRule> list = ontologyService.listRules(rule);
        ExcelUtil<OntologyRule> util = new ExcelUtil<>(OntologyRule.class);
        util.exportExcel(response, list, "业务规则数据");
    }

    /**
     * 获取业务规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:query')")
    @GetMapping("/rule/{ruleId}")
    public AjaxResult getRuleInfo(@PathVariable Long ruleId) {
        return success(ontologyService.getRule(ruleId));
    }

    /**
     * 查询指定概念的规则
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/rule/concept/{conceptId}")
    public AjaxResult getRulesByConcept(@PathVariable Long conceptId) {
        return success(ontologyService.getRulesByConceptId(conceptId));
    }

    /**
     * 查询所有启用的规则
     */
    @GetMapping("/rule/enabled")
    public AjaxResult getEnabledRules() {
        return success(ontologyService.getAllEnabledRules());
    }

    /**
     * 新增业务规则
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:add')")
    @Log(title = "业务规则", businessType = BusinessType.INSERT)
    @PostMapping("/rule")
    public AjaxResult addRule(@RequestBody OntologyRule rule) {
        if (ontologyService.checkRuleCodeUnique(rule.getRuleCode(), null) != null) {
            return error("新增规则'" + rule.getRuleName() + "'失败，规则编码已存在");
        }
        rule.setCreateBy(getUsername());
        if (rule.getEnabled() == null) {
            rule.setEnabled("1");
        }
        ontologyService.addRule(rule);
        return success();
    }

    /**
     * 修改业务规则
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:edit')")
    @Log(title = "业务规则", businessType = BusinessType.UPDATE)
    @PutMapping("/rule")
    public AjaxResult editRule(@RequestBody OntologyRule rule) {
        if (ontologyService.checkRuleCodeUnique(rule.getRuleCode(), rule.getRuleId()) != null) {
            return error("修改规则'" + rule.getRuleName() + "'失败，规则编码已存在");
        }
        rule.setUpdateBy(getUsername());
        ontologyService.updateRule(rule);
        return success();
    }

    /**
     * 删除业务规则
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:remove')")
    @Log(title = "业务规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/rule/{ruleIds}")
    public AjaxResult removeRule(@PathVariable Long[] ruleIds) {
        ontologyService.deleteRulesByIds(ruleIds);
        return success();
    }

    // ==================== Rule Evaluation ====================

    /**
     * 评估指定概念的规则
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:query')")
    @PostMapping("/rule/evaluate/{conceptId}")
    public AjaxResult evaluateRules(@PathVariable Long conceptId, @RequestBody Map<String, Object> context) {
        if (ruleEngineService == null) {
            return error("规则引擎未注入");
        }
        List<RuleEvalResult> results = ruleEngineService.evaluateRulesForConcept(conceptId, context);
        return success(results);
    }

    // ==================== Export / Import ====================

    /**
     * 导出完整本体图谱（JSON）
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:export')")
    @GetMapping("/export")
    public AjaxResult exportOntology() {
        if (ontologyExportService == null) {
            return error("导出服务未注入");
        }
        String json = ontologyExportService.exportFullOntology();
        return success(json);
    }

    /**
     * 导入完整本体图谱（JSON）
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:import')")
    @Log(title = "本体导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importOntology(@RequestBody Map<String, String> request) {
        if (ontologyExportService == null) {
            return error("导入服务未注入");
        }
        String json = request.get("data");
        if (json == null || json.isBlank()) {
            return error("导入数据不能为空");
        }
        String result = ontologyExportService.importFullOntology(json);
        return success(result);
    }

    // ==================== Action ====================

    /**
     * 查询行为列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/action/list")
    public TableDataInfo listAction(OntologyAction action) {
        startPage();
        List<OntologyAction> list = ontologyService.listActions(action);
        return getDataTable(list);
    }

    /**
     * 导出行列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:export')")
    @Log(title = "行为", businessType = BusinessType.EXPORT)
    @PostMapping("/action/export")
    public void exportAction(HttpServletResponse response, OntologyAction action) {
        List<OntologyAction> list = ontologyService.listActions(action);
        ExcelUtil<OntologyAction> util = new ExcelUtil<>(OntologyAction.class);
        util.exportExcel(response, list, "行为数据");
    }

    /**
     * 获取行为详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:query')")
    @GetMapping("/action/{actionId}")
    public AjaxResult getActionInfo(@PathVariable Long actionId) {
        return success(ontologyService.getAction(actionId));
    }

    /**
     * 查询指定概念的行为
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/action/concept/{conceptId}")
    public AjaxResult getActionsByConcept(@PathVariable Long conceptId) {
        return success(ontologyService.getActionsByConceptId(conceptId));
    }

    /**
     * 新增行为
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:add')")
    @Log(title = "行为", businessType = BusinessType.INSERT)
    @PostMapping("/action")
    public AjaxResult addAction(@RequestBody OntologyAction action) {
        if (ontologyService.checkActionCodeUnique(action.getActionCode(), null) != null) {
            return error("新增行为'" + action.getActionName() + "'失败，行为编码已存在");
        }
        action.setCreateBy(getUsername());
        action.setStatus("0");
        ontologyService.addAction(action);
        return success();
    }

    /**
     * 修改行为
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:edit')")
    @Log(title = "行为", businessType = BusinessType.UPDATE)
    @PutMapping("/action")
    public AjaxResult editAction(@RequestBody OntologyAction action) {
        if (ontologyService.checkActionCodeUnique(action.getActionCode(), action.getActionId()) != null) {
            return error("修改行为'" + action.getActionName() + "'失败，行为编码已存在");
        }
        action.setUpdateBy(getUsername());
        ontologyService.updateAction(action);
        return success();
    }

    /**
     * 删除行为
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:remove')")
    @Log(title = "行为", businessType = BusinessType.DELETE)
    @DeleteMapping("/action/{actionIds}")
    public AjaxResult removeAction(@PathVariable Long[] actionIds) {
        ontologyService.deleteActionsByIds(actionIds);
        return success();
    }

    // ==================== Field Mapping ====================

    /**
     * 查询字段映射列表
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/field-mapping/list")
    public TableDataInfo listFieldMapping(OntologyFieldMapping fieldMapping) {
        startPage();
        List<OntologyFieldMapping> list = ontologyService.listFieldMappings(fieldMapping);
        return getDataTable(list);
    }

    /**
     * 获取字段映射详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:query')")
    @GetMapping("/field-mapping/{fieldMappingId}")
    public AjaxResult getFieldMappingInfo(@PathVariable Long fieldMappingId) {
        return success(ontologyService.getFieldMapping(fieldMappingId));
    }

    /**
     * 查询指定映射的字段映射
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:list')")
    @GetMapping("/field-mapping/by-mapping/{mappingId}")
    public AjaxResult getFieldMappingsByMapping(@PathVariable Long mappingId) {
        return success(ontologyService.getFieldMappingsByMappingId(mappingId));
    }

    /**
     * 新增字段映射
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:add')")
    @Log(title = "字段映射", businessType = BusinessType.INSERT)
    @PostMapping("/field-mapping")
    public AjaxResult addFieldMapping(@RequestBody OntologyFieldMapping fieldMapping) {
        fieldMapping.setCreateBy(getUsername());
        ontologyService.addFieldMapping(fieldMapping);
        return success();
    }

    /**
     * 修改字段映射
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:edit')")
    @Log(title = "字段映射", businessType = BusinessType.UPDATE)
    @PutMapping("/field-mapping")
    public AjaxResult editFieldMapping(@RequestBody OntologyFieldMapping fieldMapping) {
        fieldMapping.setUpdateBy(getUsername());
        ontologyService.updateFieldMapping(fieldMapping);
        return success();
    }

    /**
     * 删除字段映射
     */
    @PreAuthorize("@ss.hasPermi('ai:ontology:remove')")
    @Log(title = "字段映射", businessType = BusinessType.DELETE)
    @DeleteMapping("/field-mapping/{fieldMappingIds}")
    public AjaxResult removeFieldMapping(@PathVariable Long[] fieldMappingIds) {
        ontologyService.deleteFieldMappingsByIds(fieldMappingIds);
        return success();
    }
}
