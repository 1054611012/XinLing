package com.xinling.framework.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;

import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import com.xinling.common.utils.StringUtils;

/**
 * MyBatis配置，支持*扫描包、可选配置文件、mapperLocations多模块扫描
 * @author xinling
 */
@Configuration
public class MyBatisConfig {

    @Autowired
    private Environment env;

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    /**
     * 扫描 typeAliasesPackage，支持多包逗号分隔
     */
    public static String setTypeAliasesPackage(String typeAliasesPackage) {
        if (typeAliasesPackage == null || typeAliasesPackage.trim().isEmpty()) {
            return null;
        }

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        List<String> allResult = new ArrayList<>();

        try {
            for (String aliasesPackage : typeAliasesPackage.split(",")) {
                List<String> result = new ArrayList<>();
                String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(aliasesPackage.trim()) + "/" + DEFAULT_RESOURCE_PATTERN;
                Resource[] resources = resolver.getResources(searchPath);
                if (resources != null && resources.length > 0) {
                    for (Resource resource : resources) {
                        if (resource.isReadable()) {
                            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                            try {
                                result.add(Class.forName(metadataReader.getClassMetadata().getClassName()).getPackage().getName());
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (!result.isEmpty()) {
                    allResult.addAll(new HashSet<>(result)); // 去重
                }
            }

            if (!allResult.isEmpty()) {
                return String.join(",", allResult);
            } else {
                System.out.println("Warning: 未找到任何包，使用原始typeAliasesPackage：" + typeAliasesPackage);
                return typeAliasesPackage;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return typeAliasesPackage;
    }

    /**
     * 解析 mapperLocations 支持通配符
     */
    public Resource[] resolveMapperLocations(String[] mapperLocations) {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<>();
        if (mapperLocations != null) {
            for (String mapperLocation : mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    System.out.println("Warning: Mapper file not found: " + mapperLocation);
                }
            }
        }
        return resources.toArray(new Resource[0]);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dataSource) throws Exception {
        // 获取配置，给默认值避免 null
        String typeAliasesPackage = env.getProperty("mybatis.typeAliasesPackage", "com.xinling.**.domain");
        String mapperLocations = env.getProperty("mybatis.mapperLocations", "classpath*:mapper/**/*Mapper.xml");
        String configLocation = env.getProperty("mybatis.configLocation", "classpath:mybatis/mybatis-config.xml");

        typeAliasesPackage = setTypeAliasesPackage(typeAliasesPackage);
        VFS.addImplClass(SpringBootVFS.class);

        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        if (typeAliasesPackage != null && !typeAliasesPackage.isEmpty()) {
            sessionFactory.setTypeAliasesPackage(typeAliasesPackage);
        }
        if (mapperLocations != null && !mapperLocations.isEmpty()) {
            sessionFactory.setMapperLocations(resolveMapperLocations(StringUtils.split(mapperLocations, ",")));
        }

        // 配置文件可选，不存在也不会报错
        if (configLocation != null && !configLocation.isEmpty()) {
            Resource resource = new DefaultResourceLoader().getResource(configLocation);
            if (resource.exists()) {
                sessionFactory.setConfigLocation(resource);
            } else {
                System.out.println("Warning: MyBatis config file not found: " + configLocation);
            }
        }

        return sessionFactory.getObject();
    }
}
