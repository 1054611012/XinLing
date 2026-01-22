package com.xinling.framework.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "token")
public class TokenProperties {

    /**
     * 请求头名称
     */
    private String header = "Authorization";

    /**
     * JWT 密钥（必须 >= 64 字节）
     */
    private String secret;

    /**
     * 过期时间（分钟）
     */
    private Integer expireTime = 30;
}
