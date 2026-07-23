package com.xinling.ai.service;

import com.xinling.ai.service.sql.SqlExecuteResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SqlExecutorServiceTest {

    @Test
    void testSelectStatementDetection() {
        // 测试正常的SELECT语句
        String normalSelect = "SELECT COUNT(user_id) AS total_user_count FROM sys_user WHERE STATUS='0'";
        assertTrue(normalSelect.trim().toUpperCase().matches("^\\s*SELECT\\s+.*"), 
                  "正常SELECT语句应该被正确识别");
        
        // 测试带前导空格的SELECT语句
        String spacedSelect = "   SELECT COUNT(*) FROM users";
        assertTrue(spacedSelect.trim().toUpperCase().matches("^\\s*SELECT\\s+.*"),
                  "带前导空格的SELECT语句应该被正确识别");
        
        // 测试非SELECT语句
        String updateStatement = "UPDATE users SET name='test' WHERE id=1";
        assertFalse(updateStatement.trim().toUpperCase().matches("^\\s*SELECT\\s+.*"),
                   "UPDATE语句不应该被识别为SELECT");
        
        String insertStatement = "INSERT INTO users (name) VALUES ('test')";
        assertFalse(insertStatement.trim().toUpperCase().matches("^\\s*SELECT\\s+.*"),
                   "INSERT语句不应该被识别为SELECT");
    }
}