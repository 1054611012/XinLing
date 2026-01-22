-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    session_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    role VARCHAR(20) NOT NULL COMMENT '消息角色: user, assistant',
    content TEXT NOT NULL COMMENT '消息内容',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(100) COMMENT '用户名',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    sequence_number BIGINT COMMENT '消息序列号，用于保证顺序消费',
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) COMMENT='聊天消息表';