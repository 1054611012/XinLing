package com.xinling.framework.config;

import com.xinling.common.utils.StringUtils;
import com.xinling.framework.config.properties.RefererProperties;
import com.xinling.framework.config.properties.XssProperties;
import com.xinling.common.filter.RefererFilter;
import com.xinling.common.filter.XssFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({
        XssProperties.class,
        RefererProperties.class
})
public class FilterConfig {

    private final XssProperties xssProperties;
    private final RefererProperties refererProperties;

    /**
     * XSS 过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "xss", name = "enabled", havingValue = "true")
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {

        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();

        // ✅ filter 必须始终存在
        registration.setFilter(new XssFilter());

        String urlPatternsValue = xssProperties.getUrlPatterns();
        if (StringUtils.isNotEmpty(urlPatternsValue)) {
            registration.addUrlPatterns(urlPatternsValue.split(","));
        }

        registration.addInitParameter("excludes", xssProperties.getExcludes());
        registration.setOrder(1);

        return registration;
    }

    /**
     * 防盗链过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "referer", name = "enabled", havingValue = "true")
    public FilterRegistrationBean<RefererFilter> refererFilterRegistration() {

        FilterRegistrationBean<RefererFilter> registration = new FilterRegistrationBean<>();

        // ✅ filter 必须始终存在
        registration.setFilter(new RefererFilter());

        registration.addUrlPatterns("/*");

        String allowedDomains = refererProperties.getAllowedDomains();
        if (StringUtils.isNotEmpty(allowedDomains)) {
            registration.addInitParameter("allowedDomains", allowedDomains);
        }

        registration.setOrder(2);

        return registration;
    }
}
