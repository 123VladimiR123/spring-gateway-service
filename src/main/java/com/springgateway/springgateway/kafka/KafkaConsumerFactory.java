package com.springgateway.springgateway.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerFactory {

    @Bean
    public ConsumerFactory<String, String> consumerFactory(@Value("${custom.kafka.address}") String kafkaServer) {
        Map<String, Object> map = new HashMap<>();
        map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        map.put(ConsumerConfig.GROUP_ID_CONFIG, "default-gateway-consumer");
        return new DefaultKafkaConsumerFactory<>(map);
    }
}
