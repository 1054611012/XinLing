package com.xinling.framework.config;

import com.xinling.framework.config.properties.TokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TokenProperties.class)
public class TokenConfig {
}
