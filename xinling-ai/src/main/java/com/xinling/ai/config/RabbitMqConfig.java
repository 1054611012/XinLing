package com.xinling.ai.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置 - 用于AI模块的消息监听
 *
 * @author SuXia
 * @date 2026/01/07
 */
@Configuration
@ConditionalOnProperty(name = "xinling.mq.type", havingValue = "rabbitmq", matchIfMissing = true)
public class RabbitMqConfig {

    @Value("${xinling.rabbitmq.concurrency:1}")
    private int concurrency;

    @Value("${xinling.rabbitmq.max-concurrency:1}")
    private int maxConcurrency;

    @Value("${xinling.rabbitmq.prefetch-count:1}")
    private int prefetchCount;

    /**
     * 配置RabbitMQ监听器容器工厂，用于AI模块的消息监听
     */
    @Bean("rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                               MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter); // 使用xinling-mq模块中定义的messageConverter
        factory.setConcurrentConsumers(concurrency); // 单个消费者确保顺序消费
        factory.setMaxConcurrentConsumers(maxConcurrency); // 最大并发消费者数量
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 手动ACK模式
        factory.setPrefetchCount(prefetchCount); // 每次只预取一条消息
        factory.setDefaultRequeueRejected(true); // 拒绝的消息重新入队
        return factory;
    }
}
