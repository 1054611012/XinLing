package com.xinling.stock.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 股票模块 Spring 配置
 *
 * 启用组件扫描，自动注册 Service、Controller 等 Bean
 */
@Configuration
@ComponentScan(basePackages = "com.xinling.stock")
public class StockConfig {

}
