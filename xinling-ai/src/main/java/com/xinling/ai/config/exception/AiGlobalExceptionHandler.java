package com.xinling.ai.config.exception;

import com.xinling.common.core.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * AI模块全局异常处理器
 *
 * @author SuXia
 * @date 2025/12/30
 */
@RestControllerAdvice(basePackages = "com.xinling.ai")
public class AiGlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AiGlobalExceptionHandler.class);

    /**
     * SQL语法异常处理
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public AjaxResult handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error("SQL语法错误: {}", e.getMessage(), e);
        return AjaxResult.error("SQL语法错误: " + e.getMessage());
    }

    /**
     * 数据访问异常处理
     */
    @ExceptionHandler(DataAccessException.class)
    public AjaxResult handleDataAccessException(DataAccessException e) {
        log.error("数据访问异常: {}", e.getMessage(), e);
        
        String message = e.getMessage();
        if (message != null) {
            if (message.contains("Statement.executeQuery() cannot issue statements that do not produce result sets")) {
                return AjaxResult.error("SQL执行失败: 只支持SELECT查询语句，请检查您的SQL是否正确");
            } else if (message.contains("doesn't exist")) {
                return AjaxResult.error("SQL执行失败: 表或字段不存在");
            } else if (message.contains("access denied")) {
                return AjaxResult.error("SQL执行失败: 数据库访问权限不足");
            }
        }
        
        return AjaxResult.error("数据访问异常: " + message);
    }

    /**
     * SQL异常处理
     */
    @ExceptionHandler(SQLException.class)
    public AjaxResult handleSQLException(SQLException e) {
        log.error("SQL异常: {}", e.getMessage(), e);
        return AjaxResult.error("SQL执行错误: " + e.getMessage());
    }

    /**
     * 非法参数异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public AjaxResult handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数: {}", e.getMessage(), e);
        return AjaxResult.error("参数错误: " + e.getMessage());
    }

    /**
     * 运行时异常处理
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        
        String message = e.getMessage();
        if (message != null && message.contains("SQL 执行失败")) {
            return AjaxResult.error(message);
        }
        
        return AjaxResult.error("系统异常: " + message);
    }
}