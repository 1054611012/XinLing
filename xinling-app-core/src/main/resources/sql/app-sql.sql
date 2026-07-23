-- =====================================================
-- APP系统完整SQL脚本（整合版）
-- 包含：删表 → 建表 → 演示数据
-- 生成日期：2026-06-03
-- 说明：
--   1. 执行前会先 DROP 所有旧表，请谨慎使用
--   2. 所有密码均为 123456（BCrypt 哈希）
--   3. 共 45 张表，覆盖用户/专注/睡眠/VIP/支付/分销/活动/社区/音频等模块
-- =====================================================

-- =====================================================
-- 第一部分：删除旧表（按依赖关系倒序）
-- =====================================================
DROP TABLE IF EXISTS audio_play_history;
DROP TABLE IF EXISTS audio_mix;
DROP TABLE IF EXISTS audio_item;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS user_challenge;
DROP TABLE IF EXISTS challenge;
DROP TABLE IF EXISTS private_message;
DROP TABLE IF EXISTS user_follow;
DROP TABLE IF EXISTS moment_comment;
DROP TABLE IF EXISTS moment_like;
DROP TABLE IF EXISTS moment;
DROP TABLE IF EXISTS user_exchange;
DROP TABLE IF EXISTS mall_goods;
DROP TABLE IF EXISTS user_task;
DROP TABLE IF EXISTS daily_task;
DROP TABLE IF EXISTS user_achievement;
DROP TABLE IF EXISTS achievement;
DROP TABLE IF EXISTS user_growth;
DROP TABLE IF EXISTS whitelist;
DROP TABLE IF EXISTS push_task;
DROP TABLE IF EXISTS user_activity;
DROP TABLE IF EXISTS activity;
DROP TABLE IF EXISTS distribution_settings;
DROP TABLE IF EXISTS withdraw_apply;
DROP TABLE IF EXISTS commission_record;
DROP TABLE IF EXISTS distribution_relation;
DROP TABLE IF EXISTS distributor;
DROP TABLE IF EXISTS user_coupon;
DROP TABLE IF EXISTS coupon;
DROP TABLE IF EXISTS auto_renew;
DROP TABLE IF EXISTS pay_transaction;
DROP TABLE IF EXISTS pay_config;
DROP TABLE IF EXISTS order_refund;
DROP TABLE IF EXISTS pay_order;
DROP TABLE IF EXISTS vip_gift_record;
DROP TABLE IF EXISTS vip_gift_rule;
DROP TABLE IF EXISTS user_vip;
DROP TABLE IF EXISTS vip_package;
DROP TABLE IF EXISTS sleep_diary;
DROP TABLE IF EXISTS sleep_record;
DROP TABLE IF EXISTS focus_settings;
DROP TABLE IF EXISTS focus_record;
DROP TABLE IF EXISTS user_device;
DROP TABLE IF EXISTS user_settings;
DROP TABLE IF EXISTS app_user;

-- =====================================================
-- 第二部分：创建表
-- =====================================================

-- -------------------- 用户模块 --------------------

-- APP用户表
CREATE TABLE app_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(50) UNIQUE COMMENT '邮箱',
    password_hash VARCHAR(255) COMMENT '密码哈希(BCrypt)',
    gender TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    birthday DATE COMMENT '生日',
    status TINYINT DEFAULT 0 COMMENT '状态 0-正常 1-禁用 2-冻结',
    vip_status TINYINT DEFAULT 0 COMMENT 'VIP状态 0-普通 1-VIP 2-终身VIP',
    vip_end_time DATETIME COMMENT 'VIP到期时间',
    inviter_id BIGINT DEFAULT NULL COMMENT '邀请人ID',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    last_login_time DATETIME COMMENT '最后登录时间',
    register_ip VARCHAR(50) COMMENT '注册IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-存在 1-删除',
    INDEX idx_inviter_id (inviter_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='APP用户表';

-- 用户设置表
CREATE TABLE user_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT UNIQUE COMMENT '用户ID',
    default_focus_time INT DEFAULT 25 COMMENT '默认专注时长(分钟)',
    default_break_time INT DEFAULT 5 COMMENT '默认休息时长(分钟)',
    default_audio_id BIGINT COMMENT '默认音频ID',
    dark_mode TINYINT DEFAULT 1 COMMENT '深色模式 0-关闭 1-开启 2-跟随系统',
    notification TINYINT DEFAULT 1 COMMENT '通知开关 0-关闭 1-开启',
    volume INT DEFAULT 70 COMMENT '默认音量(0-100)',
    ai_voice_id VARCHAR(50) COMMENT '默认AI语音ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设置表';

-- 登录设备表
CREATE TABLE user_device (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    device_id VARCHAR(100) COMMENT '设备唯一标识',
    device_name VARCHAR(100) COMMENT '设备名称',
    device_type VARCHAR(20) COMMENT '设备类型 Android/iOS/Web',
    login_time DATETIME COMMENT '登录时间',
    last_active_time DATETIME COMMENT '最后活跃时间',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_device_id (device_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录设备表';

-- -------------------- 专注模块 --------------------

-- 专注记录表
CREATE TABLE focus_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    duration INT COMMENT '专注时长(分钟)',
    status TINYINT COMMENT '状态 0-进行中 1-已完成 2-已中断',
    mode VARCHAR(20) COMMENT '模式 tomato/deep/free',
    tag VARCHAR(50) COMMENT '专注标签',
    interrupt_count INT DEFAULT 0 COMMENT '中断次数',
    note TEXT COMMENT '专注笔记',
    audio_mix_id BIGINT COMMENT '使用的混音ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专注记录表';

-- 专注设置表
CREATE TABLE focus_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT UNIQUE COMMENT '用户ID',
    strict_mode TINYINT DEFAULT 0 COMMENT '严格模式 0-关闭 1-开启',
    app_block TINYINT DEFAULT 0 COMMENT 'APP屏蔽 0-关闭 1-开启',
    allowed_apps JSON COMMENT '白名单应用',
    notification_block TINYINT DEFAULT 1 COMMENT '通知屏蔽',
    ai_encouragement TINYINT DEFAULT 1 COMMENT 'AI鼓励',
    encouragement_interval INT DEFAULT 30 COMMENT '鼓励间隔(分钟)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专注设置表';

-- -------------------- 睡眠模块 --------------------

-- 睡眠记录表
CREATE TABLE sleep_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    start_time DATETIME COMMENT '入睡时间',
    end_time DATETIME COMMENT '起床时间',
    duration INT COMMENT '睡眠时长(分钟)',
    sleep_score INT COMMENT '睡眠评分(0-100)',
    deep_sleep_minutes INT COMMENT '深睡时长(分钟)',
    light_sleep_minutes INT COMMENT '浅睡时长(分钟)',
    rem_sleep_minutes INT COMMENT 'REM睡眠时长(分钟)',
    interrupt_count INT DEFAULT 0 COMMENT '中断次数',
    snoring_count INT DEFAULT 0 COMMENT '打鼾次数',
    audio_mix_id BIGINT COMMENT '使用的助眠音ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='睡眠记录表';

-- 睡眠日记表
CREATE TABLE sleep_diary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    date DATE COMMENT '日期',
    bedtime_activity VARCHAR(255) COMMENT '睡前活动',
    caffeine_intake TINYINT COMMENT '咖啡因摄入 0-无 1-少量 2-大量',
    exercise TINYINT COMMENT '运动情况 0-无 1-轻度 2-中度 3-重度',
    emotion VARCHAR(20) COMMENT '情绪状态',
    note TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='睡眠日记表';

-- -------------------- 会员系统 --------------------

-- 会员套餐表
CREATE TABLE vip_package (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) COMMENT '套餐名称',
    description VARCHAR(255) COMMENT '套餐描述',
    price DECIMAL(10,2) COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    days INT COMMENT '天数',
    type VARCHAR(20) COMMENT '类型 month/quarter/year/lifetime',
    status TINYINT DEFAULT 1 COMMENT '状态 0-下架 1-上架',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员套餐表';

-- 用户会员表
CREATE TABLE user_vip (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT UNIQUE COMMENT '用户ID',
    package_id BIGINT COMMENT '套餐ID',
    package_name VARCHAR(50) COMMENT '套餐名称',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    auto_renew TINYINT DEFAULT 0 COMMENT '自动续费 0-关闭 1-开启',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户会员表';

-- 会员赠送规则表
CREATE TABLE vip_gift_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(20) NOT NULL COMMENT '规则类型: register-注册送 invite-邀请送 focus-专注送 manual-手动赠送',
    condition_value VARCHAR(500) COMMENT '条件值(JSON)',
    vip_days INT NOT NULL DEFAULT 0 COMMENT '赠送天数(0=终身)',
    auto_grant TINYINT DEFAULT 0 COMMENT '是否自动发放 0-否 1-是',
    total_limit INT DEFAULT 0 COMMENT '总名额限制(0=不限)',
    granted_count INT DEFAULT 0 COMMENT '已发放数量',
    daily_limit INT DEFAULT 0 COMMENT '每日限制(0=不限)',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    description VARCHAR(500) COMMENT '规则说明',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_rule_type (rule_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员赠送规则表';

-- 会员赠送记录表
CREATE TABLE vip_gift_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    rule_id BIGINT COMMENT '赠送规则ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_nickname VARCHAR(50) COMMENT '用户昵称',
    grant_type VARCHAR(20) NOT NULL COMMENT '赠送类型: auto-自动 manual-手动',
    vip_days INT NOT NULL COMMENT '赠送天数',
    reason VARCHAR(500) COMMENT '赠送原因',
    operator_id BIGINT COMMENT '操作人ID(手动赠送时)',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    expire_time DATETIME COMMENT '会员到期时间',
    status TINYINT DEFAULT 1 COMMENT '状态 0-失效 1-有效',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_rule_id (rule_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员赠送记录表';

-- -------------------- 订单系统 --------------------

-- 支付订单表
CREATE TABLE pay_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) UNIQUE COMMENT '订单号',
    user_id BIGINT COMMENT '用户ID',
    package_id BIGINT COMMENT '套餐ID',
    package_name VARCHAR(50) COMMENT '套餐名称',
    amount DECIMAL(10,2) COMMENT '订单金额',
    pay_amount DECIMAL(10,2) COMMENT '实际支付金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    coupon_id BIGINT DEFAULT 0 COMMENT '使用的优惠券ID',
    pay_type VARCHAR(20) COMMENT '支付方式 alipay/wechat/apple',
    order_status TINYINT COMMENT '状态 0-待支付 1-已支付 2-已取消 3-已退款 4-已过期',
    pay_time DATETIME COMMENT '支付时间',
    expire_time DATETIME COMMENT '订单过期时间',
    transaction_id VARCHAR(50) COMMENT '第三方交易号',
    refund_amount DECIMAL(10,2) DEFAULT 0 COMMENT '退款金额',
    refund_time DATETIME COMMENT '退款时间',
    refund_reason TEXT COMMENT '退款原因',
    distributor_id BIGINT DEFAULT 0 COMMENT '分销员ID',
    commission_amount DECIMAL(10,2) DEFAULT 0 COMMENT '分销佣金金额',
    activity_id BIGINT DEFAULT 0 COMMENT '参与的活动ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_order_status (order_status),
    INDEX idx_create_time (create_time),
    INDEX idx_distributor_id (distributor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';

-- 退款记录表
CREATE TABLE order_refund (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) COMMENT '订单号',
    user_id BIGINT COMMENT '用户ID',
    refund_amount DECIMAL(10,2) COMMENT '退款金额',
    refund_reason TEXT COMMENT '退款原因',
    refund_status TINYINT COMMENT '状态 0-待审核 1-审核通过 2-审核拒绝 3-退款中 4-退款完成',
    audit_user_id BIGINT COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    audit_remark TEXT COMMENT '审核备注',
    transaction_id VARCHAR(50) COMMENT '退款交易号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';

-- -------------------- 支付系统 --------------------

-- 支付配置表
CREATE TABLE pay_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    pay_type VARCHAR(20) COMMENT '支付方式 alipay/wechat/apple',
    app_id VARCHAR(100) COMMENT '应用ID',
    merchant_id VARCHAR(100) COMMENT '商户号',
    private_key TEXT COMMENT '私钥',
    public_key TEXT COMMENT '公钥',
    notify_url VARCHAR(255) COMMENT '回调地址',
    status TINYINT DEFAULT 1 COMMENT '状态 0-关闭 1-开启',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付配置表';

-- 交易记录表
CREATE TABLE pay_transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) COMMENT '订单号',
    transaction_id VARCHAR(50) COMMENT '第三方交易号',
    pay_type VARCHAR(20) COMMENT '支付方式',
    amount DECIMAL(10,2) COMMENT '交易金额',
    status TINYINT COMMENT '状态 0-待支付 1-支付成功 2-支付失败 3-已退款',
    pay_time DATETIME COMMENT '支付时间',
    raw_data TEXT COMMENT '原始支付数据',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_no (order_no),
    INDEX idx_transaction_id (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

-- 自动续费表
CREATE TABLE auto_renew (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    package_id BIGINT COMMENT '套餐ID',
    pay_type VARCHAR(20) COMMENT '支付方式',
    agreement_id VARCHAR(100) COMMENT '代扣协议ID',
    next_pay_time DATETIME COMMENT '下次扣费时间',
    status TINYINT DEFAULT 1 COMMENT '状态 0-已关闭 1-已开启',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_package (user_id, package_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自动续费表';

-- 优惠券表
CREATE TABLE coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) COMMENT '优惠券名称',
    type VARCHAR(20) COMMENT '类型 discount/reduction/full_reduction',
    value DECIMAL(10,2) COMMENT '优惠值',
    condition_amount DECIMAL(10,2) COMMENT '满减条件',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    total_count INT COMMENT '总数量',
    used_count INT DEFAULT 0 COMMENT '已使用数量',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 用户优惠券表
CREATE TABLE user_coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    coupon_id BIGINT COMMENT '优惠券ID',
    order_no VARCHAR(50) COMMENT '使用的订单号',
    get_time DATETIME COMMENT '领取时间',
    use_time DATETIME COMMENT '使用时间',
    status TINYINT DEFAULT 0 COMMENT '状态 0-未使用 1-已使用 2-已过期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_coupon_id (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- -------------------- 分销系统 --------------------

-- 分销员表
CREATE TABLE distributor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT UNIQUE COMMENT '用户ID',
    level TINYINT DEFAULT 1 COMMENT '等级 1-普通 2-高级 3-金牌',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    alipay_account VARCHAR(50) COMMENT '支付宝账号',
    wechat_account VARCHAR(50) COMMENT '微信账号',
    total_commission DECIMAL(10,2) DEFAULT 0 COMMENT '累计佣金',
    available_commission DECIMAL(10,2) DEFAULT 0 COMMENT '可提现佣金',
    frozen_commission DECIMAL(10,2) DEFAULT 0 COMMENT '冻结佣金',
    total_withdraw DECIMAL(10,2) DEFAULT 0 COMMENT '累计提现',
    total_fans INT DEFAULT 0 COMMENT '累计粉丝数',
    total_orders INT DEFAULT 0 COMMENT '累计订单数',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-已通过 2-已拒绝 3-已禁用',
    apply_time DATETIME COMMENT '申请时间',
    audit_time DATETIME COMMENT '审核时间',
    audit_remark TEXT COMMENT '审核备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分销员表';

-- 分销关系表
CREATE TABLE distribution_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT UNIQUE COMMENT '用户ID',
    parent_id BIGINT COMMENT '上级分销员ID',
    grandparent_id BIGINT DEFAULT 0 COMMENT '上上级分销员ID',
    bind_time DATETIME COMMENT '绑定时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_parent_id (parent_id),
    INDEX idx_grandparent_id (grandparent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分销关系表';

-- 佣金记录表
CREATE TABLE commission_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    distributor_id BIGINT COMMENT '分销员ID',
    order_no VARCHAR(50) COMMENT '订单号',
    user_id BIGINT COMMENT '购买用户ID',
    type TINYINT COMMENT '类型 1-一级佣金 2-二级佣金 3-团队奖励',
    amount DECIMAL(10,2) COMMENT '佣金金额',
    order_amount DECIMAL(10,2) COMMENT '订单金额',
    rate DECIMAL(5,2) COMMENT '佣金比例',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待结算 1-已结算 2-已扣除',
    settle_time DATETIME COMMENT '结算时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_distributor_id (distributor_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金记录表';

-- 提现申请表
CREATE TABLE withdraw_apply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    distributor_id BIGINT COMMENT '分销员ID',
    amount DECIMAL(10,2) COMMENT '提现金额',
    fee DECIMAL(10,2) COMMENT '手续费',
    actual_amount DECIMAL(10,2) COMMENT '实际到账金额',
    pay_type VARCHAR(20) COMMENT '提现方式 alipay/wechat',
    account VARCHAR(50) COMMENT '提现账号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-审核通过 2-审核拒绝 3-打款中 4-已完成',
    audit_user_id BIGINT COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    audit_remark TEXT COMMENT '审核备注',
    pay_time DATETIME COMMENT '打款时间',
    transaction_id VARCHAR(50) COMMENT '打款交易号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_distributor_id (distributor_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现申请表';

-- 分销设置表
CREATE TABLE distribution_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    first_level_rate DECIMAL(5,2) DEFAULT 30.00 COMMENT '一级佣金比例(%)',
    second_level_rate DECIMAL(5,2) DEFAULT 10.00 COMMENT '二级佣金比例(%)',
    team_reward_rate DECIMAL(5,2) DEFAULT 5.00 COMMENT '团队奖励比例(%)',
    min_withdraw_amount DECIMAL(10,2) DEFAULT 10.00 COMMENT '最低提现金额',
    withdraw_fee_rate DECIMAL(5,2) DEFAULT 1.00 COMMENT '提现手续费比例(%)',
    min_withdraw_fee DECIMAL(10,2) DEFAULT 1.00 COMMENT '最低提现手续费',
    settle_days INT DEFAULT 7 COMMENT '佣金结算天数',
    auto_audit TINYINT DEFAULT 1 COMMENT '是否自动审核分销员',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分销设置表';

-- -------------------- 活动通知系统 --------------------

-- 活动表
CREATE TABLE activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(100) COMMENT '活动标题',
    description TEXT COMMENT '活动描述',
    cover VARCHAR(255) COMMENT '活动封面',
    type VARCHAR(20) COMMENT '活动类型 discount/buy_one_get_one/new_user/distribution',
    rule JSON COMMENT '活动规则',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    priority INT DEFAULT 0 COMMENT '优先级',
    status TINYINT DEFAULT 0 COMMENT '状态 0-草稿 1-已上线 2-已下线',
    join_count INT DEFAULT 0 COMMENT '参与人数',
    order_count INT DEFAULT 0 COMMENT '订单数',
    total_amount DECIMAL(10,2) DEFAULT 0 COMMENT '总金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- 用户活动参与表
CREATE TABLE user_activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    activity_id BIGINT COMMENT '活动ID',
    join_time DATETIME COMMENT '参与时间',
    order_no VARCHAR(50) COMMENT '关联订单号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_activity (user_id, activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户活动参与表';

-- 推送任务表
CREATE TABLE push_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(100) COMMENT '推送标题',
    content TEXT COMMENT '推送内容',
    target_type VARCHAR(20) COMMENT '目标类型 all/user_group/specified',
    target_ids JSON COMMENT '目标用户ID列表',
    push_time DATETIME COMMENT '推送时间',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待推送 1-推送中 2-已完成 3-已失败',
    success_count INT DEFAULT 0 COMMENT '成功数',
    fail_count INT DEFAULT 0 COMMENT '失败数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推送任务表';

-- -------------------- 白名单系统 --------------------

CREATE TABLE whitelist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    type VARCHAR(20) COMMENT '类型 user/device/ip/vip',
    identifier VARCHAR(100) COMMENT '标识',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态',
    expire_time DATETIME COMMENT '过期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='白名单表';

-- -------------------- 成长体系 --------------------

-- 用户成长表
CREATE TABLE user_growth (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT UNIQUE COMMENT '用户ID',
    level INT DEFAULT 1 COMMENT '等级',
    exp INT DEFAULT 0 COMMENT '经验值',
    points INT DEFAULT 0 COMMENT '积分',
    continuous_focus_days INT DEFAULT 0 COMMENT '连续专注天数',
    continuous_sleep_days INT DEFAULT 0 COMMENT '连续睡眠天数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户成长表';

-- 成就表
CREATE TABLE achievement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) COMMENT '成就名称',
    description VARCHAR(255) COMMENT '成就描述',
    icon VARCHAR(255) COMMENT '图标',
    condition_type VARCHAR(20) COMMENT '条件类型',
    condition_value INT COMMENT '条件值',
    points_reward INT COMMENT '积分奖励',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成就表';

-- 用户成就表
CREATE TABLE user_achievement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    achievement_id BIGINT COMMENT '成就ID',
    obtain_time DATETIME COMMENT '获得时间',
    UNIQUE KEY uk_user_achievement (user_id, achievement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户成就表';

-- 每日任务表
CREATE TABLE daily_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) COMMENT '任务名称',
    description VARCHAR(255) COMMENT '任务描述',
    condition_type VARCHAR(20) COMMENT '条件类型',
    condition_value INT COMMENT '条件值',
    points_reward INT COMMENT '积分奖励',
    icon VARCHAR(255) COMMENT '图标',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日任务表';

-- 用户任务表
CREATE TABLE user_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    task_id BIGINT COMMENT '任务ID',
    date DATE COMMENT '日期',
    progress INT DEFAULT 0 COMMENT '进度',
    status TINYINT DEFAULT 0 COMMENT '状态 0-进行中 1-已完成 2-已领取',
    UNIQUE KEY uk_user_task_date (user_id, task_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户任务表';

-- 积分商城商品表
CREATE TABLE mall_goods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) COMMENT '商品名称',
    description VARCHAR(255) COMMENT '商品描述',
    cover VARCHAR(255) COMMENT '商品封面',
    type VARCHAR(20) COMMENT '类型 vip/coupon/virtual',
    price INT COMMENT '积分价格',
    stock INT COMMENT '库存',
    status TINYINT DEFAULT 1 COMMENT '状态 0-下架 1-上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分商城商品表';

-- 用户兑换记录表
CREATE TABLE user_exchange (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    goods_id BIGINT COMMENT '商品ID',
    points INT COMMENT '消耗积分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兑换记录表';

-- -------------------- 动态社区模块 --------------------

-- 动态表
CREATE TABLE moment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    content TEXT COMMENT '内容',
    images JSON COMMENT '图片URL列表',
    type VARCHAR(20) COMMENT '类型 auto/manual',
    source VARCHAR(20) COMMENT '来源 focus/sleep/achievement',
    source_id BIGINT COMMENT '来源ID',
    is_anonymous TINYINT DEFAULT 0 COMMENT '是否匿名',
    visibility TINYINT DEFAULT 0 COMMENT '可见范围 0-公开 1-仅好友 2-仅自己',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    share_count INT DEFAULT 0 COMMENT '分享数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态表';

-- 点赞表
CREATE TABLE moment_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    moment_id BIGINT COMMENT '动态ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_moment (user_id, moment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 评论表
CREATE TABLE moment_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    moment_id BIGINT COMMENT '动态ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID',
    content TEXT COMMENT '内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_moment_id (moment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 关注表
CREATE TABLE user_follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    follower_id BIGINT COMMENT '关注者ID',
    following_id BIGINT COMMENT '被关注者ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follower_following (follower_id, following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注表';

-- 私信表
CREATE TABLE private_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    from_user_id BIGINT COMMENT '发送者ID',
    to_user_id BIGINT COMMENT '接收者ID',
    content TEXT COMMENT '内容',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_to_user_id (to_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私信表';

-- 挑战活动表
CREATE TABLE challenge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(100) COMMENT '标题',
    description TEXT COMMENT '描述',
    cover VARCHAR(255) COMMENT '封面',
    type VARCHAR(20) COMMENT '类型 focus/sleep',
    duration INT COMMENT '持续天数',
    condition_value INT COMMENT '每日目标值',
    points_reward INT COMMENT '积分奖励',
    vip_days_reward INT COMMENT 'VIP天数奖励',
    badge_id BIGINT COMMENT '专属徽章ID',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态 0-下架 1-上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挑战活动表';

-- 用户挑战表
CREATE TABLE user_challenge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    challenge_id BIGINT COMMENT '挑战ID',
    join_time DATETIME COMMENT '加入时间',
    current_day INT DEFAULT 0 COMMENT '当前天数',
    completed_days INT DEFAULT 0 COMMENT '完成天数',
    status TINYINT DEFAULT 0 COMMENT '状态 0-进行中 1-已完成 2-已失败',
    UNIQUE KEY uk_user_challenge (user_id, challenge_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户挑战表';

-- -------------------- 通知消息模块 --------------------

CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    type VARCHAR(20) COMMENT '类型 system/interaction/reminder/distribution/activity/pay',
    title VARCHAR(100) COMMENT '标题',
    content TEXT COMMENT '内容',
    target_type VARCHAR(20) COMMENT '目标类型 moment/user/challenge/order/activity',
    target_id BIGINT COMMENT '目标ID',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息表';

-- -------------------- 音频模块 --------------------

CREATE TABLE audio_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(100) NOT NULL COMMENT '音频标题',
    cover_url VARCHAR(500) COMMENT '封面图',
    audio_url VARCHAR(500) COMMENT '音频文件地址',
    duration INT DEFAULT 0 COMMENT '时长(秒)',
    category VARCHAR(20) COMMENT '分类 rain/ocean/forest/night/meditation/cafe/white_noise',
    play_count INT DEFAULT 0 COMMENT '播放次数',
    is_favorite TINYINT DEFAULT 0 COMMENT '是否收藏',
    tags VARCHAR(200) COMMENT '标签 json数组',
    status TINYINT DEFAULT 1 COMMENT '状态 0-下架 1-上架',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='音频表';

CREATE TABLE audio_mix (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '混音名称',
    description VARCHAR(500) COMMENT '描述',
    cover_url VARCHAR(500) COMMENT '封面图',
    audio_ids VARCHAR(500) COMMENT '音频ID列表 json数组',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='音频混音表';

CREATE TABLE audio_play_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    audio_id BIGINT NOT NULL COMMENT '音频ID',
    played_duration INT DEFAULT 0 COMMENT '收听时长(秒)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_audio_id (audio_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='音频播放历史表';


-- =====================================================
-- 第三部分：演示数据
-- =====================================================

-- -------------------- 1. APP用户（20人） --------------------
-- 密码均为 123456（BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy）
INSERT INTO app_user (id, nickname, avatar, phone, password_hash, gender, birthday, status, vip_status, inviter_id, last_login_ip, last_login_time, register_ip, create_time) VALUES
(1,  '小明',         NULL, '13800000001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, '1998-03-15', 0, 0, NULL, '192.168.1.101', '2026-05-30 08:12:33', '10.0.0.1',  '2025-06-01 10:00:00'),
(2,  '暖暖',         NULL, '13800000002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '2000-07-22', 0, 1, 1,    '192.168.1.102', '2026-05-30 09:00:00', '10.0.0.2',  '2025-06-05 14:30:00'),
(3,  '星辰大海',     NULL, '13800000003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, '1997-11-08', 0, 0, 1,    '192.168.1.103', '2026-05-29 22:15:00', '10.0.0.3',  '2025-06-10 09:15:00'),
(4,  '向日葵',       NULL, '13800000004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '1999-05-30', 0, 2, 2,    '192.168.1.104', '2026-05-30 07:45:00', '10.0.0.4',  '2025-06-15 16:00:00'),
(5,  '追风少年',     NULL, '13800000005', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, '2001-01-01', 0, 0, NULL, '192.168.1.105', '2026-05-28 20:30:00', '10.0.0.5',  '2025-07-01 11:00:00'),
(6,  '月光宝盒',     NULL, '13800000006', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '2002-09-14', 0, 0, 3,    '192.168.1.106', '2026-05-30 06:30:00', '10.0.0.6',  '2025-07-10 08:45:00'),
(7,  '书虫',         NULL, '13800000007', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, '1996-12-25', 0, 1, 4,    '192.168.1.107', '2026-05-29 18:00:00', '10.0.0.7',  '2025-08-01 13:20:00'),
(8,  '小鱼儿',       NULL, '13800000008', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '2003-04-18', 0, 0, 2,    '192.168.1.108', '2026-05-29 21:10:00', '10.0.0.8',  '2025-08-15 09:30:00'),
(9,  '森林行者',     NULL, '13800000009', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, '1995-08-03', 0, 0, NULL, '192.168.1.109', '2026-05-27 17:45:00', '10.0.0.9',  '2025-09-01 14:00:00'),
(10, '糖糖',         NULL, '13800000010', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '2001-06-11', 0, 0, 5,    '192.168.1.110', '2026-05-30 10:00:00', '10.0.0.10', '2025-09-10 10:30:00'),
(11, '高山流水',     NULL, '13800000011', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, '1994-02-28', 0, 0, 3,    '192.168.1.111', '2026-05-26 15:20:00', '10.0.0.11', '2025-10-01 11:15:00'),
(12, '棉花糖',       NULL, '13800000012', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '2002-10-05', 0, 0, 6,    '192.168.1.112', '2026-05-28 23:00:00', '10.0.0.12', '2025-10-15 16:45:00'),
(13, '清风徐来',     NULL, '13800000013', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, '1998-07-19', 0, 0, NULL, '192.168.1.113', '2026-05-25 20:00:00', '10.0.0.13', '2025-11-01 08:00:00'),
(14, '小确杏',       NULL, '13800000014', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '2000-12-31', 0, 0, 7,    '192.168.1.114', '2026-05-28 12:30:00', '10.0.0.14', '2025-11-10 14:20:00'),
(15, '夜空中最亮的星', NULL, '13800000015', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, '1997-05-20', 0, 1, 4, '192.168.1.115', '2026-05-29 06:15:00', '10.0.0.15', '2025-12-01 09:30:00'),
(16, '彩虹糖',       NULL, '13800000016', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '2003-08-14', 0, 0, 8,    '192.168.1.116', '2026-05-27 19:40:00', '10.0.0.16', '2025-12-15 11:00:00'),
(17, '剑心',         NULL, '13800000017', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, '1993-09-09', 0, 0, NULL, '192.168.1.117', '2026-05-20 14:00:00', '10.0.0.17', '2026-01-05 15:00:00'),
(18, '草莓味的风',   NULL, '13800000018', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '2001-03-27', 0, 0, 9,    '192.168.1.118', '2026-05-26 08:50:00', '10.0.0.18', '2026-01-20 10:45:00'),
(19, '禅意',         NULL, '13800000019', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, '1992-06-06', 0, 2, 4,    '192.168.1.119', '2026-05-29 23:30:00', '10.0.0.19', '2026-02-14 08:30:00'),
(20, '小星星',       NULL, '13800000020', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '2004-11-11', 1, 0, 10,   '192.168.1.120', '2026-04-15 16:10:00', '10.0.0.20', '2026-03-01 12:00:00');

-- -------------------- 2. 用户设置 --------------------
INSERT INTO user_settings (user_id, default_focus_time, default_break_time, dark_mode, notification, volume) VALUES
(1,  25, 5,  1, 1, 70), (2,  30, 7,  2, 1, 80), (3,  25, 5,  1, 1, 50),
(4,  45, 10, 1, 0, 65), (5,  25, 5,  0, 1, 75), (6,  20, 5,  1, 1, 60),
(7,  35, 8,  2, 0, 40), (8,  25, 5,  1, 1, 85), (9,  50, 10, 1, 0, 55),
(10, 25, 5,  2, 1, 70), (11, 30, 5,  1, 1, 45), (12, 25, 5,  1, 1, 90),
(13, 40, 10, 1, 0, 60), (14, 25, 5,  0, 1, 75), (15, 25, 5,  1, 1, 65),
(16, 20, 5,  1, 1, 80), (17, 30, 5,  1, 0, 50), (18, 25, 5,  2, 1, 70),
(19, 60, 15, 1, 1, 30), (20, 25, 5,  1, 1, 60);

-- -------------------- 3. 登录设备 --------------------
INSERT INTO user_device (user_id, device_id, device_name, device_type, login_time, last_active_time, ip_address) VALUES
(1,  'DEVICE-A001',   'iPhone 15 Pro',   'iOS',     '2026-05-30 08:12:33', '2026-05-30 10:30:00', '192.168.1.101'),
(1,  'DEVICE-A001-W', 'MacBook Pro',     'Web',     '2026-05-28 19:00:00', '2026-05-28 22:00:00', '192.168.1.101'),
(2,  'DEVICE-A002',   'Xiaomi 14',       'Android', '2026-05-30 09:00:00', '2026-05-30 11:00:00', '192.168.1.102'),
(3,  'DEVICE-A003',   'iPhone 14',       'iOS',     '2026-05-29 22:15:00', '2026-05-30 00:30:00', '192.168.1.103'),
(4,  'DEVICE-A004',   'Samsung S24',     'Android', '2026-05-30 07:45:00', '2026-05-30 09:15:00', '192.168.1.104'),
(4,  'DEVICE-A004-T', 'iPad Air',        'iOS',     '2026-05-25 20:00:00', '2026-05-25 22:00:00', '192.168.1.104'),
(5,  'DEVICE-A005',   'OPPO Find X7',    'Android', '2026-05-28 20:30:00', '2026-05-28 21:45:00', '192.168.1.105'),
(6,  'DEVICE-A006',   'Honor Magic6',    'Android', '2026-05-30 06:30:00', '2026-05-30 07:15:00', '192.168.1.106'),
(7,  'DEVICE-A007',   'iPhone 15',       'iOS',     '2026-05-29 18:00:00', '2026-05-29 20:00:00', '192.168.1.107'),
(8,  'DEVICE-A008',   'Redmi K70',       'Android', '2026-05-29 21:10:00', '2026-05-29 22:00:00', '192.168.1.108'),
(9,  'DEVICE-A009',   'Huawei Mate60',   'Android', '2026-05-27 17:45:00', '2026-05-27 19:00:00', '192.168.1.109'),
(10, 'DEVICE-A010',   'iPhone 13 mini',  'iOS',     '2026-05-30 10:00:00', '2026-05-30 10:30:00', '192.168.1.110'),
(11, 'DEVICE-A011',   'vivo X100',       'Android', '2026-05-26 15:20:00', '2026-05-26 16:00:00', '192.168.1.111'),
(12, 'DEVICE-A012',   'iPhone 14 Pro',   'iOS',     '2026-05-28 23:00:00', '2026-05-29 00:30:00', '192.168.1.112'),
(13, 'DEVICE-A013',   'OnePlus 12',      'Android', '2026-05-25 20:00:00', '2026-05-25 21:00:00', '192.168.1.113'),
(14, 'DEVICE-A014',   'iPhone 15',       'iOS',     '2026-05-28 12:30:00', '2026-05-28 13:30:00', '192.168.1.114'),
(15, 'DEVICE-A015',   'Huawei P60',      'Android', '2026-05-29 06:15:00', '2026-05-29 08:00:00', '192.168.1.115'),
(16, 'DEVICE-A016',   'Samsung A54',     'Android', '2026-05-27 19:40:00', '2026-05-27 20:30:00', '192.168.1.116'),
(17, 'DEVICE-A017',   'iPhone SE',       'iOS',     '2026-05-20 14:00:00', '2026-05-20 15:00:00', '192.168.1.117'),
(18, 'DEVICE-A018',   'Xiaomi 13',       'Android', '2026-05-26 08:50:00', '2026-05-26 09:30:00', '192.168.1.118'),
(19, 'DEVICE-A019',   'iPhone 16 Pro',   'iOS',     '2026-05-29 23:30:00', '2026-05-30 01:00:00', '192.168.1.119'),
(20, 'DEVICE-A020',   'OPPO A3 Pro',     'Android', '2026-04-15 16:10:00', '2026-04-15 17:00:00', '192.168.1.120');

-- -------------------- 4. 专注记录 --------------------
INSERT INTO focus_record (user_id, start_time, end_time, duration, status, mode, tag, interrupt_count, note) VALUES
-- 用户1 小明
(1, '2026-05-30 09:00:00', '2026-05-30 09:25:00', 25,  1, 'tomato', '学习', 0, '背英语单词'),
(1, '2026-05-30 10:00:00', '2026-05-30 10:50:00', 50,  1, 'tomato', '学习', 0, '阅读英语文章'),
(1, '2026-05-29 20:00:00', '2026-05-29 20:50:00', 50,  1, 'tomato', '阅读', 1, '读《百年孤独》'),
(1, '2026-05-28 14:00:00', '2026-05-28 14:25:00', 25,  1, 'tomato', '工作', 0, NULL),
(1, '2026-05-27 09:00:00', '2026-05-27 09:25:00', 25,  1, 'tomato', '学习', 0, '数学题'),
(1, '2026-05-26 20:00:00', '2026-05-26 21:00:00', 60,  1, 'deep',   '学习', 0, '写论文'),
-- 用户2 暖暖
(2, '2026-05-30 07:00:00', '2026-05-30 08:30:00', 90,  1, 'deep',   '冥想', 0, '晨间冥想'),
(2, '2026-05-29 19:00:00', '2026-05-29 19:25:00', 25,  1, 'tomato', '写作', 0, '写日记'),
(2, '2026-05-28 10:00:00', '2026-05-28 11:30:00', 90,  1, 'deep',   '学习', 0, '备考CPA'),
(2, '2026-05-27 07:00:00', '2026-05-27 08:00:00', 60,  1, 'deep',   '冥想', 0, '正念冥想'),
(2, '2026-05-26 19:00:00', '2026-05-26 19:25:00', 25,  1, 'tomato', '阅读', 0, '读心理学书籍'),
-- 用户3 星辰大海
(3, '2026-05-29 21:00:00', '2026-05-29 21:25:00', 25,  2, 'tomato', '工作', 3, '被电话打断了'),
(3, '2026-05-28 15:00:00', '2026-05-28 16:00:00', 60,  1, 'free',   '运动', 0, '做瑜伽'),
(3, '2026-05-27 20:00:00', '2026-05-27 20:25:00', 25,  1, 'tomato', '学习', 1, '看编程视频'),
-- 用户4 向日葵
(4, '2026-05-30 06:00:00', '2026-05-30 07:30:00', 90,  1, 'deep',   '学习', 0, '学日语第30天！'),
(4, '2026-05-29 14:00:00', '2026-05-29 15:00:00', 60,  1, 'free',   '阅读', 0, '技术文档'),
(4, '2026-05-28 06:00:00', '2026-05-28 07:30:00', 90,  1, 'deep',   '学习', 0, '日语听力训练'),
(4, '2026-05-27 06:00:00', '2026-05-27 07:00:00', 60,  1, 'deep',   '学习', 0, '日语语法'),
(4, '2026-05-26 14:00:00', '2026-05-26 15:30:00', 90,  1, 'deep',   '阅读', 0, '《代码大全》'),
-- 用户5 追风少年
(5, '2026-05-28 19:00:00', '2026-05-28 19:25:00', 25,  1, 'tomato', '工作', 1, NULL),
(5, '2026-05-27 09:00:00', '2026-05-27 09:25:00', 25,  1, 'tomato', '学习', 0, 'Python课程'),
(5, '2026-05-26 19:00:00', '2026-05-26 20:00:00', 60,  1, 'free',   '学习', 0, '算法练习'),
-- 用户6 月光宝盒
(6, '2026-05-30 05:30:00', '2026-05-30 06:20:00', 50,  1, 'tomato', '运动', 0, '晨跑+拉伸'),
(6, '2026-05-29 22:00:00', '2026-05-29 22:25:00', 25,  1, 'tomato', '阅读', 0, '小说'),
(6, '2026-05-28 05:30:00', '2026-05-28 06:30:00', 60,  1, 'free',   '运动', 0, '游泳'),
-- 用户7 书虫
(7, '2026-05-29 17:00:00', '2026-05-29 18:00:00', 60,  1, 'free',   '冥想', 0, '减压'),
(7, '2026-05-28 17:00:00', '2026-05-28 18:00:00', 60,  1, 'free',   '阅读', 0, '读《人类简史》'),
(7, '2026-05-27 19:00:00', '2026-05-27 19:35:00', 35,  1, 'tomato', '阅读', 0, '读《三体》'),
-- 用户8 小鱼儿
(8, '2026-05-29 20:00:00', '2026-05-29 20:25:00', 25,  2, 'tomato', '学习', 2, '走神了'),
(8, '2026-05-28 15:00:00', '2026-05-28 15:50:00', 50,  1, 'tomato', '学习', 0, '数学作业'),
-- 用户9 森林行者
(9, '2026-05-27 16:00:00', '2026-05-27 18:00:00', 120, 1, 'deep',   '工作', 0, '项目方案'),
(9, '2026-05-26 10:00:00', '2026-05-26 12:00:00', 120, 1, 'deep',   '工作', 1, '写代码'),
-- 用户10 糖糖
(10, '2026-05-30 08:00:00', '2026-05-30 08:25:00', 25,  1, 'tomato', '学习', 0, '单词打卡'),
(10, '2026-05-29 12:00:00', '2026-05-29 12:50:00', 50,  1, 'tomato', '工作', 1, '摸鱼了一会儿'),
(10, '2026-05-28 08:00:00', '2026-05-28 08:25:00', 25,  1, 'tomato', '学习', 0, '背古诗');

-- -------------------- 5. 专注设置 --------------------
INSERT INTO focus_settings (user_id, strict_mode, app_block, notification_block, ai_encouragement, encouragement_interval) VALUES
(1,  0, 1, 1, 1, 30), (2,  1, 1, 1, 1, 45), (3,  0, 0, 1, 0, 30),
(4,  1, 1, 1, 1, 60), (5,  0, 0, 0, 1, 30), (6,  0, 1, 1, 1, 25),
(7,  1, 1, 1, 0, 30), (8,  0, 0, 1, 1, 30), (9,  1, 1, 1, 1, 60),
(10, 0, 0, 1, 1, 30), (11, 0, 0, 1, 0, 30), (12, 0, 1, 1, 1, 30),
(13, 1, 1, 0, 0, 45), (14, 0, 0, 1, 1, 30), (15, 1, 1, 1, 1, 30),
(16, 0, 0, 1, 1, 30), (17, 1, 0, 0, 0, 60), (18, 0, 1, 1, 1, 30),
(19, 1, 1, 1, 1, 60), (20, 0, 0, 1, 1, 30);

-- -------------------- 6. 睡眠记录 --------------------
INSERT INTO sleep_record (user_id, start_time, end_time, duration, sleep_score, deep_sleep_minutes, light_sleep_minutes, rem_sleep_minutes, interrupt_count, audio_mix_id) VALUES
(1,  '2026-05-29 23:30:00', '2026-05-30 07:00:00', 450, 82, 180, 150, 120, 1, 1),
(1,  '2026-05-28 23:00:00', '2026-05-29 06:30:00', 390, 65, 120, 180,  90, 3, NULL),
(1,  '2026-05-27 23:15:00', '2026-05-28 07:15:00', 480, 78, 170, 190, 120, 1, 1),
(1,  '2026-05-26 23:00:00', '2026-05-27 06:45:00', 465, 75, 160, 185, 120, 2, NULL),
(2,  '2026-05-29 22:00:00', '2026-05-30 06:30:00', 510, 91, 220, 170, 120, 0, 2),
(2,  '2026-05-28 23:00:00', '2026-05-29 07:00:00', 480, 78, 160, 200, 120, 2, 1),
(2,  '2026-05-27 22:30:00', '2026-05-28 06:30:00', 480, 85, 190, 170, 120, 1, 2),
(3,  '2026-05-29 01:00:00', '2026-05-29 08:00:00', 420, 55, 100, 220, 100, 4, NULL),
(3,  '2026-05-28 00:30:00', '2026-05-28 07:30:00', 420, 50,  90, 230, 100, 5, NULL),
(4,  '2026-05-29 22:30:00', '2026-05-30 06:00:00', 450, 88, 200, 150, 100, 0, 1),
(4,  '2026-05-28 23:00:00', '2026-05-29 06:30:00', 450, 85, 190, 160, 100, 1, 1),
(4,  '2026-05-27 22:00:00', '2026-05-28 06:00:00', 480, 90, 210, 160, 110, 0, 2),
(5,  '2026-05-28 00:00:00', '2026-05-28 07:30:00', 450, 72, 150, 200, 100, 2, NULL),
(5,  '2026-05-27 00:30:00', '2026-05-27 07:00:00', 390, 60, 110, 200,  80, 3, NULL),
(6,  '2026-05-29 22:00:00', '2026-05-30 05:00:00', 420, 80, 170, 150, 100, 1, 1),
(6,  '2026-05-28 22:30:00', '2026-05-29 05:30:00', 420, 76, 160, 160, 100, 2, NULL),
(7,  '2026-05-29 23:00:00', '2026-05-30 07:00:00', 480, 86, 200, 170, 110, 0, 3),
(7,  '2026-05-28 23:30:00', '2026-05-29 07:30:00', 480, 82, 180, 180, 120, 1, 1),
(8,  '2026-05-29 23:30:00', '2026-05-30 07:00:00', 450, 70, 140, 200, 110, 3, NULL),
(9,  '2026-05-27 23:00:00', '2026-05-28 06:30:00', 450, 80, 180, 160, 110, 1, 1),
(10, '2026-05-29 23:00:00', '2026-05-30 07:30:00', 510, 90, 210, 180, 120, 0, 2),
(10, '2026-05-28 23:30:00', '2026-05-29 07:00:00', 450, 80, 170, 170, 110, 1, 1),
(11, '2026-05-26 23:00:00', '2026-05-27 06:00:00', 420, 68, 130, 200,  90, 2, NULL),
(12, '2026-05-28 00:00:00', '2026-05-28 07:30:00', 450, 72, 150, 190, 110, 2, NULL),
(13, '2026-05-25 23:30:00', '2026-05-26 06:30:00', 420, 66, 130, 200,  90, 3, NULL),
(14, '2026-05-28 23:00:00', '2026-05-29 07:00:00', 480, 77, 160, 200, 120, 1, 1),
(15, '2026-05-29 22:30:00', '2026-05-30 06:00:00', 450, 88, 195, 150, 105, 0, 1),
(15, '2026-05-28 23:00:00', '2026-05-29 06:30:00', 450, 84, 180, 165, 105, 1, 2),
(16, '2026-05-27 23:30:00', '2026-05-28 07:00:00', 450, 70, 140, 210, 100, 3, NULL),
(17, '2026-05-20 23:00:00', '2026-05-21 06:30:00', 450, 65, 120, 220, 110, 4, NULL),
(18, '2026-05-26 00:00:00', '2026-05-26 07:00:00', 420, 73, 150, 180,  90, 2, NULL),
(19, '2026-05-29 22:00:00', '2026-05-30 05:30:00', 450, 92, 210, 140, 100, 0, 3),
(19, '2026-05-28 22:30:00', '2026-05-29 05:30:00', 420, 89, 195, 130,  95, 0, 1),
(20, '2026-04-15 23:00:00', '2026-04-16 08:00:00', 540, 60, 120, 260, 160, 5, NULL);

-- -------------------- 7. 睡眠日记 --------------------
INSERT INTO sleep_diary (user_id, date, bedtime_activity, caffeine_intake, exercise, emotion, note) VALUES
(1,  '2026-05-29', '看了半小时书',   0, 1, '平静', '睡前泡了脚，睡得很香'),
(1,  '2026-05-28', '刷手机',         2, 0, '焦虑', '喝了咖啡失眠了'),
(1,  '2026-05-27', '听白噪音',       0, 1, '放松', '效果不错'),
(2,  '2026-05-29', '做瑜伽放松',     0, 2, '放松', '今天状态很好'),
(2,  '2026-05-28', '冥想15分钟',     0, 1, '平静', '睡前冥想很有效'),
(4,  '2026-05-29', '听了白噪音',     0, 1, '平静', '睡眠质量不错'),
(4,  '2026-05-28', '冥想15分钟',     0, 2, '愉悦', '持续保持好习惯'),
(4,  '2026-05-27', '看了会儿书',     0, 1, '平静', '22点入睡很完美'),
(6,  '2026-05-29', '听了海浪声',     0, 2, '轻松', '运动完入睡很快'),
(6,  '2026-05-28', '拉伸运动',       0, 2, '轻松', NULL),
(7,  '2026-05-29', '阅读30分钟',     0, 0, '平静', '读书助眠效果好'),
(10, '2026-05-29', '喝了杯热牛奶',   1, 1, '平静', '睡够7.5小时'),
(10, '2026-05-28', '听轻音乐',       0, 0, '放松', '梦到了好玩的'),
(15, '2026-05-29', '冥想20分钟',     0, 1, '平静', '早睡早起精神好'),
(19, '2026-05-29', '打坐30分钟',     0, 1, '宁静', '身心合一的感觉'),
(19, '2026-05-28', '打坐30分钟',     0, 1, '宁静', '一觉到天亮');


-- -------------------- 8. 会员套餐 --------------------
INSERT INTO vip_package (id, name, description, price, original_price, days, type, status, sort_order) VALUES
(1, '月度会员', '30天VIP体验，解锁所有基础功能',         29.90,  49.90,   30,    'month',     1, 1),
(2, '季度会员', '90天进阶体验，享8折优惠',               69.90,  149.70,  90,    'quarter',   1, 2),
(3, '年度会员', '365天尊享体验，送30天，限时5折',        199.00, 598.00,  395,   'year',      1, 3),
(4, '终身会员', '一次付费，永久使用，含未来所有功能',    999.00, 2999.00, 36500, 'lifetime',  1, 4);

-- -------------------- 9. 用户会员 --------------------
INSERT INTO user_vip (user_id, package_id, package_name, start_time, end_time, auto_renew) VALUES
(2,  3, '年度会员', '2025-12-01 00:00:00', '2027-01-01 00:00:00', 1),
(4,  4, '终身会员', '2026-01-15 00:00:00', '2126-01-15 00:00:00', 0),
(7,  1, '月度会员', '2026-05-10 00:00:00', '2026-06-10 00:00:00', 1),
(15, 2, '季度会员', '2026-04-01 00:00:00', '2026-07-01 00:00:00', 1),
(19, 4, '终身会员', '2026-03-01 00:00:00', '2126-03-01 00:00:00', 0);

-- -------------------- 10. 会员赠送规则 --------------------
INSERT INTO vip_gift_rule (id, rule_name, rule_type, condition_value, vip_days, auto_grant, total_limit, granted_count, daily_limit, status, description, sort_order) VALUES
(1, '新用户注册送3天VIP',     'register', '{"min_days":0}',           3,   1, 0,    20,  100, 1, '新用户注册即送3天VIP体验',       1),
(2, '邀请好友注册送7天VIP',   'invite',   '{"need_purchase":true}',   7,   1, 0,    15,  50,  1, '邀请好友注册并购买，双方各得7天', 2),
(3, '连续专注7天送1天VIP',    'focus',    '{"streak_days":7}',        1,   1, 0,    8,   20,  1, '连续专注满7天自动赠送',          3),
(4, '连续专注30天送7天VIP',   'focus',    '{"streak_days":30}',       7,   1, 0,    3,   5,   1, '连续专注满30天赠送7天VIP',       4),
(5, '管理员手动赠送-补偿',    'manual',   NULL,                       0,   0, 0,    5,   0,   1, 'VIP天数0表示终身VIP',            5);

-- -------------------- 11. 会员赠送记录 --------------------
INSERT INTO vip_gift_record (rule_id, user_id, user_nickname, grant_type, vip_days, reason, expire_time, status, create_time) VALUES
(1, 1,  '小明',         'auto', 3,   '新用户注册赠送',           '2025-06-04 10:00:00', 1, '2025-06-01 10:00:00'),
(1, 2,  '暖暖',         'auto', 3,   '新用户注册赠送',           '2025-06-08 14:30:00', 1, '2025-06-05 14:30:00'),
(1, 5,  '追风少年',     'auto', 3,   '新用户注册赠送',           '2025-07-04 11:00:00', 1, '2025-07-01 11:00:00'),
(1, 9,  '森林行者',     'auto', 3,   '新用户注册赠送',           '2025-09-04 14:00:00', 1, '2025-09-01 14:00:00'),
(1, 13, '清风徐来',     'auto', 3,   '新用户注册赠送',           '2025-11-04 08:00:00', 1, '2025-11-01 08:00:00'),
(2, 1,  '小明',         'auto', 7,   '邀请暖暖注册并购买',       '2025-06-12 10:00:00', 1, '2025-06-05 14:35:00'),
(2, 3,  '星辰大海',     'auto', 7,   '被小明邀请注册奖励',       '2025-06-17 09:15:00', 1, '2025-06-10 09:15:00'),
(3, 4,  '向日葵',       'auto', 1,   '连续专注7天奖励',          '2025-08-22 06:00:00', 1, '2025-08-15 06:00:00'),
(3, 2,  '暖暖',         'auto', 1,   '连续专注7天奖励',          '2025-09-20 07:00:00', 1, '2025-09-13 07:00:00'),
(4, 4,  '向日葵',       'auto', 7,   '连续专注30天奖励！',       '2026-01-01 06:00:00', 1, '2025-12-25 06:00:00'),
(5, 19, '禅意',         'manual', 0, '终身VIP，核心用户奖励',    '2126-03-01 00:00:00', 1, '2026-03-01 08:00:00');

-- -------------------- 12. 支付订单 --------------------
INSERT INTO pay_order (order_no, user_id, package_id, package_name, amount, pay_amount, discount_amount, coupon_id, pay_type, order_status, pay_time, expire_time, transaction_id, distributor_id, commission_amount, activity_id, create_time) VALUES
('ORD20251201001', 2,  3, '年度会员', 199.00, 199.00,   0.00, 0, 'wechat', 1, '2025-12-01 10:30:00', '2025-12-02 10:30:00', 'TXN-WX-202512011030', 1, 59.70, 0, '2025-12-01 10:30:00'),
('ORD20260115002', 4,  4, '终身会员', 999.00, 899.00, 100.00, 0, 'alipay', 1, '2026-01-15 14:20:00', '2026-01-16 14:20:00', 'TXN-ALI-202601151420', 2, 269.70, 0, '2026-01-15 14:20:00'),
('ORD20260401003', 15, 2, '季度会员',  69.90,  69.90,   0.00, 0, 'wechat', 1, '2026-04-01 09:00:00', '2026-04-02 09:00:00', 'TXN-WX-202604010900', 1, 20.97, 0, '2026-04-01 09:00:00'),
('ORD20260510004', 7,  1, '月度会员',  29.90,  29.90,   0.00, 0, 'alipay', 1, '2026-05-10 08:15:00', '2026-05-11 08:15:00', 'TXN-ALI-202605100815', 2,  8.97, 0, '2026-05-10 08:15:00'),
('ORD20260520005', 3,  1, '月度会员',  29.90,   0.00,   0.00, 0, 'wechat', 0, NULL,                  '2026-05-21 08:00:00', NULL,                   0,  0.00, 0, '2026-05-20 08:00:00'),
('ORD20260525006', 1,  2, '季度会员',  69.90,  69.90,   0.00, 0, 'alipay', 1, '2026-05-25 18:30:00', '2026-05-26 18:30:00', 'TXN-ALI-202605251830', 0,  0.00, 0, '2026-05-25 18:30:00'),
('ORD20260528007', 5,  1, '月度会员',  29.90,  29.90,   0.00, 0, 'wechat', 3, '2026-05-28 12:00:00', '2026-05-29 12:00:00', 'TXN-WX-202605281200',  0,  0.00, 0, '2026-05-28 12:00:00'),
('ORD20260530008', 10, 3, '年度会员', 199.00, 199.00,   0.00, 0, 'alipay', 1, '2026-05-30 08:00:00', '2026-05-31 08:00:00', 'TXN-ALI-202605300800', 4, 59.70, 1, '2026-05-30 08:00:00'),
('ORD20260601009', 6,  1, '月度会员',  29.90,  19.90,  10.00, 1, 'wechat', 1, '2026-06-01 06:30:00', '2026-06-02 06:30:00', 'TXN-WX-202606010630',  0,  0.00, 2, '2026-06-01 06:30:00'),
('ORD20260601010', 8,  2, '季度会员',  69.90,  55.92,  13.98, 3, 'alipay', 1, '2026-06-01 09:00:00', '2026-06-02 09:00:00', 'TXN-ALI-202606010900', 0,  0.00, 2, '2026-06-01 09:00:00'),
('ORD20260602011', 14, 1, '月度会员',  29.90,  29.90,   0.00, 0, 'wechat', 1, '2026-06-02 14:00:00', '2026-06-03 14:00:00', 'TXN-WX-202606021400',  0,  0.00, 0, '2026-06-02 14:00:00'),
('ORD20260602012', 3,  3, '年度会员', 199.00, 199.00,   0.00, 0, 'alipay', 1, '2026-06-02 22:00:00', '2026-06-03 22:00:00', 'TXN-ALI-202606022200', 1, 59.70, 0, '2026-06-02 22:00:00');

-- -------------------- 13. 退款记录 --------------------
INSERT INTO order_refund (order_no, user_id, refund_amount, refund_reason, refund_status, audit_user_id, audit_time, audit_remark, create_time) VALUES
('ORD20260528007', 5, 29.90, '误操作，不需要了',     4, 1, '2026-05-28 14:00:00', '审核通过，已退款',           '2026-05-28 12:30:00'),
('ORD20260520005', 3,  0.00, '订单超时未支付自动关闭', 2, 1, '2026-05-21 09:00:00', '未支付订单无需退款',         '2026-05-21 08:30:00');

-- -------------------- 14. 支付配置 --------------------
INSERT INTO pay_config (pay_type, app_id, merchant_id, private_key, public_key, notify_url, status) VALUES
('alipay', '2021001000000001', '2088000000000001', '---PRIVATE KEY---', '---PUBLIC KEY---', 'https://api.xinling.com/pay/alipay/notify', 1),
('wechat', 'wx0000000000000001', '1900000001',     '---PRIVATE KEY---', '---PUBLIC KEY---', 'https://api.xinling.com/pay/wechat/notify', 1),
('apple',  'com.xinling.vip',   'APPLE-MERCHANT-001', '---PRIVATE KEY---', '---PUBLIC KEY---', 'https://api.xinling.com/pay/apple/notify', 0);

-- -------------------- 15. 交易记录 --------------------
INSERT INTO pay_transaction (order_no, transaction_id, pay_type, amount, status, pay_time) VALUES
('ORD20251201001', 'TXN-WX-202512011030',  'wechat', 199.00, 1, '2025-12-01 10:30:00'),
('ORD20260115002', 'TXN-ALI-202601151420', 'alipay', 899.00, 1, '2026-01-15 14:20:00'),
('ORD20260401003', 'TXN-WX-202604010900',  'wechat',  69.90, 1, '2026-04-01 09:00:00'),
('ORD20260510004', 'TXN-ALI-202605100815', 'alipay',  29.90, 1, '2026-05-10 08:15:00'),
('ORD20260525006', 'TXN-ALI-202605251830', 'alipay',  69.90, 1, '2026-05-25 18:30:00'),
('ORD20260528007', 'TXN-WX-202605281200',  'wechat',  29.90, 3, '2026-05-28 12:00:00'),
('ORD20260530008', 'TXN-ALI-202605300800', 'alipay', 199.00, 1, '2026-05-30 08:00:00'),
('ORD20260601009', 'TXN-WX-202606010630',  'wechat',  19.90, 1, '2026-06-01 06:30:00'),
('ORD20260601010', 'TXN-ALI-202606010900', 'alipay',  55.92, 1, '2026-06-01 09:00:00'),
('ORD20260602011', 'TXN-WX-202606021400',  'wechat',  29.90, 1, '2026-06-02 14:00:00'),
('ORD20260602012', 'TXN-ALI-202606022200', 'alipay', 199.00, 1, '2026-06-02 22:00:00');

-- -------------------- 16. 自动续费 --------------------
INSERT INTO auto_renew (user_id, package_id, pay_type, agreement_id, next_pay_time, status) VALUES
(2,  3, 'wechat', 'AGR-WX-20251201-001', '2027-01-01 00:00:00', 1),
(7,  1, 'alipay', 'AGR-ALI-20260510-001', '2026-06-10 00:00:00', 1),
(15, 2, 'wechat', 'AGR-WX-20260401-001', '2026-07-01 00:00:00', 1);

-- -------------------- 17. 优惠券 --------------------
INSERT INTO coupon (id, name, type, value, condition_amount, start_time, end_time, total_count, used_count, status) VALUES
(1, '新用户立减券',       'reduction',      10.00,  0.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1000, 156, 1),
(2, '满50减10',           'full_reduction', 10.00, 50.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59',  500,  89, 1),
(3, '会员8折券',          'discount',        0.80,  0.00, '2026-03-01 00:00:00', '2026-09-30 23:59:59',  200,  32, 1),
(4, '周年庆满100减30',    'full_reduction', 30.00, 100.00, '2026-06-01 00:00:00', '2026-06-30 23:59:59',  300,  45, 1),
(5, '暑期特惠9折',        'discount',        0.90,  0.00, '2026-07-01 00:00:00', '2026-08-31 23:59:59',  500,   0, 1);

-- -------------------- 18. 用户优惠券 --------------------
INSERT INTO user_coupon (user_id, coupon_id, order_no, get_time, use_time, status) VALUES
(1,  2, NULL,                  '2026-05-20 10:00:00', NULL,                  0),
(1,  4, NULL,                  '2026-06-01 00:00:00', NULL,                  0),
(2,  3, 'ORD20251201001',      '2026-03-01 10:00:00', '2026-04-01 09:00:00', 1),
(3,  1, NULL,                  '2026-05-10 08:00:00', NULL,                  0),
(4,  4, NULL,                  '2026-06-01 00:00:00', NULL,                  0),
(5,  1, NULL,                  '2026-05-28 12:00:00', NULL,                  0),
(6,  1, 'ORD20260601009',      '2026-06-01 06:00:00', '2026-06-01 06:30:00', 1),
(8,  3, 'ORD20260601010',      '2026-06-01 08:30:00', '2026-06-01 09:00:00', 1),
(10, 2, NULL,                  '2026-05-30 08:00:00', NULL,                  0),
(15, 3, NULL,                  '2026-04-01 08:00:00', NULL,                  2),
(19, 4, NULL,                  '2026-06-01 00:00:00', NULL,                  0);


-- -------------------- 19. 分销员 --------------------
INSERT INTO distributor (id, user_id, level, real_name, phone, alipay_account, wechat_account, total_commission, available_commission, frozen_commission, total_withdraw, total_fans, total_orders, status, apply_time, audit_time) VALUES
(1, 2,  3, '王暖暖', '13800000002', 'wangnn@alipay.com',  'wangnn_wx',  5680.00, 1280.00, 400.00, 4000.00, 28, 15, 1, '2025-08-01 10:00:00', '2025-08-02 14:00:00'),
(2, 4,  2, '李向葵', '13800000004', 'lixk@alipay.com',    'lixk_wx',    3200.00,  800.00, 200.00, 2200.00, 18, 10, 1, '2025-09-10 14:00:00', '2025-09-11 09:00:00'),
(3, 7,  2, '张书虫', '13800000007', 'zhangsc@alipay.com', 'zhangsc_wx', 1800.00,  600.00, 100.00, 1100.00, 12,  8, 1, '2025-10-05 11:00:00', '2025-10-05 16:00:00'),
(4, 15, 1, '刘星',   '13800000015', 'liuxing@alipay.com', 'liuxing_wx',  450.00,  200.00,  50.00,  200.00,  5,  3, 1, '2026-01-10 09:00:00', '2026-01-10 15:00:00'),
(5, 19, 1, '陈禅',   '13800000019', 'chenchan@alipay.com','chenchan_wx', 280.00,  150.00,  30.00,  100.00,  3,  2, 1, '2026-03-01 08:30:00', '2026-03-01 14:00:00');

-- -------------------- 20. 分销关系 --------------------
INSERT INTO distribution_relation (user_id, parent_id, grandparent_id, bind_time) VALUES
(3,  1, 0, '2025-10-01 10:00:00'),
(6,  1, 0, '2025-11-05 14:00:00'),
(8,  2, 1, '2025-12-10 16:00:00'),
(10, 1, 0, '2026-01-15 11:00:00'),
(12, 2, 1, '2026-02-20 09:00:00'),
(14, 3, 1, '2026-03-10 15:00:00'),
(16, 2, 1, '2026-04-05 10:00:00'),
(18, 4, 2, '2026-05-01 08:00:00'),
(20, 5, 3, '2026-05-20 14:00:00');

-- -------------------- 21. 佣金记录 --------------------
INSERT INTO commission_record (distributor_id, order_no, user_id, type, amount, order_amount, rate, status, settle_time, create_time) VALUES
(1, 'ORD20251201001', 2,  1, 59.70, 199.00, 30.00, 1, '2025-12-08 00:00:00', '2025-12-01 10:30:00'),
(1, 'ORD20260115002', 4,  2, 89.90, 899.00, 10.00, 1, '2026-01-22 00:00:00', '2026-01-15 14:20:00'),
(2, 'ORD20260115002', 4,  1, 269.70, 899.00, 30.00, 1, '2026-01-22 00:00:00', '2026-01-15 14:20:00'),
(1, 'ORD20260401003', 15, 1, 20.97,  69.90, 30.00, 1, '2026-04-08 00:00:00', '2026-04-01 09:00:00'),
(2, 'ORD20260510004', 7,  1,  8.97,  29.90, 30.00, 1, '2026-05-17 00:00:00', '2026-05-10 08:15:00'),
(1, 'ORD20260525006', 1,  1, 20.97,  69.90, 30.00, 0, NULL,                  '2026-05-25 18:30:00'),
(4, 'ORD20260530008', 10, 1, 59.70, 199.00, 30.00, 0, NULL,                  '2026-05-30 08:00:00'),
(1, 'ORD20260602012', 3,  1, 59.70, 199.00, 30.00, 0, NULL,                  '2026-06-02 22:00:00'),
(3, 'ORD20260601009', 6,  1,  5.97,  19.90, 30.00, 0, NULL,                  '2026-06-01 06:30:00');

-- -------------------- 22. 提现申请 --------------------
INSERT INTO withdraw_apply (distributor_id, amount, fee, actual_amount, pay_type, account, real_name, status, audit_user_id, audit_time, audit_remark, pay_time, transaction_id, create_time) VALUES
(1, 1000.00, 10.00,  990.00, 'alipay', 'wangnn@alipay.com',  '王暖暖', 4, 1, '2025-12-15 10:00:00', '审核通过', '2025-12-15 14:00:00', 'WF-20251215-001', '2025-12-14 09:00:00'),
(1, 2000.00, 20.00, 1980.00, 'alipay', 'wangnn@alipay.com',  '王暖暖', 4, 1, '2026-02-01 10:00:00', '审核通过', '2026-02-01 16:00:00', 'WF-20260201-001', '2026-01-30 11:00:00'),
(1, 1000.00, 10.00,  990.00, 'alipay', 'wangnn@alipay.com',  '王暖暖', 4, 1, '2026-03-20 09:00:00', '审核通过', '2026-03-20 15:00:00', 'WF-20260320-001', '2026-03-18 08:00:00'),
(2, 1000.00, 10.00,  990.00, 'alipay', 'lixk@alipay.com',    '李向葵', 4, 1, '2026-02-15 14:00:00', '审核通过', '2026-02-15 17:00:00', 'WF-20260215-001', '2026-02-13 10:00:00'),
(2, 1200.00, 12.00, 1188.00, 'alipay', 'lixk@alipay.com',    '李向葵', 1, 1, '2026-05-29 10:00:00', '审核通过，打款中', NULL, NULL, '2026-05-28 09:00:00'),
(3,  500.00,  5.00,  495.00, 'alipay', 'zhangsc@alipay.com', '张书虫', 4, 1, '2026-04-10 11:00:00', '审核通过', '2026-04-10 16:00:00', 'WF-20260410-001', '2026-04-08 14:00:00'),
(4,  200.00,  2.00,  198.00, 'alipay', 'liuxing@alipay.com', '刘星',   0, NULL, NULL, NULL, NULL, NULL, '2026-05-15 10:00:00'),
(5,  100.00,  1.00,   99.00, 'wechat', 'chenchan_wx',        '陈禅',   4, 1, '2026-05-20 10:00:00', '审核通过', '2026-05-20 15:00:00', 'WF-20260520-001', '2026-05-18 09:00:00');

-- -------------------- 23. 分销设置 --------------------
INSERT INTO distribution_settings (id, first_level_rate, second_level_rate, team_reward_rate, min_withdraw_amount, withdraw_fee_rate, min_withdraw_fee, settle_days, auto_audit) VALUES
(1, 30.00, 10.00, 5.00, 10.00, 1.00, 1.00, 7, 1);

-- -------------------- 24. 活动 --------------------
INSERT INTO activity (id, title, description, type, rule, start_time, end_time, priority, status, join_count, order_count, total_amount) VALUES
(1, '新用户专享首单5折',   '首次购买VIP套餐享受5折优惠',     'new_user',       '{"discount":0.5,"max_discount":100}',                     '2026-01-01 00:00:00', '2026-12-31 23:59:59',  10, 1, 258, 200,  39800.00),
(2, '618年中大促',         '全场8折，买一送一',              'discount',       '{"discount":0.8}',                                          '2026-06-01 00:00:00', '2026-06-20 23:59:59', 100, 1, 120,  89,  17600.00),
(3, '邀请好友得30天VIP',   '每邀请一位好友注册并购买得30天VIP','distribution',  '{"reward_type":"vip","reward_days":30}',                    '2026-01-01 00:00:00', '2026-12-31 23:59:59',   5, 1,  45,   0,      0.00),
(4, '夏日清凉特惠',        '购买年度会员送30天',             'buy_one_get_one','{"target_package":"year","bonus_days":30}',                 '2026-07-01 00:00:00', '2026-08-31 23:59:59',   8, 0,   0,   0,      0.00),
(5, '五一劳动节特惠',      '全场7折限时3天',                 'discount',       '{"discount":0.7}',                                          '2026-04-30 00:00:00', '2026-05-03 23:59:59',  80, 2,  67,  52,  10300.00),
(6, '端午节特惠',          '全场满减，满100减20',            'discount',       '{"reduction":20,"condition":100}',                          '2026-05-28 00:00:00', '2026-06-03 23:59:59',  90, 1,  35,  28,   5560.00);

-- -------------------- 25. 用户活动参与 --------------------
INSERT INTO user_activity (user_id, activity_id, join_time, order_no) VALUES
(10, 1, '2026-05-30 08:00:00', 'ORD20260530008'),
(15, 2, '2026-06-01 00:00:00', NULL),
(2,  3, '2025-12-01 10:00:00', NULL),
(6,  2, '2026-06-01 06:30:00', 'ORD20260601009'),
(8,  2, '2026-06-01 09:00:00', 'ORD20260601010'),
(3,  1, '2026-06-02 22:00:00', 'ORD20260602012'),
(1,  6, '2026-05-30 10:00:00', NULL),
(4,  6, '2026-05-29 08:00:00', NULL);

-- -------------------- 26. 推送任务 --------------------
INSERT INTO push_task (title, content, target_type, target_ids, push_time, status, success_count, fail_count, create_time) VALUES
('618大促开始啦！',      '全场8折优惠，限时20天，错过等一年！',     'all',        NULL,                                        '2026-06-01 00:00:00', 2, 18, 2, '2026-05-31 10:00:00'),
('端午节活动上线',       '端午满减优惠，满100减20，快来参与！',     'all',        NULL,                                        '2026-05-28 09:00:00', 2, 20, 0, '2026-05-27 14:00:00'),
('VIP到期提醒',         '您的会员即将到期，续费享8折优惠',          'user_group', '[7]',                                      '2026-05-31 10:00:00', 2,  1, 0, '2026-05-30 00:00:00'),
('系统升级通知',         'APP将于6月5日凌晨2-4点进行维护升级',       'all',        NULL,                                        '2026-06-03 08:00:00', 0,  0, 0, '2026-06-02 16:00:00'),
('欢迎新用户',          '欢迎加入心聆，开始您的专注之旅！',         'specified',  '[10, 17, 20]',                             '2026-05-30 08:00:00', 2,  3, 0, '2026-05-30 07:50:00');

-- -------------------- 27. 白名单 --------------------
INSERT INTO whitelist (type, identifier, description, status, expire_time) VALUES
('user', '4',   '向日葵-终身VIP用户白名单',     1, '2126-01-15 00:00:00'),
('user', '19',  '禅意-终身VIP用户白名单',       1, '2126-03-01 00:00:00'),
('device', 'DEVICE-A004', '向日葵的三星手机',   1, '2027-01-01 00:00:00'),
('ip', '192.168.1.104', '向日葵常用IP',         1, '2027-01-01 00:00:00'),
('vip', 'lifetime', '终身VIP白名单通道',        1, NULL);

-- -------------------- 28. 成长体系 --------------------
INSERT INTO user_growth (user_id, level, exp, points, continuous_focus_days, continuous_sleep_days) VALUES
(1,  5,  2850,  320,  3,  5),
(2,  8,  7200, 1280, 12, 15),
(3,  3,  1200,  180,  2,  0),
(4, 10, 12000, 2500, 30, 20),
(5,  2,   600,   80,  1,  0),
(6,  4,  2100,  450,  5,  3),
(7,  6,  3800,  680,  0,  7),
(8,  1,   200,   30,  0,  0),
(9,  7,  5600,  920,  8,  4),
(10, 4,  1800,  260,  2,  1),
(11, 2,   700,  100,  0,  0),
(12, 1,   150,   20,  0,  0),
(13, 3,   900,  150,  0,  0),
(14, 2,   500,   60,  0,  0),
(15, 6,  4200,  780,  6, 10),
(16, 1,   100,   10,  0,  0),
(17, 2,   800,  120,  0,  0),
(18, 1,   300,   40,  0,  0),
(19, 9,  9500, 1800, 15, 25),
(20, 1,    50,    5,  0,  0);

-- -------------------- 29. 成就定义 --------------------
INSERT INTO achievement (id, name, description, icon, condition_type, condition_value, points_reward) VALUES
(1,  '初出茅庐',   '完成第一次专注',              '🏃', 'focus_count',       1,    10),
(2,  '专注达人',   '累计专注100次',               '🎯', 'focus_count',     100,   100),
(3,  '专注大师',   '累计专注500次',               '🏆', 'focus_count',     500,   500),
(4,  '夜猫子',     '连续睡眠7天',                 '🦉', 'sleep_streak',      7,    50),
(5,  '睡神',       '连续睡眠30天',                '😴', 'sleep_streak',     30,   300),
(6,  '早起鸟',     '连续7天在7点前起床',          '🐦', 'early_rise',        7,    80),
(7,  '社交达人',   '发布10条动态',                '📱', 'moment_count',     10,    50),
(8,  '人气王',     '获得100个赞',                 '⭐', 'like_received',   100,   100),
(9,  '学霸',       '累计专注学习1000分钟',        '📚', 'focus_minutes',  1000,   200),
(10, '冥想者',     '完成50次冥想',                '🧘', 'meditation_count',  50,  150),
(11, '挑战者',     '完成一个挑战',                '🥇', 'challenge_complete',  1,  100),
(12, '推销员',     '邀请5个好友注册',             '🤝', 'invite_count',       5,  200),
(13, 'VIP会员',    '开通VIP会员',                 '👑', 'become_vip',         1,  500),
(14, '百人粉丝',   '获得100个粉丝',               '👥', 'follower_count',   100,  300);

-- -------------------- 30. 用户成就 --------------------
INSERT INTO user_achievement (user_id, achievement_id, obtain_time) VALUES
(2,  1,  '2025-06-10 10:00:00'), (2,  2,  '2025-12-01 10:00:00'),
(2,  4,  '2025-09-15 08:00:00'), (2,  5,  '2026-01-10 08:00:00'),
(2,  13, '2025-12-01 10:00:00'), (2,  9,  '2026-03-15 10:00:00'),
(4,  1,  '2025-06-20 08:00:00'), (4,  2,  '2025-11-15 10:00:00'),
(4,  3,  '2026-05-01 10:00:00'), (4,  4,  '2025-08-01 08:00:00'),
(4,  5,  '2025-12-15 08:00:00'), (4,  6,  '2025-10-01 07:00:00'),
(4,  9,  '2025-12-01 10:00:00'), (4, 10,  '2026-03-01 10:00:00'),
(4, 13,  '2026-01-15 14:00:00'), (4,  7,  '2026-02-10 09:00:00'),
(1,  1,  '2025-06-05 10:00:00'), (1,  4,  '2026-04-10 08:00:00'),
(7,  1,  '2025-08-05 10:00:00'), (7,  4,  '2026-05-01 08:00:00'),
(7, 13,  '2026-05-10 08:00:00'), (7,  7,  '2026-04-20 10:00:00'),
(15, 1,  '2025-12-05 10:00:00'), (15, 4,  '2026-02-10 08:00:00'),
(15,13,  '2026-04-01 09:00:00'), (15, 6,  '2026-03-15 06:30:00'),
(19, 1,  '2026-02-18 10:00:00'), (19, 2,  '2026-05-10 10:00:00'),
(19, 4,  '2026-03-20 08:00:00'), (19, 9,  '2026-04-20 10:00:00'),
(19,13,  '2026-03-01 10:00:00'), (19, 5,  '2026-05-01 08:00:00'),
(6,  1,  '2025-07-15 08:00:00'), (6,  4,  '2026-01-20 08:00:00'),
(10, 1,  '2026-05-30 08:25:00');

-- -------------------- 31. 每日任务 --------------------
INSERT INTO daily_task (id, name, description, condition_type, condition_value, points_reward, icon) VALUES
(1, '专注25分钟',     '完成一次25分钟专注',       'focus_once',       1,  10, '🎯'),
(2, '专注3次',        '今日完成3次专注',          'focus_count',      3,  30, '🔥'),
(3, '记录睡眠',       '记录一次睡眠数据',         'sleep_record',     1,  10, '🌙'),
(4, '发布一条动态',   '在社区发布一条动态',       'moment_count',     1,  15, '📝'),
(5, '点赞3条动态',   '给3条动态点赞',            'like_count',       3,  10, '👍'),
(6, '收听音频30分钟', '收听音频满30分钟',         'audio_minutes',   30,  20, '🎵'),
(7, '分享一次',       '分享一条动态给好友',       'share_count',      1,  10, '🔗');

-- -------------------- 32. 用户任务 --------------------
INSERT INTO user_task (user_id, task_id, date, progress, status) VALUES
-- 用户2 暖暖 今日任务
(2, 1, '2026-05-30', 1, 2), (2, 2, '2026-05-30', 3, 2), (2, 3, '2026-05-30', 1, 1), (2, 6, '2026-05-30', 45, 2),
-- 用户4 向日葵 今日任务
(4, 1, '2026-05-30', 1, 2), (4, 2, '2026-05-30', 3, 2), (4, 3, '2026-05-30', 1, 1), (4, 4, '2026-05-30', 0, 0),
-- 用户1 小明 今日任务
(1, 1, '2026-05-30', 1, 2), (1, 2, '2026-05-30', 2, 0), (1, 3, '2026-05-30', 1, 2), (1, 6, '2026-05-30', 20, 0),
-- 用户7 书虫 今日任务
(7, 3, '2026-05-30', 1, 2), (7, 4, '2026-05-30', 1, 1), (7, 5, '2026-05-30', 2, 0),
-- 用户10 糖糖 今日任务
(10, 1, '2026-05-30', 1, 2), (10, 2, '2026-05-30', 1, 0), (10, 6, '2026-05-30', 10, 0),
-- 用户19 禅意 今日任务
(19, 1, '2026-05-30', 1, 2), (19, 2, '2026-05-30', 3, 2), (19, 3, '2026-05-30', 1, 2), (19, 6, '2026-05-30', 60, 2), (19, 7, '2026-05-30', 1, 1);

-- -------------------- 33. 积分商城商品 --------------------
INSERT INTO mall_goods (id, name, description, cover, type, price, stock, status) VALUES
(1, '7天VIP会员',       '兑换即得7天VIP体验',           '/img/goods/vip7.png',       'vip',     500,  999, 1),
(2, '30天VIP会员',      '兑换即得30天VIP',              '/img/goods/vip30.png',      'vip',    1500,  200, 1),
(3, '满20减5优惠券',    '购买VIP满20可用',              '/img/goods/coupon5.png',    'coupon',  200,  500, 1),
(4, '限定头像框-星空',  '社区专属星空头像框',           '/img/goods/frame_star.png', 'virtual', 300,  100, 1),
(5, '限定头像框-森林',  '社区专属森林头像框',           '/img/goods/frame_forest.png','virtual',300,  100, 1),
(6, '专属称号-早起鸟',  '社区展示专属称号',             '/img/goods/title_early.png','virtual', 800,   50, 1),
(7, '90天VIP会员',      '兑换即得90天VIP',              '/img/goods/vip90.png',      'vip',    3800,   80, 1),
(8, '满50减15优惠券',   '购买VIP满50可用',              '/img/goods/coupon15.png',   'coupon',  500,  300, 1);

-- -------------------- 34. 用户兑换记录 --------------------
INSERT INTO user_exchange (user_id, goods_id, points, create_time) VALUES
(2,  1, 500,  '2026-01-15 10:00:00'),
(2,  4, 300,  '2026-02-20 14:30:00'),
(4,  7, 3800, '2026-03-01 09:00:00'),
(4,  6, 800,  '2026-04-10 11:00:00'),
(7,  3, 200,  '2026-05-12 16:00:00'),
(15, 1, 500,  '2026-02-28 10:00:00'),
(19, 5, 300,  '2026-04-05 08:30:00'),
(19, 2, 1500, '2026-05-20 20:00:00'),
(1,  3, 200,  '2026-05-25 12:00:00'),
(10, 4, 300,  '2026-05-29 09:00:00');

-- -------------------- 35. 动态 --------------------
INSERT INTO moment (id, user_id, content, images, type, source, source_id, is_anonymous, visibility, like_count, comment_count, share_count, create_time) VALUES
(1,  2,  '今天完成了90分钟深度冥想，感觉身心都被洗涤了~推荐大家试试晨间冥想！', NULL, 'auto', 'focus', 1, 0, 0, 28, 5, 2, '2026-05-30 08:35:00'),
(2,  4,  '日语学习打卡第30天！每天早起学90分钟，终于能听懂简单的日剧对话了💪', NULL, 'auto', 'focus', 4, 0, 0, 45, 12, 3, '2026-05-30 07:35:00'),
(3,  1,  '连续专注5天，终于把论文初稿写完了！感谢番茄钟救我狗命', NULL, 'auto', 'focus', 2, 0, 0, 18, 3, 1, '2026-05-30 10:55:00'),
(4,  7,  '读完《人类简史》，强烈推荐！读完感觉对世界有了全新的认识', NULL, 'manual', NULL, NULL, 0, 0, 35, 8, 4, '2026-05-28 18:05:00'),
(5,  6,  '今天晨跑5公里+拉伸，入睡特别快！运动果然是最好的助眠药', NULL, 'auto', 'focus', 6, 0, 0, 22, 4, 1, '2026-05-30 06:25:00'),
(6,  19, '连续睡眠记录25天，平均睡眠评分89分。早睡早起真的让人精神焕发', NULL, 'auto', 'sleep', NULL, 0, 0, 56, 15, 6, '2026-05-30 05:35:00'),
(7,  10, '背单词打卡完成！今天学了50个新单词，复习了80个旧单词', NULL, 'auto', 'focus', 10, 0, 0, 12, 2, 0, '2026-05-30 08:30:00'),
(8,  15, '又完成了一个深度专注的早晨，写代码的效率提高了好多', NULL, 'auto', 'focus', 8, 0, 0, 20, 6, 2, '2026-05-29 06:20:00'),
(9,  3,  '虽然今天被电话打断了3次，但至少还在坚持专注，加油！', NULL, 'auto', 'focus', 3, 0, 0, 8,  2, 0, '2026-05-29 21:30:00'),
(10, 2,  '分享我的睡前仪式：瑜伽15分钟 → 冥想10分钟 → 听白噪音 → 入睡。完美的流程！', NULL, 'manual', NULL, NULL, 0, 0, 42, 10, 5, '2026-05-29 22:10:00'),
(11, 9,  '连续专注工作120天完成了一个大项目，这种深度工作的感觉太爽了', NULL, 'auto', 'focus', 9, 0, 0, 38, 7, 3, '2026-05-27 18:05:00'),
(12, 4,  '终身VIP达成！支持心聆，希望越来越好 ❤️', NULL, 'manual', NULL, NULL, 0, 0, 68, 20, 8, '2026-05-28 07:00:00');

-- -------------------- 36. 动态点赞 --------------------
INSERT INTO moment_like (user_id, moment_id, create_time) VALUES
-- 动态1 被点赞
(1, 1, '2026-05-30 08:40:00'), (4, 1, '2026-05-30 08:42:00'), (7, 1, '2026-05-30 09:00:00'),
(10, 1, '2026-05-30 09:15:00'), (15, 1, '2026-05-30 09:30:00'), (19, 1, '2026-05-30 10:00:00'),
-- 动态2 被点赞
(1, 2, '2026-05-30 07:40:00'), (2, 2, '2026-05-30 07:45:00'), (6, 2, '2026-05-30 08:00:00'),
(7, 2, '2026-05-30 08:10:00'), (10, 2, '2026-05-30 08:30:00'), (15, 2, '2026-05-30 09:00:00'),
-- 动态6 被点赞（最热门）
(1, 6, '2026-05-30 06:00:00'), (2, 6, '2026-05-30 06:10:00'), (4, 6, '2026-05-30 06:20:00'),
(7, 6, '2026-05-30 07:00:00'), (8, 6, '2026-05-30 07:30:00'), (10, 6, '2026-05-30 08:00:00'),
(12, 6, '2026-05-30 08:30:00'), (15, 6, '2026-05-30 09:00:00'),
-- 动态12 被点赞（最多）
(1, 12, '2026-05-28 07:30:00'), (2, 12, '2026-05-28 07:35:00'), (3, 12, '2026-05-28 08:00:00'),
(5, 12, '2026-05-28 08:30:00'), (6, 12, '2026-05-28 09:00:00'), (7, 12, '2026-05-28 09:30:00'),
(8, 12, '2026-05-28 10:00:00'), (9, 12, '2026-05-28 10:30:00'), (10, 12, '2026-05-28 11:00:00');

-- -------------------- 37. 动态评论 --------------------
INSERT INTO moment_comment (user_id, moment_id, parent_id, content, like_count, create_time) VALUES
-- 动态1的评论
(1, 1, 0, '晨间冥想确实很棒，我也在坚持！', 3, '2026-05-30 09:00:00'),
(4, 1, 0, '请问冥想用的什么音频呀？', 2, '2026-05-30 09:10:00'),
(2, 1, 2, '我用的心聆里的引导冥想，特别好！', 1, '2026-05-30 09:20:00'),
(19, 1, 0, '坚持冥想半年了，整个人都不一样了', 5, '2026-05-30 10:00:00'),
-- 动态2的评论
(2, 2, 0, '好棒！30天坚持太厉害了', 4, '2026-05-30 08:00:00'),
(1, 2, 0, '一起加油！我也在学日语', 2, '2026-05-30 08:15:00'),
(10, 2, 5, '向日葵姐姐好厉害！', 1, '2026-05-30 08:30:00'),
-- 动态4的评论
(2, 4, 0, '《人类简史》确实好看， next推荐《未来简史》', 6, '2026-05-28 19:00:00'),
(1, 4, 0, '我也在读！现在读到一半了', 2, '2026-05-28 20:00:00'),
-- 动态6的评论
(2, 6, 0, '25天连续记录太牛了！', 8, '2026-05-30 06:00:00'),
(4, 6, 0, '请问有什么助眠技巧吗？', 3, '2026-05-30 06:30:00'),
(19, 6, 11, '固定作息时间最重要，加上睡前不看手机', 5, '2026-05-30 07:00:00'),
-- 动态12的评论
(2, 12, 0, '向日葵永远是我们社区的榜样！', 10, '2026-05-28 07:30:00'),
(1, 12, 0, '支持！心聆越来越好', 5, '2026-05-28 08:00:00'),
(15, 12, 0, '我也要努力攒积分换终身VIP！', 3, '2026-05-28 09:00:00');

-- -------------------- 38. 用户关注 --------------------
INSERT INTO user_follow (follower_id, following_id, create_time) VALUES
-- 暖暖 的粉丝
(1, 2, '2025-07-01 10:00:00'), (3, 2, '2025-08-15 14:00:00'), (4, 2, '2025-09-01 09:00:00'),
(6, 2, '2025-10-10 11:00:00'), (7, 2, '2025-11-20 16:00:00'), (10, 2, '2025-12-05 08:00:00'),
(12, 2, '2026-01-15 10:00:00'), (15, 2, '2026-02-01 09:00:00'), (19, 2, '2026-03-10 14:00:00'),
-- 向日葵 的粉丝
(1, 4, '2025-07-15 08:00:00'), (2, 4, '2025-08-01 10:00:00'), (3, 4, '2025-09-20 14:00:00'),
(6, 4, '2025-10-05 11:00:00'), (7, 4, '2025-11-10 16:00:00'), (8, 4, '2025-12-15 09:00:00'),
(9, 4, '2026-01-20 10:00:00'), (10, 4, '2026-02-10 08:00:00'), (12, 4, '2026-03-01 14:00:00'),
(15, 4, '2026-03-20 09:00:00'), (16, 4, '2026-04-05 10:00:00'),
-- 禅意 的粉丝
(2, 19, '2026-03-05 10:00:00'), (4, 19, '2026-03-10 14:00:00'), (7, 19, '2026-03-15 09:00:00'),
(15, 19, '2026-04-01 10:00:00'),
-- 其他关注
(2, 7, '2025-10-01 10:00:00'), (1, 6, '2025-11-15 14:00:00'), (3, 9, '2025-12-01 09:00:00'),
(5, 1, '2026-01-10 11:00:00'), (8, 2, '2026-02-20 16:00:00'),
(14, 2, '2026-04-10 10:00:00'), (16, 19, '2026-05-01 09:00:00'), (18, 4, '2026-05-15 14:00:00'),
(20, 2, '2026-05-20 10:00:00'), (11, 7, '2026-04-20 11:00:00'), (13, 1, '2026-05-10 09:00:00');

-- -------------------- 39. 私信 --------------------
INSERT INTO private_message (from_user_id, to_user_id, content, is_read, create_time) VALUES
(1, 2, '你好呀暖暖，看了你的冥想动态，非常受启发！', 1, '2026-05-30 09:00:00'),
(2, 1, '谢谢小明！一起加油呀~', 1, '2026-05-30 09:05:00'),
(1, 2, '请问你冥想用的什么音频推荐？', 1, '2026-05-30 09:10:00'),
(2, 1, '推荐心聆里的"晨间引导冥想"和"深度放松"，特别棒！', 1, '2026-05-30 09:15:00'),
(3, 4, '向日葵姐姐，你的日语学习方法能分享一下吗？', 1, '2026-05-30 08:00:00'),
(4, 3, '当然可以！主要就是每天固定时间学习+用番茄钟保持专注', 1, '2026-05-30 08:10:00'),
(3, 4, '好的谢谢！我也试试番茄钟', 0, '2026-05-30 08:15:00'),
(10, 2, '暖暖姐，你的睡眠怎么那么好呀，有什么秘诀吗？', 1, '2026-05-30 09:30:00'),
(2, 10, '主要是固定作息时间和睡前冥想，坚持一个月就有效果了', 1, '2026-05-30 09:35:00'),
(10, 2, '好的！我从今天开始试试', 0, '2026-05-30 09:40:00'),
(6, 19, '禅意大师，想请教一下打坐的技巧', 1, '2026-05-29 20:00:00'),
(19, 6, '初学建议从5分钟开始，专注于呼吸，不要想其他事情', 1, '2026-05-29 20:10:00'),
(6, 19, '谢谢！我试试看', 1, '2026-05-29 20:15:00'),
(5, 1, '小明，上次你说的Python课程叫什么名字？', 0, '2026-05-28 21:00:00'),
(8, 7, '书虫哥哥，《三体》好看吗？我也想读', 0, '2026-05-29 22:00:00');

-- -------------------- 40. 挑战活动 --------------------
INSERT INTO challenge (id, title, description, cover, type, duration, condition_value, points_reward, vip_days_reward, badge_id, start_time, end_time, status) VALUES
(1, '21天早起挑战',     '连续21天在7点前起床，养成早起好习惯',       '/img/challenge/early.png',     'sleep', 21, 1, 500,  7,  1, '2026-05-01 00:00:00', '2026-06-30 23:59:59', 1),
(2, '30天专注挑战',     '连续30天每天完成至少60分钟专注',             '/img/challenge/focus30.png',   'focus', 30, 60, 1000, 15, 2, '2026-05-15 00:00:00', '2026-07-31 23:59:59', 1),
(3, '7天冥想挑战',      '连续7天每天冥想至少15分钟',                  '/img/challenge/meditate.png',  'focus', 7,  15, 200,  3,  3, '2026-06-01 00:00:00', '2026-06-30 23:59:59', 1),
(4, '14天好睡眠挑战',   '连续14天睡眠评分达到80分以上',               '/img/challenge/sleep14.png', 'sleep', 14, 80, 400,  5,  4, '2026-05-20 00:00:00', '2026-07-20 23:59:59', 1),
(5, '百日专注大师',     '累计专注100次，成为真正的专注大师',          '/img/challenge/master.png',    'focus', 100,1,  2000, 30, 5, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1);

-- -------------------- 41. 用户挑战 --------------------
INSERT INTO user_challenge (user_id, challenge_id, join_time, current_day, completed_days, status) VALUES
(2,  1, '2026-05-01 06:30:00', 30, 28, 0),
(2,  2, '2026-05-15 07:00:00', 16, 15, 0),
(4,  1, '2026-05-01 05:50:00', 30, 30, 1),
(4,  2, '2026-05-15 06:00:00', 16, 16, 0),
(4,  3, '2026-06-01 06:00:00', 1,  1,  0),
(1,  1, '2026-05-01 06:45:00', 30, 20, 0),
(1,  2, '2026-05-15 09:00:00', 16, 10, 0),
(7,  1, '2026-05-01 06:50:00', 30, 25, 0),
(7,  4, '2026-05-20 23:00:00', 11, 9,  0),
(10, 3, '2026-06-01 08:00:00', 1,  1,  0),
(15, 1, '2026-05-01 06:00:00', 30, 27, 0),
(15, 2, '2026-05-15 06:00:00', 16, 14, 0),
(19, 1, '2026-05-01 05:00:00', 30, 30, 1),
(19, 2, '2026-05-15 05:00:00', 16, 16, 0),
(19, 4, '2026-05-20 22:00:00', 11, 11, 0),
(19, 5, '2026-01-05 09:00:00', 150,88, 0),
(6,  1, '2026-05-01 05:20:00', 30, 22, 0),
(9,  5, '2026-02-01 10:00:00', 120,65, 0);

-- -------------------- 42. 通知消息 --------------------
INSERT INTO notification (user_id, type, title, content, target_type, target_id, is_read, create_time) VALUES
-- 系统通知
(1, 'system',     '欢迎加入心聆',         '欢迎使用心聆APP！完成每日任务可以获得积分奖励哦~',            NULL, NULL, 1, '2025-06-01 10:00:00'),
(2, 'system',     '欢迎加入心聆',         '欢迎使用心聆APP！完成每日任务可以获得积分奖励哦~',            NULL, NULL, 1, '2025-06-05 14:30:00'),
-- 互动通知
(2, 'interaction', '小明赞了你的动态',     '小明赞了你关于晨间冥想的动态',                                 'moment', 1, 1, '2026-05-30 08:40:00'),
(4, 'interaction', '小明赞了你的动态',     '小明赞了你关于日语学习的动态',                                 'moment', 2, 1, '2026-05-30 07:40:00'),
(19,'interaction', '暖暖关注了你',         '暖暖关注了你，快去回关吧',                                     'user', 2, 1, '2026-03-05 10:00:00'),
(2, 'interaction', '月光宝盒评论了你',     '月光宝盒评论了你的动态：好棒！30天坚持太厉害了',              'moment', 2, 0, '2026-05-30 08:00:00'),
-- 提醒通知
(7, 'reminder',   '月度会员即将到期',     '您的月度会员将于2026-06-10到期，续费享8折优惠',                NULL, NULL, 0, '2026-06-01 08:00:00'),
(1, 'reminder',   '今日任务未完成',       '您还有2个今日任务未完成，加油！',                              NULL, NULL, 0, '2026-05-30 20:00:00'),
-- 分销通知
(2, 'distribution','收到新佣金',           '您的好友小明购买了季度会员，您获得佣金20.97元',                'order', NULL, 1, '2026-05-25 18:35:00'),
(4, 'distribution','收到新佣金',           '您的好友糖糖购买了年度会员，您获得佣金59.70元',                'order', NULL, 0, '2026-05-30 08:05:00'),
(2, 'distribution','提现成功',             '您的提现申请990.00元已到账',                                  NULL, NULL, 1, '2026-03-20 15:30:00'),
-- 活动通知
(1, 'activity',   '618大促开始啦！',      '全场8折优惠活动已开始，限时20天，快来选购吧！',              'activity', 2, 0, '2026-06-01 00:00:00'),
(3, 'activity',   '端午节特惠',           '满100减20优惠活动进行中~',                                  'activity', 6, 0, '2026-05-28 09:00:00'),
(10,'activity',   '新用户专享',           '您有新用户首单5折资格，快来购买VIP吧！',                      'activity', 1, 1, '2026-05-30 08:00:00'),
-- 支付通知
(2, 'pay',        '支付成功',             '您已成功支付年度会员199.00元',                                'order', NULL, 1, '2025-12-01 10:30:00'),
(4, 'pay',        '支付成功',             '您已成功支付终身会员899.00元',                                'order', NULL, 1, '2026-01-15 14:20:00'),
(10,'pay',        '支付成功',             '您已成功支付年度会员199.00元',                                'order', NULL, 1, '2026-05-30 08:00:00'),
(6, 'pay',        '支付成功',             '您已成功支付月度会员19.90元',                                 'order', NULL, 1, '2026-06-01 06:30:00'),
-- 挑战通知
(4, 'interaction', '挑战完成！',           '恭喜你完成21天早起挑战！获得1000积分+15天VIP奖励',            'challenge', 1, 1, '2026-05-22 06:00:00'),
(19,'interaction', '挑战完成！',           '恭喜你完成21天早起挑战！获得1000积分+15天VIP奖励',            'challenge', 1, 1, '2026-05-22 05:00:00'),
(3, 'interaction', '小明评论了你',         '小明评论了你关于专注中断的动态：加油，被打断很正常！',        'moment', 9, 0, '2026-05-29 22:00:00');

-- -------------------- 43. 音频 --------------------
INSERT INTO audio_item (id, title, cover_url, audio_url, duration, category, play_count, is_favorite, tags, status, sort_order) VALUES
(1,  '细雨绵绵',       '/img/audio/rain.jpg',       '/audio/rain_light.mp3',      3600, 'rain',        12580, 1, '["自然","放松","助眠"]',           1, 1),
(2,  '雷雨交加',       '/img/audio/thunder.jpg',    '/audio/thunder.mp3',         3600, 'rain',         8920, 0, '["自然","白噪音","专注"]',         1, 2),
(3,  '海浪拍岸',       '/img/audio/ocean.jpg',      '/audio/ocean_wave.mp3',      3600, 'ocean',       15200, 1, '["自然","海洋","放松"]',           1, 3),
(4,  '深海静谧',       '/img/audio/deep_sea.jpg',   '/audio/deep_sea.mp3',        3600, 'ocean',        6800, 0, '["自然","深睡","冥想"]',           1, 4),
(5,  '森林鸟鸣',       '/img/audio/forest.jpg',     '/audio/forest_bird.mp3',     3600, 'forest',      10500, 1, '["自然","森林","清新"]',           1, 5),
(6,  '林间溪流',       '/img/audio/creek.jpg',      '/audio/forest_creek.mp3',    3600, 'forest',       7600, 0, '["自然","溪流","专注"]',           1, 6),
(7,  '夏夜虫鸣',       '/img/audio/night.jpg',      '/audio/night_cricket.mp3',   3600, 'night',        9200, 0, '["自然","夜晚","助眠"]',           1, 7),
(8,  '月光森林',       '/img/audio/moonlight.jpg',  '/audio/moonlight_forest.mp3',3600, 'night',        5400, 0, '["自然","夜晚","冥想"]',           1, 8),
(9,  '引导冥想-放松',  '/img/audio/meditate1.jpg',  '/audio/guide_relax.mp3',     900,  'meditation',  22000, 1, '["引导","放松","减压"]',           1, 9),
(10, '引导冥想-专注',  '/img/audio/meditate2.jpg',  '/audio/guide_focus.mp3',     600,  'meditation',  18500, 1, '["引导","专注","高效"]',           1, 10),
(11, '咖啡馆白噪音',   '/img/audio/cafe.jpg',       '/audio/cafe_ambience.mp3',   3600, 'cafe',         8100, 0, '["白噪音","咖啡馆","学习"]',       1, 11),
(12, '壁炉噼啪声',     '/img/audio/fireplace.jpg',  '/audio/fireplace.mp3',       3600, 'white_noise',  6200, 0, '["白噪音","温暖","助眠"]',         1, 12),
(13, '粉红噪音',       '/img/audio/pink_noise.jpg', '/audio/pink_noise.mp3',      3600, 'white_noise',  7800, 0, '["白噪音","深睡","科学"]',         1, 13),
(14, '晨曦鸟鸣',       '/img/audio/dawn.jpg',       '/audio/dawn_bird.mp3',       1800, 'forest',       4500, 0, '["自然","清晨","唤醒"]',           1, 14),
(15, '暴雨倾盆',       '/img/audio/heavy_rain.jpg', '/audio/heavy_rain.mp3',      3600, 'rain',         5600, 0, '["自然","暴雨","白噪音"]',         1, 15);

-- -------------------- 44. 混音 --------------------
INSERT INTO audio_mix (id, name, description, cover_url, audio_ids, is_default, sort_order, status) VALUES
(1, '雨天助眠',       '细雨声搭配远处的雷声，帮助快速入睡',             '/img/mix/rain_sleep.jpg',   '[1,7]',      1, 1, 1),
(2, '深度专注',       '咖啡馆白噪音+粉红噪音，提升专注力',              '/img/mix/focus_deep.jpg',   '[11,13]',    1, 2, 1),
(3, '海浪冥想',       '海浪声搭配引导冥想，放松身心',                   '/img/mix/ocean_meditate.jpg','[3,9]',     0, 3, 1),
(4, '森林早晨',       '森林鸟鸣+林间溪流，清新自然',                    '/img/mix/forest_morning.jpg','[5,6]',     0, 4, 1),
(5, '暴风之夜',       '雷雨+壁炉声，温暖的夜晚',                        '/img/mix/storm_night.jpg',  '[2,12]',     0, 5, 1),
(6, '深海放松',       '深海静谧+粉红噪音，深度放松',                    '/img/mix/deep_relax.jpg',   '[4,13]',     0, 6, 1),
(7, '清晨唤醒',       '晨曦鸟鸣+细雨声，温柔唤醒',                      '/img/mix/morning_wake.jpg', '[14,1]',     0, 7, 1),
(8, '夏夜纳凉',       '夏夜虫鸣+细雨，夏夜的清凉',                      '/img/mix/summer_night.jpg', '[7,1]',      0, 8, 1);

-- -------------------- 45. 音频播放历史 --------------------
INSERT INTO audio_play_history (user_id, audio_id, played_duration, create_time) VALUES
-- 用户2 暖暖 喜欢冥想音频
(2, 9,  900,  '2026-05-30 07:00:00'), (2, 3,  1800, '2026-05-30 07:15:00'), (2, 9,  600,  '2026-05-29 22:00:00'),
(2, 1,  3600, '2026-05-29 22:30:00'), (2, 5,  2400, '2026-05-28 07:00:00'),
-- 用户4 向日葵 喜欢森林+专注音频
(4, 5,  3600, '2026-05-30 06:00:00'), (4, 11, 3600, '2026-05-30 14:00:00'), (4, 10, 600,  '2026-05-30 05:55:00'),
(4, 6,  2400, '2026-05-29 14:00:00'), (4, 13, 3600, '2026-05-29 22:00:00'),
-- 用户1 小明 喜欢雨声
(1, 1,  3600, '2026-05-29 23:30:00'), (1, 2,  1800, '2026-05-28 23:00:00'), (1, 11, 1200, '2026-05-30 09:00:00'),
(1, 7,  2400, '2026-05-27 23:15:00'),
-- 用户7 书虫 喜欢安静音频
(7, 13, 3600, '2026-05-29 23:00:00'), (7, 12, 3000, '2026-05-28 23:30:00'), (7, 4,  1800, '2026-05-30 17:00:00'),
(7, 9,  900,  '2026-05-29 17:00:00'),
-- 用户19 禅意 喜欢冥想+夜晚音频
(19, 9,  900,  '2026-05-29 22:00:00'), (19, 8,  3600, '2026-05-29 22:15:00'), (19, 7,  3600, '2026-05-28 22:30:00'),
(19, 4,  3600, '2026-05-28 22:30:00'), (19, 10, 600,  '2026-05-30 05:00:00'),
-- 用户6 月光宝盒 喜欢海洋+运动音频
(6, 3,  2400, '2026-05-30 05:30:00'), (6, 5,  1800, '2026-05-29 22:00:00'), (6, 14, 1200, '2026-05-30 05:25:00'),
-- 用户10 糖糖 喜欢轻松音频
(10, 11, 1800, '2026-05-30 08:00:00'), (10, 1,  1200, '2026-05-29 23:00:00'), (10, 9,  600,  '2026-05-30 07:55:00');

