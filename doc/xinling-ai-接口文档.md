# XinLing AI 模块接口文档

## 目录

1. [智能聊天](#1-智能聊天)
2. [会话管理](#2-会话管理)
3. [语音合成（TTS）](#3-语音合成tts)
4. [SQL 执行](#4-sql-执行)
5. [模型配置管理](#5-模型配置管理-管理后台)
6. [提供商管理](#6-提供商管理-管理后台)
7. [会话配置管理](#7-会话配置管理-管理后台)
8. [模型切换](#8-模型切换-管理后台)
9. [本体管理](#9-本体管理-管理后台)
10. [本体推理](#10-本体推理)

---

## 1. 智能聊天

### 1.1 V2 聊天（推荐）

LLM 自主决定是否调用工具（数据库查询、知识库搜索等），前端不需要判断场景。

```
POST /ai/chat
Content-Type: application/json
```

**请求体：**

```json
{
  "sessionId": "",          // 可选，首次不传自动生成
  "prompt": "你好，介绍一下系统功能",   // 用户消息
  "voice": "xiaoxiao"       // 可选，语音角色
}
```

**响应：** SSE (text/event-stream)

```text
data: {"content":"你好！","sessionId":"abc-123","finish":false,"timestamp":1700000000000}
data: {"content":"我是心灵助手","sessionId":"abc-123","finish":false,"timestamp":1700000000001}
data: {"content":"","sessionId":"abc-123","finish":true,"timestamp":1700000000002}
```

**前端接入示例：**

```javascript
// EventSource 或 fetch SSE
let currentSessionId = null;

async function sendMessage(prompt) {
  const resp = await fetch('/ai/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ prompt, sessionId: currentSessionId })
  });

  const reader = resp.body.getReader();
  const decoder = new TextDecoder();

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    const text = decoder.decode(value);
    // 解析 SSE data: 行
    const lines = text.split('\n').filter(l => l.startsWith('data: '));
    for (const line of lines) {
      const msg = JSON.parse(line.slice(6));
      if (msg.sessionId && !currentSessionId) {
        currentSessionId = msg.sessionId;  // 保存 sessionId
      }
      if (msg.finish) break;
      // 追加 content 到对话框
      appendMessage(msg.content);
    }
  }
}
```

### 1.2 V1 智能聊天（旧版，自动场景判断）

系统根据输入内容自动判断场景（NL2SQL / 知识问答 / 普通对话）。

```
POST /ai/smartChat
Content-Type: application/json
```

**请求体：** 同 V2

**响应：** SSE 同 V2

> 推荐使用 V2，V1 为兼容旧客户端保留。

---

## 2. 会话管理

### 2.1 获取会话列表

```
GET /ai/sessions
Authorization: Bearer <token>
```

**响应：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "sessionId": "abc-123",
      "title": "你好",
      "userId": 1,
      "userName": "admin",
      "createTime": "2026-07-13T10:00:00",
      "updateTime": "2026-07-13T10:00:00"
    }
  ]
}
```

### 2.2 获取会话历史

```
GET /ai/sessions/{sessionId}/history
Authorization: Bearer <token>
```

**响应：**

```json
{
  "code": 200,
  "data": [
    { "role": "user", "content": "你好", "createTime": "..." },
    { "role": "assistant", "content": "你好！", "createTime": "..." }
  ]
}
```

### 2.3 删除会话

```
DELETE /ai/sessions/{sessionId}
Authorization: Bearer <token>
```

**响应：** `{ "code": 200, "msg": "会话已删除" }`

### 2.4 清除会话记忆

```
DELETE /ai/sessions/{sessionId}/memory
Authorization: Bearer <token>
```

清除 LLM 的多轮对话上下文，不影响历史消息记录。

---

## 3. 语音合成（TTS）

### 3.1 文字转语音

```
POST /ai/tts
Content-Type: application/json
```

```json
{
  "prompt": "今天天气真好",     // 要合成的文本
  "voice": "xiaoxiao"          // 可选，语音角色
}
```

**响应：**

```json
{
  "code": 200,
  "msg": "语音合成成功",
  "data": "/uploads/ai/voice/tts_123456789.mp3"
}
```

> 返回的 audioUrl 可直接用于 `<audio src="...">` 播放。

### 3.2 获取语音列表

```
GET /ai/voices
```

**响应：**

```json
{
  "code": 200,
  "data": [
    { "name": "XIAOXIAO", "displayName": "晓晓", "style": "亲切" },
    { "name": "YUNXI", "displayName": "云希", "style": "温柔" }
  ]
}
```

---

## 4. SQL 执行

```
POST /ai/sql/execute
Content-Type: application/json
Authorization: Bearer <token>
```

```json
{
  "sql": "SELECT COUNT(*) FROM sys_user"
}
```

**响应：**

```json
{
  "code": 200,
  "msg": "SQL执行成功",
  "data": {
    "type": "SCALAR",
    "data": 42,
    "rowCount": 1
  }
}
```

---

## 5. 模型配置管理（管理后台）

前缀：`/ai/model`，需要 `ai:model:*` 权限。

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/ai/model/list` | ai:model:list | 模型列表（分页） |
| GET | `/ai/model/{modelId}` | ai:model:query | 模型详情 |
| POST | `/ai/model` | ai:model:add | 新增模型 |
| PUT | `/ai/model` | ai:model:edit | 修改模型 |
| DELETE | `/ai/model/{modelIds}` | ai:model:remove | 删除模型 |
| GET | `/ai/model/export` | ai:model:export | 导出 Excel |
| GET | `/ai/model/chatModels` | - | 启用的对话模型 |
| GET | `/ai/model/embeddingModels` | - | 启用的嵌入模型 |
| PUT | `/ai/model/setDefault/{modelId}` | ai:model:edit | 设为默认 |
| PUT | `/ai/model/cancelDefault/{modelId}` | ai:model:edit | 取消默认 |

---

## 6. 提供商管理（管理后台）

前缀：`/ai/provider`，需要 `ai:provider:*` 权限。

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/ai/provider/list` | ai:provider:list | 提供商列表 |
| GET | `/ai/provider/{providerId}` | ai:provider:query | 提供商详情 |
| POST | `/ai/provider` | ai:provider:add | 新增 |
| PUT | `/ai/provider` | ai:provider:edit | 修改 |
| DELETE | `/ai/provider/{providerIds}` | ai:provider:remove | 删除 |
| GET | `/ai/provider/export` | ai:provider:export | 导出 Excel |
| GET | `/ai/provider/enabled` | - | 启用的提供商 |

---

## 7. 会话配置管理（管理后台）

前缀：`/ai/session`，需要 `ai:session:*` 权限。

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/ai/session/list` | ai:session:list | 配置列表 |
| GET | `/ai/session/{configId}` | ai:session:query | 配置详情 |
| POST | `/ai/session` | ai:session:add | 新增 |
| PUT | `/ai/session` | ai:session:edit | 修改 |
| DELETE | `/ai/session/{configIds}` | ai:session:remove | 删除 |
| GET | `/ai/session/export` | ai:session:export | 导出 |
| PUT | `/ai/session/setDefault/{configId}` | ai:session:edit | 设为默认 |
| GET | `/ai/session/default` | - | 默认配置 |

---

## 8. 模型切换（管理后台）

前缀：`/ai/management`。

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| PUT | `/ai/management/switchModel/{modelId}` | ai:model:edit | 切换到指定模型 |
| PUT | `/ai/management/switchSession/{configId}` | ai:session:edit | 切换会话配置 |
| PUT | `/ai/management/useDefault` | - | 使用默认配置 |
| POST | `/ai/management/refresh` | ai:model:edit | 刷新模型缓存 |

---

## 9. 本体管理（管理后台）

前缀：`/ai/ontology`，需要 `ai:ontology:*` 权限。

### 概念 CRUD

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/ai/ontology/concept/list` | 概念列表（分页） |
| GET | `/ai/ontology/concept/{conceptId}` | 概念详情 |
| POST | `/ai/ontology/concept` | 新增概念 |
| PUT | `/ai/ontology/concept` | 修改概念 |
| DELETE | `/ai/ontology/concept/{conceptIds}` | 删除概念 |
| GET | `/ai/ontology/concept/enabled` | 所有启用概念 |
| GET | `/ai/ontology/concept/children/{parentId}` | 子概念列表 |

### 关系 CRUD

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/ai/ontology/relation/list` | 关系列表（分页） |
| GET | `/ai/ontology/relation/{relationId}` | 关系详情 |
| POST | `/ai/ontology/relation` | 新增关系 |
| PUT | `/ai/ontology/relation` | 修改关系 |
| DELETE | `/ai/ontology/relation/{relationIds}` | 删除关系 |
| GET | `/ai/ontology/relation/concept/{conceptId}` | 概念相关关系 |
| GET | `/ai/ontology/relation/between?sourceId=&targetId=` | 两概念间关系 |

---

## 10. 本体推理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/ai/ontology/reason` | 基于本体知识推理问答 |
| GET | `/ai/ontology/knowledge` | 本体知识图谱文本 |
| GET | `/ai/ontology/related/{conceptId}` | 查询相关概念 |

### 推理问答

```
POST /ai/ontology/reason
```

```json
{
  "query": "焦虑和抑郁有什么区别"
}
```

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "焦虑是一种... 抑郁是一种..."  // LLM 基于本体知识推理的回答
}
```

---

## 注意事项

1. **多轮对话流程：**
   - 首次调 `/ai/chat` 不传 `sessionId`，从 SSE 第一条消息拿到 `sessionId`
   - 后续对话带上 `sessionId` 继续
   - 如需重新开始，生成新 `sessionId` 或调用 `DELETE /ai/sessions/{id}/memory`

2. **管理后台接口需要 JWT Token**（`Authorization: Bearer <token>`）
3. **表格分页参数：** `?pageNum=1&pageSize=10`，响应包在 `{"total": N, "rows": [...]}` 中
4. **本体接口需要先在管理后台配置概念和关系**，否则推理会返回空
