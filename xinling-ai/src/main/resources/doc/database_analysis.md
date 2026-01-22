# 系统数据库表结构分析与SQL查询

## 概述

基于提供的SQL文件和文档，分析了心灵（XinLing）系统和SU CRM系统的完整数据库表结构，包括用户、部门、角色、岗位、心理模块、定时任务等核心管理模块。

## 核心表结构分析

### 1. 用户表 (sys_user)
- **主键**: user_id
- **说明**: 系统用户信息表，用于用户管理和权限控制
- **字段**:
  - user_id: 用户ID
  - dept_id: 所属部门ID
  - user_name: 用户账号
  - nick_name: 用户昵称
  - user_type: 用户类型（00系统用户）
  - email: 邮箱
  - phonenumber: 手机号
  - sex: 性别（0男 1女 2未知）
  - avatar: 头像地址
  - password: 密码
  - status: 账号状态（0正常 1停用）
  - del_flag: 删除标志（0存在 2删除）
  - login_ip: 最后登录IP
  - login_date: 最后登录时间
  - pwd_update_date: 密码最后更新时间
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间
  - remark: 备注

### 2. 部门表 (sys_dept)
- **主键**: dept_id
- **说明**: 组织架构部门信息表
- **字段**:
  - dept_id: 部门ID
  - parent_id: 父部门ID
  - ancestors: 祖级列表
  - dept_name: 部门名称
  - order_num: 显示顺序
  - leader: 负责人
  - phone: 联系电话
  - email: 邮箱
  - status: 部门状态（0正常 1停用）
  - del_flag: 删除标志（0代表存在 2代表删除）
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间

### 3. 角色表 (sys_role)
- **主键**: role_id
- **说明**: 系统角色信息表，用于管理用户角色及权限
- **字段**:
  - role_id: 角色ID
  - role_name: 角色名称
  - role_key: 角色权限标识
  - role_sort: 显示顺序
  - data_scope: 数据权限范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
  - menu_check_strictly: 菜单树选择项是否关联显示
  - dept_check_strictly: 部门树选择项是否关联显示
  - status: 角色状态（0正常 1停用）
  - del_flag: 删除标志（0存在 2删除）
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间
  - remark: 备注

### 4. 岗位表 (sys_post)
- **主键**: post_id
- **说明**: 系统岗位信息表
- **字段**:
  - post_id: 岗位ID
  - post_code: 岗位编码
  - post_name: 岗位名称
  - post_sort: 显示顺序
  - status: 岗位状态（0正常 1停用）
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间
  - remark: 备注

### 5. 菜单表 (sys_menu)
- **主键**: menu_id
- **说明**: 菜单权限表
- **字段**:
  - menu_id: 菜单ID
  - menu_name: 菜单名称
  - parent_id: 父菜单ID
  - order_num: 显示顺序
  - path: 路由地址
  - component: 组件路径
  - query: 路由参数
  - route_name: 路由名称
  - is_frame: 是否为外链（0是 1否）
  - is_cache: 是否缓存（0缓存 1不缓存）
  - menu_type: 菜单类型（M目录 C菜单 F按钮）
  - visible: 菜单状态（0显示 1隐藏）
  - status: 菜单状态（0正常 1停用）
  - perms: 权限标识
  - icon: 菜单图标
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间
  - remark: 备注

### 6. 配置表 (sys_config)
- **主键**: config_id
- **说明**: 参数配置表
- **字段**:
  - config_id: 参数主键
  - config_name: 参数名称
  - config_key: 参数键名
  - config_value: 参数键值
  - config_type: 系统内置（Y是 N否）
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间
  - remark: 备注

### 7. 字典类型表 (sys_dict_type)
- **主键**: dict_id
- **说明**: 字典类型表
- **字段**:
  - dict_id: 字典主键
  - dict_name: 字典名称
  - dict_type: 字典类型
  - status: 状态（0正常 1停用）
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间
  - remark: 备注

### 8. 字典数据表 (sys_dict_data)
- **主键**: dict_code
- **说明**: 字典数据表
- **字段**:
  - dict_code: 字典编码
  - dict_sort: 字典排序
  - dict_label: 字典标签
  - dict_value: 字典键值
  - dict_type: 字典类型
  - css_class: 样式属性（其他样式扩展）
  - list_class: 表格回显样式
  - is_default: 是否默认（Y是 N否）
  - status: 状态（0正常 1停用）
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间
  - remark: 备注

### 9. 定时任务表 (sys_job)
- **主键**: job_id
- **说明**: 定时任务调度表
- **字段**:
  - job_id: 任务ID
  - job_name: 任务名称
  - job_group: 任务组名
  - invoke_target: 调用目标字符串
  - cron_expression: cron执行表达式
  - misfire_policy: 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
  - concurrent: 是否并发执行（0允许 1禁止）
  - status: 状态（0正常 1暂停）
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间
  - remark: 备注信息

### 10. 定时任务日志表 (sys_job_log)
- **主键**: job_log_id
- **说明**: 定时任务调度日志表
- **字段**:
  - job_log_id: 任务日志ID
  - job_name: 任务名称
  - job_group: 任务组名
  - invoke_target: 调用目标字符串
  - job_message: 日志信息
  - status: 执行状态（0正常 1失败）
  - exception_info: 异常信息
  - create_time: 创建时间

### 11. 登录信息表 (sys_logininfor)
- **主键**: info_id
- **说明**: 系统访问记录
- **字段**:
  - info_id: 访问ID
  - user_name: 用户账号
  - ipaddr: 登录IP地址
  - login_location: 登录地点
  - browser: 浏览器类型
  - os: 操作系统
  - status: 登录状态（0成功 1失败）
  - msg: 提示消息
  - login_time: 访问时间

### 12. 操作日志表 (sys_oper_log)
- **主键**: oper_id
- **说明**: 操作日志记录
- **字段**:
  - oper_id: 日志主键
  - title: 模块标题
  - business_type: 业务类型（0其它 1新增 2修改 3删除）
  - method: 方法名称
  - request_method: 请求方式
  - operator_type: 操作类别（0其它 1后台用户 2手机端用户）
  - oper_name: 操作人员
  - dept_name: 部门名称
  - oper_url: 请求URL
  - oper_ip: 主机地址
  - oper_location: 操作地点
  - oper_param: 请求参数
  - json_result: 返回参数
  - status: 操作状态（0正常 1异常）
  - error_msg: 错误消息
  - oper_time: 操作时间
  - cost_time: 消耗时间

### 13. 通知公告表 (sys_notice)
- **主键**: notice_id
- **说明**: 通知公告表
- **字段**:
  - notice_id: 公告ID
  - notice_title: 公告标题
  - notice_type: 公告类型（1通知 2公告）
  - notice_content: 公告内容
  - status: 公告状态（0正常 1关闭）
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间
  - remark: 备注

### 14. 心理测评表 (psyc_test)
- **主键**: id
- **说明**: 心理测评项目表
- **字段**:
  - id: 测评项目ID
  - test_name: 测评项目名称
  - description: 测评简介
  - status: 状态（0停用 1启用）
  - total_questions: 题目数量
  - duration: 测评时长（分钟）
  - create_time: 创建时间
  - update_time: 更新时间

### 15. 心理题目表 (psyc_questions)
- **主键**: id
- **说明**: 题目表
- **字段**:
  - id: 题目ID
  - content: 题目内容
  - type: 题目类型（single-单选题，multiple-多选题，judgment-判断题，fill-填空题，essay-简答题）
  - test_id: 测试ID
  - difficulty: 难度（easy-简单，medium-中等，hard-困难）
  - analysis: 解析内容
  - source: 题目来源
  - created_by: 创建人ID
  - created_at: 创建时间
  - updated_at: 更新时间
  - deleted_at: 删除时间

### 16. 心理选项表 (psyc_options)
- **主键**: id
- **说明**: 题目选项表
- **字段**:
  - id: 选项ID
  - question_id: 题目ID
  - content: 选项内容
  - score: 选项分值
  - sort_order: 排序顺序
  - created_at: 创建时间
  - updated_at: 更新时间

### 17. 心理测评规则表 (psyc_assessment_rule)
- **主键**: id
- **说明**: 测评动态评分区间规则表
- **字段**:
  - id: 规则ID
  - test_id: 测评ID，关联测评主表
  - min_score: 区间下限（含）
  - max_score: 区间上限（含）
  - level: 等级名称
  - suggestion: 建议文案
  - reference_result: 参考结果
  - priority: 优先级
  - is_deleted: 是否删除

### 18. 心理动态表 (psyc_post)
- **主键**: id
- **说明**: 动态主表
- **字段**:
  - id: 动态ID
  - user_id: 发布用户ID
  - content: 文字内容
  - visible: 可见范围（1公开,2仅好友,3私密）
  - like_count: 点赞数
  - comment_count: 评论数
  - favorite_count: 收藏数
  - share_count: 分享数
  - status: 状态（1正常,0删除,2审核中）
  - created_at: 发布时间
  - updated_at: 更新时间

### 19. 心理动态媒体表 (psyc_post_media)
- **主键**: id
- **说明**: 动态媒体资源表
- **字段**:
  - id: 主键ID
  - post_id: 动态ID
  - media_url: 媒体URL
  - media_type: 类型（1图片,2视频）
  - sort_order: 顺序

### 20. 心理反馈表 (psyc_feedback)
- **主键**: id
- **说明**: 用户意见反馈主表
- **字段**:
  - id: 主键ID
  - user_id: 提交反馈的用户ID
  - type: 反馈类型（1-Bug问题 2-产品建议 3-投诉 4-功能需求 5-其他）
  - content: 反馈内容
  - images: 截图/图片列表，JSON数组形式
  - contact: 用户联系方式
  - device_info: 自动收集的设备信息
  - status: 处理状态（0-待处理 1-处理中 2-已处理 3-已关闭）
  - create_time: 创建时间
  - update_time: 更新时间

### 21. 心理反馈回复表 (psyc_feedback_reply)
- **主键**: id
- **说明**: 意见反馈的回复记录表
- **字段**:
  - id: 回复记录主键ID
  - feedback_id: 关联的反馈ID
  - reply_user_id: 回复的管理员ID
  - content: 回复内容
  - create_time: 创建时间

### 22. 关联表

#### 用户-角色关联表 (sys_user_role)
- **主键**: user_id + role_id
- **说明**: 用户与角色多对多关联表
- **字段**:
  - user_id: 用户ID
  - role_id: 角色ID

#### 用户-岗位关联表 (sys_user_post)
- **主键**: user_id + post_id
- **说明**: 用户与岗位多对多关联表
- **字段**:
  - user_id: 用户ID
  - post_id: 岗位ID

#### 角色-部门关联表 (sys_role_dept)
- **主键**: role_id + dept_id
- **说明**: 角色和部门关联表
- **字段**:
  - role_id: 角色ID
  - dept_id: 部门ID

#### 角色-菜单关联表 (sys_role_menu)
- **主键**: role_id + menu_id
- **说明**: 角色和菜单关联表
- **字段**:
  - role_id: 角色ID
  - menu_id: 菜单ID

#### 代码生成表 (gen_table)
- **主键**: table_id
- **说明**: 代码生成业务表
- **字段**:
  - table_id: 编号
  - table_name: 表名称
  - table_comment: 表描述
  - sub_table_name: 关联子表的表名
  - sub_table_fk_name: 子表关联的外键名
  - class_name: 实体类名称
  - tpl_category: 使用的模板
  - tpl_web_type: 前端模板类型
  - package_name: 生成包路径
  - module_name: 生成模块名
  - business_name: 生成业务名
  - function_name: 生成功能名
  - function_author: 生成功能作者
  - gen_type: 生成代码方式
  - gen_path: 生成路径
  - options: 其它生成选项
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间
  - remark: 备注

#### 代码生成列 (gen_table_column)
- **主键**: column_id
- **说明**: 代码生成业务表字段
- **字段**:
  - column_id: 编号
  - table_id: 归属表编号
  - column_name: 列名称
  - column_comment: 列描述
  - column_type: 列类型
  - java_type: JAVA类型
  - java_field: JAVA字段名
  - is_pk: 是否主键
  - is_increment: 是否自增
  - is_required: 是否必填
  - is_insert: 是否为插入字段
  - is_edit: 是否编辑字段
  - is_list: 是否列表字段
  - is_query: 是否查询字段
  - query_type: 查询方式
  - html_type: 显示类型
  - dict_type: 字典类型
  - sort: 排序
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间

## 常用SQL查询

### 1. 用户相关查询

#### 查询用户列表（带部门信息）
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
WHERE u.del_flag = '0'
ORDER BY u.create_time DESC;
```

#### 查询指定部门下的用户
```sql
SELECT 
    u.user_id,
    u.user_name,
    u.nick_name,
    u.email,
    u.phonenumber,
    u.sex,
    u.status,
    u.create_time
FROM sys_user u
WHERE u.dept_id = ?
  AND u.del_flag = '0';
```

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

### 2. 部门相关查询

#### 查询部门树结构
```sql
SELECT 
    dept_id,
    parent_id,
    dept_name,
    leader,
    status,
    create_time,
    (SELECT COUNT(*) FROM sys_user u WHERE u.dept_id = d.dept_id 
     AND u.del_flag = '0' AND u.status = '0') as user_count
FROM sys_dept d
ORDER BY parent_id, dept_id;
```

#### 查询启用的部门列表
```sql
SELECT 
    dept_id,
    parent_id,
    dept_name,
    leader,
    status,
    create_time
FROM sys_dept
WHERE status = '0'
ORDER BY dept_id;
```

### 3. 角色相关查询

#### 查询角色列表
```sql
SELECT 
    role_id,
    role_name,
    role_key,
    role_sort,
    data_scope,
    status,
    create_time
FROM sys_role
WHERE del_flag = '0'
ORDER BY role_sort;
```

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

#### 查询角色下所有用户
```sql
SELECT 
    u.user_id,
    u.user_name,
    u.nick_name,
    u.email,
    u.phonenumber,
    u.status
FROM sys_user u
INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
WHERE ur.role_id = ?
  AND u.del_flag = '0'
ORDER BY u.create_time;
```

### 4. 岗位相关查询

#### 查询岗位列表
```sql
SELECT 
    post_id,
    post_code,
    post_name,
    post_sort,
    status,
    create_time
FROM sys_post
WHERE status = '0'
ORDER BY post_sort;
```

#### 查询用户拥有的岗位
```sql
SELECT 
    p.post_id,
    p.post_code,
    p.post_name,
    p.post_sort
FROM sys_post p
INNER JOIN sys_user_post up ON p.post_id = up.post_id
WHERE up.user_id = ?
  AND p.status = '0'
ORDER BY p.post_sort;
```

#### 查询岗位下所有用户
```sql
SELECT 
    u.user_id,
    u.user_name,
    u.nick_name,
    u.email,
    u.phonenumber,
    u.status
FROM sys_user u
INNER JOIN sys_user_post up ON u.user_id = up.user_id
WHERE up.post_id = ?
  AND u.del_flag = '0'
ORDER BY u.create_time;
```

### 5. 菜单相关查询

#### 查询角色拥有的菜单
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

### 6. 心理模块相关查询

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

### 7. 综合查询

#### 查询用户完整信息（包含部门、角色、岗位）
```sql
SELECT 
    u.user_id,
    u.user_name,
    u.nick_name,
    u.email,
    u.phonenumber,
    u.sex,
    u.status,
    d.dept_name,
    GROUP_CONCAT(r.role_name) as role_names,
    GROUP_CONCAT(p.post_name) as post_names
FROM sys_user u
LEFT JOIN sys_dept d ON u.dept_id = d.dept_id
LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.role_id AND r.del_flag = '0'
LEFT JOIN sys_user_post up ON u.user_id = up.user_id
LEFT JOIN sys_post p ON up.post_id = p.post_id AND p.status = '0'
WHERE u.del_flag = '0'
GROUP BY u.user_id, u.user_name, u.nick_name, u.email, 
         u.phonenumber, u.sex, u.status, d.dept_name
ORDER BY u.create_time DESC;
```

#### 按状态统计各类数据
```sql
-- 统计用户状态
SELECT 
    CASE status 
        WHEN '0' THEN '正常' 
        WHEN '1' THEN '停用' 
    END as status_name,
    COUNT(*) as count
FROM sys_user
WHERE del_flag = '0'
GROUP BY status;

-- 统计部门状态
SELECT 
    CASE status 
        WHEN '0' THEN '正常' 
        WHEN '1' THEN '停用' 
    END as status_name,
    COUNT(*) as count
FROM sys_dept
GROUP BY status;

-- 统计角色状态
SELECT 
    CASE status 
        WHEN '0' THEN '正常' 
        WHEN '1' THEN '停用' 
    END as status_name,
    COUNT(*) as count
FROM sys_role
WHERE del_flag = '0'
GROUP BY status;

-- 统计岗位状态
SELECT 
    CASE status 
        WHEN '0' THEN '正常' 
        WHEN '1' THEN '停用' 
    END as status_name,
    COUNT(*) as count
FROM sys_post
GROUP BY status;
```

## 数据权限说明

系统支持多种数据权限范围：
- 1: 全部数据权限
- 2: 自定数据权限
- 3: 本部门数据权限
- 4: 本部门及以下数据权限
- 5: 仅本人数据权限

数据权限通过[DataScopeAspect](file:///Volumes/Suxia/IdeaProjects/XingLing-Vue/xinling-framework/src/main/java/com/xinling/framework/aspectj/DataScopeAspect.java#L35-L155)切面实现，根据用户角色的数据范围限制查询结果。

## 关联关系

- 用户与部门：多对一关系（一个用户属于一个部门）
- 用户与角色：多对多关系（通过sys_user_role表关联）
- 用户与岗位：多对多关系（通过sys_user_post表关联）
- 角色与部门：多对多关系（通过sys_role_dept表关联）
- 角色与菜单：多对多关系（通过sys_role_menu表关联）
- 心理测评与题目：一对多关系（一个测评包含多个题目）
- 题目与选项：一对多关系（一个题目包含多个选项）
- 动态与媒体：一对多关系（一个动态可包含多个媒体文件）