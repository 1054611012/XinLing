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
    "model": "llama3.1",
    "prompt": "你的问题"
  }
  ```
- **功能**: 使用Ollama进行基础对话

### 2. RAG 增强接口

#### RAG聊天
- **接口**: `POST /ai/ragChat`
- **请求体**: 
  ```json
  {
    "model": "llama3.1",
    "prompt": "基于知识库的问题"
  }
  ```
- **功能**: 使用RAG技术增强回答

### 3. NL2SQL 接口

#### 自然语言转SQL
- **接口**: `POST /ai/nl2sql`
- **请求体**: 
  ```json
  {
    "model": "llama3.1",
    "prompt": "查询用户表中所有状态为正常的数据"
  }
  ```
- **功能**: 将自然语言转换为SQL查询语句

### 4. 智能聊天接口

#### 智能聊天
- **接口**: `POST /ai/smartChat`
- **请求体**: 
  ```json
  {
    "model": "llama3.1",
    "prompt": "你的问题"
  }
  ```
- **功能**: 自动检测查询类型并选择合适的处理方式

## 技术架构

### 1. 核心组件

- **OllamaService**: 处理Ollama模型调用
- **XinLingAiService**: RAG服务接口
- **DatabaseRagService**: 数据库RAG服务
- **AiChatController**: 统一AI聊天控制器

### 2. LangChain4j 集成

- **EmbeddingModel**: 文档向量化
- **EmbeddingStore**: 向量存储
- **ContentRetriever**: 内容检索器
- **AiServices**: AI服务抽象

### 3. RAG 实现

- **文档加载**: 支持多种格式文档
- **向量化存储**: 将文档转换为向量并存储
- **相似度检索**: 基于语义相似度检索相关内容
- **增强生成**: 结合检索内容生成回答

### 4. NL2SQL 实现

- **数据库文档**: 包含表结构和字段信息
- **模式理解**: 理解数据库模式和关系
- **SQL生成**: 基于自然语言生成SQL语句

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

## 配置说明

### application.yml 配置

```yaml
ai:
  ollama:
    base-url: http://localhost:11434
    api-key: ollama
    model: llama3.1
  rag:
    knowledge-base-path: /path/to/knowledge/base
    max-results: 3
    min-score: 0.5
    chunk-size: 500
    chunk-overlap: 0
```

## 部署要求

1. **Ollama服务**: 确保Ollama服务在指定端口运行
2. **内存要求**: RAG功能需要足够的内存存储向量
3. **文档存储**: 准备需要的知识库文档

## 最佳实践

1. **文档预处理**: 确保知识库文档质量
2. **参数调优**: 根据需求调整RAG参数
3. **缓存策略**: 对频繁查询使用缓存
4. **监控指标**: 监控响应时间和准确性