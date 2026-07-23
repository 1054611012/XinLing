# 系统数据库表结构分析与SQL查询

## 概述

基于提供的SQL文件和文档，分析了心灵（XinLing）系统和SU CRM系统的完整数据库表结构，包括用户、部门、角色、岗位、心理模块、定时任务等核心管理模块。

## 字段命名规范（重要！不同表族使用不同字段名）

### sys_* 表族（系统管理模块）
- 时间字段: `create_time`, `update_time` (datetime类型)
- 操作人字段: `create_by`, `update_by` (varchar类型)
- 删除标记: `del_flag` (char类型, '0'=存在, '2'=删除)
- 状态字段: `status` (char类型, 需加引号)
- 主键: `{表名简写}_id` (如 user_id, dept_id, role_id, post_id, menu_id)

### psyc_test / psyc_feedback / psyc_feedback_reply（使用 sys_* 命名风格）
- 时间字段: `create_time`, `update_time` (datetime类型)
- 主键: `id`
- psyc_test.status: tinyint类型，0=停用, 1=启用（不加引号）
- psyc_feedback.status: tinyint类型，0=待处理, 1=处理中, 2=已处理, 3=已关闭

### psyc_questions / psyc_options / psyc_post 表族（使用 created_at 风格）
- 时间字段: `created_at`, `updated_at` (timestamp类型)
- 操作人: `created_by` (int类型，注意不是 create_by)
- psyc_questions 删除标记: `deleted_at` (timestamp类型，NULL=未删除)
- psyc_post.status: tinyint类型，1=正常, 0=删除, 2=审核中
- 主键: `id`

### psyc_post_media / psyc_assessment_rule（无时间字段）
- 无 create_time/created_at 等时间字段
- psyc_assessment_rule 删除标记: `is_deleted` (tinyint, 0=未删除, 1=已删除)

### infra_file_config 表（独立命名风格）
- 时间字段: `create_time`, `update_time`
- 操作人: `creator`, `updater`（不是 create_by/update_by）
- 删除标记: `deleted` (bit类型)

## 核心表结构分析

### 1. 用户表 (sys_user)
- **主键**: user_id
- **说明**: 系统用户信息表，用于用户管理和权限控制
- **字段**:
  - user_id: 用户ID (bigint)
  - dept_id: 所属部门ID (bigint)
  - user_name: 用户账号 (varchar)
  - nick_name: 用户昵称 (varchar)
  - user_type: 用户类型（00系统用户）(varchar)
  - email: 邮箱 (varchar)
  - phonenumber: 手机号 (varchar)
  - sex: 性别（0男 1女 2未知）(char)
  - avatar: 头像地址 (varchar)
  - password: 密码 (varchar)
  - status: 账号状态（'0'正常 '1'停用）⚠️ char类型需加引号
  - del_flag: 删除标志（'0'存在 '2'删除）⚠️ char类型需加引号
  - login_ip: 最后登录IP (varchar)
  - login_date: 最后登录时间 (datetime)
  - pwd_update_date: 密码最后更新时间 (datetime)
  - create_by: 创建者 (varchar)
  - create_time: 创建时间 (datetime)
  - update_by: 更新者 (varchar)
  - update_time: 更新时间 (datetime)
  - remark: 备注 (varchar)

### 2. 部门表 (sys_dept)
- **主键**: dept_id
- **说明**: 组织架构部门信息表
- **字段**:
  - dept_id: 部门ID (bigint)
  - parent_id: 父部门ID (bigint)
  - ancestors: 祖级列表 (varchar)
  - dept_name: 部门名称 (varchar)
  - order_num: 显示顺序 (int)
  - leader: 负责人 (varchar)
  - phone: 联系电话 (varchar)
  - email: 邮箱 (varchar)
  - status: 部门状态（'0'正常 '1'停用）⚠️ char类型需加引号
  - del_flag: 删除标志（'0'存在 '2'删除）⚠️ char类型需加引号
  - create_by: 创建者 (varchar)
  - create_time: 创建时间 (datetime)
  - update_by: 更新者 (varchar)
  - update_time: 更新时间 (datetime)

### 3. 角色表 (sys_role)
- **主键**: role_id
- **说明**: 系统角色信息表，用于管理用户角色及权限
- **字段**:
  - role_id: 角色ID (bigint)
  - role_name: 角色名称 (varchar)
  - role_key: 角色权限标识 (varchar)
  - role_sort: 显示顺序 (int)
  - data_scope: 数据权限范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
  - menu_check_strictly: 菜单树选择项是否关联显示
  - dept_check_strictly: 部门树选择项是否关联显示
  - status: 角色状态（'0'正常 '1'停用）⚠️ char类型需加引号
  - del_flag: 删除标志（'0'存在 '2'删除）⚠️ char类型需加引号
  - create_by: 创建者 (varchar)
  - create_time: 创建时间 (datetime)
  - update_by: 更新者 (varchar)
  - update_time: 更新时间 (datetime)
  - remark: 备注 (varchar)

### 4. 岗位表 (sys_post)
- **主键**: post_id
- **说明**: 系统岗位信息表
- **字段**:
  - post_id: 岗位ID (bigint)
  - post_code: 岗位编码 (varchar)
  - post_name: 岗位名称 (varchar)
  - post_sort: 显示顺序 (int)
  - status: 岗位状态（'0'正常 '1'停用）⚠️ char类型需加引号
  - create_by: 创建者 (varchar)
  - create_time: 创建时间 (datetime)
  - update_by: 更新者 (varchar)
  - update_time: 更新时间 (datetime)
  - remark: 备注 (varchar)

### 5. 菜单表 (sys_menu)
- **主键**: menu_id
- **说明**: 菜单权限表
- **字段**:
  - menu_id: 菜单ID (bigint)
  - menu_name: 菜单名称 (varchar)
  - parent_id: 父菜单ID (bigint)
  - order_num: 显示顺序 (int)
  - path: 路由地址 (varchar)
  - component: 组件路径 (varchar)
  - query: 路由参数 (varchar)
  - route_name: 路由名称 (varchar)
  - is_frame: 是否为外链（0是 1否）
  - is_cache: 是否缓存（0缓存 1不缓存）
  - menu_type: 菜单类型（M目录 C菜单 F按钮）(char)
  - visible: 菜单状态（0显示 1隐藏）(char)
  - status: 菜单状态（'0'正常 '1'停用）⚠️ char类型需加引号
  - perms: 权限标识 (varchar)
  - icon: 菜单图标 (varchar)
  - create_by: 创建者 (varchar)
  - create_time: 创建时间 (datetime)
  - update_by: 更新者 (varchar)
  - update_time: 更新时间 (datetime)
  - remark: 备注 (varchar)

### 6. 配置表 (sys_config)
- **主键**: config_id
- **说明**: 参数配置表
- **字段**:
  - config_id: 参数主键 (int)
  - config_name: 参数名称 (varchar)
  - config_key: 参数键名 (varchar)
  - config_value: 参数键值 (varchar)
  - config_type: 系统内置（Y是 N否）(char)
  - create_by: 创建者 (varchar)
  - create_time: 创建时间 (datetime)
  - update_by: 更新者 (varchar)
  - update_time: 更新时间 (datetime)
  - remark: 备注 (varchar)

### 7. 字典类型表 (sys_dict_type)
- **主键**: dict_id
- **说明**: 字典类型表
- **字段**:
  - dict_id: 字典主键 (bigint)
  - dict_name: 字典名称 (varchar)
  - dict_type: 字典类型 (varchar)
  - status: 状态（'0'正常 '1'停用）⚠️ char类型需加引号
  - create_by: 创建者 (varchar)
  - create_time: 创建时间 (datetime)
  - update_by: 更新者 (varchar)
  - update_time: 更新时间 (datetime)
  - remark: 备注 (varchar)

### 8. 字典数据表 (sys_dict_data)
- **主键**: dict_code
- **说明**: 字典数据表
- **字段**:
  - dict_code: 字典编码 (bigint)
  - dict_sort: 字典排序 (int)
  - dict_label: 字典标签 (varchar)
  - dict_value: 字典键值 (varchar)
  - dict_type: 字典类型 (varchar)
  - css_class: 样式属性（其他样式扩展）
  - list_class: 表格回显样式
  - is_default: 是否默认（Y是 N否）(char)
  - status: 状态（'0'正常 '1'停用）⚠️ char类型需加引号
  - create_by: 创建者 (varchar)
  - create_time: 创建时间 (datetime)
  - update_by: 更新者 (varchar)
  - update_time: 更新时间 (datetime)
  - remark: 备注 (varchar)

### 9. 定时任务表 (sys_job)
- **主键**: job_id
- **说明**: 定时任务调度表
- **字段**:
  - job_id: 任务ID (bigint)
  - job_name: 任务名称 (varchar)
  - job_group: 任务组名 (varchar)
  - invoke_target: 调用目标字符串 (varchar)
  - cron_expression: cron执行表达式 (varchar)
  - misfire_policy: 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
  - concurrent: 是否并发执行（0允许 1禁止）
  - status: 状态（'0'正常 '1'暂停）⚠️ char类型需加引号
  - create_by: 创建者 (varchar)
  - create_time: 创建时间 (datetime)
  - update_by: 更新者 (varchar)
  - update_time: 更新时间 (datetime)
  - remark: 备注信息 (varchar)

### 10. 定时任务日志表 (sys_job_log)
- **主键**: job_log_id
- **说明**: 定时任务调度日志表
- **字段**:
  - job_log_id: 任务日志ID (bigint)
  - job_name: 任务名称 (varchar)
  - job_group: 任务组名 (varchar)
  - invoke_target: 调用目标字符串 (varchar)
  - job_message: 日志信息 (varchar)
  - status: 执行状态（'0'正常 '1'失败）⚠️ char类型需加引号
  - exception_info: 异常信息 (varchar)
  - create_time: 创建时间 (datetime)

### 11. 登录信息表 (sys_logininfor)
- **主键**: info_id
- **说明**: 系统访问记录
- **字段**:
  - info_id: 访问ID (bigint)
  - user_name: 用户账号 (varchar)
  - ipaddr: 登录IP地址 (varchar)
  - login_location: 登录地点 (varchar)
  - browser: 浏览器类型 (varchar)
  - os: 操作系统 (varchar)
  - status: 登录状态（'0'成功 '1'失败）⚠️ char类型需加引号
  - msg: 提示消息 (varchar)
  - login_time: 访问时间 (datetime)

### 12. 操作日志表 (sys_oper_log)
- **主键**: oper_id
- **说明**: 操作日志记录
- **字段**:
  - oper_id: 日志主键 (bigint)
  - title: 模块标题 (varchar)
  - business_type: 业务类型（0其它 1新增 2修改 3删除）
  - method: 方法名称 (varchar)
  - request_method: 请求方式 (varchar)
  - operator_type: 操作类别（0其它 1后台用户 2手机端用户）
  - oper_name: 操作人员 (varchar)
  - dept_name: 部门名称 (varchar)
  - oper_url: 请求URL (varchar)
  - oper_ip: 主机地址 (varchar)
  - oper_location: 操作地点 (varchar)
  - oper_param: 请求参数 (varchar)
  - json_result: 返回参数 (varchar)
  - status: 操作状态（'0'正常 '1'异常）⚠️ char类型需加引号
  - error_msg: 错误消息 (varchar)
  - oper_time: 操作时间 (datetime)
  - cost_time: 消耗时间 (bigint)

### 13. 通知公告表 (sys_notice)
- **主键**: notice_id
- **说明**: 通知公告表
- **字段**:
  - notice_id: 公告ID (int)
  - notice_title: 公告标题 (varchar)
  - notice_type: 公告类型（1通知 2公告）(char)
  - notice_content: 公告内容 (longblob)
  - status: 公告状态（'0'正常 '1'关闭）⚠️ char类型需加引号
  - create_by: 创建者 (varchar)
  - create_time: 创建时间 (datetime)
  - update_by: 更新者 (varchar)
  - update_time: 更新时间 (datetime)
  - remark: 备注 (varchar)

### 14. 心理测评表 (psyc_test)
- **主键**: id
- **说明**: 心理测评项目表
- **命名风格**: sys_* 风格（create_time / update_time）
- **字段**:
  - id: 测评项目ID (bigint)
  - test_name: 测评项目名称 (varchar)
  - description: 测评简介 (text)
  - status: 状态（0停用 1启用）⚠️ tinyint类型，不加引号！与sys_*表的status语义相反！
  - total_questions: 题目数量 (int)
  - duration: 测评时长（分钟）(int)
  - create_time: 创建时间 (datetime)
  - update_time: 更新时间 (datetime)

### 15. 心理题目表 (psyc_questions)
- **主键**: id
- **说明**: 题目表
- **命名风格**: created_at 风格（created_at / updated_at / created_by）
- **字段**:
  - id: 题目ID (int)
  - content: 题目内容 (text)
  - type: 题目类型（single-单选题，multiple-多选题，judgment-判断题，fill-填空题，essay-简答题）(enum)
  - test_id: 测试ID (int)
  - difficulty: 难度（easy-简单，medium-中等，hard-困难）(enum)
  - analysis: 解析内容 (text)
  - source: 题目来源 (varchar)
  - created_by: 创建人ID (int) ⚠️ 注意是 created_by 不是 create_by
  - created_at: 创建时间 (timestamp) ⚠️ 注意是 created_at 不是 create_time
  - updated_at: 更新时间 (timestamp) ⚠️ 注意是 updated_at 不是 update_time
  - deleted_at: 删除时间 (timestamp) ⚠️ 软删除用 deleted_at，不是 del_flag

### 16. 心理选项表 (psyc_options)
- **主键**: id
- **说明**: 题目选项表
- **命名风格**: created_at 风格
- **字段**:
  - id: 选项ID (int)
  - question_id: 题目ID (int)
  - content: 选项内容 (text)
  - score: 选项分值 (decimal(5,2))
  - sort_order: 排序顺序 (int)
  - created_at: 创建时间 (timestamp) ⚠️ 注意是 created_at 不是 create_time
  - updated_at: 更新时间 (timestamp) ⚠️ 注意是 updated_at 不是 update_time

### 17. 心理测评规则表 (psyc_assessment_rule)
- **主键**: id
- **说明**: 测评动态评分区间规则表
- **命名风格**: 无时间字段
- **字段**:
  - id: 规则ID (bigint)
  - test_id: 测评ID，关联测评主表 (bigint)
  - min_score: 区间下限（含）(decimal(8,2))
  - max_score: 区间上限（含）(decimal(8,2))
  - level: 等级名称 (varchar)
  - suggestion: 建议文案 (text)
  - reference_result: 参考结果 (text)
  - priority: 优先级 (int)
  - is_deleted: 是否删除（0未删除 1已删除）⚠️ 注意是 is_deleted 不是 del_flag

### 18. 心理动态表 (psyc_post)
- **主键**: id
- **说明**: 动态主表
- **命名风格**: created_at 风格
- **字段**:
  - id: 动态ID (bigint)
  - user_id: 发布用户ID (bigint)
  - content: 文字内容 (text)
  - visible: 可见范围（1公开,2仅好友,3私密）(tinyint)
  - like_count: 点赞数 (int)
  - comment_count: 评论数 (int)
  - favorite_count: 收藏数 (int)
  - share_count: 分享数 (int)
  - status: 状态（1正常,0删除,2审核中）⚠️ tinyint类型，正常=1！不是0！
  - created_at: 发布时间 (timestamp) ⚠️ 注意是 created_at 不是 create_time
  - updated_at: 更新时间 (timestamp) ⚠️ 注意是 updated_at 不是 update_time

### 19. 心理动态媒体表 (psyc_post_media)
- **主键**: id
- **说明**: 动态媒体资源表
- **命名风格**: 无时间字段
- **字段**:
  - id: 主键ID (bigint)
  - post_id: 动态ID (bigint)
  - media_url: 媒体URL (varchar)
  - media_type: 类型（1图片,2视频）(tinyint)
  - sort_order: 顺序 (int)

### 20. 心理反馈表 (psyc_feedback)
- **主键**: id
- **说明**: 用户意见反馈主表
- **命名风格**: sys_* 风格（create_time / update_time）
- **字段**:
  - id: 主键ID (bigint)
  - user_id: 提交反馈的用户ID (bigint)
  - type: 反馈类型（1-Bug问题 2-产品建议 3-投诉 4-功能需求 5-其他）(tinyint)
  - content: 反馈内容 (text)
  - images: 截图/图片列表，JSON数组形式 (json)
  - contact: 用户联系方式 (varchar)
  - device_info: 自动收集的设备信息 (json)
  - status: 处理状态（0-待处理 1-处理中 2-已处理 3-已关闭）⚠️ tinyint类型，多值状态
  - create_time: 创建时间 (datetime)
  - update_time: 更新时间 (datetime)

### 21. 心理反馈回复表 (psyc_feedback_reply)
- **主键**: id
- **说明**: 意见反馈的回复记录表
- **命名风格**: sys_* 风格（create_time）
- **字段**:
  - id: 回复记录主键ID (bigint)
  - feedback_id: 关联的反馈ID (bigint)
  - reply_user_id: 回复的管理员ID (bigint)
  - content: 回复内容 (text)
  - create_time: 创建时间 (datetime)

### 22. 题目答案表 (psyc_question_answers)
- **主键**: id
- **说明**: 题目答案表
- **命名风格**: created_at 风格
- **字段**:
  - id: 答案ID (int)
  - question_id: 题目ID (int)
  - option_id: 选项ID（选择题）(int)
  - content: 答案内容（填空题、简答题）(text)
  - created_at: 创建时间 (timestamp)
  - updated_at: 更新时间 (timestamp)

### 23. 活动日志表 (sys_activity_log)
- **主键**: activity_id
- **说明**: 系统活动日志表
- **字段**:
  - activity_id: 活动ID (bigint)
  - activity_type: 活动类型 (varchar)
  - icon: 图标 (varchar)
  - title: 标题 (varchar)
  - description: 描述 (varchar)
  - business_id: 业务ID (bigint)
  - activity_time: 活动时间 (datetime)
  - status: 状态 (char)
  - create_time: 创建时间 (datetime)
  - update_time: 更新时间 (datetime)

### 24. 关联表

#### 用户-角色关联表 (sys_user_role)
- **主键**: user_id + role_id
- **说明**: 用户与角色多对多关联表
- **字段**:
  - user_id: 用户ID (bigint)
  - role_id: 角色ID (bigint)

#### 用户-岗位关联表 (sys_user_post)
- **主键**: user_id + post_id
- **说明**: 用户与岗位多对多关联表
- **字段**:
  - user_id: 用户ID (bigint)
  - post_id: 岗位ID (bigint)

#### 角色-部门关联表 (sys_role_dept)
- **主键**: role_id + dept_id
- **说明**: 角色和部门关联表
- **字段**:
  - role_id: 角色ID (bigint)
  - dept_id: 部门ID (bigint)

#### 角色-菜单关联表 (sys_role_menu)
- **主键**: role_id + menu_id
- **说明**: 角色和菜单关联表
- **字段**:
  - role_id: 角色ID (bigint)
  - menu_id: 菜单ID (bigint)

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

#### 查询启用的心理测评及题目（注意：psyc_test.status 是 tinyint 类型）
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

#### 查询心理测评选项（注意：psyc_options 使用 sort_order 排序）
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

#### 查询正常状态的心理动态及关联媒体（注意：psyc_post.status=1表示正常，created_at不是create_time）
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

#### 查询待处理的反馈（注意：psyc_feedback.status=0表示待处理）
```sql
SELECT 
    f.id,
    f.user_id,
    f.type,
    f.content,
    f.contact,
    f.status,
    f.create_time
FROM psyc_feedback f
WHERE f.status = 0
ORDER BY f.create_time DESC;
```

### 7. 登录日志查询

#### 查询登录失败的记录（注意：sys_logininfor.status='1'表示失败，需加引号）
```sql
SELECT 
    info_id,
    user_name,
    ipaddr,
    login_location,
    browser,
    os,
    status,
    msg,
    login_time
FROM sys_logininfor
WHERE status = '1'
ORDER BY login_time DESC;
```

### 8. 综合查询

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

## 各表状态字段速查表

| 表名 | 状态字段 | 类型 | 正常/成功值 | 停用/失败值 | 其他值 |
|---|---|---|---|---|---|
| sys_user | status | char | '0'=正常 | '1'=停用 | - |
| sys_dept | status | char | '0'=正常 | '1'=停用 | - |
| sys_role | status | char | '0'=正常 | '1'=停用 | - |
| sys_post | status | char | '0'=正常 | '1'=停用 | - |
| sys_menu | status | char | '0'=正常 | '1'=停用 | - |
| sys_notice | status | char | '0'=正常 | '1'=关闭 | - |
| sys_job | status | char | '0'=正常 | '1'=暂停 | - |
| sys_job_log | status | char | '0'=正常 | '1'=失败 | - |
| sys_logininfor | status | char | '0'=成功 | '1'=失败 | - |
| sys_oper_log | status | char | '0'=正常 | '1'=异常 | - |
| sys_dict_type | status | char | '0'=正常 | '1'=停用 | - |
| sys_dict_data | status | char | '0'=正常 | '1'=停用 | - |
| **psyc_test** | status | **tinyint** | **1**=启用 | **0**=停用 | - |
| **psyc_post** | status | **tinyint** | **1**=正常 | **0**=删除 | **2**=审核中 |
| **psyc_feedback** | status | **tinyint** | - | - | **0**=待处理, **1**=处理中, **2**=已处理, **3**=已关闭 |

## 各表删除标记速查表

| 表名 | 删除字段 | 类型 | 存在/未删除 | 已删除 |
|---|---|---|---|---|
| sys_user, sys_dept, sys_role, sys_menu | del_flag | char | '0' | '2' |
| sys_dict_type, sys_dict_data | del_flag | char | '0' | '2' |
| psyc_assessment_rule | is_deleted | tinyint | 0 | 1 |
| psyc_questions | deleted_at | timestamp | NULL | 非NULL |
| infra_file_config | deleted | bit | 0 | 1 |

## 关联关系

- 用户与部门：多对一关系（sys_user.dept_id -> sys_dept.dept_id）
- 用户与角色：多对多关系（通过sys_user_role表关联）
- 用户与岗位：多对多关系（通过sys_user_post表关联）
- 角色与部门：多对多关系（通过sys_role_dept表关联）
- 角色与菜单：多对多关系（通过sys_role_menu表关联）
- 心理测评与题目：一对多关系（psyc_test.id -> psyc_questions.test_id）
- 题目与选项：一对多关系（psyc_questions.id -> psyc_options.question_id）
- 题目与答案：一对多关系（psyc_questions.id -> psyc_question_answers.question_id）
- 测评与规则：一对多关系（psyc_test.id -> psyc_assessment_rule.test_id）
- 动态与媒体：一对多关系（psyc_post.id -> psyc_post_media.post_id）

## 数据权限说明

系统支持多种数据权限范围：
- 1: 全部数据权限
- 2: 自定数据权限
- 3: 本部门数据权限
- 4: 本部门及以下数据权限
- 5: 仅本人数据权限

数据权限通过DataScopeAspect切面实现，根据用户角色的数据范围限制查询结果。