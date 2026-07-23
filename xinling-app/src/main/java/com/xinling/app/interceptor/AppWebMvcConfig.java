package com.xinling.app.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppWebMvcConfig implements WebMvcConfigurer {
    private final AppAuthInterceptor appAuthInterceptor;

    public AppWebMvcConfig(AppAuthInterceptor appAuthInterceptor) {
        this.appAuthInterceptor = appAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appAuthInterceptor)
                .addPathPatterns("/api/app/**")
                .excludePathPatterns(
                        "/api/app/user/sendCode",
                        "/api/app/user/login",
                        "/api/app/user/thirdLogin",
                        "/api/app/user/refreshToken"
                );
    }
}
