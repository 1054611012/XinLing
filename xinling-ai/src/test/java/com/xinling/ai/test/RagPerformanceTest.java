package com.xinling.ai.test;

import com.xinling.ai.service.DatabaseRagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RagPerformanceTest {

    @Autowired
    private DatabaseRagService databaseRagService;

    @Test
    public void testInitializationPerformance() {
        long startTime = System.currentTimeMillis();
        
        // 测试初始化性能
        databaseRagService.initializeDatabaseDocuments();
        
        long endTime = System.currentTimeMillis();
        System.out.println("RAG初始化耗时: " + (endTime - startTime) + " ms");
    }
}