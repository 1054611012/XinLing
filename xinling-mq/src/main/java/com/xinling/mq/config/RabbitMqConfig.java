package com.xinling.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置
 *
 * @author SuXia
 * @date 2026/01/07
 */
@Configuration("mqRabbitMqConfig")
@ConditionalOnProperty(name = "xinling.mq.type", havingValue = "rabbitmq", matchIfMissing = true)
public class RabbitMqConfig {

    @Value("${xinling.rabbitmq.chat-exchange:chat.exchange}")
    private String chatExchange;

    @Value("${xinling.rabbitmq.chat-queue:chat.queue}")
    private String chatQueue;

    @Value("${xinling.rabbitmq.chat-routing-key:chat.routing.key}")
    private String chatRoutingKey;

    @Value("${xinling.rabbitmq.concurrency:1}")
    private int concurrency;

    @Value("${xinling.rabbitmq.max-concurrency:1}")
    private int maxConcurrency;

    //
    @Value("${xinling.rabbitmq.prefetch-count:1}")
    private int prefetchCount;

    public static final String CHAT_EXCHANGE = "chat.exchange";
    public static final String CHAT_QUEUE = "chat.queue";
    public static final String CHAT_ROUTING_KEY = "chat.routing.key";

    @Bean
    public Queue chatQueue() {
        return new Queue(chatQueue, true); // 持久化队列
    }

    @Bean
    public DirectExchange chatExchange() {
        return new DirectExchange(chatExchange, true, false); // 持久化交换机
    }

    @Bean
    public Binding chatBinding(Queue chatQueue, DirectExchange chatExchange) {
        return BindingBuilder.bind(chatQueue).to(chatExchange).with(chatRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    /**
     * 配置RabbitMQ监听器容器工厂，启用手动ACK确认并确保顺序消费
     */
    @Bean("rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(concurrency); // 单个消费者确保顺序消费
        factory.setMaxConcurrentConsumers(maxConcurrency); // 最大并发消费者数量
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 手动ACK模式
        factory.setPrefetchCount(prefetchCount); // 每次只预取一条消息
        factory.setDefaultRequeueRejected(true); // 拒绝的消息重新入队
        return factory;
    }
}
