package com.xinling.ai.controller;

import com.xinling.ai.domain.dto.SqlExecuteDto;
import com.xinling.ai.domain.dto.SqlRequest;
import com.xinling.ai.service.SqlExecutorService;
import com.xinling.ai.service.sql.SqlExecuteResult;
import com.xinling.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * SQL 执行控制器
 * 提供自然语言转 SQL 的执行能力
 *
 * @author SuXia
 */
@Slf4j
@RestController
@RequestMapping("/ai/sql")
public class SqlController {

    @Autowired
    private SqlExecutorService sqlExecutorService;

    /**
     * 执行 SQL
     */
    @PostMapping("/execute")
    public AjaxResult executeSql(@RequestBody SqlRequest request) {
        try {
            String sql = request.getSql();
            if (sql != null) {
                sql = sql.replace("\n", " ").replace("\r", " ");
                sql = sql.replaceAll("\\s+", " ").trim();
                if (sql.endsWith(";")) {
                    sql = sql.substring(0, sql.length() - 1).trim();
                }
            }

            log.info("执行SQL: {}", sql);
            SqlExecuteResult result = sqlExecutorService.executeAuto(sql);
            SqlExecuteDto dto = new SqlExecuteDto(result.getType().name(), result.getData());

            return AjaxResult.success("SQL执行成功", dto);
        } catch (Exception e) {
            log.error("SQL执行失败: {}", request.getSql(), e);
            return AjaxResult.error("SQL执行失败: " + e.getMessage());
        }
    }
}
