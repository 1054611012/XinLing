package com.xinling.app.interceptor;

import com.xinling.app.constant.AppConstants;
import com.xinling.app.domain.model.AppLoginUser;
import com.xinling.app.token.AppTokenService;
import com.xinling.common.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AppAuthInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AppAuthInterceptor.class);
    private final AppTokenService appTokenService;

    public AppAuthInterceptor(AppTokenService appTokenService) {
        this.appTokenService = appTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        AppLoginUser loginUser = appTokenService.getLoginUser(request);
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":401,\"message\":\"未登录或Token已过期\",\"data\":null}");
            } catch (Exception e) {
                log.error("写入响应失败", e);
            }
            return false;
        }
        appTokenService.verifyAndRefresh(loginUser);
        request.setAttribute(AppConstants.LOGIN_TOKEN_KEY, loginUser);
        return true;
    }
}
