SET FOREIGN_KEY_CHECKS = 0;

-- =============================
-- 本体扩展表（属性、实例、规则、行为、字段映射）
-- 必须在 ontology.sql 之后执行
-- =============================

DROP TABLE IF EXISTS ai_ontology_field_mapping;
DROP TABLE IF EXISTS ai_ontology_instance_value;
DROP TABLE IF EXISTS ai_ontology_instance;
DROP TABLE IF EXISTS ai_ontology_action;
DROP TABLE IF EXISTS ai_ontology_rule;
DROP TABLE IF EXISTS ai_ontology_property;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================
-- 1. 属性表（概念的可定义动态属性）
-- =============================
CREATE TABLE ai_ontology_property (
    property_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '属性ID',
    property_name VARCHAR(100) NOT NULL COMMENT '属性名称',
    property_code VARCHAR(50) NOT NULL COMMENT '属性编码',
    property_type VARCHAR(20) NOT NULL DEFAULT 'STRING' COMMENT '属性类型：STRING/INTEGER/DOUBLE/BOOLEAN/DATE/ENUM',
    concept_id BIGINT NOT NULL COMMENT '所属概念ID',
    required CHAR(1) DEFAULT '0' COMMENT '是否必填：0否 1是',
    default_value VARCHAR(500) COMMENT '默认值',
    enum_values TEXT COMMENT '枚举值列表（JSON格式，如["值1","值2"]）',
    description TEXT COMMENT '属性描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status CHAR(1) DEFAULT '0' COMMENT '状态：0启用 1禁用',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (concept_id) REFERENCES ai_ontology_concept(concept_id),
    UNIQUE KEY uk_property_code (property_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='本体属性表';

CREATE INDEX idx_property_concept ON ai_ontology_property(concept_id);

-- =============================
-- 2. 实例表（概念的具体实例/对象）
-- =============================
CREATE TABLE ai_ontology_instance (
    instance_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '实例ID',
    instance_name VARCHAR(100) NOT NULL COMMENT '实例名称',
    instance_code VARCHAR(50) NOT NULL COMMENT '实例编码',
    concept_id BIGINT NOT NULL COMMENT '所属概念ID',
    description TEXT COMMENT '实例描述',
    status CHAR(1) DEFAULT '0' COMMENT '状态：0启用 1禁用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (concept_id) REFERENCES ai_ontology_concept(concept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='本体实例表';

CREATE INDEX idx_instance_concept ON ai_ontology_instance(concept_id);
CREATE INDEX idx_instance_code ON ai_ontology_instance(instance_code);

-- =============================
-- 3. 实例属性值表（实例的具体属性值）
-- =============================
CREATE TABLE ai_ontology_instance_value (
    value_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '属性值ID',
    instance_id BIGINT NOT NULL COMMENT '实例ID',
    property_id BIGINT NOT NULL COMMENT '属性ID',
    property_value TEXT COMMENT '属性值',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (instance_id) REFERENCES ai_ontology_instance(instance_id) ON DELETE CASCADE,
    FOREIGN KEY (property_id) REFERENCES ai_ontology_property(property_id),
    UNIQUE KEY uk_instance_property (instance_id, property_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实例属性值表';

CREATE INDEX idx_instance_value_instance ON ai_ontology_instance_value(instance_id);

-- =============================
-- 4. 业务规则表（声明式业务规则）
-- =============================
CREATE TABLE ai_ontology_rule (
    rule_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规则ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_code VARCHAR(50) NOT NULL COMMENT '规则编码',
    concept_id BIGINT COMMENT '所属概念ID（NULL=全局规则）',
    `condition` TEXT COMMENT '条件（JSON格式）',
    `action` TEXT COMMENT '动作（JSON格式）',
    priority INT DEFAULT 0 COMMENT '优先级（数值越小优先级越高）',
    enabled CHAR(1) DEFAULT '1' COMMENT '是否启用：0禁用 1启用',
    description TEXT COMMENT '规则描述',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (concept_id) REFERENCES ai_ontology_concept(concept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业务规则表';

CREATE INDEX idx_rule_concept ON ai_ontology_rule(concept_id);
CREATE INDEX idx_rule_code ON ai_ontology_rule(rule_code);

-- =============================
-- 5. 行为表（概念级可执行操作）
-- =============================
CREATE TABLE ai_ontology_action (
    action_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '行为ID',
    action_name VARCHAR(100) NOT NULL COMMENT '行为名称',
    action_code VARCHAR(50) NOT NULL COMMENT '行为编码',
    concept_id BIGINT COMMENT '所属概念ID（NULL=全局行为）',
    action_type VARCHAR(20) NOT NULL DEFAULT 'TOOL' COMMENT '行为类型：TOOL/API/PROMPT',
    target VARCHAR(500) COMMENT '目标（方法名/API地址/Prompt Key）',
    parameters TEXT COMMENT '参数（JSON格式）',
    description TEXT COMMENT '行为描述',
    status CHAR(1) DEFAULT '0' COMMENT '状态：0启用 1禁用',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (concept_id) REFERENCES ai_ontology_concept(concept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行为表';

CREATE INDEX idx_action_concept ON ai_ontology_action(concept_id);
CREATE INDEX idx_action_code ON ai_ontology_action(action_code);

-- =============================
-- 6. 字段映射表（属性到业务表字段的映射）
-- =============================
CREATE TABLE ai_ontology_field_mapping (
    field_mapping_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '字段映射ID',
    mapping_id BIGINT NOT NULL COMMENT '映射ID（关联ai_ontology_mapping）',
    property_code VARCHAR(50) NOT NULL COMMENT '属性编码（关联ai_ontology_property.property_code）',
    column_name VARCHAR(100) NOT NULL COMMENT '业务表字段名',
    default_value VARCHAR(500) COMMENT '默认值',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (mapping_id) REFERENCES ai_ontology_mapping(mapping_id) ON DELETE CASCADE,
    FOREIGN KEY (property_code) REFERENCES ai_ontology_property(property_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字段映射表';

CREATE INDEX idx_field_mapping_mapping ON ai_ontology_field_mapping(mapping_id);
CREATE INDEX idx_field_mapping_property ON ai_ontology_field_mapping(property_code);

-- =============================
-- 种子数据
-- 基于 su_crm.sql 业务表结构设计
-- 概念ID需要与 su_crm.sql 中的 ai_ontology_concept 保持一致
-- =============================

-- 先清空旧数据（防重复执行，需关闭 FK 检查处理自引用约束）
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM ai_ontology_field_mapping;
DELETE FROM ai_ontology_instance_value;
DELETE FROM ai_ontology_instance;
DELETE FROM ai_ontology_action;
DELETE FROM ai_ontology_rule;
DELETE FROM ai_ontology_property;
DELETE FROM ai_ontology_relation;
DELETE FROM ai_ontology_mapping;
-- 概念表有自引用 FK (parent_id → concept_id)，需先清除 parent_id 再删除
UPDATE ai_ontology_concept SET parent_id = NULL;
DELETE FROM ai_ontology_concept;
SET FOREIGN_KEY_CHECKS = 1;

-- 概念层级：心聆平台业务域
INSERT INTO ai_ontology_concept (concept_id, concept_name, concept_code, description, parent_id, category, path, `level`, status, sort_order) VALUES
-- 根节点
(1, '心聆平台', 'xinling_platform', '心聆APP——专注冥想助眠平台', NULL, '平台概念', '/1', 0, '0', 0),

-- 用户体系
(2, '用户体系', 'user_system', '用户相关的所有概念', 1, '体系', '/1/2', 1, '0', 1),
(3, 'APP用户', 'app_user', '使用心聆APP的注册用户，对应 app_user 表', 2, '实体', '/1/2/3', 2, '0', 1),
(4, 'VIP会员', 'vip_member', '付费会员用户，享有高级功能，对应 app_user.vip_status', 2, '状态', '/1/2/4', 2, '0', 2),
(5, '分销员', 'distributor', '参与推广返佣的用户，对应 distributor 表', 2, '角色', '/1/2/5', 2, '0', 3),

-- 内容体系
(10, '内容体系', 'content_system', '平台上的所有内容资源', 1, '体系', '/1/10', 1, '0', 2),
(11, '音频素材', 'audio_item', '单个音视频素材文件，对应 audio_item 表', 10, '实体', '/1/10/11', 2, '0', 1),
(12, '音频混音', 'audio_mix', '多个音频素材的混合组合，对应 audio_mix 表', 10, '实体', '/1/10/12', 2, '0', 2),
(13, '白噪音', 'white_noise', '连续均匀的环境声音，帮助专注或睡眠', 11, '类型', '/1/10/11/13', 3, '0', 3),
(14, '自然音', 'nature_sound', '自然界声音如鸟鸣、海浪、溪流', 11, '类型', '/1/10/11/14', 3, '0', 4),
(15, '冥想内容', 'meditation', '引导用户正念冥想的课程内容，对应 meditation 表', 10, '实体', '/1/10/15', 2, '0', 5),
(16, '冥想音频', 'meditation_audio', '冥想课程中的具体音频段落', 15, '段落', '/1/10/15/16', 3, '0', 6),
(17, '冥想作者', 'meditation_author', '冥想内容的创作者，对应 meditation_author 表', 10, '实体', '/1/10/17', 2, '0', 7),
(18, '背景图', 'content_bg', '各内容类型的背景图片，对应 content_bg 表', 10, '资源', '/1/10/18', 2, '0', 8),

-- 功能体系
(20, '功能体系', 'feature_system', '应用核心功能模块', 1, '体系', '/1/20', 1, '0', 3),
(21, '专注', 'focus', '番茄钟/深度专注计时功能，对应 focus_record 表', 20, '功能', '/1/20/21', 2, '0', 1),
(22, '睡眠', 'sleep', '睡眠记录与监测功能，对应 sleep_diary/sleep_record 表', 20, '功能', '/1/20/22', 2, '0', 2),
(23, '心理测评', 'psyc_test', '心理健康测评问卷，对应 psyc_test 表', 20, '功能', '/1/20/23', 2, '0', 3),
(24, '动态社区', 'moment', '用户发布的心情动态，对应 moment 表', 20, '功能', '/1/20/24', 2, '0', 4),
(25, 'AI聊天', 'ai_chat', 'AI智能对话助手功能', 20, '功能', '/1/20/25', 2, '0', 5),

-- 成长体系
(30, '成长体系', 'growth_system', '用户成长激励机制', 1, '体系', '/1/30', 1, '0', 4),
(31, '成就', 'achievement', '可获得的成就徽章，对应 achievement 表', 30, '激励', '/1/30/31', 2, '0', 1),
(32, '挑战', 'challenge', '限时挑战活动，对应 challenge 表', 30, '激励', '/1/30/32', 2, '0', 2),
(33, '每日任务', 'daily_task', '每日可完成的任务，对应 daily_task 表', 30, '激励', '/1/30/33', 2, '0', 3),
(34, '积分商城', 'mall_goods', '可使用积分兑换的商品，对应 mall_goods 表', 30, '商城', '/1/30/34', 2, '0', 4),

-- 营销体系
(40, '营销体系', 'marketing_system', '营销活动相关', 1, '体系', '/1/40', 1, '0', 5),
(41, '活动', 'activity', '各类营销活动，对应 activity 表', 40, '营销', '/1/40/41', 2, '0', 1),
(42, '优惠券', 'coupon', '优惠券/折扣码，对应 coupon 表', 40, '营销', '/1/40/42', 2, '0', 2),
(43, '分销', 'distribution', '分销推广体系，对应 distributor/commission_record 表', 40, '体系', '/1/40/43', 2, '0', 3),

-- 交易体系
(50, '交易体系', 'trade_system', '支付与订单相关', 1, '体系', '/1/50', 1, '0', 6),
(51, '支付订单', 'pay_order', '用户支付订单，对应 pay_order 表', 50, '实体', '/1/50/51', 2, '0', 1),
(52, '支付交易', 'pay_transaction', '支付流水记录，对应 pay_transaction 表', 50, '流水', '/1/50/52', 2, '0', 2),
(53, '自动续费', 'auto_renew', '自动续费订阅记录，对应 auto_renew 表', 50, '订阅', '/1/50/53', 2, '0', 3),

-- 通知体系
(60, '通知体系', 'notification_system', '消息通知系统', 1, '体系', '/1/60', 1, '0', 7),
(61, '通知消息', 'notification', '系统中的各类通知消息，对应 notification 表', 60, '消息', '/1/60/61', 2, '0', 1);

-- 概念之间的关系（基于业务关联）
INSERT INTO ai_ontology_relation (source_concept_id, target_concept_id, relation_type, description) VALUES
-- 用户体系关系
(3, 2, 'part-of', 'APP用户是用户体系的组成部分'),
(4, 3, 'status-of', 'VIP会员是用户的付费状态'),
(5, 3, 'role-of', '分销员是用户的特殊角色'),
(5, 43, 'related-to', '分销员参与分销体系'),

-- 内容体系关系
(13, 11, 'is-a', '白噪音是一种音频素材类型'),
(14, 11, 'is-a', '自然音是一种音频素材类型'),
(12, 10, 'part-of', '音频混音是内容体系的组成部分'),
(15, 10, 'part-of', '冥想内容是内容体系的组成部分'),
(16, 15, 'part-of', '冥想音频属于冥想内容'),
(17, 10, 'part-of', '冥想作者是内容体系的组成部分'),
(18, 10, 'part-of', '背景图是内容体系的附属资源'),
(13, 21, 'related-to', '白噪音常用于专注场景'),
(13, 22, 'related-to', '白噪音常用于睡眠场景'),
(14, 21, 'related-to', '自然音可用于专注场景'),
(14, 22, 'related-to', '自然音可用于睡眠场景'),
(15, 21, 'related-to', '冥想可用于专注放松'),

-- 功能关系
(21, 20, 'part-of', '专注是核心功能'),
(22, 20, 'part-of', '睡眠是核心功能'),
(23, 20, 'part-of', '心理测评为评估功能'),
(24, 20, 'part-of', '动态社区是社交功能'),
(25, 20, 'part-of', 'AI聊天为智能助手功能'),
(24, 21, 'related-to', '专注完成可自动发布动态'),
(24, 22, 'related-to', '睡眠记录可自动发布动态'),
(24, 31, 'related-to', '获得成就可自动发布动态'),

-- 成长关系
(31, 30, 'part-of', '成就是成长激励'),
(32, 30, 'part-of', '挑战是成长激励'),
(33, 30, 'part-of', '每日任务是成长激励'),
(34, 30, 'part-of', '积分商城是成长消费出口'),
(31, 24, 'related-to', '成就可分享到动态'),
(32, 31, 'related-to', '完成挑战可获得成就'),
(33, 31, 'related-to', '完成任务可获得成就'),
(34, 52, 'related-to', '积分商品兑换产生交易'),

-- 营销关系
(41, 40, 'part-of', '活动是营销手段'),
(42, 40, 'part-of', '优惠券是营销工具'),
(43, 40, 'part-of', '分销是推广体系'),
(41, 42, 'related-to', '活动可发放优惠券'),
(41, 51, 'related-to', '活动促进订单转化'),
(42, 51, 'related-to', '优惠券用于订单抵扣'),
(43, 5, 'related-to', '分销体系由分销员构成'),

-- 交易关系
(51, 50, 'part-of', '支付订单是交易核心'),
(52, 51, 'part-of', '支付交易属于订单流程'),
(53, 50, 'part-of', '自动续费为订阅服务'),
(51, 4, 'related-to', '订单可开通VIP会员'),
(51, 34, 'related-to', '积分订单兑换商品'),

-- 通知关系
(61, 60, 'part-of', '通知消息是通知体系的实体'),
(61, 24, 'related-to', '互动通知关联动态'),
(61, 41, 'related-to', '活动通知关联活动'),
(61, 51, 'related-to', '支付通知关联订单'),
(61, 43, 'related-to', '分销通知关联佣金');

-- =============================
-- 属性种子数据
-- =============================

-- 用户属性
INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, required, description, sort_order, status)
SELECT '昵称', 'nickname', 'STRING', concept_id, '1', '用户昵称', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'app_user';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, enum_values, description, sort_order, status)
SELECT '性别', 'gender', 'ENUM', concept_id, '["0","1","2"]', '性别：0未知 1男 2女', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'app_user';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, enum_values, default_value, description, sort_order, status)
SELECT 'VIP状态', 'vip_status', 'ENUM', concept_id, '["0","1","2"]', '0', 'VIP状态：0普通 1VIP 2终身VIP', 3, '0'
FROM ai_ontology_concept WHERE concept_code = 'app_user';

-- 音频素材属性
INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, description, sort_order, status)
SELECT '标题', 'title', 'STRING', concept_id, '素材名称', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'audio_item';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, description, sort_order, status)
SELECT '时长（秒）', 'duration', 'INTEGER', concept_id, '音频时长，单位为秒', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'audio_item';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, description, sort_order, status)
SELECT '作者', 'narrator', 'STRING', concept_id, '音频作者/朗读者', 3, '0'
FROM ai_ontology_concept WHERE concept_code = 'audio_item';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, description, sort_order, status)
SELECT '标签', 'tags', 'STRING', concept_id, '关键词标签，逗号分隔', 4, '0'
FROM ai_ontology_concept WHERE concept_code = 'audio_item';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, enum_values, default_value, description, sort_order, status)
SELECT '状态', 'item_status', 'ENUM', concept_id, '["0","1"]', '1', '状态：0下架 1上架', 5, '0'
FROM ai_ontology_concept WHERE concept_code = 'audio_item';

-- 专注记录属性
INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, description, sort_order, status)
SELECT '时长（分钟）', 'duration_minutes', 'INTEGER', concept_id, '专注时长', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'focus';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, enum_values, description, sort_order, status)
SELECT '模式', 'mode', 'ENUM', concept_id, '["tomato","deep","free"]', '专注模式：番茄/深度/自由', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'focus';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, description, sort_order, status)
SELECT '标签', 'tag', 'STRING', concept_id, '专注标签如学习/工作/阅读', 3, '0'
FROM ai_ontology_concept WHERE concept_code = 'focus';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, enum_values, description, sort_order, status)
SELECT '状态', 'focus_status', 'ENUM', concept_id, '["0","1","2"]', '状态：0进行中 1已完成 2已中断', 4, '0'
FROM ai_ontology_concept WHERE concept_code = 'focus';

-- 冥想内容属性
INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, description, sort_order, status)
SELECT '标题', 'med_title', 'STRING', concept_id, '冥想内容标题', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'meditation';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, enum_values, default_value, description, sort_order, status)
SELECT '状态', 'med_status', 'ENUM', concept_id, '["0","1"]', '0', '状态：0下架 1上架', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'meditation';

-- 挑战属性
INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, enum_values, description, sort_order, status)
SELECT '类型', 'challenge_type', 'ENUM', concept_id, '["focus","sleep"]', '挑战类型：专注/睡眠', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'challenge';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, description, sort_order, status)
SELECT '持续天数', 'duration_days', 'INTEGER', concept_id, '挑战持续天数', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'challenge';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, description, sort_order, status)
SELECT '积分奖励', 'points_reward', 'INTEGER', concept_id, '完成挑战可获得积分', 3, '0'
FROM ai_ontology_concept WHERE concept_code = 'challenge';

-- 活动属性
INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, enum_values, description, sort_order, status)
SELECT '活动类型', 'activity_type', 'ENUM', concept_id, '["discount","buy_one_get_one","new_user","distribution"]', '活动类型', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'activity';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, enum_values, description, sort_order, status)
SELECT '状态', 'activity_status', 'ENUM', concept_id, '["0","1","2"]', '状态：0草稿 1已上线 2已下线', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'activity';

-- 消费券属性
INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, enum_values, description, sort_order, status)
SELECT '券类型', 'coupon_type', 'ENUM', concept_id, '["discount","reduction","full_reduction"]', '优惠券类型：折扣/立减/满减', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'coupon';

INSERT INTO ai_ontology_property (property_name, property_code, property_type, concept_id, description, sort_order, status)
SELECT '优惠值', 'coupon_value', 'DOUBLE', concept_id, '折扣率或减免金额', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'coupon';

-- =============================
-- 实例种子数据（对应 su_crm.sql 中的真实业务记录）
-- =============================

-- 专注模式实例（聚焦于功能模板而非具体用户记录）
INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '番茄专注', 'focus_tomato', concept_id, '25分钟番茄钟+5分钟休息的标准模式', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'focus';

INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '深度专注', 'focus_deep', concept_id, '90-120分钟长时段深度工作模式', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'focus';

INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '自由专注', 'focus_free', concept_id, '自定义时长的自由模式', 3, '0'
FROM ai_ontology_concept WHERE concept_code = 'focus';

-- 各类白噪音（对应 audio_item 表中的实际素材）
INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '风声', 'wind_sound', concept_id, '风声-白噪音冥想助眠，73秒', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'white_noise';

INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '雷声', 'thunder_sound', concept_id, '雷声白噪音，22秒', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'white_noise';

-- 自然音实例
INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '蝉鸣', 'cicada_sound', concept_id, '夏日蝉鸣自然音，9秒', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'nature_sound';

-- 挑战实例
INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '21天早起挑战', 'challenge_early', concept_id, '连续21天在7点前起床', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'challenge';

INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '30天专注挑战', 'challenge_focus30', concept_id, '连续30天每天完成至少60分钟专注', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'challenge';

INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '7天冥想挑战', 'challenge_meditate', concept_id, '连续7天每天冥想至少15分钟', 3, '0'
FROM ai_ontology_concept WHERE concept_code = 'challenge';

-- 优惠券实例
INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '新用户立减券', 'coupon_new_user', concept_id, '新用户立减10元', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'coupon';

INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '满50减10', 'coupon_man50', concept_id, '满50元减10元', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'coupon';

-- 积分商品实例
INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '7天VIP会员', 'goods_vip7', concept_id, '500积分兑换7天VIP体验', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'mall_goods';

INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '30天VIP会员', 'goods_vip30', concept_id, '1500积分兑换30天VIP', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'mall_goods';

INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '限定头像框-星空', 'goods_frame_star', concept_id, '300积分兑换星空头像框', 3, '0'
FROM ai_ontology_concept WHERE concept_code = 'mall_goods';

-- 心理测评实例
INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '自我接纳问卷', 'test_self_accept', concept_id, '16题2分钟，评估自我接纳水平', 1, '0'
FROM ai_ontology_concept WHERE concept_code = 'psyc_test';

INSERT INTO ai_ontology_instance (instance_name, instance_code, concept_id, description, sort_order, status)
SELECT '抑郁自评量表', 'test_sds', concept_id, '20题2分钟，SDS抑郁自评', 2, '0'
FROM ai_ontology_concept WHERE concept_code = 'psyc_test';

-- =============================
-- 实例属性值种子数据
-- =============================

-- 番茄专注的属性值
INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, '25'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'focus_tomato' AND p.property_code = 'duration_minutes';

INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, 'tomato'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'focus_tomato' AND p.property_code = 'mode';

-- 深度专注的属性值
INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, '120'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'focus_deep' AND p.property_code = 'duration_minutes';

INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, 'deep'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'focus_deep' AND p.property_code = 'mode';

-- 风声的属性值
INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, '风声-白噪音冥想助眠'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'wind_sound' AND p.property_code = 'title';

INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, '73'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'wind_sound' AND p.property_code = 'duration';

INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, '苏夏'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'wind_sound' AND p.property_code = 'narrator';

-- 21天早起挑战的属性值
INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, 'sleep'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'challenge_early' AND p.property_code = 'challenge_type';

INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, '21'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'challenge_early' AND p.property_code = 'duration_days';

INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, '500'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'challenge_early' AND p.property_code = 'points_reward';

-- 30天专注挑战的属性值
INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, 'focus'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'challenge_focus30' AND p.property_code = 'challenge_type';

INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, '30'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'challenge_focus30' AND p.property_code = 'duration_days';

INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, '1000'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'challenge_focus30' AND p.property_code = 'points_reward';

-- 自我接纳问卷的属性值
INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, '16'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'test_self_accept' AND p.property_code = 'duration_minutes';

INSERT INTO ai_ontology_instance_value (instance_id, property_id, property_value)
SELECT i.instance_id, p.property_id, '2'
FROM ai_ontology_instance i, ai_ontology_property p
WHERE i.instance_code = 'test_self_accept' AND p.property_code = 'duration';

-- 注意：duration 属性是属于 audio_item 的，这里要取 audio_item 的时长属性ID
-- 实际运行如果报错，说明 meditation 概念没有 duration 属性，去掉即可

-- =============================
-- 规则种子数据
-- =============================

-- 全局规则
INSERT INTO ai_ontology_rule (rule_name, rule_code, concept_id, `condition`, `action`, priority, enabled, description)
VALUES ('专注时长推荐', 'focus_duration_advice', NULL,
        '{"field": "duration_minutes", "operator": "lt", "value": 5}',
        '{"type": "suggest", "message": "专注时间建议不少于5分钟才能进入状态"}',
        1, '1', '专注时长少于5分钟给出建议');

INSERT INTO ai_ontology_rule (rule_name, rule_code, concept_id, `condition`, `action`, priority, enabled, description)
VALUES ('VIP即将到期提醒', 'vip_expiry_reminder', NULL,
        '{"field": "vip_end_time", "operator": "within_days", "value": 7}',
        '{"type": "notify", "message": "您的VIP即将到期，续费享优惠"}',
        2, '1', 'VIP到期前7天发送续费提醒');

INSERT INTO ai_ontology_rule (rule_name, rule_code, concept_id, `condition`, `action`, priority, enabled, description)
VALUES ('新用户首次优惠', 'new_user_discount', NULL,
        '{"field": "register_days", "operator": "lte", "value": 3}',
        '{"type": "coupon", "value": "new_user_discount", "message": "新用户专享首单5折"}',
        3, '1', '注册3天内新用户享受首单优惠');

-- 概念级规则
INSERT INTO ai_ontology_rule (rule_name, rule_code, concept_id, `condition`, `action`, priority, enabled, description)
SELECT '连续专注奖励', 'streak_focus_reward', concept_id,
        '{"field": "consecutive_days", "operator": "gte", "value": 7}',
        '{"type": "reward", "points": 100, "message": "连续专注7天，奖励100积分！"}',
        1, '1', '连续专注7天以上获得积分奖励'
FROM ai_ontology_concept WHERE concept_code = 'focus';

INSERT INTO ai_ontology_rule (rule_name, rule_code, concept_id, `condition`, `action`, priority, enabled, description)
SELECT '付费音频播放限制', 'paid_audio_restrict', concept_id,
        '{"field": "is_paid", "operator": "eq", "value": true, "user_field": "vip_status", "user_operator": "eq", "user_value": 0}',
        '{"type": "block", "message": "该音频为VIP专享，请开通会员后播放"}',
        1, '1', '非VIP用户不能播放付费音频'
FROM ai_ontology_concept WHERE concept_code = 'audio_item';

INSERT INTO ai_ontology_rule (rule_name, rule_code, concept_id, `condition`, `action`, priority, enabled, description)
SELECT '白噪音默认免费', 'white_noise_free_default', concept_id,
        NULL,
        '{"type": "override", "field": "is_paid", "value": false, "message": "白噪音内容默认免费"}',
        1, '1', '白噪音类型的音频默认为免费内容'
FROM ai_ontology_concept WHERE concept_code = 'white_noise';

-- =============================
-- 行为种子数据
-- =============================

-- 全局行为
INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
VALUES ('App数据查询', 'app_data_query', NULL, 'TOOL', 'queryAppData',
        '{"method": "ai查询", "params": {"query": "用户问句"}}',
        '查询心聆App中的各类业务数据', '0');

INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
VALUES ('发送通知', 'send_notification', NULL, 'API', '/api/app/notification/send',
        '{"method": "POST", "params": {"userId": "用户ID", "title": "标题", "content": "内容"}}',
        '向指定用户发送系统通知', '0');

-- 专注功能行为
INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
SELECT '开始专注', 'start_focus', concept_id, 'API', '/api/app/focus/start',
        '{"method": "POST", "params": {"mode": "专注模式", "duration": "时长(分钟)", "tag": "标签"}}',
        '启动一次专注计时', '0'
FROM ai_ontology_concept WHERE concept_code = 'focus';

INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
SELECT '查询专注统计', 'query_focus_stats', concept_id, 'TOOL', 'queryFocusStatistics',
        '{"method": "统计查询", "params": {"userId": "用户ID", "period": "时间范围"}}',
        '查询用户的专注统计数据', '0'
FROM ai_ontology_concept WHERE concept_code = 'focus';

-- 睡眠功能行为
INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
SELECT '记录睡眠日记', 'write_sleep_diary', concept_id, 'API', '/api/app/sleep/diary',
        '{"method": "POST", "params": {"date": "日期", "bedtimeActivity": "睡前活动"}}',
        '记录用户的睡眠日记', '0'
FROM ai_ontology_concept WHERE concept_code = 'sleep';

INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
SELECT '查询睡眠报告', 'query_sleep_report', concept_id, 'TOOL', 'querySleepReport',
        '{"method": "统计查询", "params": {"userId": "用户ID", "period": "时间范围"}}',
        '查询用户的睡眠质量报告', '0'
FROM ai_ontology_concept WHERE concept_code = 'sleep';

-- 心理测评行为
INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
SELECT '参与测评', 'take_psyc_test', concept_id, 'API', '/api/app/psyc/test/submit',
        '{"method": "POST", "params": {"testId": "测评ID", "answers": "答题结果"}}',
        '参与心理测评并提交答案', '0'
FROM ai_ontology_concept WHERE concept_code = 'psyc_test';

-- 动态社区行为
INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
SELECT '发布动态', 'publish_moment', concept_id, 'API', '/api/app/moment/publish',
        '{"method": "POST", "params": {"content": "动态内容", "visibility": "可见范围"}}',
        '在社区发布一条动态', '0'
FROM ai_ontology_concept WHERE concept_code = 'moment';

-- 挑战行为
INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
SELECT '参与挑战', 'join_challenge', concept_id, 'API', '/api/app/challenge/join',
        '{"method": "POST", "params": {"challengeId": "挑战ID"}}',
        '参加一个挑战活动', '0'
FROM ai_ontology_concept WHERE concept_code = 'challenge';

-- 积分商城行为
INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
SELECT '兑换商品', 'redeem_goods', concept_id, 'API', '/api/app/mall/redeem',
        '{"method": "POST", "params": {"goodsId": "商品ID", "quantity": "数量"}}',
        '使用积分兑换商城商品', '0'
FROM ai_ontology_concept WHERE concept_code = 'mall_goods';

-- 活动概念行为（新增：打通活动查询）
INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
SELECT '查询活动列表', 'query_activities', concept_id, 'TOOL', 'queryActivityList',
        '{"method": "DatabaseQueryTool.executeQuery", "sql": "SELECT * FROM activity WHERE status = 1 AND end_time > NOW() ORDER BY priority, start_time"}',
        '查询当前上线的营销活动列表', '0'
FROM ai_ontology_concept WHERE concept_code = 'activity';

INSERT INTO ai_ontology_action (action_name, action_code, concept_id, action_type, target, parameters, description, status)
SELECT '参与活动', 'join_activity', concept_id, 'API', '/api/app/activity/join',
        '{"method": "POST", "params": {"activityId": "活动ID"}}',
        '用户参与一个营销活动', '0'
FROM ai_ontology_concept WHERE concept_code = 'activity';

-- =============================
-- 映射种子数据（概念→业务表映射）
-- =============================

-- 白噪音映射到 audio_item
INSERT INTO ai_ontology_mapping (concept_code, concept_id, table_name, record_id, mapping_type)
SELECT 'white_noise', c.concept_id, 'audio_item', a.id, 'PRIMARY'
FROM audio_item a
CROSS JOIN ai_ontology_concept c ON c.concept_code = 'white_noise'
WHERE a.title LIKE '%风声%' OR a.title LIKE '%白噪音%' OR a.title LIKE '%雷声%';

-- 自然音映射到 audio_item
INSERT INTO ai_ontology_mapping (concept_code, concept_id, table_name, record_id, mapping_type)
SELECT 'nature_sound', c.concept_id, 'audio_item', a.id, 'PRIMARY'
FROM audio_item a
CROSS JOIN ai_ontology_concept c ON c.concept_code = 'nature_sound'
WHERE a.title LIKE '%蝉鸣%' OR a.title LIKE '%鸟鸣%' OR a.title LIKE '%海浪%' OR a.title LIKE '%溪流%';

-- 冥想音频映射到 audio_item
INSERT INTO ai_ontology_mapping (concept_code, concept_id, table_name, record_id, mapping_type)
SELECT 'meditation_audio', c.concept_id, 'audio_item', a.id, 'PRIMARY'
FROM audio_item a
CROSS JOIN ai_ontology_concept c ON c.concept_code = 'meditation_audio'
WHERE a.title LIKE '%冥想%';

-- 活动概念映射到 activity 表
INSERT INTO ai_ontology_mapping (concept_code, concept_id, table_name, record_id, mapping_type)
SELECT 'activity', c.concept_id, 'activity', a.id, 'PRIMARY'
FROM activity a
CROSS JOIN ai_ontology_concept c ON c.concept_code = 'activity';

-- =============================
-- 字段映射种子数据（属性→表字段）
-- =============================

-- 白噪音的概念属性→audio_item 表字段
INSERT INTO ai_ontology_field_mapping (mapping_id, property_code, column_name)
SELECT m.mapping_id, 'title', 'title'
FROM ai_ontology_mapping m
WHERE m.concept_code = 'white_noise' AND m.table_name = 'audio_item'
LIMIT 1;

INSERT INTO ai_ontology_field_mapping (mapping_id, property_code, column_name)
SELECT m.mapping_id, 'duration', 'duration'
FROM ai_ontology_mapping m
WHERE m.concept_code = 'white_noise' AND m.table_name = 'audio_item'
LIMIT 1;

INSERT INTO ai_ontology_field_mapping (mapping_id, property_code, column_name)
SELECT m.mapping_id, 'narrator', 'narrator'
FROM ai_ontology_mapping m
WHERE m.concept_code = 'white_noise' AND m.table_name = 'audio_item'
LIMIT 1;

-- 自然音字段映射
INSERT INTO ai_ontology_field_mapping (mapping_id, property_code, column_name)
SELECT m.mapping_id, 'title', 'title'
FROM ai_ontology_mapping m
WHERE m.concept_code = 'nature_sound' AND m.table_name = 'audio_item'
LIMIT 1;
