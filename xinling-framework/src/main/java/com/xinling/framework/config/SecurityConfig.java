package com.xinling.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.DispatcherType;

import com.xinling.framework.config.properties.PermitAllUrlProperties;
import com.xinling.framework.security.filter.JwtAuthenticationTokenFilter;
import com.xinling.framework.security.handle.AuthenticationEntryPointImpl;
import com.xinling.framework.security.handle.LogoutSuccessHandlerImpl;

@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
@ConditionalOnProperty(name = "xinling.security.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    @Autowired
    private CorsFilter corsFilter;

    @Autowired
    private PermitAllUrlProperties permitAllUrl;

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.cacheControl(cache -> cache.disable())
                        .frameOptions(options -> options.sameOrigin()))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // 放行 ASYNC Dispatcher（Flux / SSE）
                    auth.dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll();

                    // 放行 permitAllUrl 配置
                    for (String url : permitAllUrl.getUrls()) {
                        if (url != null) auth.requestMatchers(url).permitAll();
                    }

                    // 放行聊天接口
                    auth.requestMatchers("/chat", "/ai/chat", "/api/ai/chat", "/ai/**").permitAll();

                    // 登录及公共接口
                    auth.requestMatchers("/login", "/register", "/captchaImage",
                            "/api/noticeList", "/push/connect/**", "/push/**",
                            "/push/send/**", "/push/status", "/push/disconnect/**").permitAll();

                    // APP移动端全部放行（APP使用独立的 AppAuthInterceptor 认证）
                    auth.requestMatchers("/api/app/**").permitAll();

                    // Website端全部放行
                    auth.requestMatchers("/api/website/**").permitAll();

                    // 官网页面放行
                    auth.requestMatchers("/website/**").permitAll();

                    // Website端全部放行
                    auth.requestMatchers("/api/website/**").permitAll();

                    // 官网页面放行
                    auth.requestMatchers("/website/**").permitAll();

                    // 静态资源（含 /dev-api 前缀，兼容前端代理）
                    auth.requestMatchers(HttpMethod.GET,
                            "/", "/*.html", "/css/**", "/js/**", "/images/**",
                            "/img/**", "/plugins/**", "/static/**", "/public/**",
                            "/profile/**", "/uploads/**", "/dev-api/uploads/**",
                            "/dev-api/profile/**", "/favicon.ico", "*.css", "*.js").permitAll();

                    // Knife4j / Swagger / Druid
                    auth.requestMatchers("/doc.html", "/swagger-ui.html", "/swagger-ui/**",
                            "/webjars/**", "/v3/api-docs/**", "/v3/api-docs",
                            "/api-docs/**", "/api-docs", "/druid/**").permitAll();

                    // 其余接口需要认证
                    auth.anyRequest().authenticated();
                })
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler))
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class)
                .addFilterBefore(corsFilter, LogoutFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
