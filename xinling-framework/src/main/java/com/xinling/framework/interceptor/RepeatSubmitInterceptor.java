package com.xinling.framework.interceptor;

import java.lang.reflect.Method;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.method.HandlerMethod;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.alibaba.fastjson2.JSON;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.xinling.common.annotation.RepeatSubmit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.xinling.common.core.domain.AjaxResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.xinling.common.utils.ServletUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * 防止重复提交拦截器
 *
 * @author xinling
 */
@Component
@ConditionalOnProperty(name = "xinling.security.enabled", havingValue = "true", matchIfMissing = true)
public abstract class RepeatSubmitInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (handler instanceof HandlerMethod)
        {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null)
            {
                if (this.isRepeatSubmit(request, annotation))
                {
                    AjaxResult ajaxResult = AjaxResult.error(annotation.message());
                    ServletUtils.renderString(response, JSON.toJSONString(ajaxResult));
                    return false;
                }
            }
            return true;
        }
        else
        {
            return true;
        }
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request 请求信息
     * @param annotation 防重复注解参数
     * @return 结果
     * @throws Exception
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation);
}
