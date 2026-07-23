import request from './request'
import type { ApiResponse } from '@/types/api'

export interface OntologyConcept {
  conceptId: number
  conceptName: string
  conceptCode: string
  description: string
  parentId: number
  parentName: string
  category: string
  status: string
  sortOrder: number
}

export interface OntologyRelation {
  relationId: number
  sourceConceptId: number
  sourceConceptName: string
  targetConceptId: number
  targetConceptName: string
  relationType: string
  description: string
}

export interface OntologyProperty {
  propertyId: number
  propertyName: string
  propertyCode: string
  propertyType: string
  conceptId: number
  conceptName: string
  required: string
  defaultValue: string
  enumValues: string
  description: string
  sortOrder: number
  status: string
}

export interface OntologyInstance {
  instanceId: number
  instanceName: string
  instanceCode: string
  conceptId: number
  conceptName: string
  description: string
  status: string
  sortOrder: number
}

export interface OntologyInstanceValue {
  valueId: number
  instanceId: number
  propertyId: number
  propertyName: string
  propertyValue: string
}

export interface OntologyRule {
  ruleId: number
  ruleName: string
  ruleCode: string
  conceptId: number
  conceptName: string
  condition: string
  action: string
  priority: number
  enabled: string
  description: string
}

export interface OntologyAction {
  actionId: number
  actionName: string
  actionCode: string
  conceptId: number
  conceptName: string
  actionType: string
  target: string
  parameters: string
  description: string
  status: string
}

export interface OntologyFieldMapping {
  fieldMappingId: number
  mappingId: number
  propertyCode: string
  columnName: string
  defaultValue: string
}

export function listConcepts(params?: Partial<OntologyConcept>): Promise<ApiResponse<{ rows: OntologyConcept[]; total: number }>> {
  return request.get('/ai/ontology/concept/list', { params })
}

export function getConcept(conceptId: number): Promise<ApiResponse<OntologyConcept>> {
  return request.get(`/ai/ontology/concept/${conceptId}`)
}

export function addConcept(data: Omit<OntologyConcept, 'conceptId' | 'parentName'>): Promise<ApiResponse> {
  return request.post('/ai/ontology/concept', data)
}

export function updateConcept(data: OntologyConcept): Promise<ApiResponse> {
  return request.put('/ai/ontology/concept', data)
}

export function deleteConcept(conceptIds: number[]): Promise<ApiResponse> {
  return request.delete(`/ai/ontology/concept/${conceptIds.join(',')}`)
}

export function getEnabledConcepts(): Promise<ApiResponse<OntologyConcept[]>> {
  return request.get('/ai/ontology/concept/enabled')
}

export function getChildConcepts(parentId: number): Promise<ApiResponse<OntologyConcept[]>> {
  return request.get(`/ai/ontology/concept/children/${parentId}`)
}

export function listRelations(params?: Partial<OntologyRelation>): Promise<ApiResponse<{ rows: OntologyRelation[]; total: number }>> {
  return request.get('/ai/ontology/relation/list', { params })
}

export function getRelation(relationId: number): Promise<ApiResponse<OntologyRelation>> {
  return request.get(`/ai/ontology/relation/${relationId}`)
}

export function addRelation(data: Omit<OntologyRelation, 'relationId' | 'sourceConceptName' | 'targetConceptName'>): Promise<ApiResponse> {
  return request.post('/ai/ontology/relation', data)
}

export function updateRelation(data: OntologyRelation): Promise<ApiResponse> {
  return request.put('/ai/ontology/relation', data)
}

export function deleteRelation(relationIds: number[]): Promise<ApiResponse> {
  return request.delete(`/ai/ontology/relation/${relationIds.join(',')}`)
}

export function getRelationsByConcept(conceptId: number): Promise<ApiResponse<OntologyRelation[]>> {
  return request.get(`/ai/ontology/relation/concept/${conceptId}`)
}

export function getRelationBetween(sourceId: number, targetId: number): Promise<ApiResponse<OntologyRelation>> {
  return request.get('/ai/ontology/relation/between', { params: { sourceId, targetId } })
}

export function ontologyReason(query: string): Promise<ApiResponse<string>> {
  return request.post('/ai/ontology/reason', { query })
}

export function getOntologyKnowledge(): Promise<ApiResponse<string>> {
  return request.get('/ai/ontology/knowledge')
}

export function getRelatedConcepts(conceptId: number): Promise<ApiResponse<OntologyConcept[]>> {
  return request.get(`/ai/ontology/related/${conceptId}`)
}

// ==================== Property ====================

export function listProperties(params?: Partial<OntologyProperty>): Promise<ApiResponse<{ rows: OntologyProperty[]; total: number }>> {
  return request.get('/ai/ontology/property/list', { params })
}

export function getProperty(propertyId: number): Promise<ApiResponse<OntologyProperty>> {
  return request.get(`/ai/ontology/property/${propertyId}`)
}

export function getPropertiesByConcept(conceptId: number): Promise<ApiResponse<OntologyProperty[]>> {
  return request.get(`/ai/ontology/property/concept/${conceptId}`)
}

// ==================== Instance ====================

export function listInstances(params?: Partial<OntologyInstance>): Promise<ApiResponse<{ rows: OntologyInstance[]; total: number }>> {
  return request.get('/ai/ontology/instance/list', { params })
}

export function getInstance(instanceId: number): Promise<ApiResponse<OntologyInstance>> {
  return request.get(`/ai/ontology/instance/${instanceId}`)
}

export function getInstancesByConcept(conceptId: number): Promise<ApiResponse<OntologyInstance[]>> {
  return request.get(`/ai/ontology/instance/concept/${conceptId}`)
}

// ==================== Instance Value ====================

export function listInstanceValues(params?: Partial<OntologyInstanceValue>): Promise<ApiResponse<{ rows: OntologyInstanceValue[]; total: number }>> {
  return request.get('/ai/ontology/instance/value/list', { params })
}

export function getValuesByInstance(instanceId: number): Promise<ApiResponse<OntologyInstanceValue[]>> {
  return request.get(`/ai/ontology/instance/value/by-instance/${instanceId}`)
}

// ==================== Rule ====================

export function listRules(params?: Partial<OntologyRule>): Promise<ApiResponse<{ rows: OntologyRule[]; total: number }>> {
  return request.get('/ai/ontology/rule/list', { params })
}

export function getRule(ruleId: number): Promise<ApiResponse<OntologyRule>> {
  return request.get(`/ai/ontology/rule/${ruleId}`)
}

export function getRulesByConcept(conceptId: number): Promise<ApiResponse<OntologyRule[]>> {
  return request.get(`/ai/ontology/rule/concept/${conceptId}`)
}

export function getEnabledRules(): Promise<ApiResponse<OntologyRule[]>> {
  return request.get('/ai/ontology/rule/enabled')
}

// ==================== Action ====================

export function listActions(params?: Partial<OntologyAction>): Promise<ApiResponse<{ rows: OntologyAction[]; total: number }>> {
  return request.get('/ai/ontology/action/list', { params })
}

export function getAction(actionId: number): Promise<ApiResponse<OntologyAction>> {
  return request.get(`/ai/ontology/action/${actionId}`)
}

export function getActionsByConcept(conceptId: number): Promise<ApiResponse<OntologyAction[]>> {
  return request.get(`/ai/ontology/action/concept/${conceptId}`)
}

// ==================== Field Mapping ====================

export function listFieldMappings(params?: Partial<OntologyFieldMapping>): Promise<ApiResponse<{ rows: OntologyFieldMapping[]; total: number }>> {
  return request.get('/ai/ontology/field-mapping/list', { params })
}

export function getFieldMappingsByMapping(mappingId: number): Promise<ApiResponse<OntologyFieldMapping[]>> {
  return request.get(`/ai/ontology/field-mapping/by-mapping/${mappingId}`)
}