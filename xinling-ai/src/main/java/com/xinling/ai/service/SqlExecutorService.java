package com.xinling.ai.service;

import com.xinling.ai.service.sql.SqlExecuteResult;
import com.xinling.ai.service.sql.SqlType;
import com.xinling.ai.service.utils.SqlAliasExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SqlExecutorService {

    private static final Logger log = LoggerFactory.getLogger(SqlExecutorService.class);

    private final JdbcTemplate jdbcTemplate;

    public SqlExecutorService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 🚀 自动识别 SQL 类型并执行（自愈版）
     */
    public SqlExecuteResult executeAuto(String sql) {
        sql = sql.trim();
        
        // 首先判断SQL类型，避免在非查询语句上执行查询方法
        String trimmedSql = sql.trim();
        String upperSql = trimmedSql.toUpperCase();
        
        // 如果是非SELECT语句，直接抛出异常
        if (!upperSql.startsWith("SELECT")) {
            throw new IllegalArgumentException("只支持SELECT查询语句，当前SQL: " + sql);
        }
        
        // 更新sql变量为去除首尾空格的版本
        sql = trimmedSql;

        // 1️⃣ 尝试单值聚合查询（COUNT/SUM/MAX/MIN）
        try {
            Long scalar = jdbcTemplate.queryForObject(sql, Long.class);
            if (scalar != null) {
                // 检查是否包含聚合函数，如果是则提取别名并封装
                Object resultData;
                if (SqlAliasExtractor.containsAggregateFunction(sql)) {
                    resultData = SqlAliasExtractor.wrapScalarForResult(sql, scalar);
                } else {
                    resultData = scalar;
                }
                return new SqlExecuteResult(SqlType.SCALAR, resultData);
            }
        } catch (DataAccessException e) {
            log.debug("非单值查询，继续尝试其他类型: {}", e.getMessage());
            // 不是单值，继续执行
        } catch (Exception e) {
            log.error("单值查询执行失败: {}", sql, e);
            throw new RuntimeException("SQL 执行失败: " + e.getMessage(), e);
        }

        // 2️⃣ 尝试多行多列查询
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            if (rows.isEmpty()) {
                return new SqlExecuteResult(SqlType.MULTI_COLUMN, rows);
            }

            // 判断是否单列
            if (rows.get(0).size() == 1) {
                List<Object> singleColumn = rows.stream()
                        .map(m -> m.values().iterator().next())
                        .toList();
                return new SqlExecuteResult(SqlType.SINGLE_COLUMN, singleColumn);
            }

            return new SqlExecuteResult(SqlType.MULTI_COLUMN, rows);

        } catch (DataAccessException e) {
            log.error("查询执行失败: {}", sql, e);
            throw new RuntimeException("SQL 执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 单值聚合
     */
    public long executeScalarLong(String sql) {
        try {
            // 验证是否为SELECT语句
            String trimmedSql = sql.trim();
            if (!trimmedSql.toUpperCase().matches("^\\s*SELECT\\s+.*")) {
                throw new IllegalArgumentException("只支持SELECT查询语句");
            }
            sql = trimmedSql;
            
            Long value = jdbcTemplate.queryForObject(sql, Long.class);
            return value != null ? value : 0L;
        } catch (DataAccessException e) {
            log.error("标量查询执行失败: {}", sql, e);
            throw new RuntimeException("标量查询执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 单列多行
     */
    public List<Object> executeSingleColumn(String sql) {
        try {
            // 验证是否为SELECT语句
            String trimmedSql = sql.trim();
            if (!trimmedSql.toUpperCase().matches("^\\s*SELECT\\s+.*")) {
                throw new IllegalArgumentException("只支持SELECT查询语句");
            }
            sql = trimmedSql;
            
            return jdbcTemplate.query(sql, new SingleColumnRowMapper<>());
        } catch (DataAccessException e) {
            log.error("单列查询执行失败: {}", sql, e);
            throw new RuntimeException("单列查询执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 多列结果
     */
    public List<Map<String, Object>> executeMultiColumn(String sql) {
        try {
            // 验证是否为SELECT语句
            String trimmedSql = sql.trim();
            if (!trimmedSql.toUpperCase().matches("^\\s*SELECT\\s+.*")) {
                throw new IllegalArgumentException("只支持SELECT查询语句");
            }
            sql = trimmedSql;
            
            return jdbcTemplate.queryForList(sql);
        } catch (DataAccessException e) {
            log.error("多列查询执行失败: {}", sql, e);
            throw new RuntimeException("多列查询执行失败: " + e.getMessage(), e);
        }
    }
}
