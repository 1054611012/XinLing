package com.xinling.ai.service.tools;

import com.xinling.ai.service.SchemaDiscoveryService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 数据库 Schema 查询工具 — LLM 可调用以探索数据库表结构
 *
 * 让 AI 能够了解数据库中有哪些表和字段，从而生成有意义的 SQL 查询。
 *
 * @author SuXia
 * @date 2026/07/14
 */
@Slf4j
@Component
public class SchemaQueryTool {

    @Autowired
    private SchemaDiscoveryService schemaDiscoveryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Tool("获取整个数据库的完整DDL结构（所有表和字段信息）。用于了解数据库中有哪些表、字段、类型、注释等。首次调用会生成缓存，后续返回缓存结果")
    public String getDatabaseSchema() {
        log.info("SchemaQueryTool - getDatabaseSchema 调用");
        try {
            String ddl = schemaDiscoveryService.getDatabaseDdl();
            if (ddl == null || ddl.isBlank()) {
                return "数据库结构信息不可用。";
            }
            // DDL可能很长，截断到合理长度
            if (ddl.length() > 8000) {
                return ddl.substring(0, 8000) + "\n...(结构信息较长，已截断。可查询具体表的详细信息)";
            }
            return ddl;
        } catch (Exception e) {
            log.error("获取数据库Schema失败", e);
            return "获取数据库结构出错: " + e.getMessage();
        }
    }

    @Tool("获取指定表的字段信息（列名、类型、是否可空、注释等）。在了解整体库结构后，用于深入查看某张表的详细字段定义")
    public String getTableSchema(@P("表名") String tableName) {
        log.info("SchemaQueryTool - getTableSchema: {}", tableName);
        try {
            String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            if (dbName == null) {
                return "无法获取当前数据库名";
            }

            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                    "SELECT COLUMN_NAME, COLUMN_TYPE, DATA_TYPE, IS_NULLABLE, " +
                    "       IFNULL(COLUMN_DEFAULT, '') AS COLUMN_DEFAULT, " +
                    "       IFNULL(COLUMN_COMMENT, '') AS COLUMN_COMMENT, " +
                    "       COLUMN_KEY, EXTRA " +
                    "FROM information_schema.COLUMNS " +
                    "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
                    "ORDER BY ORDINAL_POSITION",
                    dbName, tableName);

            if (columns.isEmpty()) {
                return "数据库中不存在表: " + tableName;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("表: ").append(tableName).append("\n");
            sb.append(String.format("%-20s %-25s %-8s %-8s %s\n",
                    "字段名", "类型", "可空", "键", "注释"));
            sb.append("-".repeat(80)).append("\n");

            for (Map<String, Object> col : columns) {
                sb.append(String.format("%-20s %-25s %-8s %-8s %s\n",
                        col.get("COLUMN_NAME"),
                        col.get("COLUMN_TYPE"),
                        "YES".equals(col.get("IS_NULLABLE")) ? "YES" : "NO",
                        "PRI".equals(col.get("COLUMN_KEY")) ? "PRI" :
                        "UNI".equals(col.get("COLUMN_KEY")) ? "UNI" :
                        "MUL".equals(col.get("COLUMN_KEY")) ? "MUL" : "",
                        col.get("COLUMN_COMMENT")));
            }

            return sb.toString();
        } catch (Exception e) {
            log.error("获取表结构失败: {}", tableName, e);
            return "获取表结构出错: " + e.getMessage();
        }
    }

    @Tool("获取系统中所有业务表的名称列表。用于快速了解数据库有哪些表，再决定查看具体哪张表的详细信息")
    public String listAllTables() {
        log.info("SchemaQueryTool - listAllTables 调用");
        try {
            String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            if (dbName == null) {
                return "无法获取当前数据库名";
            }

            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                    "SELECT TABLE_NAME, TABLE_COMMENT " +
                    "FROM information_schema.TABLES " +
                    "WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE' " +
                    "ORDER BY TABLE_NAME",
                    dbName);

            if (tables.isEmpty()) {
                return "数据库中没有表";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("数据库: ").append(dbName).append("\n");
            sb.append("共 ").append(tables.size()).append(" 张表\n\n");
            sb.append(String.format("%-35s %s\n", "表名", "注释"));
            sb.append("-".repeat(65)).append("\n");

            for (Map<String, Object> table : tables) {
                String comment = table.get("TABLE_COMMENT") != null ?
                        (String) table.get("TABLE_COMMENT") : "";
                sb.append(String.format("%-35s %s\n", table.get("TABLE_NAME"), comment));
            }

            return sb.toString();
        } catch (Exception e) {
            log.error("获取表列表失败", e);
            return "获取表列表出错: " + e.getMessage();
        }
    }
}
