package com.xinling.app.service.impl;

import com.xinling.app.constant.AppConstants;
import com.xinling.app.service.IVerificationCodeService;
import com.xinling.common.core.redis.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现
 *
 * 生产环境需对接阿里云/腾讯云短信SDK
 */
@Service
public class VerificationCodeServiceImpl implements IVerificationCodeService {

    private static final Logger log = LoggerFactory.getLogger(VerificationCodeServiceImpl.class);

    private final RedisCache redisCache;

    public VerificationCodeServiceImpl(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    public void sendCode(String phone, String scene) {
        // 检查发送频率（60秒内不能重复发送）
        String intervalKey = AppConstants.VERIFY_CODE_KEY + "interval:" + phone;
        Object lastSend = redisCache.getCacheObject(intervalKey);
        if (lastSend != null) {
            throw new RuntimeException("发送太频繁，请60秒后再试");
        }

        // 检查每日上限
        String dailyKey = AppConstants.VERIFY_CODE_KEY + "daily:" + phone;
        Integer dailyCount = redisCache.getCacheObject(dailyKey);
        if (dailyCount != null && dailyCount >= AppConstants.VERIFY_CODE_DAILY_LIMIT) {
            throw new RuntimeException("今日验证码发送次数已达上限");
        }

        // 生成6位随机验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

        // 存入Redis（5分钟有效）
        String codeKey = AppConstants.VERIFY_CODE_KEY + phone;
        redisCache.setCacheObject(codeKey, code, (int) AppConstants.VERIFY_CODE_EXPIRE, TimeUnit.MINUTES);

        // 记录发送间隔
        redisCache.setCacheObject(intervalKey, "1", (int) AppConstants.VERIFY_CODE_INTERVAL, TimeUnit.SECONDS);

        // 记录每日次数
        if (dailyCount == null) {
            redisCache.setCacheObject(dailyKey, 1, 24, TimeUnit.HOURS);
        } else {
            redisCache.setCacheObject(dailyKey, dailyCount + 1, 24, TimeUnit.HOURS);
        }

        // TODO: 对接短信服务商发送验证码
        log.info("[验证码] 手机号: {}, 场景: {}, 验证码: {}", phone, scene, code);
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        String codeKey = AppConstants.VERIFY_CODE_KEY + phone;
        String savedCode = redisCache.getCacheObject(codeKey);
        if (savedCode == null) {
            throw new RuntimeException("验证码已过期，请重新获取");
        }
        if (!savedCode.equals(code)) {
            throw new RuntimeException("验证码错误");
        }
        return true;
    }

    @Override
    public void deleteCode(String phone) {
        String codeKey = AppConstants.VERIFY_CODE_KEY + phone;
        redisCache.deleteObject(codeKey);
    }
}
