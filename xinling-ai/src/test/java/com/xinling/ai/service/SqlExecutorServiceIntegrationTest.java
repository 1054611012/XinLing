package com.xinling.ai.service;

import com.xinling.ai.service.sql.SqlExecuteResult;
import com.xinling.ai.service.sql.SqlType;
import com.xinling.ai.service.utils.SqlAliasExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class SqlExecutorServiceIntegrationTest {

    @Test
    public void testCountQueryWithAlias() {
        String sql = "SELECT COUNT(user_id) AS total_user_count FROM sys_user WHERE status = '0'";
        
        // 测试别名提取
        boolean hasAggregate = SqlAliasExtractor.containsAggregateFunction(sql);
        assertTrue(hasAggregate, "应该检测到聚合函数");
        
        // 测试结果包装
        Long testValue = 2L;
        Object result = SqlAliasExtractor.wrapScalarForResult(sql, testValue);
        
        // 验证返回的是Map类型
        assertInstanceOf(Map.class, result, "结果应该是Map类型");
        
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertEquals(1, resultMap.size(), "Map应该只有一个键值对");
        assertTrue(resultMap.containsKey("total_user_count"), "应该包含total_user_count键");
        assertEquals(testValue, resultMap.get("total_user_count"), "值应该匹配");
    }

    @Test
    public void testSimpleValueWithoutAlias() {
        String sql = "SELECT 42";
        
        // 测试别名提取
        boolean hasAggregate = SqlAliasExtractor.containsAggregateFunction(sql);
        assertFalse(hasAggregate, "不应该检测到聚合函数");
        
        // 测试结果包装
        Long testValue = 42L;
        Object result = SqlAliasExtractor.wrapScalarForResult(sql, testValue);
        
        // 验证直接返回数值
        assertInstanceOf(Long.class, result, "结果应该是Long类型");
        assertEquals(testValue, result, "值应该匹配");
    }
}