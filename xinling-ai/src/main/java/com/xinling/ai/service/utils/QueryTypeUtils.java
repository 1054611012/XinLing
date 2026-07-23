package com.xinling.ai.service.utils;

import com.xinling.ai.domain.enums.RagScene;

/**
 * 查询类型识别工具类
 *
 * @author SuXia
 * @date 2025/12/30
 */
public class QueryTypeUtils {

    /**
     * 判断是否为SQL查询
     * @param lowerQuery 已转小写的查询内容
     */
    public static boolean isSqlQuery(String lowerQuery) {
        return lowerQuery.contains("查询") || lowerQuery.contains("select") ||
               lowerQuery.contains("数据库") || lowerQuery.contains("表") ||
               lowerQuery.contains("字段") || lowerQuery.contains("数据");
    }

    /**
     * 判断是否为统计类查询
     * @param lowerQuery 已转小写的查询内容
     */
    public static boolean isStatisticsQuery(String lowerQuery) {
        return lowerQuery.contains("统计") || lowerQuery.contains("有多少") ||
               lowerQuery.contains("总计") || lowerQuery.contains("总数") ||
               lowerQuery.contains("数量") || lowerQuery.contains("占比") ||
               lowerQuery.contains("比例") || lowerQuery.contains("排名") ||
               lowerQuery.contains("排行") || lowerQuery.contains("分析") ||
               lowerQuery.contains("趋势") || lowerQuery.contains("分布") ||
               lowerQuery.contains("汇总") || lowerQuery.contains("图表");
    }

    /**
     * 判断是否为知识库查询
     * @param lowerQuery 已转小写的查询内容
     */
    public static boolean isKnowledgeQuery(String lowerQuery) {
        if (lowerQuery == null || lowerQuery.isBlank()) {
            return false;
        }

        // 知识库相关关键词
        String[] keywords = {
            "知识", "文档", "介绍", "定义", "说明",
            "是什么", "什么是",
            "怎么", "如何",
            "知识库"
        };
        for (String kw : keywords) {
            if (lowerQuery.contains(kw)) {
                return true;
            }
        }

        // 其他情况返回false
        return false;
    }

    /**
     * 统一意图识别方法
     * @param query 用户输入
     * @return RagScene
     */
    public static RagScene detect(String query) {
        if (query == null || query.isBlank()) {
            return RagScene.QA;
        }

        String lowerQuery = query.toLowerCase();

        // NL2SQL：统计 & SQL 属于同一类
        if (isStatisticsQuery(lowerQuery) || isSqlQuery(lowerQuery)) {
            return RagScene.NL2SQL;
        }

        // 知识库 / 说明类
        if (isKnowledgeQuery(lowerQuery)) {
            return RagScene.KNOWLEDGE_QA;
        }

        // 默认兜底
        return RagScene.QA;
    }
}
