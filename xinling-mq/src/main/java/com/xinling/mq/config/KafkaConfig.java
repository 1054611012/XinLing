package com.xinling.mq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinling.mq.dto.ChatMessageDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka配置类
 *
 * @author SuXia
 * @date 2026/01/19
 */
@EnableKafka
@Configuration("mqKafkaConfig")
@ConditionalOnProperty(name = "xinling.mq.type", havingValue = "kafka")
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${xinling.kafka.consumer.group-id:chat_group}")
    private String consumerGroupId;

    @Value("${xinling.kafka.consumer.concurrency:1}")
    private int concurrency;

    /**
     * 配置生产者属性
     *
     * @return 生产者属性Map
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // 确保消息发送的可靠性
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        return props;
    }

    /**
     * 配置生产者工厂
     *
     * @return 生产者工厂
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * 配置Kafka模板
     *
     * @return Kafka模板
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * 配置消费者属性
     *
     * @return 消费者属性Map
     */
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // 自动提交偏移量配置
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // 会话超时时间
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        // 最小拉取数据量
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
        // 拉取超时时间
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        // 心跳间隔
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        // 最大轮询记录数
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        return props;
    }

    /**
     * 配置消费者工厂
     *
     * @return 消费者工厂
     */
    @Bean
    public ConsumerFactory<String, ChatMessageDto> consumerFactory() {
        JsonDeserializer<ChatMessageDto> jsonDeserializer = new JsonDeserializer<>(ChatMessageDto.class, false);
        jsonDeserializer.addTrustedPackages("com.xinling.mq.dto");
        return new DefaultKafkaConsumerFactory<>(
            consumerConfigs(),
            new StringDeserializer(),
            jsonDeserializer
        );
    }

    /**
     * 配置并发Kafka监听器容器工厂
     *
     * @return 并发Kafka监听器容器工厂
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessageDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatMessageDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);
        // 确保手动提交偏移量
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    /**
     * 配置ObjectMapper Bean
     *
     * @return ObjectMapper实例
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
