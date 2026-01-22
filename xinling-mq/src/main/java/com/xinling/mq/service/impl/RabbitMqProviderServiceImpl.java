package com.xinling.mq.service.impl;

import com.xinling.mq.dto.ChatMessageDto;
import com.xinling.mq.service.ChatMessageService;
import com.xinling.mq.service.MqProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RabbitMQ提供者服务实现
 *
 * @author SuXia
 * @date 2026/01/19
 */
@Slf4j
public class RabbitMqProviderServiceImpl implements MqProviderService {

    @Autowired
    private ChatMessageService chatMessageService;

    @Override
    public void sendChatMessage(ChatMessageDto chatMessageDto) {
        chatMessageService.sendChatMessage(chatMessageDto);
    }

    @Override
    public void sendChatMessage(ChatMessageDto chatMessageDto, Long sequenceNumber) {
        chatMessageService.sendChatMessage(chatMessageDto, sequenceNumber);
    }

    @Override
    public String getMqType() {
        return "rabbitmq";
    }
}
