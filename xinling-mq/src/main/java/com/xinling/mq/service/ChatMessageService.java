package com.xinling.mq.service;

import com.xinling.mq.dto.ChatMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 聊天消息服务
 *
 * @author SuXia
 * @date 2026/01/07
 */
@Slf4j
@Service
public class ChatMessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${xinling.rabbitmq.chat-routing-key:chat.routing.key}")
    private String chatRoutingKey;

    /**
     * 发送聊天消息到队列
     *
     * @param chatMessageDto 聊天消息DTO
     */
    public void sendChatMessage(ChatMessageDto chatMessageDto) {
        try {
            // 确保消息有唯一的序列号
            if (chatMessageDto.getSequenceNumber() == null) {
                chatMessageDto.setSequenceNumber(System.currentTimeMillis());
            }

            rabbitTemplate.convertAndSend("chat.exchange", chatRoutingKey, chatMessageDto);
            log.debug("发送消息到RabbitMQ队列，会话ID: {}, 序列号: {}", chatMessageDto.getSessionId(), chatMessageDto.getSequenceNumber());
        } catch (Exception e) {
            log.error("发送聊天消息到队列失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送聊天消息到队列（带自定义序列号）
     *
     * @param chatMessageDto 聊天消息DTO
     * @param sequenceNumber 序列号，用于保证顺序消费
     */
    public void sendChatMessage(ChatMessageDto chatMessageDto, Long sequenceNumber) {
        try {
            chatMessageDto.setSequenceNumber(sequenceNumber);
            rabbitTemplate.convertAndSend("chat.exchange", chatRoutingKey, chatMessageDto);
            log.debug("发送消息到RabbitMQ队列，会话ID: {}, 序列号: {}", chatMessageDto.getSessionId(), chatMessageDto.getSequenceNumber());
        } catch (Exception e) {
            log.error("发送聊天消息到队列失败: {}", e.getMessage(), e);
        }
    }
}
