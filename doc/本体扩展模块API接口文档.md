# 本体扩展模块 API 接口文档

**基础路径**: `/ai/ontology`
**权限前缀**: `ai:ontology:*`（目前所有扩展实体共享同一套权限）
**Content-Type**: `application/json`

---

## 目录

1. [属性管理 Property](#1-属性管理-property)
2. [实例管理 Instance](#2-实例管理-instance)
3. [实例属性值 InstanceValue](#3-实例属性值-instancevalue)
4. [业务规则 Rule](#4-业务规则-rule)
5. [行为 Action](#5-行为-action)
6. [字段映射 FieldMapping](#6-字段映射-fieldmapping)

---

## 1. 属性管理 Property

### 1.1 查询属性列表

```
GET /ai/ontology/property/list
```

**权限**: `ai:ontology:list`  
**分页**: ✅ 支持 `pageNum`/`pageSize` 参数

**请求参数**（Query）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| propertyName | String | 否 | 属性名称（模糊匹配） |
| propertyCode | String | 否 | 属性编码（精确匹配） |
| propertyType | String | 否 | 属性类型（STRING/INTEGER/DOUBLE/BOOLEAN/DATE/ENUM） |
| conceptId | Long | 否 | 所属概念ID |
| status | String | 否 | 状态（0=启用 1=禁用） |
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

**响应**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "rows": [
    {
      "propertyId": 1,
      "propertyName": "时长（秒）",
      "propertyCode": "duration",
      "propertyType": "INTEGER",
      "conceptId": 10,
      "conceptName": "音频内容",
      "required": "0",
      "defaultValue": null,
      "enumValues": null,
      "description": "音频时长，单位为秒",
      "sortOrder": 1,
      "status": "0",
      "createBy": "",
      "createTime": "2026-07-14 12:00:00",
      "updateBy": null,
      "updateTime": null
    }
  ],
  "total": 1
}
```

---

### 1.2 获取属性详情

```
GET /ai/ontology/property/{propertyId}
```

**权限**: `ai:ontology:query`

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| propertyId | Long | 属性ID |

**响应**：单体 `OntologyProperty` 对象，字段同上

---

### 1.3 查询指定概念的属性

```
GET /ai/ontology/property/concept/{conceptId}
```

**权限**: `ai:ontology:list`

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| conceptId | Long | 概念ID |

**响应**：`OntologyProperty[]` 数组

---

### 1.4 新增属性

```
POST /ai/ontology/property
```

**权限**: `ai:ontology:add`

**请求体**：

```json
{
  "propertyName": "比特率",
  "propertyCode": "bitrate",
  "propertyType": "ENUM",
  "conceptId": 10,
  "required": "0",
  "defaultValue": "320k",
  "enumValues": "[\"128k\",\"192k\",\"320k\"]",
  "description": "音频比特率",
  "sortOrder": 6,
  "status": "0"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| propertyName | String | 是 | 属性名称 |
| propertyCode | String | 是 | 属性编码，全局唯一 |
| propertyType | String | 是 | STRING/INTEGER/DOUBLE/BOOLEAN/DATE/ENUM |
| conceptId | Long | 是 | 所属概念ID |
| required | String | 否 | 0=非必填 1=必填，默认0 |
| defaultValue | String | 否 | 默认值 |
| enumValues | String | 否 | 枚举值，JSON数组字符串 |
| description | String | 否 | 属性描述 |
| sortOrder | Integer | 否 | 排序，默认0 |
| status | String | 否 | 0=启用 1=禁用，默认0 |

**校验规则**：`propertyCode` 全局唯一，重复返回错误

**响应**：
```json
{ "code": 200, "msg": "操作成功" }
```

---

### 1.5 修改属性

```
PUT /ai/ontology/property
```

**权限**: `ai:ontology:edit`

**请求体**：同新增（需包含 `propertyId`）

**校验规则**：`propertyCode` 修改时排他（排除自身ID）

---

### 1.6 删除属性

```
DELETE /ai/ontology/property/{propertyIds}
```

**权限**: `ai:ontology:remove`

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| propertyIds | Long[] | 属性ID数组（逗号分隔，如 `1,2,3`） |

---

### 1.7 导出属性

```
POST /ai/ontology/property/export
```

**权限**: `ai:ontology:export`  
**响应**: Excel 文件下载

---

## 2. 实例管理 Instance

### 2.1 查询实例列表

```
GET /ai/ontology/instance/list
```

**权限**: `ai:ontology:list`  
**分页**: ✅

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| instanceName | String | 否 | 实例名称（模糊匹配） |
| instanceCode | String | 否 | 实例编码（精确匹配） |
| conceptId | Long | 否 | 所属概念ID |
| status | String | 否 | 状态（0=启用 1=禁用） |

**响应**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "rows": [
    {
      "instanceId": 1,
      "instanceName": "Qwen2.5 3B",
      "instanceCode": "qwen2.5_3b",
      "conceptId": 3,
      "conceptName": "LLM",
      "description": "通义千问2.5 3B参数版本",
      "status": "0",
      "sortOrder": 1,
      "createBy": "",
      "createTime": "2026-07-14 12:00:00",
      "updateBy": null,
      "updateTime": null
    }
  ],
  "total": 1
}
```

---

### 2.2 获取实例详情

```
GET /ai/ontology/instance/{instanceId}
```

**权限**: `ai:ontology:query`

---

### 2.3 查询指定概念的实例

```
GET /ai/ontology/instance/concept/{conceptId}
```

**权限**: `ai:ontology:list`

---

### 2.4 新增实例

```
POST /ai/ontology/instance
```

**请求体**：

```json
{
  "instanceName": "Llama 3 8B",
  "instanceCode": "llama3_8b",
  "conceptId": 3,
  "description": "Meta开源大语言模型 8B版本",
  "sortOrder": 4,
  "status": "0"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| instanceName | String | 是 | 实例名称 |
| instanceCode | String | 是 | 实例编码，全局唯一 |
| conceptId | Long | 是 | 所属概念ID |
| description | String | 否 | 实例描述 |
| sortOrder | Integer | 否 | 排序 |
| status | String | 否 | 0=启用 1=禁用，默认0 |

**校验规则**：`instanceCode` 全局唯一

---

### 2.5 修改实例 / 2.6 删除实例 / 2.7 导出实例

```
PUT    /ai/ontology/instance
DELETE /ai/ontology/instance/{instanceIds}
POST   /ai/ontology/instance/export
```

与上述概念属性模式相同。

---

## 3. 实例属性值 InstanceValue

### 3.1 查询属性值列表

```
GET /ai/ontology/instance/value/list
```

**权限**: `ai:ontology:list`  
**分页**: ✅

**请求参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| instanceId | Long | 实例ID（精确过滤） |
| propertyId | Long | 属性ID（精确过滤） |

---

### 3.2 获取属性值详情

```
GET /ai/ontology/instance/value/{valueId}
```

---

### 3.3 查询指定实例的所有属性值

```
GET /ai/ontology/instance/value/by-instance/{instanceId}
```

**响应**：
```json
{
  "code": 200,
  "rows": [
    {
      "valueId": 1,
      "instanceId": 1,
      "propertyId": 6,
      "propertyName": "上下文窗口",
      "propertyValue": "32768"
    }
  ]
}
```

---

### 3.4 新增属性值

```
POST /ai/ontology/instance/value
```

**请求体**：

```json
{
  "instanceId": 1,
  "propertyId": 6,
  "propertyValue": "32768"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| instanceId | Long | 是 | 实例ID |
| propertyId | Long | 是 | 属性ID |
| propertyValue | String | 否 | 属性值（按属性类型存储） |

**注意**：同一实例+同一属性只能有一个值（UNIQUE KEY 约束）

---

### 3.5 修改属性值

```
PUT /ai/ontology/instance/value
```

**请求体**：需包含 `valueId`

---

### 3.6 删除属性值

```
DELETE /ai/ontology/instance/value/{valueIds}
```

---

## 4. 业务规则 Rule

### 4.1 查询规则列表

```
GET /ai/ontology/rule/list
```

**权限**: `ai:ontology:list`  
**分页**: ✅

**请求参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| ruleName | String | 规则名称（模糊匹配） |
| ruleCode | String | 规则编码（精确匹配） |
| conceptId | Long | 所属概念ID |
| enabled | String | 是否启用（0=禁用 1=启用） |

**响应**：
```json
{
  "code": 200,
  "rows": [
    {
      "ruleId": 1,
      "ruleName": "音频时长限制",
      "ruleCode": "audio_duration_limit",
      "conceptId": null,
      "conceptName": null,
      "condition": "{\"field\": \"duration\", \"operator\": \"gt\", \"value\": 7200}",
      "action": "{\"type\": \"warning\", \"message\": \"音频时长超过2小时，建议分段\"}",
      "priority": 1,
      "enabled": "1",
      "description": "所有音频内容的时长超过2小时时给出分段建议"
    }
  ]
}
```

### 4.2 获取规则详情

```
GET /ai/ontology/rule/{ruleId}
```

### 4.3 查询指定概念的规则

```
GET /ai/ontology/rule/concept/{conceptId}
```

返回该概念关联的所有规则（不含全局规则）。

### 4.4 查询所有启用的规则

```
GET /ai/ontology/rule/enabled
```

**权限**: 公开（无需登录）  
返回全部 `enabled = '1'` 的规则（含全局规则），供 AI 或规则引擎消费。

### 4.5 新增规则

```
POST /ai/ontology/rule
```

**请求体**：

```json
{
  "ruleName": "付费音频验证",
  "ruleCode": "paid_audio_check",
  "conceptId": 10,
  "condition": "{\"field\": \"is_paid\", \"operator\": \"eq\", \"value\": true}",
  "action": "{\"type\": \"block\", \"message\": \"请先购买后播放\"}",
  "priority": 1,
  "enabled": "1",
  "description": "付费音频需要先验证购买状态"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ruleName | String | 是 | 规则名称 |
| ruleCode | String | 是 | 规则编码，全局唯一 |
| conceptId | Long | 否 | 所属概念ID（null=全局规则） |
| condition | String | 否 | 条件JSON |
| action | String | 否 | 动作JSON |
| priority | Integer | 否 | 优先级，越小越优先，默认0 |
| enabled | String | 否 | 0=禁用 1=启用，默认1 |
| description | String | 否 | 规则描述 |

**condition JSON 约定**（建议方案，不强制）：

```json
// 比较运算
{"field": "duration", "operator": "gt", "value": 7200}
// 范围运算  
{"field": "temperature", "operator": "between", "value": [0, 2]}
// 等于
{"field": "is_paid", "operator": "eq", "value": true}
```

**action JSON 约定**（建议方案）：

```json
// 告警
{"type": "warning", "message": "提示信息"}
// 拦截
{"type": "block", "message": "拦截原因"}
// 覆盖
{"type": "override", "field": "is_paid", "value": false}
// 校验
{"type": "validate", "message": "校验失败原因"}
```

### 4.6 修改规则 / 4.7 删除规则 / 4.8 导出规则

```
PUT    /ai/ontology/rule
DELETE /ai/ontology/rule/{ruleIds}
POST   /ai/ontology/rule/export
```

---

## 5. 行为 Action

### 5.1 查询行为列表

```
GET /ai/ontology/action/list
```

**权限**: `ai:ontology:list`  
**分页**: ✅

**请求参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| actionName | String | 行为名称（模糊匹配） |
| actionCode | String | 行为编码（精确匹配） |
| conceptId | Long | 所属概念ID |
| actionType | String | 行为类型（TOOL/API/PROMPT） |
| status | String | 状态（0=启用 1=禁用） |

**响应**：
```json
{
  "code": 200,
  "rows": [
    {
      "actionId": 1,
      "actionName": "文本对话",
      "actionCode": "chat",
      "conceptId": 3,
      "conceptName": "LLM",
      "actionType": "TOOL",
      "target": "chatCompletion",
      "parameters": "{\"method\": \"chat\", \"params\": {\"messages\": \"对话消息列表\"}}",
      "description": "与LLM进行文本对话交互",
      "status": "0"
    }
  ]
}
```

### 5.2 获取行为详情

```
GET /ai/ontology/action/{actionId}
```

### 5.3 查询指定概念的行为

```
GET /ai/ontology/action/concept/{conceptId}
```

### 5.4 新增行为

```
POST /ai/ontology/action
```

**请求体**：

```json
{
  "actionName": "文本转语音",
  "actionCode": "tts",
  "conceptId": 10,
  "actionType": "API",
  "target": "/api/v1/tts",
  "parameters": "{\"method\": \"POST\", \"params\": {\"text\": \"待转换文本\", \"voice\": \"default\"}}",
  "description": "将文本转换为语音",
  "status": "0"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| actionName | String | 是 | 行为名称 |
| actionCode | String | 是 | 行为编码，全局唯一 |
| conceptId | Long | 否 | 所属概念ID（null=全局行为） |
| actionType | String | 是 | TOOL=工具调用, API=HTTP接口, PROMPT=提示词模板 |
| target | String | 否 | 目标：方法名/API路径/Prompt Key |
| parameters | String | 否 | 参数配置JSON |
| description | String | 否 | 行为描述 |
| status | String | 否 | 0=启用 1=禁用，默认0 |

### 5.5 修改行为 / 5.6 删除行为 / 5.7 导出行

```
PUT    /ai/ontology/action
DELETE /ai/ontology/action/{actionIds}
POST   /ai/ontology/action/export
```

---

## 6. 字段映射 FieldMapping

### 6.1 查询字段映射列表

```
GET /ai/ontology/field-mapping/list
```

**权限**: `ai:ontology:list`  
**分页**: ✅

**请求参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| mappingId | Long | 所属映射ID |
| propertyCode | String | 属性编码 |
| columnName | String | 业务表字段名 |

**响应**：
```json
{
  "code": 200,
  "rows": [
    {
      "fieldMappingId": 1,
      "mappingId": 1,
      "propertyCode": "duration",
      "columnName": "duration",
      "defaultValue": null
    }
  ]
}
```

### 6.2 获取字段映射详情

```
GET /ai/ontology/field-mapping/{fieldMappingId}
```

### 6.3 查询指定映射的字段映射

```
GET /ai/ontology/field-mapping/by-mapping/{mappingId}
```

返回某条概念映射（`ai_ontology_mapping`）下所有的字段级映射。

### 6.4 新增字段映射

```
POST /ai/ontology/field-mapping
```

**请求体**：

```json
{
  "mappingId": 1,
  "propertyCode": "format",
  "columnName": "file_format",
  "defaultValue": "MP3"
}
```

### 6.5 修改字段映射

```
PUT /ai/ontology/field-mapping
```

### 6.6 删除字段映射

```
DELETE /ai/ontology/field-mapping/{fieldMappingIds}
```

---

## 附录 A：LLM 调用工具

后端通过 `OntologyExtendedTool` 暴露了 5 个 `@Tool` 给 LLM：

| 工具名 | 功能 | 参数 |
|--------|------|------|
| `queryConceptProperties` | 查询概念属性定义 | conceptName |
| `queryInstancesByConcept` | 查询概念实例列表 | conceptName |
| `queryInstanceValues` | 查询实例属性值 | instanceCode |
| `queryRulesForConcept` | 查询概念的业务规则 | conceptIdentifier |
| `queryActionsForConcept` | 查询概念的可执行行为 | conceptIdentifier |

LLM 可在对话中主动调用这些工具获取本体信息。

---

## 附录 B：现有本体 API

以下为已有的基础 API（未在本文档展开）：

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/ai/ontology/concept/list` | 概念列表 |
| GET | `/ai/ontology/concept/{conceptId}` | 概念详情 |
| POST | `/ai/ontology/concept` | 新增概念 |
| PUT | `/ai/ontology/concept` | 修改概念 |
| DELETE | `/ai/ontology/concept/{conceptIds}` | 删除概念 |
| GET | `/ai/ontology/concept/enabled` | 启用概念列表 |
| GET | `/ai/ontology/concept/children/{parentId}` | 子概念列表 |
| POST | `/ai/ontology/concept/export` | 导出概念 |
| GET | `/ai/ontology/relation/list` | 关系列表 |
| GET | `/ai/ontology/relation/{relationId}` | 关系详情 |
| POST | `/ai/ontology/relation` | 新增关系 |
| PUT | `/ai/ontology/relation` | 修改关系 |
| DELETE | `/ai/ontology/relation/{relationIds}` | 删除关系 |
| GET | `/ai/ontology/relation/concept/{conceptId}` | 概念的关系 |
| GET | `/ai/ontology/relation/between` | 两概念间的关系 |
| POST | `/ai/ontology/reason` | 本体推理 |
| GET | `/ai/ontology/knowledge` | 知识图谱文本 |
| GET | `/ai/ontology/related/{conceptId}` | 相关概念 |
