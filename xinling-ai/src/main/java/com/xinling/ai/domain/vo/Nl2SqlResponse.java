package com.xinling.ai.domain.vo;

import lombok.Data;

/**
 * NL2SQL响应对象
 * 
 * @author SuXia
 * @date 2025/12/29
 */
@Data
public class Nl2SqlResponse {

    /**
     * 响应类型
     * - intent_analysis: 意图分析阶段
     * - sql_generation: SQL生成阶段
     * - query_results: 查询结果阶段
     * - explanation: 结果解释阶段
     */
    private String responseType;

    /**
     * 响应内容
     */
    private String content;

    /**
     * SQL语句（仅在sql_generation阶段有效）
     */
    private String sql;

    /**
     * 查询结果（仅在query_results阶段有效）
     */
    private Object results;

    /**
     * 是否结束
     */
    private Boolean finish = false;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 错误信息（如果有）
     */
    private String error;

    public static Nl2SqlResponse intentAnalysis(String content, String sessionId) {
        Nl2SqlResponse response = new Nl2SqlResponse();
        response.setResponseType("intent_analysis");
        response.setContent(content);
        response.setSessionId(sessionId);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static Nl2SqlResponse sqlGeneration(String sql, String sessionId) {
        Nl2SqlResponse response = new Nl2SqlResponse();
        response.setResponseType("sql_generation");
        response.setSql(sql);
        response.setContent("生成SQL: " + sql);
        response.setSessionId(sessionId);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static Nl2SqlResponse queryResults(Object results, String sessionId) {
        Nl2SqlResponse response = new Nl2SqlResponse();
        response.setResponseType("query_results");
        response.setResults(results);
        response.setContent("查询结果: " + results);
        response.setSessionId(sessionId);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static Nl2SqlResponse explanation(String content, String sessionId) {
        Nl2SqlResponse response = new Nl2SqlResponse();
        response.setResponseType("explanation");
        response.setContent(content);
        response.setSessionId(sessionId);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static Nl2SqlResponse finish(String sessionId) {
        Nl2SqlResponse response = new Nl2SqlResponse();
        response.setFinish(true);
        response.setSessionId(sessionId);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static Nl2SqlResponse error(String error, String sessionId) {
        Nl2SqlResponse response = new Nl2SqlResponse();
        response.setError(error);
        response.setSessionId(sessionId);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}