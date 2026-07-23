SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS ai_ontology_mapping;
DROP TABLE IF EXISTS ai_ontology_relation;
DROP TABLE IF EXISTS ai_ontology_concept;

SET FOREIGN_KEY_CHECKS = 1;

-- 本体概念表
CREATE TABLE ai_ontology_concept (
    concept_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '概念ID',
    concept_name VARCHAR(100) NOT NULL COMMENT '概念名称',
    concept_code VARCHAR(50) NOT NULL UNIQUE COMMENT '概念编码',
    description TEXT COMMENT '概念描述',
    parent_id BIGINT COMMENT '父概念ID（支持层级结构）',
    category VARCHAR(50) COMMENT '类别',
    path VARCHAR(500) COMMENT '层级路径（如 /1/10/15，从根到自身的完整路径）',
    level INT DEFAULT 0 COMMENT '层级深度（根节点=0）',
    status CHAR(1) DEFAULT '0' COMMENT '状态：0启用 1禁用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (parent_id) REFERENCES ai_ontology_concept(concept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='本体概念表';

-- 本体关系表
CREATE TABLE ai_ontology_relation (
    relation_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关系ID',
    source_concept_id BIGINT NOT NULL COMMENT '源概念ID',
    target_concept_id BIGINT NOT NULL COMMENT '目标概念ID',
    relation_type VARCHAR(50) NOT NULL COMMENT '关系类型：is-a、part-of、related-to、instance-of等',
    description TEXT COMMENT '关系描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (source_concept_id) REFERENCES ai_ontology_concept(concept_id),
    FOREIGN KEY (target_concept_id) REFERENCES ai_ontology_concept(concept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='本体关系表';

-- 本体映射表（核心：实现概念与任意业务表记录的关联）
-- 优点：1) 业务表无需改动结构 2) 支持一对多 3) 新增业务表只需在此表登记
CREATE TABLE ai_ontology_mapping (
    mapping_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '映射ID',
    concept_code VARCHAR(50) NOT NULL COMMENT '概念编码（关联ai_ontology_concept.concept_code）',
    concept_id BIGINT COMMENT '概念ID（外键，优先使用）',
    table_name VARCHAR(100) NOT NULL COMMENT '业务表名（如audio_item、audio_mix）',
    record_id BIGINT NOT NULL COMMENT '业务记录ID',
    mapping_type VARCHAR(20) DEFAULT 'PRIMARY' COMMENT '映射类型：PRIMARY主映射/TAG标签映射',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_concept_table_record (concept_code, table_name, record_id),
    FOREIGN KEY (concept_id) REFERENCES ai_ontology_concept(concept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='本体映射表';

-- 索引优化
CREATE INDEX idx_concept_parent ON ai_ontology_concept(parent_id);
CREATE INDEX idx_concept_category ON ai_ontology_concept(category);
CREATE INDEX idx_concept_path ON ai_ontology_concept(path(255));
CREATE INDEX idx_relation_source ON ai_ontology_relation(source_concept_id);
CREATE INDEX idx_relation_target ON ai_ontology_relation(target_concept_id);
CREATE INDEX idx_relation_type ON ai_ontology_relation(relation_type);
CREATE INDEX idx_mapping_concept ON ai_ontology_mapping(concept_code);
CREATE INDEX idx_mapping_concept_id ON ai_ontology_mapping(concept_id);
CREATE INDEX idx_mapping_table_record ON ai_ontology_mapping(table_name, record_id);

-- 初始化示例数据（基于 su_crm.sql 业务域）
-- 概念层级结构：根→体系→实体/功能/激励
INSERT INTO ai_ontology_concept (concept_id, concept_name, concept_code, description, parent_id, category, status, sort_order) VALUES
-- 根节点
(1, '心聆平台', 'xinling_platform', '心聆APP——专注冥想助眠平台', NULL, '平台概念', '0', 0),

-- 用户体系
(2, '用户体系', 'user_system', '用户相关的所有概念', 1, '体系', '0', 1),
(3, 'APP用户', 'app_user', '使用心聆APP的注册用户，对应 app_user 表', 2, '实体', '0', 1),
(4, 'VIP会员', 'vip_member', '付费会员用户，享有高级功能', 2, '状态', '0', 2),
(5, '分销员', 'distributor', '参与推广返佣的用户，对应 distributor 表', 2, '角色', '0', 3),

-- 内容体系
(10, '内容体系', 'content_system', '平台上的所有内容资源', 1, '体系', '0', 2),
(11, '音频素材', 'audio_item', '单个音视频素材文件，对应 audio_item 表', 10, '实体', '0', 1),
(12, '音频混音', 'audio_mix', '多个音频素材的混合组合', 10, '实体', '0', 2),
(13, '白噪音', 'white_noise', '连续均匀的环境声音，帮助专注或睡眠', 11, '类型', '0', 3),
(14, '自然音', 'nature_sound', '自然界声音如鸟鸣、海浪、溪流', 11, '类型', '0', 4),
(15, '冥想内容', 'meditation', '引导用户正念冥想的课程内容', 10, '实体', '0', 5),
(16, '冥想音频', 'meditation_audio', '冥想课程中的具体音频段落', 15, '段落', '0', 6),
(17, '冥想作者', 'meditation_author', '冥想内容的创作者', 10, '实体', '0', 7),
(18, '背景图', 'content_bg', '各内容类型的背景图片', 10, '资源', '0', 8),

-- 功能体系
(20, '功能体系', 'feature_system', '应用核心功能模块', 1, '体系', '0', 3),
(21, '专注', 'focus', '番茄钟/深度专注计时功能，对应 focus_record 表', 20, '功能', '0', 1),
(22, '睡眠', 'sleep', '睡眠记录与监测功能', 20, '功能', '0', 2),
(23, '心理测评', 'psyc_test', '心理健康测评问卷，对应 psyc_test 表', 20, '功能', '0', 3),
(24, '动态社区', 'moment', '用户发布的心情动态，对应 moment 表', 20, '功能', '0', 4),
(25, 'AI聊天', 'ai_chat', 'AI智能对话助手功能', 20, '功能', '0', 5),

-- 成长体系
(30, '成长体系', 'growth_system', '用户成长激励机制', 1, '体系', '0', 4),
(31, '成就', 'achievement', '可获得的成就徽章，对应 achievement 表', 30, '激励', '0', 1),
(32, '挑战', 'challenge', '限时挑战活动，对应 challenge 表', 30, '激励', '0', 2),
(33, '每日任务', 'daily_task', '每日可完成的任务', 30, '激励', '0', 3),
(34, '积分商城', 'mall_goods', '可使用积分兑换的商品，对应 mall_goods 表', 30, '商城', '0', 4),

-- 营销体系
(40, '营销体系', 'marketing_system', '营销活动相关', 1, '体系', '0', 5),
(41, '活动', 'activity', '各类营销活动，对应 activity 表', 40, '营销', '0', 1),
(42, '优惠券', 'coupon', '优惠券/折扣码，对应 coupon 表', 40, '营销', '0', 2),
(43, '分销', 'distribution', '分销推广体系', 40, '体系', '0', 3),

-- 交易体系
(50, '交易体系', 'trade_system', '支付与订单相关', 1, '体系', '0', 6),
(51, '支付订单', 'pay_order', '用户支付订单，对应 pay_order 表', 50, '实体', '0', 1),
(52, '支付交易', 'pay_transaction', '支付流水记录', 50, '流水', '0', 2),
(53, '自动续费', 'auto_renew', '自动续费订阅记录', 50, '订阅', '0', 3),

-- 通知体系
(60, '通知体系', 'notification_system', '消息通知系统', 1, '体系', '0', 7),
(61, '通知消息', 'notification', '系统中的各类通知消息，对应 notification 表', 60, '消息', '0', 1);

-- 概念关系
INSERT INTO ai_ontology_relation (source_concept_id, target_concept_id, relation_type, description) VALUES
-- 用户体系
(3, 2, 'part-of', 'APP用户是用户体系的组成部分'),
(4, 3, 'status-of', 'VIP会员是用户的付费状态'),
(5, 3, 'role-of', '分销员是用户的特殊角色'),
-- 内容体系
(13, 11, 'is-a', '白噪音是一种音频素材类型'),
(14, 11, 'is-a', '自然音是一种音频素材类型'),
(12, 10, 'part-of', '音频混音是内容体系的组成部分'),
(15, 10, 'part-of', '冥想内容是内容体系的组成部分'),
(16, 15, 'part-of', '冥想音频属于冥想内容'),
(13, 21, 'related-to', '白噪音常用于专注场景'),
(13, 22, 'related-to', '白噪音常用于睡眠场景'),
(14, 21, 'related-to', '自然音可用于专注场景'),
(14, 22, 'related-to', '自然音可用于睡眠场景'),
-- 功能体系
(21, 20, 'part-of', '专注是核心功能'),
(22, 20, 'part-of', '睡眠是核心功能'),
(23, 20, 'part-of', '心理测评为评估功能'),
(24, 20, 'part-of', '动态社区是社交功能'),
-- 成长体系
(31, 30, 'part-of', '成就是成长激励'),
(32, 30, 'part-of', '挑战是成长激励'),
(33, 30, 'part-of', '每日任务是成长激励'),
(34, 30, 'part-of', '积分商城是成长消费出口'),
-- 营销体系
(41, 40, 'part-of', '活动是营销手段'),
(42, 40, 'part-of', '优惠券是营销工具'),
(43, 40, 'part-of', '分销是推广体系'),
-- 交易体系
(51, 50, 'part-of', '支付订单是交易核心'),
(52, 51, 'part-of', '支付交易属于订单流程'),
(53, 50, 'part-of', '自动续费为订阅服务'),
-- 通知体系
(61, 60, 'part-of', '通知消息是通知体系的实体');

