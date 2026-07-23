package com.xinling.framework.config;

import com.xinling.common.config.XinLingConfig;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private XinLingConfig xinLingConfig;

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置头像上传路径
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file:" + XinLingConfig.getAvatarPath() + "/");

        // 配置文件上传路径
        registry.addResourceHandler("/profile/**", "/dev-api/profile/**")
                .addResourceLocations("file:" + XinLingConfig.getProfile() + "/");

        // 配置uploads路径（支持直接访问和通过/dev-api访问）
        registry.addResourceHandler("/uploads/**", "/dev-api/uploads/**")
                .addResourceLocations("file:" + XinLingConfig.getUploadPath() + "/");

        // 配置Druid监控页面的静态资源映射
        registry.addResourceHandler("/druid/**")
                .addResourceLocations("classpath:/META-INF/resources/");
    }
}
