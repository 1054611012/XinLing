package com.xinling.mq.service;

import com.xinling.mq.dto.ChatMessageDto;

/**
 * MQ提供者服务接口 - 支持多种MQ实现
 * 
 * @author SuXia
 * @date 2026/01/19
 */
public interface MqProviderService {
    
    /**
     * 发送聊天消息
     * 
     * @param chatMessageDto 聊天消息DTO
     */
    void sendChatMessage(ChatMessageDto chatMessageDto);
    
    /**
     * 发送聊天消息（带序列号）
     * 
     * @param chatMessageDto 聊天消息DTO
     * @param sequenceNumber 序列号，用于保证顺序消费
     */
    void sendChatMessage(ChatMessageDto chatMessageDto, Long sequenceNumber);
    
    /**
     * 获取MQ类型
     * 
     * @return MQ类型
     */
    String getMqType();
}