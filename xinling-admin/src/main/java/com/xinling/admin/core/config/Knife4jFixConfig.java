package com.xinling.admin.core.config;

import com.github.xiaoymin.knife4j.spring.configuration.Knife4jProperties;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jSetting;
import com.github.xiaoymin.knife4j.spring.extension.Knife4jOpenApiCustomizer;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.xiaoymin.knife4j.core.model.MarkdownProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Knife4j OpenApiCustomizer 补丁.
 * <p>
 * Knife4j 4.5.0 的 addOrderExtension() 调用了
 * SpringDocConfigProperties.getGroupConfigs()，期望返回类型为 List，
 * 但 springdoc 2.7+ 改成了 Set，导致 NoSuchMethodError。
 * <p>
 * 此补丁通过在匿名子类中捕获必要属性，覆盖 customise() 方法
 * 跳过 addOrderExtension()，同时保留 x-setting / x-markdownFiles 等功能。
 */
@Configuration
public class Knife4jFixConfig {

    @Bean
    @ConditionalOnMissingBean
    public Knife4jOpenApiCustomizer knife4jOpenApiCustomizer(
            Knife4jProperties knife4jProperties,
            SpringDocConfigProperties docProperties) {

        // 保存字段快照，匿名子类中无法访问父类包级私有字段
        final boolean enabled = knife4jProperties.isEnable();
        final Knife4jSetting setting = knife4jProperties.getSetting();
        final List<MarkdownProperty> documents = knife4jProperties.getDocuments();

        return new Knife4jOpenApiCustomizer(knife4jProperties, docProperties) {
            @Override
            public void customise(OpenAPI openApi) {
                if (!enabled) {
                    return;
                }

                OpenApiExtensionResolver resolver =
                        new OpenApiExtensionResolver(setting, documents);
                resolver.start();

                Map<String, Object> extensions = new HashMap<>();
                extensions.put("x-setting", setting);
                extensions.put("x-markdownFiles", resolver.getMarkdownFiles());
                openApi.addExtension("x-openapi", extensions);

                // 有意跳过 addOrderExtension(openApi):
                // 该方法调用的 SpringDocConfigProperties.getGroupConfigs()
                // 在 springdoc 2.7+ 中返回类型从 List 改为 Set，与 Knife4j 4.5.0 不兼容。
                // 缺失的功能仅是 @ApiSupport.order 标签排序，不影响接口文档正常展示和调试。
            }
        };
    }
}
