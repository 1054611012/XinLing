package com.xinling.ai.config;

import com.xinling.ai.service.ai.XinLingAssistant;
import com.xinling.ai.service.tools.DatabaseQueryTool;
import com.xinling.ai.service.tools.DateTimeTool;
import com.xinling.ai.service.tools.KnowledgeSearchTool;
import com.xinling.ai.service.tools.OntologyExtendedTool;
import com.xinling.ai.service.tools.OntologyQueryTool;
import com.xinling.ai.service.tools.SchemaQueryTool;
import com.xinling.ai.service.memory.ChatMemoryProviderService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AiServices 配置 — 使用 LangChain4j AiServices.builder() 创建声明式 AI 助手
 *
 * @author SuXia
 * @date 2026/05/21
 */
@Configuration
public class AiServiceConfig {

    @Autowired
    private ChatModel chatLanguageModel;

    @Autowired
    private StreamingChatModel streamingChatLanguageModel;

    @Autowired
    private ChatMemoryProviderService chatMemoryProvider;

    @Autowired
    private DatabaseQueryTool databaseQueryTool;

    @Autowired
    private KnowledgeSearchTool knowledgeSearchTool;

    @Autowired
    private DateTimeTool dateTimeTool;

    @Autowired
    private OntologyQueryTool ontologyQueryTool;

    @Autowired
    private SchemaQueryTool schemaQueryTool;

    @Autowired
    private OntologyExtendedTool ontologyExtendedTool;

    @Bean
    public XinLingAssistant xinLingAssistant() {
        return AiServices.builder(XinLingAssistant.class)
                .chatModel(chatLanguageModel)
                .streamingChatModel(streamingChatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .tools(databaseQueryTool, knowledgeSearchTool, dateTimeTool, ontologyQueryTool, schemaQueryTool, ontologyExtendedTool)
                .build();
    }
}
