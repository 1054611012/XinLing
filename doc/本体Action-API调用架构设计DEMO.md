# 本体 Action - API 调用架构设计 DEMO

> 版本：1.0.0-DEMO
> 模块：xinling-ai
> 定位：解决"复杂系统下本体（Ontology）如何合理调用 API 接口"的设计方案与代码 DEMO
> 前置阅读：[本体AI系统架构文档.md](./本体AI系统架构文档.md)

---

## 一、问题分析

### 1.1 现状

当前本体系统的 `ai_ontology_action` 表已能描述"概念能做什么事"：

```sql
('开始专注', 'start_focus', 'API', '/api/app/focus/start', '{"duration": "integer"}')
('兑换商品', 'redeem_goods', 'API', '/api/app/mall/redeem', '{"goodsId": "long"}')
```

LLM 通过 `OntologyExtendedTool.queryActionsForConcept()` 可以**查到**这些行为，但链路在"执行"这一步是断的：

```
用户: "帮我开始25分钟番茄专注"
  → LLM 查到 start_focus 行为 ✓
  → LLM 复述"是否开始25分钟番茄专注？" ✓
  → 用户确认 ✓
  → 调用 API 执行 ✗ ← 没有执行引擎，断在这里
```

### 1.2 复杂系统下的五个核心矛盾

| # | 矛盾 | 说明 |
|---|------|------|
| 1 | **定义太薄** | `target` + `parameters` 两个字段无法表达 HTTP 方法、鉴权方式、超时、重试、幂等、风险等级 |
| 2 | **无执行引擎** | 没有统一的 Action 调度入口，每个动作各写各的会迅速失控 |
| 3 | **参数不可信** | LLM 从对话抽取的参数可能幻觉；`userId` 等身份字段**绝不能**由 LLM 填写 |
| 4 | **写操作无防护** | 发布动态、兑换商品、支付都是写操作，LLM 幻觉直接造成脏数据/资损 |
| 5 | **单体自调 HTTP** | target 指向本系统 REST 路径，自己 HTTP 调自己：要带 token、多一跳网络、鉴权链路易死循环 |

### 1.3 设计目标

1. Action 从"描述性元数据"升级为"**可执行的函数定义**"
2. 本体与业务 API 之间增加**执行层**，LLM 永远不直接碰业务接口
3. 读操作自动化，写操作**人在环路**（Human-in-the-loop）
4. 单体架构下内部动作**直连 Service**，不为调用自己而走 HTTP
5. 全链路可审计、可幂等、可灰度

---

## 二、总体架构

### 2.1 分层架构

```
┌────────────────────────────────────────────────────────────┐
│  LLM 层 (LangChain4j AiServices)                            │
│  XinLingAssistant + ActionCallTool（唯一新增 Tool）          │
└──────────────────────┬─────────────────────────────────────┘
                       │ actionCode + 抽取的参数(JSON)
┌──────────────────────▼─────────────────────────────────────┐
│  编排层 ActionOrchestrator                                  │
│  ① 加载动作定义   ② 参数Schema校验   ③ 上下文注入            │
│  ④ 风险分级判断   ⑤ 确认流(挂起/恢复)  ⑥ 路由执行器          │
└──────────────────────┬─────────────────────────────────────┘
                       │
┌──────────────────────▼─────────────────────────────────────┐
│  执行层 ActionExecutor（策略模式，按 executorType 路由）      │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │ToolExec. │ │ServiceEx.│ │ HttpExec.│ │  MqExec. │       │
│  │SpringBean│ │内部Service│ │外部三方API│ │异步任务  │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└──────────────────────┬─────────────────────────────────────┘
                       │
┌──────────────────────▼─────────────────────────────────────┐
│  支撑层                                                     │
│  确认单(Redis)  执行日志(MySQL)  幂等键(Redis)  限流(Redis)  │
└────────────────────────────────────────────────────────────┘
```

### 2.2 关键决策：内部动作为什么直连 Service 而不是 HTTP

| 方案 | 优点 | 缺点 | 结论 |
|------|------|------|------|
| 自调 HTTP REST | 与 APP 接口完全复用 | 需伪造/传递 token；多一跳网络；超时叠加；鉴权过滤器链可能拦截 AI 调用 | ✗ |
| **直连内部 Service** | 无网络开销；事务可加入；权限显式校验；异常类型不丢失 | 需要薄适配层 | ✓ 内部动作默认 |
| MQ 异步 | 削峰、长任务不阻塞会话 | 结果需回调/轮询 | ✓ 长耗时动作 |

> 原则：**HTTP 执行器只用于真正的外部三方 API**（如短信、支付网关、地图）。本系统动作一律走 `SERVICE` 执行器。

---

## 三、数据模型设计

### 3.1 扩展 `ai_ontology_action`（DDL 增量）

```sql
ALTER TABLE ai_ontology_action
    ADD COLUMN executor_type  VARCHAR(20)  NOT NULL DEFAULT 'TOOL'
        COMMENT '执行器类型：TOOL/SERVICE/HTTP/MQ',
    ADD COLUMN risk_level     VARCHAR(20)  NOT NULL DEFAULT 'READ'
        COMMENT '风险等级：READ只读/WRITE写操作/FINANCIAL资金相关',
    ADD COLUMN confirm_policy VARCHAR(20)  NOT NULL DEFAULT 'AUTO'
        COMMENT '确认策略：AUTO直接执行/CONFIRM需用户确认/DOUBLE二次确认',
    ADD COLUMN http_method    VARCHAR(10)  NULL COMMENT 'HTTP方法(仅HTTP类型)',
    ADD COLUMN service_bean   VARCHAR(100) NULL COMMENT '内部Service Bean名(仅SERVICE类型)',
    ADD COLUMN service_method VARCHAR(100) NULL COMMENT '内部Service方法名(仅SERVICE类型)',
    ADD COLUMN param_schema   TEXT         NULL COMMENT '参数JSON Schema(校验用)',
    ADD COLUMN context_inject VARCHAR(500) NULL COMMENT '上下文注入字段,逗号分隔,如userId,sessionId',
    ADD COLUMN idempotent     CHAR(1)      DEFAULT '0' COMMENT '是否幂等(自动生成幂等键)',
    ADD COLUMN timeout_ms     INT          DEFAULT 10000 COMMENT '超时毫秒',
    ADD COLUMN rate_limit     INT          NULL COMMENT '每用户每分钟限流次数,NULL不限';
```

### 3.2 新增执行日志表

```sql
CREATE TABLE ai_action_execution_log (
    log_id        BIGINT       PRIMARY KEY AUTO_INCREMENT,
    trace_id      VARCHAR(64)  NOT NULL COMMENT '链路ID(关联会话)',
    action_code   VARCHAR(50)  NOT NULL,
    user_id       BIGINT       NULL,
    session_id    VARCHAR(64)  NULL,
    llm_params    TEXT         NULL COMMENT 'LLM抽取的原始参数',
    final_params  TEXT         NULL COMMENT '上下文注入后的最终参数',
    confirm_status VARCHAR(20) NULL COMMENT 'AUTO/CONFIRMED/REJECTED/TIMEOUT',
    executor_type VARCHAR(20)  NOT NULL,
    exec_status   VARCHAR(20)  NOT NULL COMMENT 'SUCCESS/FAIL/RUNNING',
    result        TEXT         NULL,
    error_msg     VARCHAR(2000) NULL,
    cost_ms       BIGINT       NULL,
    idempotent_key VARCHAR(100) NULL,
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    KEY idx_trace (trace_id),
    KEY idx_user_action (user_id, action_code, create_time)
) COMMENT '本体动作执行日志';
```

### 3.3 种子数据示例（升级后的动作定义）

```sql
-- 读操作：直接执行
UPDATE ai_ontology_action SET executor_type='SERVICE', risk_level='READ',
    service_bean='focusService', service_method='getFocusStatistics',
    param_schema='{"type":"object","properties":{"days":{"type":"integer","default":7}}}'
WHERE action_code='query_focus_statistics';

-- 写操作：需确认 + 上下文注入 + 幂等
UPDATE ai_ontology_action SET executor_type='SERVICE', risk_level='WRITE',
    confirm_policy='CONFIRM', idempotent='1',
    service_bean='focusService', service_method='startFocus',
    context_inject='userId',
    param_schema='{"type":"object","required":["mode","duration"],
      "properties":{"mode":{"type":"string","enum":["POMODORO","DEEP","FREE"]},
                    "duration":{"type":"integer","minimum":5,"maximum":180}}}'
WHERE action_code='start_focus';

-- 资金相关：二次确认
UPDATE ai_ontology_action SET executor_type='SERVICE', risk_level='FINANCIAL',
    confirm_policy='DOUBLE', idempotent='1', rate_limit=5,
    service_bean='mallService', service_method='redeemGoods',
    context_inject='userId',
    param_schema='{"type":"object","required":["goodsId"],
      "properties":{"goodsId":{"type":"integer"}}}'
WHERE action_code='redeem_goods';
```

---

## 四、核心流程设计

### 4.1 执行主流程（时序）

```
用户          LLM            ActionCallTool     Orchestrator      确认单      Executor     业务Service
 │ "开始番茄专注" │                  │                 │             │            │             │
 │──────────────▶│                │                 │             │            │             │
 │               │ call(start_focus│                 │             │            │             │
 │               │  {duration:25}) │                 │             │            │             │
 │               │───────────────▶│ execute()        │             │            │             │
 │               │                │─────────────────▶│ ①加载定义    │            │             │
 │               │                │                  │ ②Schema校验  │            │             │
 │               │                │                  │ ③注入userId  │            │             │
 │               │                │                  │ ④WRITE→需确认│            │             │
 │               │                │                  │─────────────▶│ 挂起PENDING │             │
 │               │◀───────────────│ "请确认: 番茄专注25分钟?"        │            │             │
 │ "确认"        │                │                  │             │            │             │
 │──────────────▶│ confirm(traceId)│                 │             │            │             │
 │               │───────────────▶│─────────────────▶│ ⑤恢复执行    │            │             │
 │               │                │                  │ ⑥幂等键检查  │            │             │
 │               │                │                  │──────────────────────────▶│ startFocus() │
 │               │                │                  │◀──────────────────────────│  OK         │
 │               │◀───────────────│ "已开始25分钟番茄专注"           │  落审计日志  │             │
```

### 4.2 风险分级与确认策略矩阵

| risk_level | confirm_policy | 行为 | 示例 |
|-----------|---------------|------|------|
| READ | AUTO | 直接执行，结果回给 LLM | 查询专注统计、查询活动列表 |
| WRITE | CONFIRM | LLM 生成确认卡片 → 用户确认 → 执行 | 开始专注、发布动态、记录睡眠日记 |
| FINANCIAL | DOUBLE | 确认 + 二次确认（输入"确认兑换"或点击二次按钮） | 兑换商品、支付下单 |

确认单存 Redis：`action:confirm:{traceId}` → `{actionCode, finalParams, expire: 5min}`，超时自动作废。

---

## 五、代码 DEMO

### 5.1 统一执行请求/结果

```java
package com.xinling.ai.service.action;

/** 动作执行上下文（userId 等身份字段只从这里来，绝不信任 LLM 传入） */
public record ActionContext(Long userId, String sessionId, String traceId) {}

/** 统一执行结果，序列化后作为 Tool 返回值喂回 LLM */
public record ActionResult(boolean success, String code, String message, Object data) {
    public static ActionResult ok(Object data)            { return new ActionResult(true, "0", "success", data); }
    public static ActionResult needConfirm(String ticket) { return new ActionResult(false, "NEED_CONFIRM", ticket, null); }
    public static ActionResult fail(String msg)           { return new ActionResult(false, "FAIL", msg, null); }
}
```

### 5.2 执行器 SPI

```java
package com.xinling.ai.service.action.executor;

public interface ActionExecutor {
    String type();                                   // TOOL / SERVICE / HTTP / MQ
    ActionResult execute(OntologyAction action, Map<String, Object> params, ActionContext ctx);
}
```

### 5.3 SERVICE 执行器（内部直连，核心）

```java
@Component
@RequiredArgsConstructor
public class ServiceActionExecutor implements ActionExecutor {

    private final ApplicationContext applicationContext;

    @Override public String type() { return "SERVICE"; }

    @Override
    public ActionResult execute(OntologyAction action, Map<String, Object> params, ActionContext ctx) {
        try {
            Object bean = applicationContext.getBean(action.getServiceBean());
            Method method = ReflectionUtils.findMethod(bean.getClass(),
                    action.getServiceMethod(), Map.class, Long.class);
            if (method == null) {
                return ActionResult.fail("动作绑定的方法不存在: " + action.getServiceMethod());
            }
            // 约定：所有动作绑定的 Service 方法签名为 (Map<String,Object> params, Long userId)
            Object result = ReflectionUtils.invokeMethod(method, bean, params, ctx.userId());
            return ActionResult.ok(result);
        } catch (ServiceException e) {
            return ActionResult.fail(e.getMessage());      // 业务异常原样透传给 LLM 组织话术
        } catch (Exception e) {
            log.error("动作执行异常 action={}", action.getActionCode(), e);
            return ActionResult.fail("系统繁忙，请稍后再试");
        }
    }
}
```

> 业务侧只需新增薄适配方法，例如 `FocusService.startFocus(Map params, Long userId)`，内部复用原有逻辑，**不改动 APP Controller**。

### 5.4 编排器（校验 → 注入 → 确认 → 路由 → 审计）

```java
@Service
@RequiredArgsConstructor
public class ActionOrchestrator {

    private final OntologyActionMapper actionMapper;
    private final List<ActionExecutor> executors;          // Spring 自动注入所有执行器
    private final StringRedisTemplate redis;
    private final ActionExecutionLogMapper logMapper;

    public ActionResult orchestrate(String actionCode, Map<String, Object> llmParams, ActionContext ctx) {
        OntologyAction action = actionMapper.selectByCode(actionCode);
        if (action == null || !"0".equals(action.getStatus())) {
            return ActionResult.fail("该操作不存在或已下线");
        }

        // ① 参数 Schema 校验（LLM 参数不可信，先过校验）
        String err = JsonSchemaValidator.validate(action.getParamSchema(), llmParams);
        if (err != null) return ActionResult.fail("参数不正确: " + err);

        // ② 上下文注入：身份字段强制覆盖，LLM 传了也丢弃
        Map<String, Object> finalParams = new HashMap<>(llmParams);
        if (StringUtils.contains(action.getContextInject(), "userId")) {
            finalParams.put("userId", ctx.userId());
        }

        // ③ 限流
        if (action.getRateLimit() != null && !tryAcquire(ctx.userId(), action)) {
            return ActionResult.fail("操作太频繁，请稍后再试");
        }

        // ④ 确认流：写操作挂起，返回确认票据给 LLM 组织确认话术
        if (!"AUTO".equals(action.getConfirmPolicy())) {
            String ticket = ctx.traceId();
            redis.opsForValue().set("action:confirm:" + ticket,
                    JSON.toJSONString(Map.of("actionCode", actionCode, "params", finalParams)),
                    5, TimeUnit.MINUTES);
            return ActionResult.needConfirm(ticket);
        }

        return doExecute(action, finalParams, ctx);
    }

    /** 用户确认后由 confirm 入口调用 */
    public ActionResult confirm(String ticket, ActionContext ctx) {
        String json = redis.opsForValue().getAndDelete("action:confirm:" + ticket);
        if (json == null) return ActionResult.fail("确认已过期，请重新发起");
        ConfirmTicket t = JSON.parseObject(json, ConfirmTicket.class);
        OntologyAction action = actionMapper.selectByCode(t.actionCode());
        return doExecute(action, t.params(), ctx);
    }

    private ActionResult doExecute(OntologyAction action, Map<String, Object> params, ActionContext ctx) {
        // ⑤ 幂等：同一 traceId 重复提交直接返回首个结果
        if ("1".equals(action.getIdempotent())) {
            String key = "action:idem:" + ctx.traceId();
            if (Boolean.FALSE.equals(redis.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.MINUTES))) {
                return ActionResult.fail("请勿重复提交");
            }
        }
        // ⑥ 路由执行器 + ⑦ 审计
        long start = System.currentTimeMillis();
        ActionExecutor executor = executors.stream()
                .filter(e -> e.type().equals(action.getExecutorType()))
                .findFirst().orElseThrow();
        ActionResult result = executor.execute(action, params, ctx);
        logMapper.insert(ActionExecutionLog.of(ctx, action, params, result,
                System.currentTimeMillis() - start));
        return result;
    }
}
```

### 5.5 暴露给 LLM 的唯一新 Tool

```java
@Component
@RequiredArgsConstructor
public class ActionCallTool {

    private final ActionOrchestrator orchestrator;

    @Tool("执行本体中定义的业务动作。仅当用户明确表达要执行某个操作，且已通过 "
        + "queryActionsForConcept 确认该动作存在时调用。参数必须严格匹配动作的参数定义。")
    public String executeAction(@P("动作编码，如 start_focus") String actionCode,
                                @P("动作参数JSON") String paramsJson) {
        ActionContext ctx = new ActionContext(SecurityUtils.getLoginUserId(),
                ChatContext.currentSessionId(), IdUtils.fastSimpleUUID());
        ActionResult result = orchestrator.orchestrate(actionCode,
                JSON.parseObject(paramsJson, Map.class), ctx);

        if ("NEED_CONFIRM".equals(result.code())) {
            // 引导 LLM 生成确认话术，用户回复"确认"后走 confirm 接口
            return "动作已就绪，等待用户确认。请向用户复述操作内容并询问是否确认，确认票据: " + result.message();
        }
        return JSON.toJSONString(result);
    }
}
```

注册进 `AiServiceConfig` 的 `.tools(...)` 链即可，与现有 6 个 Tool 并列。

### 5.6 HTTP 执行器（仅外部三方 API）

```java
@Component
public class HttpActionExecutor implements ActionExecutor {
    private final RestClient restClient = RestClient.create();

    @Override public String type() { return "HTTP"; }

    @Override
    public ActionResult execute(OntologyAction action, Map<String, Object> params, ActionContext ctx) {
        // 支持 target 中的 {placeholder} 路径变量替换；超时取 action.timeoutMs
        // 鉴权信息（AppKey/Secret）从配置中心读取，绝不入库到 action 表明文
        ...
    }
}
```

---

## 六、安全设计清单

| 防线 | 机制 | 说明 |
|------|------|------|
| 参数可信 | JSON Schema 校验 + 上下文注入 | LLM 参数先校验；userId 强制从登录态注入 |
| 写操作防护 | 确认流（CONFIRM/DOUBLE） | LLM 必须复述操作内容，用户显式确认才执行 |
| 越权防护 | 业务 Service 内二次鉴权 | 动作绑定方法内仍校验数据归属（如只能操作自己的专注记录） |
| 防重 | 幂等键（traceId） | 网络重试/用户重复点击不产生重复订单 |
| 防刷 | 每用户限流（rate_limit） | Redis 滑动窗口 |
| 审计 | ai_action_execution_log | 谁、何时、什么动作、LLM原始参数 vs 最终参数、结果 |
| 密钥 | 外部 API 密钥走配置中心 | action 表只存地址，不存密钥 |
| 灰度 | action.status 开关 | 单个动作可秒级下线，不影响主流程 |

---

## 七、落地路线

| 阶段 | 内容 | 工作量估算 |
|------|------|-----------|
| P0 | action 表加字段 + 执行日志表 + Orchestrator + ServiceExecutor + ActionCallTool | 3~4 天 |
| P0 | 选 2 个动作试点：`query_focus_statistics`(READ) + `start_focus`(WRITE确认流) | 2 天 |
| P1 | 确认流前端卡片（APP 聊天页确认按钮）+ 幂等 + 限流 | 3 天 |
| P1 | 管理后台：动作定义可视化配置页（复用现有 OntologyController 的 action CRUD） | 2 天 |
| P2 | HttpExecutor（外部 API）+ MqExecutor（长任务）+ 审计查询页 | 3 天 |

---

## 八、与现有架构的关系

```
现有：OntologyExtendedTool.queryActionsForConcept()  → "能做什么"（保持不变）
新增：ActionCallTool.executeAction()                 → "去做"（本方案）
新增：ActionOrchestrator + 4 种 Executor             → 执行层（本方案核心）
扩展：ai_ontology_action 表 9 个字段                 → 定义层增强
新增：ai_action_execution_log 表                     → 审计层
```

本方案**不改动**现有本体 9 表结构的主体、不改动 6 个现有 Tool、不改动 APP 业务 Controller，只新增执行层和 1 个 Tool，属于纯增量改造。
