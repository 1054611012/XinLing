package com.xinling.framework.config;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Value;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;

/**
 * druid 配置
 *
 * @author xinling
 */
@Configuration
public class DruidConfig
{
    @Value("${spring.datasource.druid.master.url:jdbc:mysql://127.0.0.1:3306/su_crm?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&connectTimeout=60000&socketTimeout=60000&allowPublicKeyRetrieval=true}")
    private String masterUrl;
    
    @Value("${spring.datasource.druid.master.username:root}")
    private String masterUsername;
    
    @Value("${spring.datasource.druid.master.password:123456}")
    private String masterPassword;
    
    @Value("${spring.datasource.druid.master.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String masterDriverClassName;

    @Bean
    @Primary
    public DataSource dynamicDataSource()
    {
        DruidDataSource dataSource = new DruidDataSource();
        // 设置基本数据库连接信息（从配置文件读取）
        dataSource.setUrl(masterUrl);
        dataSource.setUsername(masterUsername);
        dataSource.setPassword(masterPassword);
        dataSource.setDriverClassName(masterDriverClassName);
        
        // 设置连接池参数
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(10);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(60000);
        dataSource.setConnectTimeout(30000);
        dataSource.setSocketTimeout(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setMaxEvictableIdleTimeMillis(900000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        
        return dataSource;
    }

    /**
     * 去除监控页面底部的广告
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean removeDruidFilterRegistrationBean()
    {
        // 创建filter进行过滤
        Filter filter = new Filter()
        {
            @Override
            public void init(jakarta.servlet.FilterConfig filterConfig) throws ServletException
            {
            }
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException
            {
                chain.doFilter(request, response);
                // 重置缓冲区，响应头不会被重置
                response.resetBuffer();
                // 获取common.js
                String text = Utils.readFromResource("support/http/resources/js/common.js");
                // 正则替换banner, 除去底部的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                response.getWriter().write(text);
            }
            @Override
            public void destroy()
            {
            }
        };
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*/js/common.js"); // 修改为通用的js路径匹配
        return registrationBean;
    }
}