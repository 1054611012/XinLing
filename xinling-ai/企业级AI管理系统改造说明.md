# 企业级AI管理系统 - 改造说明

##  概述

本次改造将原有的AI模块升级为企业级AI管理系统，支持：
- ✅ **多模型提供商管理**：支持本地微调模型（Ollama）和云端自定义模型（OpenAI、阿里云百炼、智谱AI等）
- ✅ **动态模型切换**：运行时无需重启即可切换模型
- ✅ **企业级管理界面**：完整的CRUD操作和权限控制
- ✅ **会话配置管理**：支持多套会话配置，包括RAG参数、历史记录等

## 🗂️ 项目结构

```
xinling-ai/
── src/main/java/com/xinling/ai/
│   ├── controller/
│   │   ├── AiModelProviderController.java      # AI模型提供商管理
│   │   ├── AiModelConfigController.java        # AI模型配置管理
│   │   ├── AiSessionConfigController.java      # AI会话配置管理
│   │   └── AiManagementController.java         # AI模型运行时管理
│   ├── domain/entity/
│   │   ├── AiModelProvider.java                # 模型提供商实体
│   │   ├── AiModelConfig.java                  # 模型配置实体
│   │   └── AiSessionConfig.java                # 会话配置实体
│   ├── mapper/
│   │   ├── AiModelProviderMapper.java
│   │   ├── AiModelConfigMapper.java
│   │   └── AiSessionConfigMapper.java
│   ├── service/
│   │   ├── IAiModelProviderService.java
│   │   ├── IAiModelConfigService.java
│   │   ├── IAiSessionConfigService.java
│   │   ├── AiDynamicModelManager.java          # 动态模型管理器（核心）
│   │   ├── AiModelSwitchService.java           # 模型切换服务
│   │   └── impl/
│   │       ├── AiModelProviderServiceImpl.java
│   │       ├── AiModelConfigServiceImpl.java
│   │       └── AiSessionConfigServiceImpl.java
│   └── config/
│       └── LangChain4jConfig.java              # 重构后的配置类
└── src/main/resources/mapper/ai/
    ├── AiModelProviderMapper.xml
    ├── AiModelConfigMapper.xml
    └── AiSessionConfigMapper.xml
```

## 📊 数据库表结构

### 1. ai_model_provider（AI模型提供商表）
管理不同的AI服务提供商：
- 本地模型：Ollama
- 云端模型：阿里云百炼、OpenAI、智谱AI等

### 2. ai_model_config（AI模型配置表）
配置具体模型的参数：
- 模型类型：chat（对话）、embedding（嵌入）、image（图像）
- 模型参数：上下文窗口、最大token、温度、top-p等
- API密钥管理

### 3. ai_session_config（AI会话配置表）
管理会话级别的配置：
- 对话模型和嵌入模型关联
- RAG参数配置
- 历史消息数量
- 系统提示词

## 🚀 快速开始

### 1. 执行数据库脚本

```bash
# 1. 创建表结构
mysql -u root -p your_database < sql/ai_model_management.sql

# 2. 添加菜单
mysql -u root -p your_database < sql/ai_menu.sql
```

### 2. 系统启动

启动后，系统会：
1. 尝试从数据库加载默认模型配置
2. 如果数据库未配置，则降级使用配置文件（application.yml）中的Ollama配置
3. 支持运行时通过管理界面切换模型

### 3. 管理界面操作

访问系统后，在左侧菜单找到 **"AI管理"**，包含三个子菜单：

#### 3.1 模型提供商管理
- 添加新的AI提供商（本地/云端）
- 配置提供商的API地址
- 启用/停用提供商

#### 3.2 模型配置管理
- 为每个提供商配置具体模型
- 设置模型参数（temperature、top_p、max_tokens等）
- 设置默认模型（同类型只能有一个默认）
- 查看可用模型列表

#### 3.3 会话配置管理
- 创建不同的会话配置模板
- 配置RAG检索参数
- 设置系统提示词
- 切换不同的会话配置

## 🔧 API接口说明

### 模型提供商管理
- `GET /ai/provider/list` - 查询提供商列表
- `POST /ai/provider` - 新增提供商
- `PUT /ai/provider` - 修改提供商
- `DELETE /ai/provider/{ids}` - 删除提供商
- `GET /ai/provider/enabled` - 获取启用的提供商

### 模型配置管理
- `GET /ai/model/list` - 查询模型列表
- `POST /ai/model` - 新增模型
- `PUT /ai/model` - 修改模型
- `DELETE /ai/model/{ids}` - 删除模型
- `PUT /ai/model/setDefault/{modelId}` - 设置默认模型
- `GET /ai/model/chatModels` - 获取启用的对话模型
- `GET /ai/model/embeddingModels` - 获取启用的嵌入模型

### 会话配置管理
- `GET /ai/session/list` - 查询会话配置列表
- `POST /ai/session` - 新增会话配置
- `PUT /ai/session` - 修改会话配置
- `DELETE /ai/session/{ids}` - 删除会话配置
- `PUT /ai/session/setDefault/{configId}` - 设置默认配置
- `GET /ai/session/default` - 获取默认配置

### 模型运行时管理
- `PUT /ai/management/switchModel/{modelId}` - 切换到指定模型
- `PUT /ai/management/switchSession/{configId}` - 切换到指定会话配置
- `PUT /ai/management/useDefault` - 使用默认配置
- `POST /ai/management/refresh` - 刷新所有模型缓存
- `GET /ai/management/currentModel` - 获取当前使用的模型

## 💡 核心功能

### 1. 动态模型切换
系统使用 `AiDynamicModelManager` 实现模型的热切换：

```java
// 切换到指定模型
boolean success = aiModelSwitchService.switchToModel(modelId);

// 切换到会话配置
boolean success = aiModelSwitchService.switchToSessionConfig(configId);

// 刷新缓存（配置更新后）
aiModelSwitchService.refreshAllModels();
```

### 2. 多提供商支持
支持多种AI提供商：

| 提供商 | 类型 | 说明 |
|--------|------|------|
| Ollama | local | 本地部署的开源模型 |
| 阿里云百炼 | cloud | 阿里云通义千问系列 |
| OpenAI | cloud | GPT-4、GPT-3.5等 |
| 智谱AI | cloud | ChatGLM系列 |

### 3. 模型缓存机制
- 首次加载模型时创建实例并缓存
- 配置更新后清除缓存，下次访问时重新创建
- 支持单个模型缓存清除和全部清除

## 📝 配置示例

### 添加Ollama本地模型

1. **提供商配置**：
   - 提供商名称：Ollama本地服务
   - 提供商编码：ollama
   - 提供商类型：local
   - API地址：http://localhost:11434

2. **模型配置**：
   - 模型名称：Qwen2.5-7B
   - 模型编码：qwen2.5:7b
   - 模型类型：chat
   - 上下文窗口：32768
   - 最大Token：8192
   - 温度：0.7

### 添加阿里云百炼模型

1. **提供商配置**：
   - 提供商名称：阿里云百炼
   - 提供商编码：aliyun-bailian
   - 提供商类型：cloud
   - API地址：https://dashscope.aliyuncs.com/compatible-mode/v1

2. **模型配置**：
   - 模型名称：通义千问-Plus
   - 模型编码：qwen-plus
   - 模型类型：chat
   - API密钥：your-api-key
   - 上下文窗口：32768
   - 最大Token：8192

## ⚠️ 注意事项

1. **数据库初始化**：首次使用需要执行SQL脚本创建表和初始数据
2. **API密钥安全**：云端模型的API密钥需要妥善保管，建议加密存储
3. **模型兼容性**：不同提供商的模型参数可能不同，需根据实际情况调整
4. **缓存刷新**：修改模型配置后，需要刷新缓存或重启服务才能生效
5. **降级机制**：如果数据库配置不可用，系统会自动降级到配置文件中的Ollama配置

## 🔄 升级影响

### 对现有功能的影响
- ✅ **向后兼容**：保留了原有的配置文件方式，数据库配置不可用时自动降级
- ✅ **无需修改业务代码**：现有的AI服务调用代码无需修改
- ✅ **渐进式迁移**：可以逐步迁移到数据库配置方式

### 新增功能
- ✅ 企业级模型管理界面
- ✅ 运行时动态切换模型
- ✅ 多提供商支持
- ✅ 灵活的会话配置
- ✅ 完整的权限控制

## 📚 相关文件清单

### 数据库脚本
- `sql/ai_model_management.sql` - 表结构和初始数据
- `sql/ai_menu.sql` - 菜单和权限配置

### 实体类
- `AiModelProvider.java` - 模型提供商实体
- `AiModelConfig.java` - 模型配置实体
- `AiSessionConfig.java` - 会话配置实体

### Mapper层
- `AiModelProviderMapper.java/xml`
- `AiModelConfigMapper.java/xml`
- `AiSessionConfigMapper.java/xml`

### Service层
- `IAiModelProviderService.java` / `AiModelProviderServiceImpl.java`
- `IAiModelConfigService.java` / `AiModelConfigServiceImpl.java`
- `IAiSessionConfigService.java` / `AiSessionConfigServiceImpl.java`
- `AiDynamicModelManager.java` - 动态模型管理器（核心）
- `AiModelSwitchService.java` - 模型切换服务

### Controller层
- `AiModelProviderController.java`
- `AiModelConfigController.java`
- `AiSessionConfigController.java`
- `AiManagementController.java`

### 配置类
- `LangChain4jConfig.java` - 重构后的配置类

## 🎯 后续优化建议

1. **API密钥加密存储**：使用加密算法保护API密钥
2. **模型调用监控**：添加模型调用次数、响应时间等监控指标
3. **负载均衡**：支持多个相同模型的负载均衡
4. **模型性能测试**：添加模型性能基准测试功能
5. **前端界面开发**：开发Vue前端管理界面（路径：ai/provider/index等）

## 📞 技术支持

如有问题，请联系开发团队或查看项目文档。

---

**版本**: 1.0.0  
**更新日期**: 2025-01-22  
**作者**: SuXia
