package com.xinling.ai.service.tools;

import com.xinling.ai.service.SqlExecutorService;
import com.xinling.ai.service.sql.SqlExecuteResult;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据库查询工具 — LLM 可调用以执行 SQL 查询
 *
 * @author SuXia
 * @date 2026/05/21
 */
@Slf4j
@Component
public class DatabaseQueryTool {

    @Autowired
    private SqlExecutorService sqlExecutorService;

    @Tool("执行MySQL SELECT查询。用于统计数量、查询列表、数据分析等场景。SQL必须以SELECT开头，禁止INSERT/UPDATE/DELETE/DDL")
    public String executeSql(@P("SQL查询语句，必须以SELECT开头，仅允许一条SQL") String sql) {
        log.info("DatabaseQueryTool 调用，SQL: {}", sql);
        try {
            // 安全校验：只允许 SELECT
            String trimmed = sql.trim();
            String upperSql = trimmed.toUpperCase();

            if (!upperSql.startsWith("SELECT")) {
                return "错误: 只允许执行 SELECT 查询语句。提供的SQL: " + sql;
            }

            // 禁止危险关键字
            String[] dangerous = {"INSERT", "UPDATE", "DELETE", "DROP", "TRUNCATE",
                    "ALTER", "CREATE", "EXEC", "EXECUTE", "INTO", "LOAD", "GRANT", "REVOKE"};
            for (String keyword : dangerous) {
                if (upperSql.contains(keyword)) {
                    return "错误: SQL包含禁止的关键字: " + keyword;
                }
            }

            SqlExecuteResult result = sqlExecutorService.executeAuto(trimmed);
            return formatResult(result);
        } catch (Exception e) {
            log.error("数据库查询执行失败: {}", sql, e);
            return "数据库查询出错: " + e.getMessage();
        }
    }

    private String formatResult(SqlExecuteResult result) {
        if (result.getData() == null) {
            return "查询结果为空。";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("查询结果 (").append(result.getType()).append("):\n");
        sb.append(result.getData().toString());

        if (sb.length() > 4000) {
            return sb.substring(0, 4000) + "\n...(结果过长，已截断)";
        }
        return sb.toString();
    }
}
