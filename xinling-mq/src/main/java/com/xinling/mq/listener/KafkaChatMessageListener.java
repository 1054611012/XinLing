package com.xinling.mq.listener;

import com.xinling.mq.dto.ChatMessageDto;
import com.xinling.mq.service.persistence.ChatMessagePersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Kafka聊天消息监听器
 * 负责接收Kafka中的聊天消息并持久化到数据库
 *
 * @author SuXia
 * @date 2026/01/19
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "xinling.mq.type", havingValue = "kafka")
public class KafkaChatMessageListener {

    @Autowired
    private ChatMessagePersistenceService chatMessagePersistenceService;

    @Value("${xinling.kafka.chat-topic:chat.topic}")
    private String chatTopic;

    /**
     * 监听聊天消息主题，将消息持久化到数据库
     *
     * @param chatMessageDto 聊天消息DTO
     * @param acknowledgment 确认对象，用于手动ACK
     */
    @KafkaListener(
        topics = "${xinling.kafka.chat-topic:chat.topic}",
        groupId = "${xinling.kafka.consumer.group-id:chat_group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleChatMessagePersistence(ChatMessageDto chatMessageDto, Acknowledgment acknowledgment) {
        try {
            log.info("Kafka接收到需要持久化的聊天消息，会话ID: {}, 角色: {}, 序列号: {}",
                     chatMessageDto.getSessionId(), chatMessageDto.getRole(), chatMessageDto.getSequenceNumber());

            // 将消息持久化到数据库
            chatMessagePersistenceService.saveChatMessageToDatabase(chatMessageDto);

            log.info("聊天消息已通过Kafka持久化到数据库，会话ID: {}", chatMessageDto.getSessionId());

            // 业务处理成功，手动ACK确认
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
                log.debug("Kafka消息已确认，会话ID: {}", chatMessageDto.getSessionId());
            }

        } catch (Exception e) {
            log.error("通过Kafka持久化聊天消息失败，会话ID: {}", chatMessageDto.getSessionId(), e);
            // 在异常情况下，不确认消息，让其重新消费
        }
    }
}