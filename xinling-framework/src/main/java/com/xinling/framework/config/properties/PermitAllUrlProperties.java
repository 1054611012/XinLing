package com.xinling.framework.config.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.xinling.common.annotation.Anonymous;

/**
 * 设置Anonymous注解允许匿名访问的url
 *
 * @author xinling
 */
@Configuration
public class PermitAllUrlProperties implements InitializingBean, ApplicationContextAware
{
    private ApplicationContext applicationContext;

    private List<String> urls = new ArrayList<>();

    @Override
    public void afterPropertiesSet()
    {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        map.keySet().forEach(info -> {
            HandlerMethod handlerMethod = map.get(info);

            // 获取方法上边的注解，只添加不包含路径参数和通配符问题的路径
            Anonymous method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Anonymous.class);
            if (method != null) {
                // 检查PatternsCondition是否为null，以避免NPE
                if (info.getPatternsCondition() != null) {
                    info.getPatternsCondition().getPatterns().forEach(url -> {
                        // 检查URL是否包含可能导致问题的字符
                        if (isValidPattern(url)) {
                            urls.add(url);
                            System.out.println("PermitAllUrlProperties: 添加安全的URL模式: " + url);
                        } else {
                            // 添加日志来识别有问题的URL模式
                            System.out.println("PermitAllUrlProperties: 跳过有问题的URL模式: " + url);
                        }
                    });
                }
            }

            // 获取类上边的注解，只添加不包含路径参数和通配符问题的路径
            Anonymous controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Anonymous.class);
            if (controller != null) {
                // 检查PatternsCondition是否为null，以避免NPE
                if (info.getPatternsCondition() != null) {
                    info.getPatternsCondition().getPatterns().forEach(url -> {
                        // 检查URL是否包含可能导致问题的字符
                        if (isValidPattern(url)) {
                            urls.add(url);
                            System.out.println("PermitAllUrlProperties: 添加安全的URL模式: " + url);
                        } else {
                            // 添加日志来识别有问题的URL模式
                            System.out.println("PermitAllUrlProperties: 跳过有问题的URL模式: " + url);
                        }
                    });
                }
            }
        });
    }

    /**
     * 检查路径模式是否有效，避免包含可能导致PatternParseException的模式
     */
    private boolean isValidPattern(String url) {
        if (url == null) {
            return false;
        }
        
        // 检查是否包含路径参数
        if (url.contains("{") && url.contains("}")) {
            return false;
        }
        
        // 检查是否包含 {*...} 模式
        if (url.contains("{*")) {
            return false;
        }
        
        // 检查是否包含非法的路径模式
        if (url.contains("*{") || url.contains("}*")) {
            return false;
        }
        
        // 检查是否包含连续的通配符
        if (url.contains("***")) {
            return false;
        }
        
        // 检查 ** 模式使用是否合规
        // 在Spring中，/** 应该是独立的模式，不应该在 ** 后面直接跟其他非路径字符
        int pos = 0;
        while ((pos = url.indexOf("**", pos)) != -1) {
            // 检查 ** 前面的字符是否合规
            if (pos > 0) {
                char prevChar = url.charAt(pos - 1);
                // 如果 ** 前面不是 / 或开头，则可能有问题
                if (prevChar != '/') {
                    return false;
                }
            }
            
            // 检查 ** 后面的字符
            if (pos + 2 < url.length()) {
                char nextChar = url.charAt(pos + 2);
                // 如果 ** 后面直接跟着字母数字等非路径分隔符字符，可能导致问题
                if (Character.isLetterOrDigit(nextChar) && nextChar != '*') {
                    // 但允许一些特定的合法模式，如 /**/ 或 /**.extension
                    String afterDoubleWildcard = url.substring(pos + 2);
                    if (!afterDoubleWildcard.startsWith("/") && !afterDoubleWildcard.startsWith("/*")) {
                        return false;
                    }
                }
            }
            pos += 2;
        }
        
        // 额外检查：防止模式中出现连续的特殊字符导致解析错误
        if (url.contains("**{") || url.contains("{**")) {
            return false;
        }
        
        // 检查是否包含可能导致问题的复杂模式组合
        if (url.contains("**{") || url.contains("}**") || url.contains("*{") || url.contains("}*")) {
            return false;
        }
        
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        this.applicationContext = context;
    }

    public List<String> getUrls()
    {
        return urls;
    }

    public void setUrls(List<String> urls)
    {
        this.urls = urls;
    }
}