package com.xinling.ai.service.memory;

import com.xinling.ai.domain.config.AiPrompt;
import com.xinling.ai.domain.config.AiSessionConfig;
import com.xinling.ai.service.IAiPromptService;
import com.xinling.ai.service.IAiSessionConfigService;
import com.xinling.common.constant.RedisKeys;
import com.xinling.common.core.redis.RedisCache;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天内存提供者 — 支持提示词注入
 *
 * 核心逻辑：
 * 1. 使用 PromptAwareChatMemory 替代 MessageWindowChatMemory（SystemMessage永不淘汰）
 * 2. 创建新session时，查Redis获取 sessionId→configId 映射
 * 3. 查 config → 查关联 prompts → 按 model_id 过滤 → 模板变量替换 → 注入 SystemMessage
 *
 * @author SuXia
 */
@Slf4j
@Component
public class ChatMemoryProviderService implements ChatMemoryProvider {

    private static final int MAX_MESSAGES = 50;
    private static final int MAX_SESSIONS = 1000;

    private final Map<String, ChatMemory> sessionMemories = new ConcurrentHashMap<>();

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IAiSessionConfigService aiSessionConfigService;

    @Autowired
    private IAiPromptService aiPromptService;

    @Override
    public ChatMemory get(Object memoryId) {
        String sessionId = memoryId.toString();
        return sessionMemories.computeIfAbsent(sessionId, id -> {
            PromptAwareChatMemory memory = new PromptAwareChatMemory(MAX_MESSAGES);
            try {
                // 从Redis查 sessionId → configId 映射
                String configKey = RedisKeys.SESSION_CONFIG_PREFIX + sessionId;
                Long configId = redisCache.getCacheObject(configKey);
                if (configId == null) {
                    // 没有映射 → 使用默认配置
                    log.debug("会话 {} 无config映射，使用默认配置", sessionId);
                    injectDefaultPrompts(memory);
                } else {
                    // 有映射 → 查配置 + 查prompts + 注入
                    injectPromptsByConfig(memory, configId);
                }
            } catch (Exception e) {
                log.warn("会话 {} 注入提示词失败: {}", sessionId, e.getMessage());
            }
            // 防止内存泄漏
            if (sessionMemories.size() > MAX_SESSIONS) {
                String oldestKey = sessionMemories.keySet().iterator().next();
                sessionMemories.remove(oldestKey);
                log.info("会话内存超限，已清理最旧会话: {}", oldestKey);
            }
            return memory;
        });
    }

    /**
     * 注入默认会话配置的提示词
     */
    private void injectDefaultPrompts(PromptAwareChatMemory memory) {
        AiSessionConfig defaultConfig = aiSessionConfigService.selectDefaultSessionConfig();
        if (defaultConfig == null) {
            log.warn("未找到默认会话配置，跳过提示词注入");
            return;
        }
        injectPrompts(memory, defaultConfig, defaultConfig.getChatModelId());
    }

    /**
     * 根据configId注入提示词
     */
    private void injectPromptsByConfig(PromptAwareChatMemory memory, Long configId) {
        AiSessionConfig config = aiSessionConfigService.selectAiSessionConfigById(configId);
        if (config == null) {
            log.warn("会话配置不存在: configId={}，跳过提示词注入", configId);
            return;
        }
        injectPrompts(memory, config, config.getChatModelId());
    }

    /**
     * 注入提示词到ChatMemory
     * 过滤规则：status='0'(启用) AND (model_id IS NULL OR model_id = 当前模型ID)
     */
    private void injectPrompts(PromptAwareChatMemory memory, AiSessionConfig config, Long modelId) {
        try {
            List<AiPrompt> prompts;
            if (modelId != null) {
                prompts = aiPromptService.selectEnabledPromptsByConfigId(config.getConfigId(), modelId);
            } else {
                prompts = aiPromptService.selectPromptsByConfigId(config.getConfigId());
            }

            if (prompts == null || prompts.isEmpty()) {
                log.debug("配置 {} 无可用提示词", config.getConfigName());
                return;
            }

            for (AiPrompt prompt : prompts) {
                if (!"0".equals(prompt.getStatus())) {
                    continue; // 跳过已停用
                }
                String content = resolveTemplateVariables(prompt.getContent(), config);
                memory.add(new SystemMessage(content));
                log.debug("注入提示词: {} (config={})", prompt.getPromptName(), config.getConfigName());
            }
            log.info("会话已注入 {} 条提示词 (config={})", prompts.size(), config.getConfigName());
        } catch (Exception e) {
            log.error("注入提示词失败: configId={}", config.getConfigId(), e);
        }
    }

    /**
     * 模板变量替换
     */
    private String resolveTemplateVariables(String content, AiSessionConfig config) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        String result = content;
        result = result.replace("{currentDate}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        if (config != null && config.getConfigName() != null) {
            result = result.replace("{configName}", config.getConfigName());
        }
        return result;
    }

    /**
     * 清除指定会话的内存
     */
    public void clearSession(String sessionId) {
        ChatMemory removed = sessionMemories.remove(sessionId);
        if (removed != null) {
            log.debug("已清除会话内存: {}", sessionId);
        }
    }

    /**
     * 获取当前活跃会话数
     */
    public int getActiveSessionCount() {
        return sessionMemories.size();
    }
}
