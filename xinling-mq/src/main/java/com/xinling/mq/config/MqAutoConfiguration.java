package com.xinling.mq.config;

import com.xinling.mq.service.MqProviderService;
import com.xinling.mq.service.impl.KafkaMqProviderServiceImpl;
import com.xinling.mq.service.impl.RabbitMqProviderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ自动配置类
 * 根据配置动态启用相应的MQ实现
 *
 * @author SuXia
 * @date 2026/01/19
 */
@Slf4j
@Configuration
public class MqAutoConfiguration {

    /**
     * RabbitMQ提供者服务bean
     *
     * @return RabbitMQ提供者服务
     */
    @Bean
    @ConditionalOnProperty(name = "xinling.mq.type", havingValue = "rabbitmq", matchIfMissing = true)
    public MqProviderService rabbitMqProviderService() {
        log.info("启用RabbitMQ提供者服务");
        return new RabbitMqProviderServiceImpl();
    }

    /**
     * Kafka提供者服务bean
     *
     * @return Kafka提供者服务
     */
    @Bean
    @ConditionalOnProperty(name = "xinling.mq.type", havingValue = "kafka")
    public MqProviderService kafkaMqProviderService() {
        log.info("启用Kafka提供者服务");
        return new KafkaMqProviderServiceImpl();
    }
}
