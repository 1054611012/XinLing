# Ollama 服务连接问题解决方案

## 问题描述

应用启动时出现以下错误：
```
java.net.ConnectException: Failed to connect to localhost/[0:0:0:0:0:0:0:1]:11434
```

这是因为应用尝试连接本地 Ollama 服务进行向量嵌入（Embedding）操作，但 Ollama 服务未运行或无法访问。

## 已实施的修复

代码已更新为容错模式：
- ✅ 在初始化前检查 Ollama 服务是否可用
- ✅ 如果服务不可用，跳过 RAG 初始化但不影响应用启动
- ✅ 提供清晰的日志提示，告知用户需要启动 Ollama 服务

**现在应用可以正常启动，即使 Ollama 服务未运行。**

## 如何启用完整的 RAG 功能

### 方案 1：启动本地 Ollama 服务（推荐用于本地开发）

1. **安装 Ollama**
   ```bash
   # macOS
   brew install ollama
   
   # Linux
   curl -fsSL https://ollama.ai/install.sh | sh
   
   # Windows
   # 从 https://ollama.ai/download 下载安装包
   ```

2. **启动 Ollama 服务**
   ```bash
   ollama serve
   ```

3. **拉取所需的模型**
   ```bash
   # 拉取聊天模型
   ollama pull qwen2.5:3b
   
   # 拉取专门的嵌入模型（推荐）
   ollama pull nomic-embed-text
   ```

4. **验证服务是否运行**
   ```bash
   curl http://localhost:11434/api/tags
   ```

5. **重启应用**
   应用会自动检测 Ollama 服务并完成 RAG 数据初始化

### 方案 2：修改配置文件使用云模型

如果您不想使用本地 Ollama，可以修改 `application.yml`：

```yaml
ai:
  model:
    provider: openai  # 或 deepseek
    use-local: false
  
  openai:
    api-key: your-api-key
    base-url: https://api.openai.com/v1
    model: gpt-4o-mini
```

同时需要修改 [Langchain4jConfig.java](file:///Volumes/Suxia/IdeaProjects/XinLing/xinling-ai/src/main/java/com/xinling/ai/config/Langchain4jConfig.java) 中的 `embeddingModel()` Bean，使用云服务的嵌入模型。

### 方案 3：暂时禁用 RAG 自动初始化

如果暂时不需要 RAG 功能，可以在 `application.yml` 中禁用：

```yaml
ai:
  rag:
    enable-initialization: false
```

## 配置说明

当前 Ollama 配置位于 `xinling-admin/src/main/resources/application.yml`：

```yaml
ai:
  ollama:
    base-url: http://localhost:11434  # Ollama 服务地址
    model: qwen2.5:3b                  # 聊天模型名称
    timeout-seconds: 360               # 超时时间（秒）
```

**注意：** 当前配置使用同一个模型进行聊天和嵌入操作。为了获得更好的性能和效果，建议：

1. 使用专门的嵌入模型（如 `nomic-embed-text` 或 `mxbai-embed-large`）
2. 在 [Langchain4jConfig.java](file:///Volumes/Suxia/IdeaProjects/XinLing/xinling-ai/src/main/java/com/xinling/ai/config/Langchain4jConfig.java) 中分别配置聊天模型和嵌入模型

## 验证 RAG 功能

启动应用后，检查日志：

✅ **成功初始化的日志：**
```
开始初始化数据库文档到RAG知识库
文档 doc/database_analysis.md 已加载到RAG知识库，共XX个分段
文档 doc/sql_query_analysis.md 已加载到RAG知识库，共XX个分段
数据库文档初始化完成，共加载 2 个文档
RAG数据初始化完成
```

⚠️ **服务不可用的日志：**
```
EmbeddingModel 连接测试失败: Failed to connect to localhost/[0:0:0:0:0:0:0:1]:11434
EmbeddingModel 服务不可用，跳过数据库文档初始化。请确保 Ollama 服务正在运行。
RAG数据初始化过程中发生错误，但不影响应用启动。
```

## 常见问题

### Q1: 为什么使用本地 Ollama？
- 数据隐私：所有数据处理都在本地完成
- 无需 API Key：完全免费使用
- 离线可用：不依赖网络连接

### Q2: 本地模型性能如何？
- 取决于您的硬件配置（CPU/GPU、内存）
- 对于开发和测试场景足够使用
- 生产环境建议使用云服务或专用 GPU 服务器

### Q3: 如何优化嵌入性能？
1. 使用专门的嵌入模型而非通用聊天模型
2. 调整批量处理大小
3. 考虑使用向量数据库（如 Chroma、Milvus）替代内存存储

## 技术支持

如有其他问题，请查看：
- [系统开发环境搭建手册.md](file:///Volumes/Suxia/IdeaProjects/XinLing/doc/系统开发环境搭建手册.md)
- [AI 使用指南](file:///Volumes/Suxia/IdeaProjects/XinLing/xinling-ai/src/main/resources/doc/ai_usage_guide.md)
