package com.xinling.app.token;

import com.xinling.app.constant.AppConstants;
import com.xinling.app.domain.model.AppLoginUser;
import com.xinling.common.core.redis.RedisCache;
import com.xinling.common.utils.uuid.IdUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * APP用户 Token 管理
 */
@Component
public class AppTokenService {

    private static final Logger log = LoggerFactory.getLogger(AppTokenService.class);

    @Value("${token.secret}")
    private String secret;

    @Value("${token.header}")
    private String header;

    private final RedisCache redisCache;

    public AppTokenService(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    /**
     * 从请求中获取登录用户
     */
    public AppLoginUser getLoginUser(HttpServletRequest request) {
        String token = getToken(request);
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            Claims claims = parseToken(token);
            String uuid = claims.get("login_user_key", String.class);
            return redisCache.getCacheObject(getTokenKey(uuid));
        } catch (Exception e) {
            log.debug("解析APP token失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 创建Token
     */
    public String createToken(AppLoginUser loginUser) {
        String uuid = IdUtils.fastUUID();
        loginUser.setToken(uuid);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put("login_user_key", uuid);
        claims.put("user_id", loginUser.getUserId());
        return createJwt(claims);
    }

    /**
     * 验证Token并自动续期
     */
    public void verifyAndRefresh(AppLoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long threshold = AppConstants.TOKEN_REFRESH_THRESHOLD * 60 * 1000L;
        if (expireTime - System.currentTimeMillis() <= threshold) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新Token（延长Redis过期时间）
     */
    public void refreshToken(AppLoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + AppConstants.TOKEN_EXPIRE * 60 * 1000);
        redisCache.setCacheObject(
                getTokenKey(loginUser.getToken()),
                loginUser,
                (int) AppConstants.TOKEN_EXPIRE,
                TimeUnit.MINUTES
        );
    }

    /**
     * 删除Token
     */
    public void delLoginUser(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                Claims claims = parseToken(token);
                String uuid = claims.get("login_user_key", String.class);
                redisCache.deleteObject(getTokenKey(uuid));
            } catch (Exception e) {
                log.warn("删除APP token失败: {}", e.getMessage());
            }
        }
    }

    private String createJwt(Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (token != null && token.startsWith(AppConstants.TOKEN_PREFIX)) {
            return token.substring(AppConstants.TOKEN_PREFIX.length());
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return AppConstants.LOGIN_TOKEN_KEY + uuid;
    }
}
