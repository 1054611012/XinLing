package com.xinling.framework.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Referer过滤配置属性
 *
 * @author xinling
 */
@ConfigurationProperties(prefix = "referer")
@Component
public class RefererProperties {

    /**
     * 开启Referer过滤开关
     */
    private boolean enabled = false;

    /**
     * 允许的域名列表，多个域名用逗号分隔
     */
    private String allowedDomains = "localhost,127.0.0.1,xinling.vip,www.xinling.vip";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAllowedDomains() {
        return allowedDomains;
    }

    public void setAllowedDomains(String allowedDomains) {
        this.allowedDomains = allowedDomains;
    }
}