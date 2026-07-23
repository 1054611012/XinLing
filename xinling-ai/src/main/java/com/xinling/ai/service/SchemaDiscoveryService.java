package com.xinling.ai.service;

import com.xinling.common.constant.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 动态数据库 Schema 发现服务
 * 从 MySQL information_schema 自动获取所有表的列信息，生成 DDL 文本注入 System Prompt
 * 缓存到 Redis（TTL 1小时），避免每次查询都访问 DB
 *
 * @author SuXia
 * @date 2026/05/21
 */
@Slf4j
@Service
public class SchemaDiscoveryService {

    private static final String SCHEMA_CACHE_KEY = RedisKeys.AI_PREFIX + "schema:ddl";
    private static final long CACHE_TTL_HOURS = 1;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取数据库 DDL 描述（优先从 Redis 缓存读取）
     */
    public String getDatabaseDdl() {
        // 先查缓存
        String cached = redisTemplate.opsForValue().get(SCHEMA_CACHE_KEY);
        if (cached != null && !cached.isBlank()) {
            return cached;
        }

        // 缓存未命中，重新生成
        String ddl = generateDdlFromSchema();
        if (ddl != null && !ddl.isBlank()) {
            redisTemplate.opsForValue().set(SCHEMA_CACHE_KEY, ddl, CACHE_TTL_HOURS, TimeUnit.HOURS);
        }
        return ddl;
    }

    /**
     * 刷新缓存（表结构变更后调用）
     */
    public void refreshCache() {
        redisTemplate.delete(SCHEMA_CACHE_KEY);
        log.info("Schema 缓存已刷新");
    }

    /**
     * 从 information_schema 生成 DDL 描述文本
     */
    private String generateDdlFromSchema() {
        try {
            // 获取当前数据库名
            String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            if (dbName == null) {
                log.warn("无法获取当前数据库名");
                return null;
            }

            // 查询所有表名
            List<String> tables = jdbcTemplate.queryForList(
                    "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE'",
                    String.class, dbName);

            if (tables.isEmpty()) {
                log.warn("未找到任何表");
                return null;
            }

            StringBuilder ddl = new StringBuilder();
            ddl.append("-- 数据库: ").append(dbName).append("\n");
            ddl.append("-- 表数量: ").append(tables.size()).append("\n\n");

            for (String tableName : tables) {
                ddl.append(generateTableDdl(dbName, tableName)).append("\n");
            }

            return ddl.toString();
        } catch (Exception e) {
            log.error("生成数据库 DDL 描述失败", e);
            return null;
        }
    }

    /**
     * 生成单表的 DDL 描述
     */
    private String generateTableDdl(String dbName, String tableName) {
        try {
            // 查询列信息
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                    "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT, COLUMN_KEY " +
                    "FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
                    "ORDER BY ORDINAL_POSITION",
                    dbName, tableName);

            // 获取表注释
            String tableComment = jdbcTemplate.queryForObject(
                    "SELECT TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?",
                    String.class, dbName, tableName);
            if (tableComment == null || tableComment.isBlank()) {
                tableComment = tableName;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE ").append(tableName).append(" (");
            if (!tableComment.equals(tableName)) {
                sb.append(" -- ").append(tableComment);
            }
            sb.append("\n");

            for (int i = 0; i < columns.size(); i++) {
                Map<String, Object> col = columns.get(i);
                String colName = (String) col.get("COLUMN_NAME");
                String colType = (String) col.get("COLUMN_TYPE");
                String nullable = "YES".equals(col.get("IS_NULLABLE")) ? "NULL" : "NOT NULL";
                String defaultValue = col.get("COLUMN_DEFAULT") != null ? " DEFAULT " + col.get("COLUMN_DEFAULT") : "";
                String comment = col.get("COLUMN_COMMENT") != null ? (String) col.get("COLUMN_COMMENT") : "";
                String key = "PRI".equals(col.get("COLUMN_KEY")) ? " PRIMARY KEY" : "";

                sb.append("  ").append(colName).append(" ").append(colType)
                        .append(" ").append(nullable).append(defaultValue).append(key);

                if (!comment.isBlank()) {
                    sb.append(" -- ").append(comment);
                }
                if (i < columns.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append(");\n");

            return sb.toString();
        } catch (Exception e) {
            log.error("生成表 {} 的 DDL 描述失败", tableName, e);
            return "-- 表 " + tableName + " (无法获取结构: " + e.getMessage() + ")\n";
        }
    }

    /**
     * 启动时预热缓存
     */
    @PostConstruct
    public void warmUpCache() {
        try {
            log.info("预热 Schema 缓存...");
            getDatabaseDdl();
            log.info("Schema 缓存预热完成");
        } catch (Exception e) {
            log.warn("Schema 缓存预热失败（可能数据库未就绪），将在首次查询时延迟加载: {}", e.getMessage());
        }
    }
}
