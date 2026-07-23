/*
 Navicat Premium Dump SQL

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80406 (8.4.6)
 Source Host           : 127.0.0.1:3306
 Source Schema         : su_crm

 Target Server Type    : MySQL
 Target Server Version : 80406 (8.4.6)
 File Encoding         : 65001

 Date: 23/01/2026 14:26:32
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`
(
    `table_id`          bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    `table_name`        varchar(200) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '表名称',
    `table_comment`     varchar(500) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '表描述',
    `sub_table_name`    varchar(64) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '关联子表的表名',
    `sub_table_fk_name` varchar(64) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '子表关联的外键名',
    `class_name`        varchar(100) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '实体类名称',
    `tpl_category`      varchar(200) COLLATE utf8mb4_general_ci  DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
    `tpl_web_type`      varchar(30) COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
    `package_name`      varchar(100) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '生成包路径',
    `module_name`       varchar(30) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '生成模块名',
    `business_name`     varchar(30) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '生成业务名',
    `function_name`     varchar(50) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '生成功能名',
    `function_author`   varchar(50) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '生成功能作者',
    `gen_type`          char(1) COLLATE utf8mb4_general_ci       DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
    `gen_path`          varchar(200) COLLATE utf8mb4_general_ci  DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
    `options`           varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '其它生成选项',
    `create_by`         varchar(64) COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '创建者',
    `create_time`       datetime                                 DEFAULT NULL COMMENT '创建时间',
    `update_by`         varchar(64) COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '更新者',
    `update_time`       datetime                                 DEFAULT NULL COMMENT '更新时间',
    `remark`            varchar(500) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='代码生成业务表';

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`
(
    `column_id`      bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    `table_id`       bigint                                  DEFAULT NULL COMMENT '归属表编号',
    `column_name`    varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列名称',
    `column_comment` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列描述',
    `column_type`    varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列类型',
    `java_type`      varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'JAVA类型',
    `java_field`     varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'JAVA字段名',
    `is_pk`          char(1) COLLATE utf8mb4_general_ci      DEFAULT NULL COMMENT '是否主键（1是）',
    `is_increment`   char(1) COLLATE utf8mb4_general_ci      DEFAULT NULL COMMENT '是否自增（1是）',
    `is_required`    char(1) COLLATE utf8mb4_general_ci      DEFAULT NULL COMMENT '是否必填（1是）',
    `is_insert`      char(1) COLLATE utf8mb4_general_ci      DEFAULT NULL COMMENT '是否为插入字段（1是）',
    `is_edit`        char(1) COLLATE utf8mb4_general_ci      DEFAULT NULL COMMENT '是否编辑字段（1是）',
    `is_list`        char(1) COLLATE utf8mb4_general_ci      DEFAULT NULL COMMENT '是否列表字段（1是）',
    `is_query`       char(1) COLLATE utf8mb4_general_ci      DEFAULT NULL COMMENT '是否查询字段（1是）',
    `query_type`     varchar(200) COLLATE utf8mb4_general_ci DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
    `html_type`      varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
    `dict_type`      varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
    `sort`           int                                     DEFAULT NULL COMMENT '排序',
    `create_by`      varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time`    datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_by`      varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time`    datetime                                DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`column_id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='代码生成业务表字段';

-- ----------------------------
-- Table structure for infra_file_config
-- ----------------------------
DROP TABLE IF EXISTS `infra_file_config`;
CREATE TABLE `infra_file_config`
(
    `id`          bigint                                                         NOT NULL AUTO_INCREMENT COMMENT '编号',
    `name`        varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NOT NULL COMMENT '配置名',
    `storage`     tinyint                                                        NOT NULL COMMENT '存储器',
    `remark`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci           DEFAULT NULL COMMENT '备注',
    `master`      bit(1)                                                         NOT NULL COMMENT '是否为主配置',
    `config`      varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储配置',
    `creator`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci            DEFAULT '' COMMENT '创建者',
    `deleted`     bit(1)                                                         NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `create_time` datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci            DEFAULT '' COMMENT '更新者',
    `update_time` datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件配置表';

-- ----------------------------
-- Table structure for psyc_assessment_rule
-- ----------------------------
DROP TABLE IF EXISTS `psyc_assessment_rule`;
CREATE TABLE `psyc_assessment_rule`
(
    `id`               bigint        NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `test_id`          bigint        NOT NULL COMMENT '测评ID，关联测评主表',
    `min_score`        decimal(8, 2) NOT NULL COMMENT '区间下限（含），如：50.00',
    `max_score`        decimal(8, 2) NOT NULL COMMENT '区间上限（含），如：100.00',
    `level`            varchar(200)  NOT NULL COMMENT '等级名称，如：优秀、良好、一般',
    `suggestion`       text COMMENT '建议文案，针对该等级的指导建议',
    `reference_result` text COMMENT '参考结果，该等级对应的典型特征、案例说明等补充信息',
    `priority`         int                    DEFAULT '0' COMMENT '优先级，值越大优先匹配（当区间重叠时生效）',
    `is_deleted`       tinyint       NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY                `idx_test_id` (`test_id`),
    KEY                `idx_is_deleted` (`is_deleted`),
    CONSTRAINT `psyc_assessment_rule_ibfk_1` FOREIGN KEY (`test_id`) REFERENCES `psyc_test` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='测评动态评分区间规则表';

-- ----------------------------
-- Table structure for psyc_feedback
-- ----------------------------
DROP TABLE IF EXISTS `psyc_feedback`;
CREATE TABLE `psyc_feedback`
(
    `id`          bigint                          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     bigint                          NOT NULL COMMENT '提交反馈的用户ID',
    `type`        tinyint                         NOT NULL COMMENT '反馈类型：1-Bug问题 2-产品建议 3-投诉 4-功能需求 5-其他',
    `content`     text COLLATE utf8mb4_general_ci NOT NULL COMMENT '反馈内容（用户填写的文本描述）',
    `images`      json                                     DEFAULT NULL COMMENT '截图/图片列表，JSON数组形式',
    `contact`     varchar(100) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '用户联系方式（手机号/邮箱）',
    `device_info` json                                     DEFAULT NULL COMMENT '自动收集的设备信息（系统、型号、APP版本等）',
    `status`      tinyint                         NOT NULL DEFAULT '0' COMMENT '处理状态：0-待处理 1-处理中 2-已处理 3-已关闭',
    `create_time` datetime                                 DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY           `idx_user` (`user_id`) COMMENT '用户ID索引（用于查询某用户的反馈）',
    KEY           `idx_status` (`status`) COMMENT '状态索引（用于后台筛选状态）'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户意见反馈主表';

-- ----------------------------
-- Table structure for psyc_feedback_reply
-- ----------------------------
DROP TABLE IF EXISTS `psyc_feedback_reply`;
CREATE TABLE `psyc_feedback_reply`
(
    `id`            bigint                          NOT NULL AUTO_INCREMENT COMMENT '回复记录主键ID',
    `feedback_id`   bigint                          NOT NULL COMMENT '关联的反馈ID（外键）',
    `reply_user_id` bigint                          NOT NULL COMMENT '回复的管理员ID',
    `content`       text COLLATE utf8mb4_general_ci NOT NULL COMMENT '回复内容',
    `create_time`   datetime                        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY             `feedback_id` (`feedback_id`),
    CONSTRAINT `psyc_feedback_reply_ibfk_1` FOREIGN KEY (`feedback_id`) REFERENCES `psyc_feedback` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='意见反馈的回复记录表';

-- ----------------------------
-- Table structure for psyc_options
-- ----------------------------
DROP TABLE IF EXISTS `psyc_options`;
CREATE TABLE `psyc_options`
(
    `id`          int           NOT NULL AUTO_INCREMENT COMMENT '选项ID',
    `question_id` int           NOT NULL COMMENT '题目ID',
    `content`     text          NOT NULL COMMENT '选项内容',
    `score`       decimal(5, 2) NOT NULL DEFAULT '0.00' COMMENT '选项分值',
    `sort_order`  int           NOT NULL COMMENT '排序顺序',
    `created_at`  timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_question_id` (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题目选项表';

-- ----------------------------
-- Table structure for psyc_post
-- ----------------------------
DROP TABLE IF EXISTS `psyc_post`;
CREATE TABLE `psyc_post`
(
    `id`             bigint NOT NULL AUTO_INCREMENT COMMENT '动态ID',
    `user_id`        bigint NOT NULL COMMENT '发布用户ID',
    `content`        text COMMENT '文字内容',
    `visible`        tinyint DEFAULT '1' COMMENT '可见范围(1公开,2仅好友,3私密)',
    `like_count`     int     DEFAULT '0' COMMENT '点赞数',
    `comment_count`  int     DEFAULT '0' COMMENT '评论数',
    `favorite_count` int     DEFAULT '0' COMMENT '收藏数',
    `share_count`    int     DEFAULT '0' COMMENT '分享数',
    `status`         tinyint DEFAULT '1' COMMENT '状态(1正常,0删除,2审核中)',
    `created_at`     timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `updated_at`     timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY              `idx_user_id` (`user_id`),
    KEY              `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='动态主表';

-- ----------------------------
-- Table structure for psyc_post_media
-- ----------------------------
DROP TABLE IF EXISTS `psyc_post_media`;
CREATE TABLE `psyc_post_media`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `post_id`    bigint       NOT NULL COMMENT '动态ID',
    `media_url`  varchar(255) NOT NULL COMMENT '媒体URL',
    `media_type` tinyint DEFAULT '1' COMMENT '类型(1图片,2视频)',
    `sort_order` int     DEFAULT '0' COMMENT '顺序',
    PRIMARY KEY (`id`),
    KEY          `idx_post_id` (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='动态媒体资源表';

-- ----------------------------
-- Table structure for psyc_question_answers
-- ----------------------------
DROP TABLE IF EXISTS `psyc_question_answers`;
CREATE TABLE `psyc_question_answers`
(
    `id`          int NOT NULL AUTO_INCREMENT COMMENT '答案ID',
    `question_id` int NOT NULL COMMENT '题目ID',
    `option_id`   int DEFAULT NULL COMMENT '选项ID（选择题）',
    `content`     text COMMENT '答案内容（填空题、简答题）',
    `created_at`  timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_question_id` (`question_id`),
    KEY           `idx_option_id` (`option_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题目答案表';

-- ----------------------------
-- Table structure for psyc_questions
-- ----------------------------
DROP TABLE IF EXISTS `psyc_questions`;
CREATE TABLE `psyc_questions`
(
    `id`         int  NOT NULL AUTO_INCREMENT COMMENT '题目ID',
    `content`    text NOT NULL COMMENT '题目内容',
    `type`       enum('single','multiple','judgment','fill','essay') NOT NULL COMMENT '题目类型：single-单选题，multiple-多选题，judgment-判断题，fill-填空题，essay-简答题',
    `test_id`    int  NOT NULL COMMENT '测试ID',
    `difficulty` enum('easy','medium','hard') NOT NULL COMMENT '难度：easy-简单，medium-中等，hard-困难',
    `analysis`   text COMMENT '解析内容',
    `source`     varchar(100) DEFAULT NULL COMMENT '题目来源',
    `created_by` int          DEFAULT NULL COMMENT '创建人ID',
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    KEY          `idx_test_id` (`test_id`),
    KEY          `idx_type` (`type`),
    KEY          `idx_difficulty` (`difficulty`),
    KEY          `idx_created_by` (`created_by`),
    KEY          `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题目表';

-- ----------------------------
-- Table structure for psyc_test
-- ----------------------------
DROP TABLE IF EXISTS `psyc_test`;
CREATE TABLE `psyc_test`
(
    `id`              bigint       NOT NULL AUTO_INCREMENT COMMENT '测评项目ID',
    `test_name`       varchar(100) NOT NULL COMMENT '测评项目名称',
    `description`     text COMMENT '测评简介',
    `status`          tinyint  DEFAULT '1' COMMENT '状态：0停用 1启用',
    `total_questions` int      DEFAULT '0' COMMENT '题目数量',
    `duration`        int      DEFAULT '30' COMMENT '测评时长（分钟）',
    `create_time`     datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理测评项目表';

-- ----------------------------
-- Table structure for QRTZ_BLOB_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `QRTZ_BLOB_TRIGGERS`
(
    `sched_name`    varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
    `trigger_name`  varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
    `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    `blob_data`     blob COMMENT '存放持久化Trigger对象',
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`),
    CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Blob类型的触发器表';

-- ----------------------------
-- Table structure for QRTZ_CALENDARS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
CREATE TABLE `QRTZ_CALENDARS`
(
    `sched_name`    varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
    `calendar_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '日历名称',
    `calendar`      blob                                    NOT NULL COMMENT '存放持久化calendar对象',
    PRIMARY KEY (`sched_name`, `calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='日历信息表';

-- ----------------------------
-- Table structure for QRTZ_CRON_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
CREATE TABLE `QRTZ_CRON_TRIGGERS`
(
    `sched_name`      varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
    `trigger_name`    varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
    `trigger_group`   varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    `cron_expression` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cron表达式',
    `time_zone_id`    varchar(80) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '时区',
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`),
    CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Cron类型的触发器表';

-- ----------------------------
-- Table structure for QRTZ_FIRED_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `QRTZ_FIRED_TRIGGERS`
(
    `sched_name`        varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
    `entry_id`          varchar(95) COLLATE utf8mb4_general_ci  NOT NULL COMMENT '调度器实例id',
    `trigger_name`      varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
    `trigger_group`     varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    `instance_name`     varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度器实例名',
    `fired_time`        bigint                                  NOT NULL COMMENT '触发的时间',
    `sched_time`        bigint                                  NOT NULL COMMENT '定时器制定的时间',
    `priority`          int                                     NOT NULL COMMENT '优先级',
    `state`             varchar(16) COLLATE utf8mb4_general_ci  NOT NULL COMMENT '状态',
    `job_name`          varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务名称',
    `job_group`         varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务组名',
    `is_nonconcurrent`  varchar(1) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '是否并发',
    `requests_recovery` varchar(1) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '是否接受恢复执行',
    PRIMARY KEY (`sched_name`, `entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='已触发的触发器表';

-- ----------------------------
-- Table structure for QRTZ_JOB_DETAILS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
CREATE TABLE `QRTZ_JOB_DETAILS`
(
    `sched_name`        varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
    `job_name`          varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
    `job_group`         varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
    `description`       varchar(250) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相关介绍',
    `job_class_name`    varchar(250) COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行任务类名称',
    `is_durable`        varchar(1) COLLATE utf8mb4_general_ci   NOT NULL COMMENT '是否持久化',
    `is_nonconcurrent`  varchar(1) COLLATE utf8mb4_general_ci   NOT NULL COMMENT '是否并发',
    `is_update_data`    varchar(1) COLLATE utf8mb4_general_ci   NOT NULL COMMENT '是否更新数据',
    `requests_recovery` varchar(1) COLLATE utf8mb4_general_ci   NOT NULL COMMENT '是否接受恢复执行',
    `job_data`          blob COMMENT '存放持久化job对象',
    PRIMARY KEY (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务详细信息表';

-- ----------------------------
-- Table structure for QRTZ_LOCKS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_LOCKS`;
CREATE TABLE `QRTZ_LOCKS`
(
    `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
    `lock_name`  varchar(40) COLLATE utf8mb4_general_ci  NOT NULL COMMENT '悲观锁名称',
    PRIMARY KEY (`sched_name`, `lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='存储的悲观锁信息表';

-- ----------------------------
-- Table structure for QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS`
(
    `sched_name`    varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
    `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    PRIMARY KEY (`sched_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='暂停的触发器表';

-- ----------------------------
-- Table structure for QRTZ_SCHEDULER_STATE
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
CREATE TABLE `QRTZ_SCHEDULER_STATE`
(
    `sched_name`        varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
    `instance_name`     varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '实例名称',
    `last_checkin_time` bigint                                  NOT NULL COMMENT '上次检查时间',
    `checkin_interval`  bigint                                  NOT NULL COMMENT '检查间隔时间',
    PRIMARY KEY (`sched_name`, `instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调度器状态表';

-- ----------------------------
-- Table structure for QRTZ_SIMPLE_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS`
(
    `sched_name`      varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
    `trigger_name`    varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
    `trigger_group`   varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    `repeat_count`    bigint                                  NOT NULL COMMENT '重复的次数统计',
    `repeat_interval` bigint                                  NOT NULL COMMENT '重复的间隔时间',
    `times_triggered` bigint                                  NOT NULL COMMENT '已经触发的次数',
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`),
    CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='简单触发器的信息表';

-- ----------------------------
-- Table structure for QRTZ_SIMPROP_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS`
(
    `sched_name`    varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
    `trigger_name`  varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
    `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    `str_prop_1`    varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
    `str_prop_2`    varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
    `str_prop_3`    varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
    `int_prop_1`    int                                     DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
    `int_prop_2`    int                                     DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
    `long_prop_1`   bigint                                  DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
    `long_prop_2`   bigint                                  DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
    `dec_prop_1`    decimal(13, 4)                          DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
    `dec_prop_2`    decimal(13, 4)                          DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
    `bool_prop_1`   varchar(1) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
    `bool_prop_2`   varchar(1) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`),
    CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='同步机制的行锁表';

-- ----------------------------
-- Table structure for QRTZ_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
CREATE TABLE `QRTZ_TRIGGERS`
(
    `sched_name`     varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
    `trigger_name`   varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器的名字',
    `trigger_group`  varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器所属组的名字',
    `job_name`       varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
    `job_group`      varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
    `description`    varchar(250) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相关介绍',
    `next_fire_time` bigint                                  DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
    `prev_fire_time` bigint                                  DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
    `priority`       int                                     DEFAULT NULL COMMENT '优先级',
    `trigger_state`  varchar(16) COLLATE utf8mb4_general_ci  NOT NULL COMMENT '触发器状态',
    `trigger_type`   varchar(8) COLLATE utf8mb4_general_ci   NOT NULL COMMENT '触发器的类型',
    `start_time`     bigint                                  NOT NULL COMMENT '开始时间',
    `end_time`       bigint                                  DEFAULT NULL COMMENT '结束时间',
    `calendar_name`  varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日程表名称',
    `misfire_instr`  smallint                                DEFAULT NULL COMMENT '补偿执行的策略',
    `job_data`       blob COMMENT '存放持久化job对象',
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`),
    KEY              `sched_name` (`sched_name`,`job_name`,`job_group`),
    CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `QRTZ_JOB_DETAILS` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='触发器详细信息表';

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `config_id`    int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    `config_name`  varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数名称',
    `config_key`   varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键名',
    `config_value` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键值',
    `config_type`  char(1) COLLATE utf8mb4_general_ci      DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
    `create_by`    varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time`  datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_by`    varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time`  datetime                                DEFAULT NULL COMMENT '更新时间',
    `remark`       varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='参数配置表';

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`
(
    `dept_id`     bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
    `parent_id`   bigint                                 DEFAULT '0' COMMENT '父部门id',
    `ancestors`   varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '祖级列表',
    `dept_name`   varchar(30) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '部门名称',
    `order_num`   int                                    DEFAULT '0' COMMENT '显示顺序',
    `leader`      varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人',
    `phone`       varchar(11) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
    `email`       varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
    `status`      char(1) COLLATE utf8mb4_general_ci     DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
    `del_flag`    char(1) COLLATE utf8mb4_general_ci     DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`   varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime                               DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime                               DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`
(
    `dict_code`   bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    `dict_sort`   int                                     DEFAULT '0' COMMENT '字典排序',
    `dict_label`  varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典标签',
    `dict_value`  varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典键值',
    `dict_type`   varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
    `css_class`   varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
    `list_class`  varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表格回显样式',
    `is_default`  char(1) COLLATE utf8mb4_general_ci      DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
    `status`      char(1) COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by`   varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time` datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time` datetime                                DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`
(
    `dict_id`     bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    `dict_name`   varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典名称',
    `dict_type`   varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
    `status`      char(1) COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by`   varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time` datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time` datetime                                DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`dict_id`),
    UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`
(
    `job_id`          bigint                                  NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `job_name`        varchar(64) COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '任务名称',
    `job_group`       varchar(64) COLLATE utf8mb4_general_ci  NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
    `invoke_target`   varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
    `cron_expression` varchar(255) COLLATE utf8mb4_general_ci          DEFAULT '' COMMENT 'cron执行表达式',
    `misfire_policy`  varchar(20) COLLATE utf8mb4_general_ci           DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
    `concurrent`      char(1) COLLATE utf8mb4_general_ci               DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
    `status`          char(1) COLLATE utf8mb4_general_ci               DEFAULT '0' COMMENT '状态（0正常 1暂停）',
    `create_by`       varchar(64) COLLATE utf8mb4_general_ci           DEFAULT '' COMMENT '创建者',
    `create_time`     datetime                                         DEFAULT NULL COMMENT '创建时间',
    `update_by`       varchar(64) COLLATE utf8mb4_general_ci           DEFAULT '' COMMENT '更新者',
    `update_time`     datetime                                         DEFAULT NULL COMMENT '更新时间',
    `remark`          varchar(500) COLLATE utf8mb4_general_ci          DEFAULT '' COMMENT '备注信息',
    PRIMARY KEY (`job_id`, `job_name`, `job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务调度表';

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`
(
    `job_log_id`     bigint                                  NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
    `job_name`       varchar(64) COLLATE utf8mb4_general_ci  NOT NULL COMMENT '任务名称',
    `job_group`      varchar(64) COLLATE utf8mb4_general_ci  NOT NULL COMMENT '任务组名',
    `invoke_target`  varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
    `job_message`    varchar(500) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '日志信息',
    `status`         char(1) COLLATE utf8mb4_general_ci       DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
    `exception_info` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '异常信息',
    `create_time`    datetime                                 DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务调度日志表';

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`
(
    `info_id`        bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    `user_name`      varchar(50) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '用户账号',
    `ipaddr`         varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录IP地址',
    `login_location` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录地点',
    `browser`        varchar(50) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '浏览器类型',
    `os`             varchar(50) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '操作系统',
    `status`         char(1) COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
    `msg`            varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '提示消息',
    `login_time`     datetime                                DEFAULT NULL COMMENT '访问时间',
    PRIMARY KEY (`info_id`),
    KEY              `idx_sys_logininfor_s` (`status`),
    KEY              `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=522 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统访问记录';

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `menu_id`     bigint                                 NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`   varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
    `parent_id`   bigint                                  DEFAULT '0' COMMENT '父菜单ID',
    `order_num`   int                                     DEFAULT '0' COMMENT '显示顺序',
    `path`        varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由地址',
    `component`   varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件路径',
    `query`       varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由参数',
    `route_name`  varchar(50) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '路由名称',
    `is_frame`    int                                     DEFAULT '1' COMMENT '是否为外链（0是 1否）',
    `is_cache`    int                                     DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
    `menu_type`   char(1) COLLATE utf8mb4_general_ci      DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `visible`     char(1) COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
    `status`      char(1) COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
    `perms`       varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限标识',
    `icon`        varchar(100) COLLATE utf8mb4_general_ci DEFAULT '#' COMMENT '菜单图标',
    `create_by`   varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time` datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time` datetime                                DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2051 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`
(
    `notice_id`      int                                    NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `notice_title`   varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
    `notice_type`    char(1) COLLATE utf8mb4_general_ci     NOT NULL COMMENT '公告类型（1通知 2公告）',
    `notice_content` longblob COMMENT '公告内容',
    `status`         char(1) COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
    `create_by`      varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time`    datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_by`      varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time`    datetime                                DEFAULT NULL COMMENT '更新时间',
    `remark`         varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通知公告表';

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`
(
    `oper_id`        bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `title`          varchar(50) COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '模块标题',
    `business_type`  int                                      DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    `method`         varchar(200) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '方法名称',
    `request_method` varchar(10) COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '请求方式',
    `operator_type`  int                                      DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
    `oper_name`      varchar(50) COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '操作人员',
    `dept_name`      varchar(50) COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '部门名称',
    `oper_url`       varchar(255) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '请求URL',
    `oper_ip`        varchar(128) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '主机地址',
    `oper_location`  varchar(255) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '操作地点',
    `oper_param`     varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求参数',
    `json_result`    varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '返回参数',
    `status`         int                                      DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
    `error_msg`      varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '错误消息',
    `oper_time`      datetime                                 DEFAULT NULL COMMENT '操作时间',
    `cost_time`      bigint                                   DEFAULT '0' COMMENT '消耗时间',
    PRIMARY KEY (`oper_id`),
    KEY              `idx_sys_oper_log_bt` (`business_type`),
    KEY              `idx_sys_oper_log_s` (`status`),
    KEY              `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=260 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志记录';

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`
(
    `post_id`     bigint                                 NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `post_code`   varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
    `post_name`   varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
    `post_sort`   int                                    NOT NULL COMMENT '显示顺序',
    `status`      char(1) COLLATE utf8mb4_general_ci     NOT NULL COMMENT '状态（0正常 1停用）',
    `create_by`   varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time` datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time` datetime                                DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='岗位信息表';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `role_id`             bigint                                  NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`           varchar(30) COLLATE utf8mb4_general_ci  NOT NULL COMMENT '角色名称',
    `role_key`            varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
    `role_sort`           int                                     NOT NULL COMMENT '显示顺序',
    `data_scope`          char(1) COLLATE utf8mb4_general_ci      DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
    `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
    `status`              char(1) COLLATE utf8mb4_general_ci      NOT NULL COMMENT '角色状态（0正常 1停用）',
    `del_flag`            char(1) COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`           varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time`         datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_by`           varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time`         datetime                                DEFAULT NULL COMMENT '更新时间',
    `remark`              varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`
(
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `dept_id` bigint NOT NULL COMMENT '部门ID',
    PRIMARY KEY (`role_id`, `dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和部门关联表';

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `menu_id` bigint NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和菜单关联表';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`         bigint                                 NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `dept_id`         bigint                                  DEFAULT NULL COMMENT '部门ID',
    `user_name`       varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
    `nick_name`       varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
    `user_type`       varchar(2) COLLATE utf8mb4_general_ci   DEFAULT '00' COMMENT '用户类型（00系统用户）',
    `email`           varchar(50) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '用户邮箱',
    `phonenumber`     varchar(11) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '手机号码',
    `sex`             char(1) COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
    `avatar`          varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '头像地址',
    `password`        varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '密码',
    `status`          char(1) COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
    `del_flag`        char(1) COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `login_ip`        varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '最后登录IP',
    `login_date`      datetime                                DEFAULT NULL COMMENT '最后登录时间',
    `pwd_update_date` datetime                                DEFAULT NULL COMMENT '密码最后更新时间',
    `create_by`       varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time`     datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_by`       varchar(64) COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time`     datetime                                DEFAULT NULL COMMENT '更新时间',
    `remark`          varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`
(
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `post_id` bigint NOT NULL COMMENT '岗位ID',
    PRIMARY KEY (`user_id`, `post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户与岗位关联表';

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户和角色关联表';

SET
FOREIGN_KEY_CHECKS = 1;
