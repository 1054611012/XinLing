package com.xinling.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 
 * @author SuXia
 * @date 2026/01/19
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置Druid监控页面的静态资源映射
        registry.addResourceHandler("/druid/**")
                .addResourceLocations("classpath:/META-INF/resources/");
    }
}