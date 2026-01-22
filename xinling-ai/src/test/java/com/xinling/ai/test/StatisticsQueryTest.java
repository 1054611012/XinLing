package com.xinling.ai.test;

import com.xinling.ai.utils.QueryTypeUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 统计类查询测试
 * 
 * @author SuXia
 * @date 2025/12/30
 */
public class StatisticsQueryTest {

    @Test
    public void testIsStatisticsQuery() {
        // 测试统计类查询识别
        assertTrue(QueryTypeUtils.isStatisticsQuery("统计用户数量"));
        assertTrue(QueryTypeUtils.isStatisticsQuery("有多少用户"));
        assertTrue(QueryTypeUtils.isStatisticsQuery("用户总数是多少"));
        assertTrue(QueryTypeUtils.isStatisticsQuery("统计用户分布情况"));
        assertTrue(QueryTypeUtils.isStatisticsQuery("用户占比分析"));
        assertTrue(QueryTypeUtils.isStatisticsQuery("用户排名"));
        assertTrue(QueryTypeUtils.isStatisticsQuery("用户趋势分析"));
        assertTrue(QueryTypeUtils.isStatisticsQuery("用户分布情况"));
        assertTrue(QueryTypeUtils.isStatisticsQuery("生成用户统计图"));
        assertTrue(QueryTypeUtils.isStatisticsQuery("用户汇总信息"));
        
        // 测试非统计类查询
        assertFalse(QueryTypeUtils.isStatisticsQuery("你好"));
        assertFalse(QueryTypeUtils.isStatisticsQuery("今天天气怎么样"));
        assertFalse(QueryTypeUtils.isStatisticsQuery("什么是人工智能"));
    }

    @Test
    public void testIsSqlQuery() {
        // 测试SQL查询识别
        assertTrue(QueryTypeUtils.isSqlQuery("查询用户信息"));
        assertTrue(QueryTypeUtils.isSqlQuery("select * from user"));
        assertTrue(QueryTypeUtils.isSqlQuery("数据库用户表"));
        assertTrue(QueryTypeUtils.isSqlQuery("查询表结构"));
        
        // 测试非SQL查询
        assertFalse(QueryTypeUtils.isSqlQuery("你好"));
        assertFalse(QueryTypeUtils.isSqlQuery("今天天气怎么样"));
    }
    
    @Test
    public void testIsKnowledgeQuery() {
        // 测试知识库查询识别
        assertTrue(QueryTypeUtils.isKnowledgeQuery("知识库中有什么信息"));
        assertTrue(QueryTypeUtils.isKnowledgeQuery("文档中关于用户管理的说明"));
        assertTrue(QueryTypeUtils.isKnowledgeQuery("什么是人工智能"));
        assertTrue(QueryTypeUtils.isKnowledgeQuery("如何使用系统"));
        assertTrue(QueryTypeUtils.isKnowledgeQuery("系统功能介绍"));
        assertTrue(QueryTypeUtils.isKnowledgeQuery("用户管理的定义"));
        
        // 测试非知识库查询
        assertFalse(QueryTypeUtils.isKnowledgeQuery("查询用户信息"));
        assertFalse(QueryTypeUtils.isKnowledgeQuery("统计用户数量"));
    }
}