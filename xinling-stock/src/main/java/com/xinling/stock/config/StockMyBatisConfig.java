package com.xinling.stock.config;

import org.springframework.context.annotation.Configuration;

/**
 * 股票模块 MyBatis 配置
 *
 * Mapper 由框架全局 @MapperScan("com.xinling.**.mapper") 自动扫描
 * XML 映射由全局 mybatis.mapperLocations=classpath*:mapper/**\/*Mapper.xml 自动加载
 * 本类仅作为模块标记配置存在
 */
@Configuration
public class StockMyBatisConfig {

}
