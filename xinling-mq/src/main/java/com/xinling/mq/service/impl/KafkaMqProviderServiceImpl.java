package com.xinling.mq.service.impl;

import com.xinling.mq.dto.ChatMessageDto;
import com.xinling.mq.service.MqProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka提供者服务实现
 *
 * @author SuXia
 * @date 2026/01/19
 */
@Slf4j
public class KafkaMqProviderServiceImpl implements MqProviderService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${xinling.kafka.chat-topic:chat.topic}")
    private String chatTopic;

    @Override
    public void sendChatMessage(ChatMessageDto chatMessageDto) {
        // 确保消息有唯一的序列号
        if (chatMessageDto.getSequenceNumber() == null) {
            chatMessageDto.setSequenceNumber(System.currentTimeMillis());
        }

        sendMessage(chatMessageDto);
    }

    @Override
    public void sendChatMessage(ChatMessageDto chatMessageDto, Long sequenceNumber) {
        chatMessageDto.setSequenceNumber(sequenceNumber);
        sendMessage(chatMessageDto);
    }

    private void sendMessage(ChatMessageDto chatMessageDto) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(chatTopic, chatMessageDto.getSessionId(), chatMessageDto);

            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("发送消息到Kafka失败: {}", throwable.getMessage(), throwable);
                } else {
                    log.debug("发送消息到Kafka成功，会话ID: {}, 序列号: {}, 分区: {}, 偏移量: {}",
                             chatMessageDto.getSessionId(), chatMessageDto.getSequenceNumber(),
                             result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("发送聊天消息到Kafka失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getMqType() {
        return "kafka";
    }
}
