package com.xinling.framework.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * XSS配置属性
 *
 * @author xinling
 */
@ConfigurationProperties(prefix = "xss")
@Component
@Data
public class XssProperties {

    /**
     * 开启XSS过滤开关
     */
    private boolean enabled = true;

    /**
     * 排除路径，多个路径用逗号分隔
     */
    private String excludes = "/system/notice";

    /**
     * 需要过滤的URL模式，多个用逗号分隔
     */
    private String urlPatterns = "/system/*,/monitor/*,/tool/*";

}
