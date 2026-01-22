# LangChain4j 与 RAG 集成

## 概述

本项目已成功集成LangChain4j和RAG（检索增强生成）功能，为心灵（XinLing）项目提供了AI能力。

## 功能特性

- **文档加载与解析**：支持PDF、文本等多种格式文档的加载和解析
- **向量存储**：使用嵌入模型将文档转换为向量并存储
- **检索增强生成**：基于知识库内容生成回答
- **Ollama集成**：支持使用本地Ollama模型
- **配置化管理**：通过application.yml进行AI相关配置
- **NL2SQL功能**：自然语言到SQL查询的转换
- **智能聊天**：自动检测查询类型并选择合适的处理方式
- **云/本地模型切换**：支持Ollama（本地）与OpenAI（云端）模型的动态切换
- **流式对话**：支持流式响应，提供实时交互体验
- **多轮对话**：支持会话上下文管理，实现多轮对话能力

## 核心组件

### 1. 配置类
- [AiConfigProperties](src/main/java/com/xinling/ai/config/AiConfigProperties.java)：AI配置属性
- [Langchain4jConfig](src/main/java/com/xinling/ai/config/Langchain4jConfig.java)：LangChain4j相关配置

### 2. 服务类
- [OllamaService](src/main/java/com/xinling/ai/service/OllamaService.java)：Ollama模型调用服务
- [XinLingAiService](src/main/java/com/xinling/ai/service/XinLingAiService.java)：RAG服务接口
- [DatabaseRagService](src/main/java/com/xinling/ai/service/DatabaseRagService.java)：数据库RAG服务
- [RagExecutor](src/main/java/com/xinling/ai/service/RagExecutor.java)：RAG执行器
- [DocumentLoaderService](src/main/java/com/xinling/ai/service/DocumentLoaderService.java)：文档加载服务

### 3. 控制器
- [AiController](src/main/java/com/xinling/ai/controller/AiController.java)：AI聊天控制器
- [EnhancedAiController](src/main/java/com/xinling/ai/controller/EnhancedAiController.java)：增强版AI控制器

## API 接口说明

### 1. 基础聊天接口

#### 获取模型列表
- **接口**: `GET /ai/listModels`
- **功能**: 获取Ollama可用模型列表

#### 基础聊天
- **接口**: `POST /ai/chat`
- **请求体**: 
  ```json
  {
    "prompt": "你的问题",
    "model": "qwen2.5:3b"
  }
  ```
- **功能**: 使用Ollama进行基础对话

### 2. RAG 增强接口

#### RAG聊天
- **接口**: `POST /ai/ragChat`
- **请求体**: 
  ```json
  {
    "prompt": "基于知识库的问题",
    "model": "qwen2.5:3b"
  }
  ```
- **功能**: 使用RAG技术增强回答

### 3. 流式聊天接口

#### 流式聊天
- **接口**: `POST /ai/streamChat`
- **请求体**: 
  ```json
  {
    "sessionId": "session-123",
    "prompt": "你的问题",
    "model": "qwen2.5:3b",
    "enableRag": true,
    "stream": true
  }
  ```
- **功能**: 使用流式响应返回聊天内容

### 4. 智能聊天接口

#### 智能聊天
- **接口**: `POST /ai/smartChat`
- **请求体**: 
  ```json
  {
    "sessionId": "session-123",
    "prompt": "你的问题",
    "model": "qwen2.5:3b",
    "enableRag": true
  }
  ```
- **功能**: 自动检测查询类型并选择合适的处理方式

## Ollama配置说明

### 安装Ollama
1. 访问 [Ollama官网](https://ollama.ai) 下载并安装Ollama
2. 启动Ollama服务（默认端口11434）

### 安装模型
运行以下命令安装模型（以qwen2.5为例）：
```bash
ollama pull qwen2.5:3b
```

### 模型配置
在 `application.yml` 中配置模型：
```yaml
ai:
  model:
    provider: ollama          # 模型提供商: ollama, openai等
    default-model: qwen2.5:3b   # 默认云模型名称
    use-local: true           # 是否使用本地模型
  ollama:
    base-url: http://localhost:11434
    api-key: ollama
    model: qwen2.5:3b          # 本地模型名称
    timeout-seconds: 60       # 添加Ollama请求超时时间（秒）
  rag:
    knowledge-base-path: /path/to/knowledge/base
    max-results: 3
    min-score: 0.5
    chunk-size: 500
    chunk-overlap: 0
```

### 云/本地模型切换
系统支持动态切换云模型和本地模型：
- 本地模型（Ollama）：私有化部署，数据安全
- 云端模型（OpenAI）：高性能，广泛知识库

通过修改 `ai.model.use-local` 配置可实现模型切换。

## 使用说明

### 1. 初始化数据库文档
启动应用后，调用 `/ai/initDbDocs` 接口初始化数据库结构文档到RAG知识库。

### 2. 加载知识库文档
将需要的知识库文档（PDF、TXT、MD等格式）放在 `knowledge-base-path` 配置的路径下，然后调用 `/ai/loadKnowledgeBase` 接口加载文档。

### 3. 进行AI对话
调用相应的API接口进行AI对话，系统会根据配置自动选择合适的处理方式。

## 架构设计

```
┌────────────┐
│   前端     │  EventSource / fetch(stream)
└─────▲──────┘
│ SSE
┌─────┴──────────────┐
│  AiController      │  /ai/chat, /ai/streamChat
└─────▲──────────────┘
│ Flux<ChatResponse>
┌─────┴──────────────┐
│ XinLingAiService   │ ← RAG服务编排
│  ├─ OllamaService  │ ← Ollama模型调用
│  ├─ ContentRetriever│ ← 内容检索器
│  └─ ChatMemory     │ ← 多轮对话内存
└─────▲──────────────┘
│
┌─────┴──────────────┐
│  LangChain4j       │
│  OllamaChatModel   │  stream()
└─────▲──────────────┘
│
┌─────┴──────────────┐
│   Ollama Server    │
│   Qwen/Llama3/Phi │
└────────────────────┘

┌────────────────────┐
│ Vector DB           │ In-Memory / Chroma
│ Embedding Model     │ AllMiniLmL6V2
└────────────────────┘
```

## 扩展功能

- **NL2SQL**: 支持自然语言到SQL查询的转换
- **多知识库**: 支持多个知识库的切换
- **会话管理**: 支持多轮对话上下文管理
- **流式响应**: 提供实时流式响应体验
- **错误处理**: 完善的异常处理机制