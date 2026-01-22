package com.xinling.web.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.xinling.common.config.XinLingConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * SpringDoc OpenAPI 配置
 *
 * @author xinling
 */
@AutoConfiguration
@EnableWebMvc
public class SwaggerConfig
{
    @Autowired
    private XinLingConfig xinLingConfig;

    @Value("${swagger.enabled:true}")
    private boolean enabled;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("心灵管理系统_接口文档")
                        .description("描述：用于管理集团旗下公司的人员信息,具体包括XXX,XXX模块...")
                        .contact(new Contact()
                                .name(xinLingConfig.getName())
                                .url("http://www.xinling.vip"))
                        .version(xinLingConfig.getVersion())
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}