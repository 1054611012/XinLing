package com.xinling.ai.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

/**
 * SQL别名提取工具类
 * 用于从SQL语句中提取字段别名，特别是COUNT等聚合函数的别名
 *
 * @author SuXia
 * @date 2026/2/3
 */
public class SqlAliasExtractor {

    private static final Logger log = LoggerFactory.getLogger(SqlAliasExtractor.class);

    // 匹配聚合函数的正则表达式
    private static final Pattern AGGREGATE_FUNCTION_PATTERN = 
        Pattern.compile("(COUNT|SUM|MAX|MIN|AVG)\\s*\\([^)]+\\)\\s*(?:AS\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)", 
                       Pattern.CASE_INSENSITIVE);
    
    // 匹配简单字段别名的正则表达式
    private static final Pattern SIMPLE_ALIAS_PATTERN = 
        Pattern.compile("\\*\\s*(?:AS\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)", 
                       Pattern.CASE_INSENSITIVE);

    /**
     * 从SQL语句中提取字段别名
     * @param sql SQL语句
     * @return 别名映射，key为别名，value为原始表达式
     */
    public static Map<String, String> extractAliases(String sql) {
        Map<String, String> aliases = new HashMap<>();
        
        if (sql == null || sql.trim().isEmpty()) {
            return aliases;
        }

        try {
            // 提取聚合函数别名
            Matcher aggregateMatcher = AGGREGATE_FUNCTION_PATTERN.matcher(sql);
            while (aggregateMatcher.find()) {
                String functionName = aggregateMatcher.group(1);
                String alias = aggregateMatcher.group(2);
                String fullExpression = aggregateMatcher.group(0);
                
                // 移除AS关键字获取干净的别名
                alias = alias.replaceAll("(?i)^AS\\s+", "").trim();
                
                aliases.put(alias, fullExpression);
                log.debug("发现聚合函数别名: {} -> {}", alias, fullExpression);
            }

            // 提取简单字段别名（如 SELECT * AS alias_name）
            Matcher simpleMatcher = SIMPLE_ALIAS_PATTERN.matcher(sql);
            while (simpleMatcher.find()) {
                String alias = simpleMatcher.group(1);
                aliases.put(alias, "*");
                log.debug("发现简单别名: {} -> *", alias);
            }

        } catch (Exception e) {
            log.warn("解析SQL别名时出现异常: {}", e.getMessage());
        }

        return aliases;
    }

    /**
     * 判断SQL是否包含聚合函数
     * @param sql SQL语句
     * @return 是否包含聚合函数
     */
    public static boolean containsAggregateFunction(String sql) {
        if (sql == null) return false;
        
        String upperSql = sql.toUpperCase();
        return upperSql.contains("COUNT(") || 
               upperSql.contains("SUM(") || 
               upperSql.contains("MAX(") || 
               upperSql.contains("MIN(") || 
               upperSql.contains("AVG(");
    }

    /**
     * 将标量值包装为适合JSON序列化的格式，使用SQL中的别名作为key
     * @param sql SQL语句
     * @param scalarValue 标量值
     * @return 包含别名key的Map或直接返回值
     */
    public static Object wrapScalarForResult(String sql, Object scalarValue) {
        // 提取别名
        Map<String, String> aliases = extractAliases(sql);
        
        if (!aliases.isEmpty()) {
            // 使用第一个找到的别名创建简单的键值对结构
            String firstAlias = aliases.keySet().iterator().next();
            Map<String, Object> result = new HashMap<>();
            result.put(firstAlias, scalarValue);
            return result;
        } else {
            // 如果没有找到别名，直接返回值
            return scalarValue;
        }
    }
}