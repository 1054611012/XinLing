package com.xinling.app.utils;

import com.xinling.app.constant.AppConstants;
import com.xinling.app.domain.model.AppLoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AppContextUtil {

    public static AppLoginUser getLoginUser() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        HttpServletRequest request = attrs.getRequest();
        return (AppLoginUser) request.getAttribute(AppConstants.LOGIN_TOKEN_KEY);
    }

    public static Long getUserId() {
        AppLoginUser user = getLoginUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getNickname() {
        AppLoginUser user = getLoginUser();
        return user != null ? user.getNickname() : null;
    }
}
