# 数据库结构与SQL查询结果分析

## 1. 数据库结构概览

根据提供的SQL文件（su_crm.sql）和系统数据库分析文档，我们整理出完整的数据库结构，包括系统管理模块和心理测评模块。

### 1.1 系统管理模块表结构

#### 核心表结构
- **sys_user** - 用户信息表
- **sys_dept** - 部门信息表  
- **sys_role** - 角色信息表
- **sys_post** - 岗位信息表
- **sys_menu** - 菜单权限表

#### 系统配置表
- **sys_config** - 参数配置表
- **sys_dict_type** - 字典类型表
- **sys_dict_data** - 字典数据表

#### 日志与监控表
- **sys_logininfor** - 登录信息表
- **sys_oper_log** - 操作日志表
- **sys_job** - 定时任务表
- **sys_job_log** - 定时任务日志表

### 1.2 心理测评模块表结构

#### 测评相关表
- **psyc_test** - 心理测评项目表
- **psyc_questions** - 题目表
- **psyc_options** - 题目选项表
- **psyc_assessment_rule** - 测评评分规则表

#### 社交互动表
- **psyc_post** - 动态主表
- **psyc_post_media** - 动态媒体资源表

#### 反馈管理表
- **psyc_feedback** - 用户意见反馈表
- **psyc_feedback_reply** - 意见反馈回复表

### 1.3 关联表结构

- **sys_user_role** - 用户角色关联表
- **sys_user_post** - 用户岗位关联表
- **sys_role_menu** - 角色菜单关联表
- **sys_role_dept** - 角色部门关联表

## 2. 常用SQL查询示例与结果分析

### 2.1 用户管理查询

#### 查询所有启用的用户及其部门信息
```sql
SELECT 
    u.user_id,
    u.user_name,
    u.nick_name,
    u.email,
    u.phonenumber,
    u.sex,
    u.status,
    u.create_time,
    d.dept_name
FROM sys_user u
LEFT JOIN sys_dept d ON u.dept_id = d.dept_id
WHERE u.del_flag = '0' AND u.status = '0'
ORDER BY u.create_time DESC;
```

**预期结果**：
- 返回所有未删除且启用的用户信息
- 包含用户的基本信息和所属部门名称
- 按创建时间倒序排列

#### 按部门统计用户数量
```sql
SELECT 
    d.dept_name,
    COUNT(u.user_id) as user_count
FROM sys_dept d
LEFT JOIN sys_user u ON d.dept_id = u.dept_id 
    AND u.del_flag = '0' 
    AND u.status = '0'
GROUP BY d.dept_id, d.dept_name
ORDER BY user_count DESC;
```

**预期结果**：
- 返回每个部门的名称和该部门的用户数量
- 包括没有用户的部门（数量为0）
- 按用户数量降序排列

### 2.2 角色权限查询

#### 查询用户拥有的角色
```sql
SELECT 
    r.role_id,
    r.role_name,
    r.role_key,
    r.role_sort,
    r.status
FROM sys_role r
INNER JOIN sys_user_role ur ON r.role_id = ur.role_id
WHERE ur.user_id = ?
  AND r.del_flag = '0'
  AND r.status = '0'
ORDER BY r.role_sort;
```

**预期结果**：
- 返回指定用户拥有的所有角色信息
- 只返回未删除且启用的角色
- 按角色排序字段排序

#### 查询角色拥有的菜单权限
```sql
SELECT 
    m.menu_id,
    m.parent_id,
    m.menu_name,
    m.path,
    m.component,
    m.menu_type,
    m.perms,
    m.order_num,
    m.status,
    m.create_time
FROM sys_menu m
INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id
WHERE rm.role_id = ?
  AND m.status = '0'
  AND m.menu_type IN ('M', 'C')
ORDER BY m.parent_id, m.order_num;
```

**预期结果**：
- 返回指定角色拥有的所有菜单权限
- 只返回启用的菜单（目录和菜单类型）
- 按父菜单ID和排序字段排序

### 2.3 心理测评查询

#### 查询心理测评及题目
```sql
SELECT 
    t.id as test_id,
    t.test_name,
    t.description,
    t.status,
    t.total_questions,
    q.id as question_id,
    q.content as question_content,
    q.type as question_type,
    q.difficulty
FROM psyc_test t
LEFT JOIN psyc_questions q ON t.id = q.test_id
WHERE t.status = 1
ORDER BY t.id, q.id;
```

**预期结果**：
- 返回所有启用的心理测评及其包含的题目
- 包括测评的基本信息和题目内容
- 按测评ID和题目ID排序

#### 查询心理测评选项
```sql
SELECT 
    o.id,
    o.question_id,
    o.content,
    o.score,
    o.sort_order
FROM psyc_options o
WHERE o.question_id = ?
ORDER BY o.sort_order;
```

**预期结果**：
- 返回指定题目的所有选项
- 包括选项内容、分值和排序
- 按排序字段排序

### 2.4 动态内容查询

#### 查询心理动态及关联媒体
```sql
SELECT 
    p.id,
    p.user_id,
    p.content,
    p.visible,
    p.like_count,
    p.comment_count,
    p.status,
    p.created_at,
    m.media_url,
    m.media_type
FROM psyc_post p
LEFT JOIN psyc_post_media m ON p.id = m.post_id
WHERE p.status = 1
ORDER BY p.created_at DESC;
```

**预期结果**：
- 返回所有正常状态的动态
- 包括动态内容和关联的媒体信息
- 按发布时间倒序排列

## 3. 数据库设计特点分析

### 3.1 表结构设计特点

1. **统一的字段规范**：
   - 所有表都有标准的创建和更新时间字段
   - 统一的删除标志字段（del_flag）
   - 标准的创建者和更新者字段

2. **层次化设计**：
   - 部门表使用祖先列表实现层级关系
   - 菜单表支持多级菜单结构
   - 角色支持多种数据权限范围

3. **灵活的权限控制**：
   - 用户-角色-菜单三级权限体系
   - 支持多种数据权限范围
   - 精细化的菜单权限控制

### 3.2 索引策略

1. **状态字段索引**：
   - 登录信息表的状态和时间字段有索引
   - 操作日志表的业务类型和状态字段有索引

2. **外键关联索引**：
   - 关联表通常在外键字段上建立索引
   - 提高关联查询效率

### 3.3 扩展性考虑

1. **字典表设计**：
   - 支持系统内置和自定义字典
   - 支持样式和列表回显配置

2. **心理模块设计**：
   - 支持多种题型（单选、多选、判断、填空、简答）
   - 支持难度分级和评分规则
   - 支持多媒体内容发布

## 4. 性能优化建议

### 4.1 查询优化

1. **使用适当的索引**：
   - 在经常用于WHERE条件的字段上建立索引
   - 在JOIN条件字段上建立索引
   - 在ORDER BY字段上考虑建立索引

2. **避免N+1查询**：
   - 使用JOIN一次性获取关联数据
   - 合理使用批量查询

3. **分页查询**：
   - 对于大量数据的查询，使用LIMIT分页
   - 避免一次性查询过多数据

### 4.2 数据库维护

1. **定期清理**：
   - 清理过期的日志数据
   - 定期归档历史数据

2. **监控查询性能**：
   - 监控慢查询日志
   - 定期分析执行计划

## 5. 安全性考虑

1. **数据权限控制**：
   - 通过DataScopeAspect实现数据权限控制
   - 支持多种数据范围权限

2. **敏感信息保护**：
   - 用户密码使用加密存储
   - 敏感操作记录操作日志

3. **SQL注入防护**：
   - 使用参数化查询
   - 验证和过滤用户输入

这个完整的数据库结构和SQL查询分析为系统开发和维护提供了详细的参考依据。