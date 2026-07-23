# 心聆平台 - 本体AI系统架构文档

> 版本：1.0.0  
> 最后更新：2026-07-17  
> 模块：xinling-ai

---

## 一、概述

心聆平台本体AI系统是一个**基于知识图谱的智能推理系统**，通过自定义本体引擎（Ontology Engine）构建业务知识图谱，为 LLM 提供结构化的业务知识支撑，从而实现精准的业务理解和推理问答。

### 核心定位

```
用户提问 → LLM（理解意图）
              ↓
        本体引擎（查询图谱）
              ↓
        工具调用（获取数据）
              ↓
        LLM（生成回答）
```

本体系统在整个 AI 架构中扮演**"业务知识中枢"**的角色——它将心聆平台的所有业务对象、关系、属性、规则和行为编织成一个可被 LLM 理解和查询的知识网络。

---

## 二、系统架构

### 2.1 模块层次

```
┌─────────────────────────────────────────────────────────┐
│                    前端 / 用户界面                        │
│   管理后台 (Vue)         移动端 (Vue3)    API 客户端      │
└──────────────────────┬──────────────────────────────────┘
                       │ HTTP/SSE
┌──────────────────────▼──────────────────────────────────┐
│              Controller 层 (REST API)                    │
│  OntologyController    AiModelConfigController           │
│  AiPromptController    AiSessionConfigController         │
│  ChatController        AiManagementController            │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│              Service 层 (业务逻辑)                       │
│  ┌────────────────┐  ┌──────────────────────────────┐   │
│  │ OntologyService │  │ ChatMemoryProviderService   │   │
│  │  (本体CRUD+推理) │  │  (会话管理+提示词注入)      │   │
│  └───────┬────────┘  └──────────┬───────────────────┘   │
│          │                      │                        │
│  ┌───────▼──────────────────────▼───────────────────┐   │
│  │         AI 服务 / LangChain4j                     │   │
│  │  ┌──────────────┐  ┌────────────────────────┐    │   │
│  │  │ XinLingAssist.│  │   Tool (6个)           │    │   │
│  │  │ (AiServices)  │  │  OntologyQueryTool     │    │   │
│  │  └──────────────┘  │  OntologyExtendedTool   │    │   │
│  │                    │  DatabaseQueryTool       │    │   │
│  │                    │  KnowledgeSearchTool     │    │   │
│  │                    │  SchemaQueryTool         │    │   │
│  │                    │  DateTimeTool            │    │   │
│  │                    └────────────────────────┘    │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│         Mapper / DAO 层 (MyBatis + MySQL)               │
│  9个本体表 + 4个AI配置表 + 2个MQ表                       │
└──────────────────────────────────────────────────────────┘
```

### 2.2 数据流

```
用户: "白噪音有哪些？"
  │
  ▼
LLM: 理解意图 → 判断需要查本体
  │                                 ┌─────────────────┐
  ▼                                 │  ai_ontology_   │
OntologyQueryTool.getAllConcepts()──┤  concept         │
  │                    ┌───────────┤  ai_ontology_   │
  ▼                    │           │  relation        │
查询白噪音的子概念──────┘           │  ai_ontology_   │
  │                               │  mapping         │
  ▼                               │  ...             │
LLM: 组装回答                      └─────────────────┘
  │
  ▼
用户: "白噪音有风声、雷声，常用于专注和睡眠"
```

---

## 三、数据库设计

本体系统共 9 张表，分为**核心层**和**扩展层**两类。

### 3.1 核心层（ontology.sql）

#### ai_ontology_concept — 概念表

平台所有业务对象的抽象概念定义，支持父子层级。

| 字段 | 类型 | 说明 |
|------|------|------|
| concept_id | BIGINT PK | 概念ID |
| concept_name | VARCHAR(100) | 概念名称 |
| concept_code | VARCHAR(50) UNIQUE | 概念编码 |
| description | TEXT | 概念描述 |
| parent_id | BIGINT FK | 父概念ID（自引用） |
| category | VARCHAR(50) | 概念类别 |
| status | CHAR(1) | 状态：0启用 1禁用 |
| sort_order | INT | 排序 |

#### ai_ontology_relation — 关系表

概念之间的语义关系，构成知识图谱的边。

| 字段 | 类型 | 说明 |
|------|------|------|
| relation_id | BIGINT PK | 关系ID |
| source_concept_id | BIGINT FK | 源概念ID |
| target_concept_id | BIGINT FK | 目标概念ID |
| relation_type | VARCHAR(50) | 关系类型 |
| description | TEXT | 关系描述 |

**关系类型**：`part-of`(组成)、`is-a`(属于)、`related-to`(关联)、`status-of`(状态)、`role-of`(角色)

#### ai_ontology_mapping — 映射表（核心桥接）

**将概念与任意业务表记录关联**，实现"概念→数据"的通用桥接。

| 字段 | 类型 | 说明 |
|------|------|------|
| mapping_id | BIGINT PK | 映射ID |
| concept_code | VARCHAR(50) | 概念编码 |
| table_name | VARCHAR(100) | 业务表名 |
| record_id | BIGINT | 业务记录ID |

设计优势：
- 业务表无需增加任何字段
- 支持一对多（一条记录可关联多个概念）
- 新增业务表只需在此表登记，零侵入

### 3.2 扩展层（ontology_extended.sql）

#### ai_ontology_property — 属性表

概念的动态属性定义。

| 字段 | 类型 | 说明 |
|------|------|------|
| property_id | BIGINT PK | 属性ID |
| property_name | VARCHAR(100) | 属性名称 |
| property_code | VARCHAR(50) UNIQUE | 属性编码 |
| property_type | VARCHAR(20) | 类型：STRING/INTEGER/DOUBLE/BOOLEAN/DATE/ENUM |
| concept_id | BIGINT FK | 所属概念ID |
| required | CHAR(1) | 是否必填 |
| enum_values | TEXT | 枚举值列表(JSON) |
| description | TEXT | 属性描述 |

#### ai_ontology_instance — 实例表

概念的具体实例/对象。

| 字段 | 类型 | 说明 |
|------|------|------|
| instance_id | BIGINT PK | 实例ID |
| instance_name | VARCHAR(100) | 实例名称 |
| instance_code | VARCHAR(50) | 实例编码 |
| concept_id | BIGINT FK | 所属概念ID |
| status | CHAR(1) | 状态 |

#### ai_ontology_instance_value — 实例属性值表

实例的具体属性键值对。

| 字段 | 类型 | 说明 |
|------|------|------|
| value_id | BIGINT PK | 属性值ID |
| instance_id | BIGINT FK | 实例ID |
| property_id | BIGINT FK | 属性ID |
| property_value | TEXT | 属性值 |

#### ai_ontology_rule — 业务规则表

声明式业务规则，可绑定到概念或全局。

| 字段 | 类型 | 说明 |
|------|------|------|
| rule_id | BIGINT PK | 规则ID |
| rule_name | VARCHAR(100) | 规则名称 |
| rule_code | VARCHAR(50) | 规则编码 |
| concept_id | BIGINT FK | 所属概念ID（NULL=全局） |
| condition | TEXT(JSON) | 触发条件 |
| action | TEXT(JSON) | 触发动作 |
| priority | INT | 优先级 |
| enabled | CHAR(1) | 是否启用 |

#### ai_ontology_action — 行为表

概念级可执行操作，定义了概念"能做哪些事"。

| 字段 | 类型 | 说明 |
|------|------|------|
| action_id | BIGINT PK | 行为ID |
| action_name | VARCHAR(100) | 行为名称 |
| action_code | VARCHAR(50) | 行为编码 |
| concept_id | BIGINT FK | 所属概念ID |
| action_type | VARCHAR(20) | 类型：TOOL/API/PROMPT |
| target | VARCHAR(500) | 目标（方法名/API地址/Prompt Key） |
| parameters | TEXT(JSON) | 参数定义 |

#### ai_ontology_field_mapping — 字段映射表

属性到业务表字段的精细映射。

| 字段 | 类型 | 说明 |
|------|------|------|
| field_mapping_id | BIGINT PK | 字段映射ID |
| mapping_id | BIGINT FK | 映射ID |
| property_code | VARCHAR(50) FK | 属性编码 |
| column_name | VARCHAR(100) | 业务表字段名 |

---

## 四、领域模型

### 4.1 包结构

```
com.xinling.ai.domain.ontology/
├── OntologyConcept.java        概念
├── OntologyRelation.java       关系
├── OntologyProperty.java       属性
├── OntologyInstance.java       实例
├── OntologyInstanceValue.java  实例属性值
├── OntologyRule.java           业务规则
├── OntologyAction.java         行为
├── OntologyMapping.java        概念-业务映射
└── OntologyFieldMapping.java   字段映射
```

所有实体继承自 `BaseEntity`（提供 createBy / createTime / updateBy / updateTime 审计字段），使用 Lombok `@Data` 简化。

### 4.2 核心实体关系

```
OntologyConcept (父概念)
  ├── parent_id → OntologyConcept (树形层级)
  ├── OntologyRelation.sourceConceptId → "源头"
  ├── OntologyRelation.targetConceptId → "目标"
  ├── OntologyProperty.conceptId → "有哪些属性"
  ├── OntologyInstance.conceptId → "有哪些实例"
  ├── OntologyRule.conceptId → "有哪些规则"
  ├── OntologyAction.conceptId → "有哪些行为"
  └── OntologyMapping.conceptCode → "关联了哪些业务数据"

OntologyInstance
  └── OntologyInstanceValue.instanceId + propertyId → "实例的属性值"
  
OntologyMapping
  └── OntologyFieldMapping.mappingId → "字段级映射"
```

---

## 五、Service 层

### 5.1 OntologyService

完整提供所有本体对象的 CRUD + 推理能力。

**核心方法：**

```java
// === 概念 CRUD ===
addConcept() / updateConcept() / deleteConcept() / getConcept() / listConcepts()

// === 关系管理 ===
addRelation() / findRelationBetween() / findRelatedConcepts()
getRelationsByConceptId()

// === 属性管理 ===
listProperties() / addProperty() / getPropertiesByConceptId()

// === 实例管理 ===
listInstances() / addInstance() / getInstancesByConceptId()

// === 实例属性值 ===
listInstanceValues() / getValuesByInstanceId()

// === 规则管理 ===
listRules() / addRule() / getRulesByConceptId() / getAllEnabledRules()

// === 行为管理 ===
listActions() / addAction() / getActionsByConceptId()

// === 映射管理 ===
listMappings() / getMappingsByConceptCode()

// === 字段映射 ===
listFieldMappings() / getFieldMappingsByMappingId()

// === 推理 ===
reason(String query)       // 基于本体图谱的 LLM 推理问答
getOntologyText()          // 获取知识图谱文本表示
ragQuery(String query)     // 本体增强 RAG 查询
```

### 5.2 推理实现

`reason()` 方法的流程：

1. **加载本体文本**：调用 `getOntologyText()` 从数据库读取所有概念和关系
2. **加载提示词**：从 `ai_prompt` 表加载 ontology 配置关联的系统提示词模板
3. **模板替换**：将 `{ontology}` 占位符替换为实际的本体文本
4. **LLM 推理**：构造 `SystemMessage + UserMessage` 调用 ChatModel
5. **返回结果**：返回 LLM 生成的推理回答

### 5.3 编码唯一性校验

每个实体都提供 `checkXxxCodeUnique()` 方法，确保编码在各自范围内唯一。

---

## 六、API 层

### 6.1 OntologyController

路径前缀：`/ai/ontology`

| 端点 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/concept/list` | GET | 概念列表 | ai:ontology:list |
| `/concept/{id}` | GET | 概念详情 | ai:ontology:query |
| `/concept` | POST | 新增概念 | ai:ontology:add |
| `/concept` | PUT | 修改概念 | ai:ontology:edit |
| `/concept/{ids}` | DELETE | 删除概念 | ai:ontology:remove |
| `/concept/enabled` | GET | 所有启用概念 | - |
| `/concept/children/{parentId}` | GET | 子概念 | - |
| `/relation/list` | GET | 关系列表 | ai:ontology:list |
| `/relation/{id}` | GET | 关系详情 | ai:ontology:query |
| `/relation` | POST | 新增关系 | ai:ontology:add |
| `/relation` | PUT | 修改关系 | ai:ontology:edit |
| `/relation/{ids}` | DELETE | 删除关系 | ai:ontology:remove |
| `/relation/concept/{id}` | GET | 概念的所有关系 | - |
| `/relation/between` | GET | 两概念间关系 | - |
| `/reason` | POST | 本体推理问答 | - |
| `/knowledge` | GET | 知识图谱文本 | - |
| `/related/{id}` | GET | 相关概念 | - |
| `/property/*` | CRUD | 属性管理 | ai:ontology:* |
| `/instance/*` | CRUD | 实例管理 | ai:ontology:* |
| `/instance/value/*` | CRUD | 实例属性值 | ai:ontology:* |
| `/rule/*` | CRUD | 规则管理 | ai:ontology:* |
| `/rule/enabled` | GET | 所有启用规则 | - |
| `/action/*` | CRUD | 行为管理 | ai:ontology:* |
| `/field-mapping/*` | CRUD | 字段映射 | ai:ontology:* |

**权限前缀说明**：所有本体管理端点共用 `ai:ontology` 权限体系（list/query/add/edit/remove/export），由管理后台配置。

---

## 七、AI 集成层

### 7.1 工具系统（LLM Function Calling）

本体系统通过两个工具被 LLM 调用：

#### OntologyQueryTool — 本体基础查询

| 工具 | 说明 | LLM 使用场景 |
|------|------|-------------|
| `queryConceptDefinition(name)` | 查询概念定义 | "什么是冥想？" |
| `findRelatedConcepts(name)` | 查找相关概念 | "和冥想有关的有哪些？" |
| `queryRelationBetween(s, t)` | 查询两概念关系 | "白噪音和睡眠有什么关系？" |
| `ontologyReasoning(query)` | 本体推理问答 | "连续专注7天有什么奖励？" |
| `getAllConcepts()` | 获取所有概念 | "平台上有什么功能？" |
| `queryChildConcepts(parent)` | 查询子概念 | "内容体系包括哪些？" |
| `queryRelatedData(code)` | 查关联业务数据 | "白噪音有哪些音频素材？" |

#### OntologyExtendedTool — 本体扩展查询

| 工具 | 说明 | LLM 使用场景 |
|------|------|-------------|
| `queryConceptProperties(name)` | 概念属性定义 | "专注记录有哪些字段？" |
| `queryInstancesByConcept(name)` | 概念实例 | "有哪些挑战活动？" |
| `queryInstanceValues(code)` | 实例属性值 | "番茄专注模式的时长？" |
| `queryRulesForConcept(name)` | 业务规则 | "VIP到期有什么规则？" |
| `queryActionsForConcept(name)` | 可执行行为 | "专注功能能做什么操作？" |

### 7.2 工具注册

在 `AiServiceConfig` 中，所有工具通过 `AiServices.builder()` 注册到 `XinLingAssistant`：

```java
@Bean
public XinLingAssistant xinLingAssistant() {
    return AiServices.builder(XinLingAssistant.class)
            .chatModel(chatLanguageModel)
            .streamingChatModel(streamingChatLanguageModel)
            .chatMemoryProvider(chatMemoryProvider)
            .tools(
                databaseQueryTool,
                knowledgeSearchTool,
                dateTimeTool,
                ontologyQueryTool,
                schemaQueryTool,
                ontologyExtendedTool
            )
            .build();
}
```

### 7.3 提示词系统

AI 的行为通过三层配置控制：

```
AiSessionConfig（会话配置）
  ├── configKey: "ontology" / "platform" / "nl2sql" / "mobile"
  ├── chatModelId → 指定使用的模型
  ├── embeddingModelId → 指定嵌入模型
  ├── enableRag → 是否启用 RAG
  └── prompts[] → 关联的提示词列表

AiPrompt（提示词模板）
  ├── content → 提示词内容（支持 {ontology} 等变量替换）
  ├── modelId → 适配的模型（NULL=通用）
  └── status → 启用状态
```

**Session → Config 映射**：通过 Redis 缓存 `sessionId → configId` 的映射，`ChatMemoryProviderService` 在创建会话时自动加载对应的提示词注入到 ChatMemory。

### 7.4 RAG 场景集成

在 `RagExecutor` 中，本体问答作为独立场景：

```java
switch (scene) {
    case ONTOLOGY_QA:
        return ontologyService.reason(query);  // 走本体推理
    case NL2SQL:
        return databaseRagService.queryDatabaseInfo(query);
    case QA:
    case KNOWLEDGE_QA:
    default:
        // 走向量检索 RAG
}
```

`QueryTypeUtils.detect()` 自动判断用户问题属于哪个场景。

---

## 八、知识图谱数据

### 8.1 概念层级结构

系统预置了**8大体系、60+概念**的完整知识图谱：

```
心聆平台 (xinling_platform)
├── 用户体系 (user_system)
│   ├── APP用户 (app_user)
│   ├── VIP会员 (vip_member)
│   └── 分销员 (distributor)
├── 内容体系 (content_system)
│   ├── 音频素材 (audio_item)
│   │   ├── 白噪音 (white_noise)
│   │   └── 自然音 (nature_sound)
│   ├── 音频混音 (audio_mix)
│   ├── 冥想内容 (meditation)
│   │   └── 冥想音频 (meditation_audio)
│   ├── 冥想作者 (meditation_author)
│   └── 背景图 (content_bg)
├── 功能体系 (feature_system)
│   ├── 专注 (focus)
│   ├── 睡眠 (sleep)
│   ├── 心理测评 (psyc_test)
│   ├── 动态社区 (moment)
│   └── AI聊天 (ai_chat)
├── 成长体系 (growth_system)
│   ├── 成就 (achievement)
│   ├── 挑战 (challenge)
│   ├── 每日任务 (daily_task)
│   └── 积分商城 (mall_goods)
├── 营销体系 (marketing_system)
│   ├── 活动 (activity)
│   ├── 优惠券 (coupon)
│   └── 分销 (distribution)
├── 交易体系 (trade_system)
│   ├── 支付订单 (pay_order)
│   ├── 支付交易 (pay_transaction)
│   └── 自动续费 (auto_renew)
└── 通知体系 (notification_system)
    └── 通知消息 (notification)
```

### 8.2 关系类型及数量

- **part-of**（组成关系）：概念 A 是概念 B 的组成部分
- **is-a**（类别关系）：概念 A 是概念 B 的一种类型
- **related-to**（关联关系）：概念 A 与概念 B 业务相关
- **status-of**（状态关系）：概念 A 是概念 B 的状态
- **role-of**（角色关系）：概念 A 是概念 B 的特殊角色

### 8.3 属性定义

预置了跨多个概念的属性定义：

| 所属概念 | 属性数 | 示例 |
|---------|--------|------|
| APP用户 | 3 | 昵称(STRING)、性别(ENUM)、VIP状态(ENUM) |
| 音频素材 | 5 | 标题(STRING)、时长(INTEGER)、作者(STRING)、标签(STRING)、状态(ENUM) |
| 专注 | 4 | 时长(INTEGER)、模式(ENUM)、标签(STRING)、状态(ENUM) |
| 冥想内容 | 2 | 标题(STRING)、状态(ENUM) |
| 挑战 | 3 | 类型(ENUM)、持续天数(INTEGER)、积分奖励(INTEGER) |
| 活动 | 2 | 活动类型(ENUM)、状态(ENUM) |
| 优惠券 | 2 | 券类型(ENUM)、优惠值(DOUBLE) |

### 8.4 实例数据

预置了 15+ 个具体实例：

| 类型 | 实例 | 说明 |
|------|------|------|
| 专注模式 | 番茄专注、深度专注、自由专注 | 三种专注计时模式 |
| 白噪音 | 风声、雷声 | 对应 audio_item 表记录 |
| 自然音 | 蝉鸣 | 对应 audio_item 表记录 |
| 挑战 | 21天早起、30天专注、7天冥想 | 对应 challenge 表 |
| 优惠券 | 新用户立减券、满50减10 | 对应 coupon 表 |
| 积分商品 | 7天VIP、30天VIP、星空头像框 | 对应 mall_goods 表 |
| 心理测评 | 自我接纳问卷、SDS抑郁自评 | 对应 psyc_test 表 |

### 8.5 业务规则

预置了 6 条规则（全局3条 + 概念级3条）：

| 规则 | 条件 | 动作 | 说明 |
|------|------|------|------|
| 专注时长推荐 | duration_minutes < 5 | 建议至少5分钟 | 全局 |
| VIP到期提醒 | 到期前7天 | 发送续费通知 | 全局 |
| 新用户优惠 | 注册≤3天 | 发放新人优惠券 | 全局 |
| 连续专注奖励 | 连续≥7天 | 奖励100积分 | 专注概念 |
| 付费音频限制 | 非VIP→付费音频 | 阻止播放 | 音频素材 |
| 白噪音免费 | 无条件 | 默认免费 | 白噪音概念 |

### 8.6 可执行行为

预置 10+ 个行为定义，标记了概念支持的操作：

| 行为 | 类型 | 目标 | 所属概念 |
|------|------|------|---------|
| App数据查询 | TOOL | queryAppData | 全局 |
| 发送通知 | API | /api/app/notification/send | 全局 |
| 开始专注 | API | /api/app/focus/start | 专注 |
| 查询专注统计 | TOOL | queryFocusStatistics | 专注 |
| 记录睡眠日记 | API | /api/app/sleep/diary | 睡眠 |
| 查询睡眠报告 | TOOL | querySleepReport | 睡眠 |
| 参与测评 | API | /api/app/psyc/test/submit | 心理测评 |
| 发布动态 | API | /api/app/moment/publish | 动态社区 |
| 参与挑战 | API | /api/app/challenge/join | 挑战 |
| 兑换商品 | API | /api/app/mall/redeem | 积分商城 |
| 查询活动列表 | TOOL | queryActivityList | 活动 |
| 参与活动 | API | /api/app/activity/join | 活动 |

---

## 九、配置管理

### 9.1 AI 模型提供商配置

| 提供商 | 编码 | 类型 | API地址 |
|--------|------|------|---------|
| Ollama本地服务 | ollama | local | http://localhost:11434 |
| 阿里云百炼 | aliyun-bailian | cloud | https://dashscope.aliyuncs.com/compatible-mode/v1 |
| OpenAI | openai | cloud | https://api.openai.com/v1 |
| 智谱AI | zhipu-ai | cloud | https://open.bigmodel.cn/api/paas/v4 |
| DeepSeek | deepseek | cloud | https://api.deepseek.com/v1 |

### 9.2 会话配置键

| configKey | 用途 | 说明 |
|-----------|------|------|
| platform | 平台通用助手 | 默认配置 |
| ontology | 本体推理 | 使用本体知识图谱回答 |
| mobile | 移动端助手 | 移动端聊天配置 |
| nl2sql | 自然语言转SQL | 数据查询场景 |

---

## 十、部署和初始化

### 10.1 SQL 脚本执行顺序

```bash
# 1. 基础业务表（如已存在可跳过）
xinling-ai/src/main/resources/sql/su_crm.sql

# 2. 本体核心表（概念、关系、映射）
xinling-ai/src/main/resources/sql/ontology.sql

# 3. 本体扩展表（属性、实例、规则、行为、字段映射）
xinling-ai/src/main/resources/sql/ontology_extended.sql

# 4. 聊天消息表
xinling-ai/src/main/resources/sql/chat_message.sql
```

### 10.2 系统启动初始化

在 `AiInitializationConfig` 中，系统启动时会：
1. 异步加载知识库文档到 EmbeddingStore
2. 使用 Redis 分布式锁防止集群重复初始化
3. Record 级别的初始化进度日志

---

## 十一、技术栈

| 组件 | 版本/选型 |
|------|-----------|
| 语言 | Java 21 |
| 框架 | Spring Boot 3.5.4 |
| AI框架 | LangChain4j 1.10.0 |
| 数据库 | MySQL 8.0+ |
| ORM | MyBatis (RuoYi风格) |
| 模型支持 | Ollama / OpenAI / 阿里云百炼 / DeepSeek / 智谱AI |
| 缓存 | Redis (本体文本缓存 + session映射) |
| 消息队列 | RabbitMQ / Kafka (可选) |

---

## 十二、本体AI应用场景示例

### 场景1：用户提问"白噪音有哪些？"

```
用户 → LLM
  → 识别到"白噪音"是本体概念
  → 调用 OntologyQueryTool.getAllConcepts() → 找到白噪音概念
  → 调用 OntologyQueryTool.queryChildConcepts("白噪音")
  → 返回：风声、雷声
  → 调用 OntologyQueryTool.queryRelatedData("white_noise")
  → 关联到 audio_item 表
  → LLM 组装回答
```

### 场景2：用户提问"连续专注7天有什么奖励？"

```
用户 → LLM
  → 调用 OntologyQueryTool.queryRulesForConcept("focus")
  → 找到规则：连续专注≥7天 → 奖励100积分
  → LLM 回答"连续专注7天可获得100积分奖励"
```

### 场景3：用户提问"我想开始番茄专注"

```
用户 → LLM
  → 调用 OntologyExtendedTool.queryActionsForConcept("focus")
  → 找到行为：start_focus（API: /api/app/focus/start）
  → 调用 OntologyExtendedTool.queryInstancesByConcept("focus")
  → 找到实例：番茄专注、深度专注、自由专注
  → LLM 确认"是否开始25分钟番茄专注？"
  → 用户确认 → 调用 API 执行
```

---

## 十三、开发指南

### 13.1 新增一个概念

```sql
-- 1. 添加概念
INSERT INTO ai_ontology_concept (concept_name, concept_code, description, parent_id, category)
VALUES ('感恩日记', 'gratitude_journal', '记录每日感恩事项的功能', 20, '功能');

-- 2. 添加关系
INSERT INTO ai_ontology_relation (source_concept_id, target_concept_id, relation_type, description)
VALUES (新ID, 20, 'part-of', '感恩日记是功能体系的一部分');

-- 3. 添加属性
INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id)
VALUES ('心情评分', 'mood_score', 'INTEGER', 新ID);

-- 4. 可选：添加实例和行为
```

### 13.2 新增一个工具

```java
@Component
public class MyNewTool {
    @Autowired
    private OntologyService ontologyService;
    
    @Tool("我的新工具描述")
    public String myNewMethod(@P("参数说明") String param) {
        // 实现逻辑
    }
}
```

然后在 `AiServiceConfig` 中注册到 `.tools()` 链中。

---

## 十四、项目文件清单

### Java 源文件（29个）

```
xinling-ai/src/main/java/com/xinling/ai/
├── config/
│   ├── AiServiceConfig.java           AiServices组装
│   ├── LangChain4jConfig.java         LangChain4j Bean定义
│   └── AiConfigProperties.java        配置属性绑定
├── controller/
│   ├── OntologyController.java        本体管理REST API
│   ├── ChatController.java            AI聊天接口
│   ├── AiModelConfigController.java   模型配置管理
│   ├── AiModelProviderController.java 提供商管理
│   ├── AiPromptController.java        提示词管理
│   ├── AiSessionConfigController.java 会话配置管理
│   └── AiManagementController.java    管理操作
├── service/
│   ├── OntologyService.java           本体CRUD+推理
│   ├── AiDynamicModelManager.java     动态模型管理
│   ├── RagExecutor.java               RAG执行器
│   ├── memory/
│   │   ├── ChatMemoryProviderService.java  会话+提示词注入
│   │   └── PromptAwareChatMemory.java      自定义ChatMemory
│   └── tools/
│       ├── OntologyQueryTool.java     本体查询工具
│       ├── OntologyExtendedTool.java  本体扩展查询工具
│       ├── DatabaseQueryTool.java     数据库查询工具
│       ├── SchemaQueryTool.java       表结构查询工具
│       ├── KnowledgeSearchTool.java   知识库搜索工具
│       └── DateTimeTool.java          日期时间工具
├── domain/
│   ├── ontology/                      9个本体实体
│   └── config/                        AiModelConfig等
├── mapper/                            9个本体Mapper接口
└── config/exception/                  全局异常处理
```

### SQL 文件

```
xinling-ai/src/main/resources/sql/
├── ontology.sql                 本体核心表 + 种子数据
├── ontology_extended.sql        本体扩展表 + 完整种子数据
└── chat_message.sql             聊天消息表
```

### Mapper XML 文件

```
xinling-ai/src/main/resources/mapper/ai/
├── OntologyConceptMapper.xml
├── OntologyRelationMapper.xml
├── OntologyPropertyMapper.xml
├── OntologyInstanceMapper.xml
├── OntologyInstanceValueMapper.xml
├── OntologyRuleMapper.xml
├── OntologyActionMapper.xml
├── OntologyMappingMapper.xml
├── OntologyFieldMappingMapper.xml
├── AiModelConfigMapper.xml
├── AiModelProviderMapper.xml
├── AiPromptMapper.xml
└── AiSessionConfigMapper.xml
```

---

## 附录

### A. 权限标识

| 权限标识 | 说明 |
|---------|------|
| `ai:model:list/query/add/edit/remove` | 模型配置管理 |
| `ai:provider:list/query/add/edit/remove` | 提供商管理 |
| `ai:ontology:list/query/add/edit/remove/export` | 本体管理 |
| `ai:prompt:list/query/add/edit/remove` | 提示词管理 |
| `ai:session:list/query/add/edit/remove` | 会话配置管理 |

### B. 配置项（application.yml）

```yaml
ai:
  model:
    provider: ollama           # 默认提供商
    default-model: qwen2.5:3b  # 默认模型
    use-local: true            # 是否使用本地模型
  ollama:
    base-url: http://localhost:11434
    chat-model: qwen2.5:3b
    embedding-model: nomic-embed-text
    timeout-seconds: 360
  rag:
    max-results: 5
    min-score: 0.3
    chunk-size: 500
    enable-initialization: true
  langfuse:
    enabled: false
```
