package com.xinling.ai.service.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.memory.ChatMemory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 支持 SystemMessage 自保护的 ChatMemory 实现
 *
 * 核心逻辑：
 * - SystemMessage 独立存储，不参与滑动窗口淘汰
 * - 普通对话消息（User/Ai）使用滑动窗口，超出 maxMessages 时移除最旧的对话消息
 * - messages() 每次返回 系统消息 + 对话消息 的合并列表
 *
 * 解决 MessageWindowChatMemory 在长对话中淘汰 SystemMessage 的问题
 *
 * @author SuXia
 */
@Slf4j
public class PromptAwareChatMemory implements ChatMemory {

    private static final int DEFAULT_MAX_MESSAGES = 50;

    /** SystemMessage 独立存储，永不淘汰 */
    private final List<ChatMessage> systemMessages = new ArrayList<>();

    /** 普通对话消息，受滑动窗口限制 */
    private final LinkedList<ChatMessage> conversationMessages = new LinkedList<>();

    /** 会话ID */
    private final Object id;

    /** 最大对话消息数（不计 SystemMessage） */
    private final int maxMessages;

    public PromptAwareChatMemory() {
        this(null, DEFAULT_MAX_MESSAGES);
    }

    public PromptAwareChatMemory(Object id) {
        this(id, DEFAULT_MAX_MESSAGES);
    }

    public PromptAwareChatMemory(int maxMessages) {
        this(null, maxMessages);
    }

    public PromptAwareChatMemory(Object id, int maxMessages) {
        this.id = id;
        this.maxMessages = maxMessages;
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public void add(ChatMessage message) {
        Objects.requireNonNull(message, "message cannot be null");

        if (message.type() == ChatMessageType.SYSTEM) {
            // SystemMessage: 独立存储，不进滑动窗口，永不淘汰
            systemMessages.add(message);
        } else {
            // 普通消息：进滑动窗口
            conversationMessages.add(message);
            // 超出窗口大小时，淘汰最旧的对话消息
            while (conversationMessages.size() > maxMessages) {
                ChatMessage removed = conversationMessages.removeFirst();
                log.debug("对话消息超出窗口限制({})，已淘汰最旧消息: type={}", maxMessages, removed.type());
            }
        }
    }

    @Override
    public List<ChatMessage> messages() {
        // 每次返回 系统消息 + 对话消息 的合并列表
        // 保证 SystemMessage 始终在对话最前面且不会被截断
        List<ChatMessage> all = new ArrayList<>(systemMessages);
        all.addAll(conversationMessages);
        return all;
    }

    @Override
    public void clear() {
        systemMessages.clear();
        conversationMessages.clear();
    }

    /**
     * 只清除对话消息，保留系统消息
     */
    public void clearConversation() {
        conversationMessages.clear();
    }

    /**
     * 清除所有系统消息
     */
    public void clearSystemMessages() {
        systemMessages.clear();
    }

    /**
     * 获取系统消息数量
     */
    public int systemMessageCount() {
        return systemMessages.size();
    }

    /**
     * 获取当前对话消息数
     */
    public int conversationMessageCount() {
        return conversationMessages.size();
    }

    @Override
    public String toString() {
        return String.format("PromptAwareChatMemory(system=%d, conversation=%d/%d)",
                systemMessages.size(), conversationMessages.size(), maxMessages);
    }
}
