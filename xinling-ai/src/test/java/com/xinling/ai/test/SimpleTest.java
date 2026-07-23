package com.xinling.ai.test;

public class SimpleTest {
    public static void main(String[] args) {
        System.out.println("测试 isKnowledgeQuery 方法:");
        
        // 测试知识库查询识别
        System.out.println("知识库中有什么信息: " + com.xinling.ai.service.utils.QueryTypeUtils.isKnowledgeQuery("知识库中有什么信息"));
        System.out.println("文档中关于用户管理的说明: " + com.xinling.ai.service.utils.QueryTypeUtils.isKnowledgeQuery("文档中关于用户管理的说明"));
        System.out.println("什么是人工智能: " + com.xinling.ai.service.utils.QueryTypeUtils.isKnowledgeQuery("什么是人工智能"));
        System.out.println("如何使用系统: " + com.xinling.ai.service.utils.QueryTypeUtils.isKnowledgeQuery("如何使用系统"));
        System.out.println("系统功能介绍: " + com.xinling.ai.service.utils.QueryTypeUtils.isKnowledgeQuery("系统功能介绍"));
        System.out.println("用户管理的定义: " + com.xinling.ai.service.utils.QueryTypeUtils.isKnowledgeQuery("用户管理的定义"));
        
        // 测试非知识库查询
        System.out.println("查询用户信息: " + com.xinling.ai.service.utils.QueryTypeUtils.isKnowledgeQuery("查询用户信息"));
        System.out.println("统计用户数量: " + com.xinling.ai.service.utils.QueryTypeUtils.isKnowledgeQuery("统计用户数量"));
    }
}
