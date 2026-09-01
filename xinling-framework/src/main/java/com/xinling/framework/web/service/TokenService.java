package com.xinling.framework.web.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.xinling.framework.config.properties.TokenProperties;
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.xinling.common.constant.CacheConstants;
import com.xinling.common.constant.Constants;
import com.xinling.common.core.domain.model.LoginUser;
import com.xinling.common.core.redis.RedisCache;
import com.xinling.common.utils.ServletUtils;
import com.xinling.common.utils.StringUtils;
import com.xinling.common.utils.html.UserAgentUtils;
import com.xinling.common.utils.ip.AddressUtils;
import com.xinling.common.utils.ip.IpUtils;
import com.xinling.common.utils.uuid.IdUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "xinling.security.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    private final RedisCache redisCache;
    private final TokenProperties tokenProperties;

    protected static final long MILLIS_MINUTE = 60 * 1000;
    private static final long MILLIS_MINUTE_TWENTY = 20 * MILLIS_MINUTE;

    public LoginUser getLoginUser(HttpServletRequest request) {
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            try {
                Claims claims = parseToken(token);
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                return redisCache.getCacheObject(getTokenKey(uuid));
            } catch (Exception e) {
                log.error("解析 token 失败: {}", e.getMessage());
            }
        }
        return null;
    }

    public String createToken(LoginUser loginUser) {
        String uuid = IdUtils.fastUUID();
        loginUser.setToken(uuid);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, uuid);
        claims.put(Constants.JWT_USERNAME, loginUser.getUsername());
        return createJwt(claims);
    }

    public void verifyToken(LoginUser loginUser) {
        if (loginUser.getExpireTime() - System.currentTimeMillis() <= MILLIS_MINUTE_TWENTY) {
            refreshToken(loginUser);
        }
    }

    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + tokenProperties.getExpireTime() * MILLIS_MINUTE);
        redisCache.setCacheObject(
                getTokenKey(loginUser.getToken()),
                loginUser,
                tokenProperties.getExpireTime(),
                TimeUnit.MINUTES
        );
    }

    private void setUserAgent(LoginUser loginUser) {
        String userAgent = ServletUtils.getRequest().getHeader("User-Agent");
        String ip = IpUtils.getIpAddr();
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(UserAgentUtils.getBrowser(userAgent));
        loginUser.setOs(UserAgentUtils.getOperatingSystem(userAgent));
    }

    private String createJwt(Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(tokenProperties.getHeader());
        // 兼容 Knife4j（doc.html）等调试工具默认使用的 Admin-Token 请求头
        if (StringUtils.isEmpty(token)) {
            token = request.getHeader("Admin-Token");
        }
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.substring(Constants.TOKEN_PREFIX.length());
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return CacheConstants.LOGIN_TOKEN_KEY + uuid;
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 设置用户登录信息（刷新 Redis 中的登录态）
     */
    public void setLoginUser(LoginUser loginUser) {
        if (StringUtils.isNotNull(loginUser)
                && StringUtils.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }
}
