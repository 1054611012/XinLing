package com.xinling.ai.domain.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author SuXia
 * @date 2026/1/20 10:00
 */
@Data
public class Nl2SqlResult {

    private String sql;                         // LLM 生成的 SQL
    private List<Map<String, Object>> data;     // SQL 查询结果
    private String answer;                      // 自然语言解释

    public Nl2SqlResult() {}

    public Nl2SqlResult(String sql, List<Map<String, Object>> data, String answer) {
        this.sql = sql;
        this.data = data;
        this.answer = answer;
    }
}
