<p align="center">
	<!-- <img alt="logo" src="https://oscimg.oschina.net/oscnet/up-d3d0a9303e11d522a06cd263f3079027715.png"> -->
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">心灵AI v1.0.0</h1>
<h4 align="center">基于Spring Boot 3.5.x + Vue前后端分离的AI增强企业级管理系统</h4>
<p align="center">
	<!-- <a href="https://gitee.com/y_project/xinling-Vue/stargazers"><img src="https://gitee.com/y_project/xinling-Vue/badge/star.svg?theme=dark"></a> -->
	<a href="https://gitee.com/y_project/xinling-Vue"><img src="https://img.shields.io/badge/xinling-v1.0.0-brightgreen.svg"></a>
	<a href="https://gitee.com/y_project/xinling-Vue/blob/master/LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg"></a>
</p>

## 平台简介

心灵AI是一套全部开源的企业级快速开发平台，深度融合了AI技术，为个人开发者和企业提供智能化解决方案。系统基于最新的Spring Boot 3.x和Jakarta EE规范，提供强大的AI功能和可扩展的企业级特性。

* 前端采用Vue、Element UI，提供现代化用户界面
* 后端采用Spring Boot 3.x、Spring Security、Redis & JWT，确保系统安全稳定
* 权限认证使用JWT，支持多终端认证系统
* 支持动态权限菜单加载，灵活的权限控制机制
* 高效率开发，代码生成器一键生成前后端代码
* 支持RabbitMQ/Kafka双重消息中间件，可根据配置动态切换
* 集成AI功能，支持智能对话和知识库问答

## 核心功能

### 1. 用户与权限管理
- **用户管理**：系统操作者配置，包括用户信息维护、状态管理、密码策略等
- **部门管理**：组织机构配置（公司、部门、小组），树形结构展现，支持数据权限控制
- **岗位管理**：配置用户担任职务，便于人员管理和职责划分
- **角色管理**：灵活的角色权限分配，支持菜单权限、数据范围权限控制
- **菜单管理**：系统菜单配置，操作权限和按钮权限标识管理

### 2. 系统配置与监控
- **字典管理**：系统中常用的固定数据维护，如状态值、类型定义等
- **参数管理**：系统动态参数配置，支持运行时调整系统行为
- **操作日志**：系统操作日志记录与查询，异常信息追踪
- **登录日志**：用户登录记录查询，包含登录异常检测
- **在线用户**：实时监控系统活跃用户状态
- **服务监控**：系统CPU、内存、磁盘、堆栈等资源使用情况监控
- **缓存监控**：Redis缓存信息查询与命令统计
- **连接池监视**：数据库连接池状态监控，SQL性能分析

### 3. 业务功能
- **通知公告**：系统通知公告发布与维护
- **定时任务**：在线任务调度管理，支持任务增删改查及执行结果查看
- **代码生成**：前后端代码自动生成（Java、HTML、XML、SQL），支持CRUD代码下载
- **系统接口**：基于业务代码自动生成API接口文档

### 4. AI智能功能
- **多模型支持**：集成Ollama、DeepSeek、OpenAI等多种AI模型，支持灵活切换
- **智能对话系统**：提供自然语言交互界面，支持多轮对话场景
- **RAG知识库**：基于向量检索的智能问答系统，支持文档智能搜索和问答
- **对话历史管理**：完整的对话记录存储和管理功能
- **智能提示**：根据上下文提供智能建议和辅助功能

### 5. 消息中间件功能
- **可切换架构**：支持RabbitMQ和Kafka两种消息中间件，通过配置动态切换
- **聊天消息持久化**：异步将聊天消息持久化到数据库，确保数据可靠性
- **消息顺序消费**：保证消息按序处理，确保数据一致性
- **可靠消息传输**：支持手动ACK机制，确保消息不丢失
- **高可用设计**：支持集群部署，提供容错和负载均衡能力

### 6. 前端功能
- **在线构建器**：可视化表单构建工具，拖拽生成HTML代码
- **响应式界面**：适配桌面端和移动端，提供一致的用户体验
- **富组件库**：提供丰富的UI组件，包括图表、编辑器、上传组件等
- **国际化支持**：多语言界面，满足国际化需求

## 技术架构

### 后端技术栈
- **核心框架**：Spring Boot 3.5.4，基于Jakarta EE 10规范
- **安全框架**：Spring Security，实现认证授权功能
- **持久层框架**：MyBatis，灵活的SQL映射和控制
- **数据库连接池**：Alibaba Druid，高性能数据库连接管理
- **缓存技术**：Redis，分布式缓存和会话管理
- **消息中间件**：RabbitMQ/Kafka，异步消息处理
- **AI集成**：LangChain4j，大语言模型集成框架
- **分页插件**：PageHelper，便捷的分页查询功能
- **Web框架**：Spring Web MVC，基于Jakarta Servlet API
- **数据验证**：Jakarta Bean Validation，统一验证框架

### 前端技术栈
- **前端框架**：Vue 2.x，响应式MVVM框架
- **UI组件库**：Element UI，企业级UI组件库
- **构建工具**：Webpack，模块打包和构建
- **HTTP客户端**：Axios，HTTP请求处理
- **状态管理**：Vuex，集中式状态管理
- **路由管理**：Vue Router，单页面应用路由管理

## 特色功能详解

### AI智能集成
系统深度集成了AI技术，提供以下能力：
- **多模型支持**：可根据需求切换不同的AI模型提供商
- **RAG技术**：通过向量检索实现精准的知识库问答
- **对话记忆**：支持上下文感知的多轮对话
- **自然语言处理**：支持中文语义理解和生成
- **智能推荐**：基于用户行为提供个性化服务

### 可切换消息中间件
- **双协议支持**：通过`xinling.mq.type`配置参数动态切换RabbitMQ/Kafka
- **统一API**：抽象的消息接口，业务代码无需修改即可切换实现
- **条件化配置**：基于配置自动加载对应的消息中间件组件
- **无缝迁移**：支持平滑切换消息中间件而无需停机

### Jakarta EE 10迁移
- **完整迁移**：从javax.*迁移到jakarta.*命名空间
- **Spring Boot 3.x兼容**：充分利用新版本特性和性能优化
- **安全性提升**：利用最新的安全特性保障系统安全

### 安全与权限
- **JWT认证**：无状态的令牌认证机制
- **细粒度权限**：支持URL、按钮级别的权限控制
- **数据权限**：基于角色的数据访问范围控制
- **会话管理**：分布式会话管理，支持集群部署

## 功能操作教程

### 1. 用户管理操作
1. **添加用户**：系统管理 → 用户管理 → 新增
    - 输入用户基本信息（用户名、邮箱、手机号等）
    - 分配部门、岗位、角色
    - 设置密码策略
    - 点击保存完成用户创建

2. **用户权限分配**：在用户编辑页面可分配角色，不同角色具有不同的菜单和数据权限

### 2. AI对话功能使用
1. **进入AI对话**：点击顶部导航栏的"AI助手"
2. **选择AI模型**：在对话界面顶部可切换不同的AI模型
3. **开始对话**：在输入框中输入问题，回车发送
4. **查看历史**：左侧列表显示历史对话记录，可随时切换查看

### 3. RAG知识库使用
1. **上传文档**：AI管理 → 知识库 → 上传文档
    - 支持PDF、Word、TXT等格式
    - 系统自动解析文档内容并建立向量索引
2. **知识库问答**：在AI对话界面选择对应知识库进行针对性问答
3. **管理文档**：可查看、删除已上传的文档

### 4. 消息中间件切换
1. **配置切换**：修改[application.yml](file:///Volumes/Suxia/IdeaProjects/XingLing-Vue/xinling-admin/target/classes/application.yml)中的`xinling.mq.type`参数
    - `rabbitmq`：使用RabbitMQ
    - `kafka`：使用Kafka
2. **重启服务**：修改配置后需要重启服务生效
3. **验证切换**：查看启动日志确认加载了对应的消息中间件组件

### 5. 代码生成器使用
1. **数据表配置**：系统工具 → 代码生成 → 选择对应数据表
2. **生成配置**：设置生成选项（包名、作者、模板等）
3. **预览代码**：可预览生成的代码内容
4. **生成代码**：确认无误后生成前后端代码
5. **下载代码**：下载生成的代码包

### 6. 定时任务管理
1. **创建任务**：系统监控 → 定时任务 → 新增
    - 设置任务名称、cron表达式
    - 配置任务执行类和方法
    - 设置任务参数
2. **任务操作**：可暂停、恢复、立即执行任务
3. **查看日志**：查看任务执行历史和结果

## 快速开始

1. 克隆项目代码
2. 配置数据库连接信息（application.yml）
3. 启动Redis服务
4. 如需AI功能，配置相应的AI模型提供商
5. 根据需要配置消息中间件类型（RabbitMQ或Kafka）
6. 启动项目，使用admin/admin123登录

## 开发环境要求

- **JDK**：17或更高版本
- **Maven**：3.6或更高版本
- **Node.js**：14或更高版本
- **Redis**：6.0或更高版本
- **MySQL**：5.7或8.0版本
- **消息中间件**：RabbitMQ或Kafka（按需选择）

## 在线体验

- 登录账号：admin/admin123
- 演示地址：http://vue.xinling.vip
- 文档地址：http://doc.xinling.vip

## 演示图

<!-- <table>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/cd1f90be5f2684f4560c9519c0f2a232ee8.jpg"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/1cbcf0e6f257c7d3a063c0e3f2ff989e4b3.jpg"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-8074972883b5ba0622e13246738ebba237a.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-9f88719cdfca9af2e58b352a20e23d43b12.png"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-39bf2584ec3a529b0d5a3b70d15c9b37646.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-936ec82d1f4872e1bc980927654b6007307.png"/></td>
    </tr>
	<tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-b2d62ceb95d2dd9b3fbe157bb70d26001e9.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-d67451d308b7a79ad6819723396f7c3d77a.png"/></td>
    </tr>	 
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/5e8c387724954459291aafd5eb52b456f53.jpg"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/644e78da53c2e92a95dfda4f76e6d117c4b.jpg"/></td>
    </tr>
	<tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-8370a0d02977eebf6dbf854c8450293c937.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-49003ed83f60f633e7153609a53a2b644f7.png"/></td>
    </tr>
	<tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-d4fe726319ece268d4746602c39cffc0621.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-c195234bbcd30be6927f037a6755e6ab69c.png"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/b6115bc8c31de52951982e509930b20684a.jpg"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-5e4daac0bb59612c5038448acbcef235e3a.png"/></td>
    </tr>
</table> -->
