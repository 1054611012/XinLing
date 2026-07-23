package com.xinling.app.constant;

/**
 * APP模块常量
 */
public interface AppConstants {

    /** Token 请求头 */
    String AUTH_HEADER = "Authorization";

    /** Token 前缀 */
    String TOKEN_PREFIX = "Bearer ";

    /** 登录用户 Redis Key 前缀 */
    String LOGIN_TOKEN_KEY = "app_login_tokens:";

    /** 验证码 Redis Key 前缀 */
    String VERIFY_CODE_KEY = "app_verify_code:";

    /** 验证码发送间隔（秒） */
    long VERIFY_CODE_INTERVAL = 60;

    /** 验证码有效期（分钟） */
    long VERIFY_CODE_EXPIRE = 5;

    /** 验证码每日上限 */
    long VERIFY_CODE_DAILY_LIMIT = 10;

    /** Token 有效期（分钟） */
    long TOKEN_EXPIRE = 1440; // 24小时

    /** Token 续期阈值（分钟） */
    long TOKEN_REFRESH_THRESHOLD = 30;

    /** 用户默认昵称前缀 */
    String DEFAULT_NICKNAME_PREFIX = "用户";
}