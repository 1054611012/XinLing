package com.xinling.common.constant;

/**
 * @author SuXia
 * @date 2026/1/20 17:21
 */
public class RedisKeys {

    /** 聊天标题 */
    public static final String CHAT_TITLE = "chat:title:";

    /** 聊天用户会话 */
    public static final String CHAT_USER_SESSIONS = "chat:user_sessions:";

    /** 聊天历史记录 */
    public static final String CHAT_HISTORY = "chat:history:";

    /** 聊天会话用户 */
    public static final String SESSION_USER_PREFIX = "chat:session_user:";

    /** 聊天会话 */
    public static final String CHAT_SESSION_PREFIX = "chat:session:";

    /** 聊天会话配置映射 */
    public static final String SESSION_CONFIG_PREFIX = "chat:session_config:";

    /** RAG初始化状态 */
    public static final String RAG_INITIALIZED_KEY = "ai:rag:initialized";

    /** AI 模块 Redis Key 前缀 */
    public static final String AI_PREFIX = "ai:";

    /** AI提示词缓存（按configId） */
    public static final String AI_PROMPTS_CONFIG = "ai:prompts:config:";

    /** AI提示词缓存（按configId:modelId） */
    public static final String AI_PROMPTS_CONFIG_MODEL = "ai:prompts:config:model:";
}
