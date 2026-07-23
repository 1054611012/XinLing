package com.xinling.ai.domain.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL执行结果DTO
 * 用于承载SQL查询的执行结果数据
 *
 * @author SuXia
 * @date 2026/2/3
 */
@Data
public class SqlExecuteDto {

    /**
     * SQL类型：SCALAR(单值) / SINGLE_COLUMN(单列) / MULTI_COLUMN(多列)
     */
    private String type;

    /**
     * 查询结果数据
     */
    private Object data;

    /**
     * 结果行数（用于前端展示统计信息）
     */
    private Integer rowCount;

    public SqlExecuteDto() {}

    public SqlExecuteDto(String type, Object data) {
        this.type = type;
        this.data = data;
        // 计算行数
        if (data instanceof List) {
            this.rowCount = ((List<?>) data).size();
        } else if (data instanceof Number) {
            this.rowCount = 1;
        } else {
            this.rowCount = 0;
        }
    }

    /**
     * 创建单值结果
     */
    public static SqlExecuteDto ofScalar(String type, Object value) {
        return new SqlExecuteDto(type, value);
    }

    /**
     * 创建单列结果
     */
    public static SqlExecuteDto ofSingleColumn(String type, List<Object> values) {
        return new SqlExecuteDto(type, values);
    }

    /**
     * 创建多列结果
     */
    public static SqlExecuteDto ofMultiColumn(String type, List<Map<String, Object>> rows) {
        return new SqlExecuteDto(type, rows);
    }
}