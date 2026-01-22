package com.xinling.framework.config;

import java.util.concurrent.TimeUnit;

import com.alibaba.druid.support.jakarta.StatViewServlet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.boot.web.servlet.ServletRegistrationBean;

@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    /**
     * Druid StatViewServlet 注册
     */
    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServlet() {
        ServletRegistrationBean<StatViewServlet> bean =
                new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        bean.addInitParameter("loginUsername", "admin"); // 登录账号
        bean.addInitParameter("loginPassword", "admin"); // 登录密码
        bean.addInitParameter("resetEnable", "false");   // 禁用重置功能
        return bean;
    }

    /**
     * 静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Druid 静态资源
        registry.addResourceHandler("/druid/**")
                .addResourceLocations("classpath:/META-INF/resources/");

        // Swagger
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/")
                .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());
        registry.addResourceHandler("/v3/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());

        // 通用静态资源
        registry.addResourceHandler("/static/**", "/*.html", "/css/**", "/js/**", "/img/**", "/images/**", "/plugins/**", "/public/**", "/profile/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());
    }

    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(1800L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
