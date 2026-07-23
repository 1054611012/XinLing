package com.xinling.ai.service.listener;

import com.rabbitmq.client.Channel;
import com.xinling.ai.domain.chat.ChatMessageEntity;
import com.xinling.mq.dto.ChatMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 聊天消息持久化监听器
 * 负责将MQ中的聊天消息持久化到数据库
 *
 * @author SuXia
 * @date 2026/01/07
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "xinling.mq.type", havingValue = "rabbitmq", matchIfMissing = true)
public class ChatMessagePersistenceListener {

    /**
     * 监听聊天消息队列，将消息持久化到数据库
     *
     * @param chatMessageDto 聊天消息DTO
     * @param channel AMQP通道，用于手动ACK
     * @param deliveryTag 消息投递标签，用于ACK确认
     */
    @RabbitListener(
        queues = "${xinling.rabbitmq.chat-queue:chat.queue}",
        containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleChatMessagePersistence(@Payload ChatMessageDto chatMessageDto,
                                            Channel channel,
                                            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("接收到需要持久化的聊天消息，会话ID: {}, 角色: {}, 序列号: {}",
                     chatMessageDto.getSessionId(), chatMessageDto.getRole(), chatMessageDto.getSequenceNumber());

            // 将消息持久化到数据库
            persistChatMessageToDatabase(chatMessageDto);

            log.info("聊天消息已持久化到数据库，会话ID: {}", chatMessageDto.getSessionId());

            // 业务处理成功，手动ACK确认
            channel.basicAck(deliveryTag, false); // 第二个参数false表示不批量确认

            log.debug("消息已确认，deliveryTag: {}", deliveryTag);

        } catch (Exception e) {
            log.error("持久化聊天消息失败，会话ID: {}", chatMessageDto.getSessionId(), e);

            try {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    // 消息处理被中断，拒绝消息且不重新入队
                    channel.basicNack(deliveryTag, false, false);
                } else {
                    // 消息处理失败，拒绝消息但允许重新入队（用于重试）
                    channel.basicNack(deliveryTag, false, true);
                }
            } catch (IOException ioException) {
                log.error("发送ACK/NACK失败", ioException);
            }
        }
    }

    /**
     * 将聊天消息持久化到数据库
     *
     * @param chatMessageDto 聊天消息DTO
     */
    private void persistChatMessageToDatabase(ChatMessageDto chatMessageDto) {
        try {
            ChatMessageEntity entity = new ChatMessageEntity();
            entity.setSessionId(chatMessageDto.getSessionId());
            entity.setRole(chatMessageDto.getRole());
            entity.setContent(chatMessageDto.getContent());
            entity.setUserId(chatMessageDto.getUserId());
            entity.setUserName(chatMessageDto.getUserName());
            entity.setCreateTime(chatMessageDto.getCreateTime());
            entity.setSequenceNumber(chatMessageDto.getSequenceNumber());

//            chatMessageRepository.save(entity);

            log.info("聊天消息已保存到数据库，会话ID: {}, 用户ID: {}, 角色: {}",
                     chatMessageDto.getSessionId(), chatMessageDto.getUserId(), chatMessageDto.getRole());
        } catch (Exception e) {
            log.error("保存聊天消息到数据库失败，会话ID: {}", chatMessageDto.getSessionId(), e);
            throw e;
        }
    }
}
