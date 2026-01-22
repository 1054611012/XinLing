# LangChain4j、RAG、NL2SQL 与 Ollama 集成使用说明

## 概述

本项目成功集成了LangChain4j、RAG（检索增强生成）、NL2SQL（自然语言到SQL转换）和Ollama功能，提供了多种AI能力。

## 功能特性

### 1. Ollama 基础聊天
- 直接使用Ollama模型进行对话
- 支持流式响应
- 可获取模型列表

### 2. RAG（检索增强生成）
- 使用向量数据库存储知识
- 基于文档内容生成回答
- 支持文档加载到知识库

### 3. NL2SQL（自然语言到SQL转换）
- 将自然语言查询转换为SQL语句
- 基于数据库文档结构生成查询
- 支持复杂查询生成

### 4. 智能聊天
- 自动检测查询类型
- 根据查询内容选择合适的处理方式
- 提供最优的回答结果

### 5. 流式对话
- 支持流式响应，逐token返回内容
- 前端可实时渲染，提升交互体验
- 支持Server-Sent Events (SSE)

### 6. 多轮对话
- 维护会话历史
- 结合历史上下文 + 检索结果 + 当前问题生成回答
- 支持会话上下文管理

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
- **响应**: Server-Sent Events 格式
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
- **响应**: Server-Sent Events 格式
- **功能**: 自动检测查询类型并选择合适的处理方式

### 5. 系统管理接口

#### 初始化数据库文档
- **接口**: `POST /ai/initDbDocs`
- **功能**: 将数据库结构信息加载到RAG知识库

#### 加载知识库文档
- **接口**: `POST /ai/loadKnowledgeBase`
- **功能**: 从配置路径加载知识库文档到向量存储

## 技术架构

### 1. 核心组件

- **OllamaService**: 处理Ollama模型调用
- **XinLingAiService**: RAG服务接口
- **DatabaseRagService**: 数据库RAG服务
- **DocumentLoaderService**: 文档加载服务
- **RagExecutor**: RAG执行器
- **AiController**: 统一AI聊天控制器

### 2. LangChain4j 集成

- **EmbeddingModel**: 文档向量化 (AllMiniLmL6V2)
- **EmbeddingStore**: 向量存储 (In-Memory)
- **ContentRetriever**: 内容检索器
- **ChatLanguageModel**: 聊天语言模型
- **StreamingChatLanguageModel**: 流式聊天语言模型

### 3. RAG 实现

- **文档加载**: 支持PDF、TXT、MD等多种格式文档
- **向量化存储**: 将文档转换为向量并存储
- **相似度检索**: 基于语义相似度检索相关内容
- **增强生成**: 结合检索内容生成回答

### 4. 流式响应实现

- **Reactor模式**: 使用Project Reactor实现响应式流
- **Server-Sent Events**: 支持SSE协议
- **Flux流**: 使用Spring WebFlux的Flux类型

## 使用场景

### 1. 知识库问答
- 上传文档到知识库
- 通过自然语言查询文档内容
- 获取基于文档的准确回答

### 2. 数据库查询
- 通过自然语言查询数据库
- 自动生成SQL语句
- 执行查询并返回结果

### 3. 智能客服
- 处理用户常见问题
- 提供个性化回答
- 自动分类查询类型

### 4. 多轮对话
- 维护会话上下文
- 基于历史对话生成回答
- 支持连续交互

## 配置说明

### application.yml 配置

```yaml
ai:
  # 模型配置
  model:
    provider: ollama          # 模型提供商: ollama, openai等
    default-model: qwen2.5:3b   # 默认云模型名称
    use-local: true           # 是否使用本地模型
  # Ollama配置
  ollama:
    base-url: http://localhost:11434
    api-key: ollama
    model: qwen2.5:3b
    timeout-seconds: 60       # 添加Ollama请求超时时间（秒）
  # RAG配置
  rag:
    # 知识库文档路径
    knowledge-base-path: /Volumes/Suxia/IdeaProjects/XingLing-Vue/knowledge-base
    # 检索结果数量
    max-results: 3
    # 最小相似度分数
    min-score: 0.5
    # 文档分块大小
    chunk-size: 500
    # 文档重叠大小
    chunk-overlap: 0
```

## 部署要求

1. **Ollama服务**: 确保Ollama服务在指定端口运行
2. **内存要求**: RAG功能需要足够的内存存储向量
3. **文档存储**: 准备需要的知识库文档
4. **JDK版本**: 推荐使用JDK 17或更高版本

## 最佳实践

1. **文档预处理**: 确保知识库文档质量
2. **参数调优**: 根据需求调整RAG参数
3. **缓存策略**: 对频繁查询使用缓存
4. **监控指标**: 监控响应时间和准确性
5. **会话管理**: 合理管理会话生命周期
6. **错误处理**: 实现完善的异常处理机制

## 性能优化

1. **向量数据库**: 考虑使用更高效的向量数据库（如Chroma、Pinecone等）
2. **缓存机制**: 实现查询结果缓存
3. **异步处理**: 使用异步处理提高并发能力
4. **连接池**: 配置适当的连接池参数

## 扩展性考虑

1. **多模型支持**: 支持多种AI模型切换
2. **多知识库**: 支持多个知识库的动态切换
3. **自定义提示词**: 支持自定义提示词模板
4. **插件化架构**: 支持功能模块插件化