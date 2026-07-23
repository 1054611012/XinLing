# 企业级AI管理系统 - 快速开发指南

## 📋 开发前必读

### 1. 环境准备

```bash
# 确保已安装
- Node.js >= 16.0
- npm >= 8.0 或 yarn >= 1.22
- 若依前端项目（Vue3 + Element Plus版本）
```

### 2. 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Element Plus | 最新版 | UI组件库 |
| Axios | 最新版 | HTTP请求 |
| Vue Router | 4.x | 路由管理 |
| Pinia | 最新版 | 状态管理 |

---

## 🚀 快速开始（5步完成）

### 步骤1: 执行数据库脚本

```sql
-- 1. 执行表结构脚本
source /Volumes/Suxia/IdeaProjects/XinLing/sql/ai_model_management.sql

-- 2. 执行菜单脚本
source /Volumes/Suxia/IdeaProjects/XinLing/sql/ai_menu.sql
```

**执行后验证**：
```sql
-- 检查表是否创建成功
SHOW TABLES LIKE 'ai_%';

-- 检查菜单是否插入成功
SELECT * FROM sys_menu WHERE menu_name = 'AI管理';
```

### 步骤2: 创建前端API文件

在项目 `src/api/` 目录下创建 `ai` 文件夹，并创建以下4个文件：

#### 2.1 `src/api/ai/provider.js` - 提供商API

```javascript
import request from '@/utils/request'

// 查询列表
export function listProvider(query) {
  return request({ url: '/ai/provider/list', method: 'get', params: query })
}

// 查询详情
export function getProvider(providerId) {
  return request({ url: '/ai/provider/' + providerId, method: 'get' })
}

// 新增
export function addProvider(data) {
  return request({ url: '/ai/provider', method: 'post', data })
}

// 修改
export function updateProvider(data) {
  return request({ url: '/ai/provider', method: 'put', data })
}

// 删除
export function delProvider(providerIds) {
  return request({ url: '/ai/provider/' + providerIds, method: 'delete' })
}

// 获取启用的提供商
export function getEnabledProviders() {
  return request({ url: '/ai/provider/enabled', method: 'get' })
}
```

#### 2.2 `src/api/ai/model.js` - 模型配置API

```javascript
import request from '@/utils/request'

// 查询列表
export function listModel(query) {
  return request({ url: '/ai/model/list', method: 'get', params: query })
}

// 查询详情
export function getModel(modelId) {
  return request({ url: '/ai/model/' + modelId, method: 'get' })
}

// 新增
export function addModel(data) {
  return request({ url: '/ai/model', method: 'post', data })
}

// 修改
export function updateModel(data) {
  return request({ url: '/ai/model', method: 'put', data })
}

// 删除
export function delModel(modelIds) {
  return request({ url: '/ai/model/' + modelIds, method: 'delete' })
}

// 设置默认模型
export function setDefaultModel(modelId) {
  return request({ url: '/ai/model/setDefault/' + modelId, method: 'put' })
}

// 获取启用的对话模型
export function getChatModels() {
  return request({ url: '/ai/model/chatModels', method: 'get' })
}

// 获取启用的嵌入模型
export function getEmbeddingModels() {
  return request({ url: '/ai/model/embeddingModels', method: 'get' })
}
```

#### 2.3 `src/api/ai/session.js` - 会话配置API

```javascript
import request from '@/utils/request'

// 查询列表
export function listSession(query) {
  return request({ url: '/ai/session/list', method: 'get', params: query })
}

// 查询详情
export function getSession(configId) {
  return request({ url: '/ai/session/' + configId, method: 'get' })
}

// 新增
export function addSession(data) {
  return request({ url: '/ai/session', method: 'post', data })
}

// 修改
export function updateSession(data) {
  return request({ url: '/ai/session', method: 'put', data })
}

// 删除
export function delSession(configIds) {
  return request({ url: '/ai/session/' + configIds, method: 'delete' })
}

// 设置默认配置
export function setDefaultSession(configId) {
  return request({ url: '/ai/session/setDefault/' + configId, method: 'put' })
}

// 获取默认配置
export function getDefaultSession() {
  return request({ url: '/ai/session/default', method: 'get' })
}
```

#### 2.4 `src/api/ai/management.js` - 运行时管理API

```javascript
import request from '@/utils/request'

// 切换模型
export function switchModel(modelId) {
  return request({ url: '/ai/management/switchModel/' + modelId, method: 'put' })
}

// 切换会话配置
export function switchSession(configId) {
  return request({ url: '/ai/management/switchSession/' + configId, method: 'put' })
}

// 使用默认配置
export function useDefault() {
  return request({ url: '/ai/management/useDefault', method: 'put' })
}

// 刷新模型缓存
export function refreshModels() {
  return request({ url: '/ai/management/refresh', method: 'post' })
}

// 获取当前模型
export function getCurrentModel() {
  return request({ url: '/ai/management/currentModel', method: 'get' })
}
```

### 步骤3: 创建前端页面

在项目 `src/views/` 目录下创建 `ai` 文件夹，并创建以下3个页面：

#### 3.1 `src/views/ai/provider/index.vue` - 提供商管理

完整代码参考：[前端开发文档.md](./前端开发文档.md) 第749-1046行

#### 3.2 `src/views/ai/model/index.vue` - 模型配置管理

**关键功能点**：
- 提供商联动下拉选择
- 模型类型选择（chat/embedding/image）
- 云端模型需填写API密钥
- 温度参数和Top-P参数（0-2范围）
- 设置默认模型按钮

**核心表单字段**：
```vue
<template>
  <el-form :model="form" :rules="rules">
    <el-form-item label="提供商" prop="providerId">
      <el-select v-model="form.providerId" placeholder="请选择提供商">
        <el-option 
          v-for="item in providerOptions" 
          :key="item.providerId"
          :label="item.providerName"
          :value="item.providerId"
        />
      </el-select>
    </el-form-item>
    
    <el-form-item label="模型名称" prop="modelName">
      <el-input v-model="form.modelName" />
    </el-form-item>
    
    <el-form-item label="模型编码" prop="modelCode">
      <el-input v-model="form.modelCode" />
    </el-form-item>
    
    <el-form-item label="模型类型" prop="modelType">
      <el-select v-model="form.modelType">
        <el-option label="对话模型" value="chat" />
        <el-option label="嵌入模型" value="embedding" />
        <el-option label="图像模型" value="image" />
      </el-select>
    </el-form-item>
    
    <!-- 云端模型需要API密钥 -->
    <el-form-item label="API密钥" prop="apiKey" v-if="isCloudModel">
      <el-input v-model="form.apiKey" type="password" show-password />
    </el-form-item>
    
    <el-form-item label="温度参数" prop="temperature">
      <el-input-number 
        v-model="form.temperature" 
        :min="0" 
        :max="2" 
        :step="0.1" 
        :precision="2" 
      />
    </el-form-item>
  </el-form>
</template>

<script setup>
import { listProvider } from '@/api/ai/provider'
import { listModel, getModel, addModel, updateModel, delModel, setDefaultModel } from '@/api/ai/model'

const providerOptions = ref([])

// 加载提供商选项
function loadProviders() {
  listProvider({ pageNum: 1, pageSize: 100 }).then(res => {
    providerOptions.value = res.rows
  })
}

// 设置默认模型
function handleSetDefault(row) {
  proxy.$modal.confirm(`确认将"${row.modelName}"设为默认模型？`).then(() => {
    return setDefaultModel(row.modelId)
  }).then(() => {
    proxy.$modal.msgSuccess('设置成功')
    getList()
  })
}

loadProviders()
</script>
```

#### 3.3 `src/views/ai/session/index.vue` - 会话配置管理

**关键功能点**：
- 对话模型和嵌入模型联动选择
- RAG配置开关和参数
- 系统提示词多行文本框
- 设置默认配置按钮

**核心表单字段**：
```vue
<template>
  <el-form :model="form" :rules="rules">
    <el-form-item label="配置名称" prop="configName">
      <el-input v-model="form.configName" />
    </el-form-item>
    
    <el-form-item label="对话模型" prop="chatModelId">
      <el-select v-model="form.chatModelId" placeholder="请选择对话模型">
        <el-option 
          v-for="item in chatModelOptions" 
          :key="item.modelId"
          :label="item.modelName"
          :value="item.modelId"
        />
      </el-select>
    </el-form-item>
    
    <el-form-item label="嵌入模型" prop="embeddingModelId">
      <el-select v-model="form.embeddingModelId" placeholder="请选择嵌入模型">
        <el-option 
          v-for="item in embeddingModelOptions" 
          :key="item.modelId"
          :label="item.modelName"
          :value="item.modelId"
        />
      </el-select>
    </el-form-item>
    
    <el-form-item label="启用RAG" prop="enableRag">
      <el-radio-group v-model="form.enableRag">
        <el-radio label="1">是</el-radio>
        <el-radio label="0">否</el-radio>
      </el-radio-group>
    </el-form-item>
    
    <el-form-item label="RAG检索数量" prop="ragMaxResults" v-if="form.enableRag === '1'">
      <el-input-number v-model="form.ragMaxResults" :min="1" :max="10" />
    </el-form-item>
    
    <el-form-item label="系统提示词" prop="systemPrompt">
      <el-input 
        v-model="form.systemPrompt" 
        type="textarea" 
        :rows="5"
        placeholder="请输入系统提示词"
      />
    </el-form-item>
  </el-form>
</template>

<script setup>
import { getChatModels, getEmbeddingModels } from '@/api/ai/model'
import { listSession, getSession, addSession, updateSession, delSession, setDefaultSession } from '@/api/ai/session'

const chatModelOptions = ref([])
const embeddingModelOptions = ref([])

// 加载模型选项
function loadModelOptions() {
  getChatModels().then(res => {
    chatModelOptions.value = res.data
  })
  
  getEmbeddingModels().then(res => {
    embeddingModelOptions.value = res.data
  })
}

// 设置默认配置
function handleSetDefault(row) {
  proxy.$modal.confirm(`确认将"${row.configName}"设为默认配置？`).then(() => {
    return setDefaultSession(row.configId)
  }).then(() => {
    proxy.$modal.msgSuccess('设置成功')
    getList()
  })
}

loadModelOptions()
</script>
```

### 步骤4: 配置路由和菜单

**若依框架会自动从数据库读取菜单并生成路由**，只需确保：

1. 数据库菜单的 `component` 字段与页面路径一致：
   - `ai/provider/index` → `src/views/ai/provider/index.vue`
   - `ai/model/index` → `src/views/ai/model/index.vue`
   - `ai/session/index` → `src/views/ai/session/index.vue`

2. 菜单的 `perms` 字段与按钮权限一致：
   - `ai:provider:list` - 查看提供商列表
   - `ai:model:list` - 查看模型列表
   - `ai:session:list` - 查看会话列表

### 步骤5: 测试验证

1. **启动后端服务**
```bash
cd /Volumes/Suxia/IdeaProjects/XinLing
./xinling.sh
```

2. **启动前端服务**
```bash
npm run dev
```

3. **访问系统**
- 登录系统后，左侧菜单应显示"AI管理"
- 依次测试三个页面的增删改查功能
- 测试设置默认模型和默认配置功能

---

## 📊 数据结构速查表

### 1. AiModelProvider（模型提供商）

| 字段 | 类型 | 必填 | 说明 | 默认值 |
|------|------|------|------|--------|
| providerName | String | ✅ | 提供商名称 | - |
| providerCode | String | ✅ | 提供商编码（唯一） | - |
| providerType | String | ✅ | 类型：local/cloud | cloud |
| apiBaseUrl | String | ❌ | API基础地址 | - |
| sortOrder | Number | ❌ | 排序 | 0 |
| status | String | ✅ | 状态：0/1 | 0 |
| remark | String | ❌ | 备注 | - |

**示例数据**：
```json
{
  "providerName": "Ollama本地服务",
  "providerCode": "ollama",
  "providerType": "local",
  "apiBaseUrl": "http://localhost:11434",
  "sortOrder": 1,
  "status": "0"
}
```

### 2. AiModelConfig（模型配置）

| 字段 | 类型 | 必填 | 说明 | 默认值 |
|------|------|------|------|--------|
| providerId | Number | ✅ | 提供商ID | - |
| modelName | String | ✅ | 模型名称 | - |
| modelCode | String | ✅ | 模型编码（唯一） | - |
| modelType | String | ✅ | 类型：chat/embedding/image | - |
| apiKey | String | 条件 | API密钥（云端必填） | - |
| contextWindow | Number | ❌ | 上下文窗口大小 | - |
| maxTokens | Number | ❌ | 最大输出token数 | - |
| temperature | Number | ❌ | 温度参数（0-2） | 0.7 |
| topP | Number | ❌ | Top-P参数（0-1） | 1.0 |
| timeoutSeconds | Number | ❌ | 超时时间（秒） | 300 |
| isDefault | String | ❌ | 是否默认：0/1 | 0 |
| sortOrder | Number | ❌ | 排序 | 0 |
| status | String | ✅ | 状态：0/1 | 0 |
| remark | String | ❌ | 备注 | - |

**示例数据**：
```json
{
  "providerId": 1,
  "modelName": "Qwen2.5-3B",
  "modelCode": "qwen2.5:3b",
  "modelType": "chat",
  "apiKey": null,
  "contextWindow": 32768,
  "maxTokens": 8192,
  "temperature": 0.7,
  "topP": 1.0,
  "timeoutSeconds": 300,
  "status": "0"
}
```

### 3. AiSessionConfig（会话配置）

| 字段 | 类型 | 必填 | 说明 | 默认值 |
|------|------|------|------|--------|
| configName | String | ✅ | 配置名称 | - |
| chatModelId | Number | ✅ | 对话模型ID | - |
| embeddingModelId | Number | ❌ | 嵌入模型ID | - |
| maxHistoryMessages | Number | ❌ | 最大历史消息数 | 20 |
| enableRag | String | ✅ | 启用RAG：0/1 | 0 |
| ragMaxResults | Number | ❌ | RAG检索数量 | 3 |
| ragMinScore | Number | ❌ | RAG最小相似度 | 0.5 |
| systemPrompt | String | ❌ | 系统提示词 | - |
| isDefault | String | ❌ | 是否默认：0/1 | 0 |
| status | String | ✅ | 状态：0/1 | 0 |
| remark | String | ❌ | 备注 | - |

**示例数据**：
```json
{
  "configName": "默认配置",
  "chatModelId": 1,
  "embeddingModelId": 2,
  "maxHistoryMessages": 20,
  "enableRag": "1",
  "ragMaxResults": 3,
  "ragMinScore": 0.5,
  "systemPrompt": "你是一个专业的AI助手",
  "status": "0"
}
```

---

## 🔐 权限控制速查

### 按钮权限标识

| 页面 | 权限标识 | 说明 |
|------|----------|------|
| **提供商管理** | | |
| | `ai:provider:list` | 查看列表 |
| | `ai:provider:add` | 新增按钮 |
| | `ai:provider:edit` | 编辑按钮 |
| | `ai:provider:remove` | 删除按钮 |
| | `ai:provider:export` | 导出按钮 |
| **模型配置管理** | | |
| | `ai:model:list` | 查看列表 |
| | `ai:model:add` | 新增按钮 |
| | `ai:model:edit` | 编辑按钮 + 设置默认 |
| | `ai:model:remove` | 删除按钮 |
| | `ai:model:export` | 导出按钮 |
| **会话配置管理** | | |
| | `ai:session:list` | 查看列表 |
| | `ai:session:add` | 新增按钮 |
| | `ai:session:edit` | 编辑按钮 + 设置默认 |
| | `ai:session:remove` | 删除按钮 |
| | `ai:session:export` | 导出按钮 |

### 按钮权限使用示例

```vue
<template>
  <!-- 新增按钮 -->
  <el-button v-hasPermi="['ai:provider:add']" @click="handleAdd">新增</el-button>
  
  <!-- 编辑按钮 -->
  <el-button v-hasPermi="['ai:provider:edit']" @click="handleUpdate(row)">编辑</el-button>
  
  <!-- 删除按钮 -->
  <el-button v-hasPermi="['ai:provider:remove']" @click="handleDelete(row)">删除</el-button>
  
  <!-- 导出按钮 -->
  <el-button v-hasPermi="['ai:provider:export']" @click="handleExport">导出</el-button>
</template>
```

---

## ⚡ 核心功能实现

### 1. 设置默认模型/配置

```javascript
// 模型配置页面 - 设置默认模型
function handleSetDefault(row) {
  proxy.$modal.confirm(`确认将"${row.modelName}"设为默认模型？`).then(() => {
    return setDefaultModel(row.modelId)
  }).then(() => {
    proxy.$modal.msgSuccess('设置成功')
    getList()
  })
}

// 会话配置页面 - 设置默认配置
function handleSetDefault(row) {
  proxy.$modal.confirm(`确认将"${row.configName}"设为默认配置？`).then(() => {
    return setDefaultSession(row.configId)
  }).then(() => {
    proxy.$modal.msgSuccess('设置成功')
    getList()
  })
}
```

### 2. 模型切换功能

```vue
<template>
  <el-select v-model="currentModel" @change="handleSwitch">
    <el-option 
      v-for="model in modelList" 
      :key="model.modelId"
      :label="model.modelName"
      :value="model.modelId"
    >
      <span>{{ model.modelName }}</span>
      <el-tag v-if="model.isDefault === '1'" size="small" type="success">
        默认
      </el-tag>
    </el-option>
  </el-select>
</template>

<script setup>
import { switchModel } from '@/api/ai/management'

function handleSwitch(modelId) {
  switchModel(modelId).then(() => {
    ElMessage.success('模型切换成功')
  }).catch(() => {
    ElMessage.error('模型切换失败')
  })
}
</script>
```

### 3. 关联数据联动

```javascript
// 模型配置 - 根据提供商类型显示/隐藏API密钥字段
const isCloudModel = computed(() => {
  const provider = providerOptions.value.find(p => p.providerId === form.value.providerId)
  return provider && provider.providerType === 'cloud'
})

// 会话配置 - RAG开关控制参数显示
<el-form-item label="RAG检索数量" v-if="form.enableRag === '1'">
  <el-input-number v-model="form.ragMaxResults" />
</el-form-item>
```

---

## 🛠️ 常见问题速查

### Q1: 新增模型时，提供商下拉框为空？

**解决方法**：确保先加载提供商列表
```javascript
onMounted(() => {
  listProvider({ pageNum: 1, pageSize: 100 }).then(res => {
    providerOptions.value = res.rows
  })
})
```

### Q2: 设置默认模型后，列表没有刷新？

**解决方法**：设置成功后调用 `getList()` 刷新列表
```javascript
setDefaultModel(modelId).then(() => {
  ElMessage.success('设置成功')
  getList() // 必须调用刷新
})
```

### Q3: 如何判断模型类型显示不同的表单字段？

**解决方法**：使用 `v-if` 条件渲染
```vue
<!-- 只有对话模型才显示温度参数 -->
<el-form-item label="温度参数" v-if="form.modelType === 'chat'">
  <el-input-number v-model="form.temperature" />
</el-form-item>

<!-- 只有嵌入模型才显示上下文窗口 -->
<el-form-item label="上下文窗口" v-if="form.modelType === 'embedding'">
  <el-input-number v-model="form.contextWindow" />
</el-form-item>
```

### Q4: 如何实现批量删除？

**解决方法**：
```vue
<template>
  <el-table @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="55" />
  </el-table>
  
  <el-button 
    :disabled="multiple" 
    @click="handleBatchDelete"
  >
    批量删除
  </el-button>
</template>

<script setup>
const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.modelId)
  multiple.value = !selection.length
}

function handleBatchDelete() {
  proxy.$modal.confirm('是否确认删除选中的数据项？').then(() => {
    return delModel(ids.value.join(','))
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  })
}
</script>
```

### Q5: 数字字段提交时类型不对？

**解决方法**：确保表单字段类型为 Number
```javascript
const form = ref({
  providerId: undefined,
  contextWindow: undefined,
  maxTokens: undefined,
  temperature: undefined,
  sortOrder: 0
})

// 提交前验证类型
function submitForm() {
  proxy.$refs['formRef'].validate(valid => {
    if (valid) {
      // 确保数字字段正确
      form.value.sortOrder = Number(form.value.sortOrder) || 0
      form.value.temperature = Number(form.value.temperature) || 0.7
      
      // 提交数据
      addModel(form.value)
    }
  })
}
```

---

## 📝 开发规范

### 1. 文件命名

```
src/
├── api/ai/
│   ├── provider.js      # 提供商API
│   ├── model.js         # 模型配置API
│   ├── session.js       # 会话配置API
│   └── management.js    # 运行时管理API
└── views/ai/
    ├── provider/
    │   └── index.vue    # 提供商管理页面
    ├── model/
    │   └── index.vue    # 模型配置页面
    └── session/
        └── index.vue    # 会话配置页面
```

### 2. 组件命名

- 组件name属性使用大驼峰：`AiProvider`、`AiModel`、`AiSession`
- 文件名使用小写：`index.vue`

```vue
<script setup name="AiProvider">
// 提供商管理页面
</script>
```

### 3. 表单验证规则

```javascript
const rules = {
  providerName: [
    { required: true, message: '提供商名称不能为空', trigger: 'blur' }
  ],
  providerCode: [
    { required: true, message: '提供商编码不能为空', trigger: 'blur' },
    { 
      pattern: /^[a-zA-Z0-9_-]+$/, 
      message: '只能包含字母、数字、下划线和连字符', 
      trigger: 'blur' 
    }
  ],
  temperature: [
    { type: 'number', min: 0, max: 2, message: '温度参数范围为0-2', trigger: 'blur' }
  ]
}
```

---

## ✅ 开发检查清单

### 开发前
- [ ] 已执行数据库脚本（表结构 + 菜单）
- [ ] 已创建API文件（4个.js文件）
- [ ] 已创建页面文件（3个.vue文件）
- [ ] 已配置后端路由（若依自动生成）

### 开发中
- [ ] 实现列表查询（分页、搜索）
- [ ] 实现新增功能（表单验证）
- [ ] 实现修改功能（数据回显）
- [ ] 实现删除功能（单条 + 批量）
- [ ] 实现设置默认功能
- [ ] 实现权限控制（v-hasPermi）

### 开发后
- [ ] 测试增删改查功能
- [ ] 测试权限控制
- [ ] 测试表单验证
- [ ] 测试关联数据联动
- [ ] 测试默认设置功能
- [ ] 检查错误提示

---

## 📚 参考资源

- **完整开发文档**：[前端开发文档.md](./前端开发文档.md) - 包含完整的API接口文档、页面示例、常见问题等
- **后端架构说明**：[企业级AI管理系统改造说明.md](./企业级AI管理系统改造说明.md)
- **数据库脚本**：
  - [ai_model_management.sql](../../sql/ai_model_management.sql) - 表结构和初始数据
  - [ai_menu.sql](../../sql/ai_menu.sql) - 菜单配置
- **若依官方文档**：https://doc.ruoyi.vip/

---

**文档版本**: 1.0.0  
**最后更新**: 2025-01-22  
**适用对象**: 前端开发人员  
**预计开发时间**: 2-3个工作日（3个页面）
