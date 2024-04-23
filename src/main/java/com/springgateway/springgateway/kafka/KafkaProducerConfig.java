package com.springgateway.springgateway.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, String> producerFactory(@Value("${custom.kafka.address}") String kafkaServer) {
        Map<String, Object> map = new HashMap<>();
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(map);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(@Value("${custom.kafka.address}") String kafkaServer) {
        return new KafkaTemplate<>(producerFactory(kafkaServer));
    }
}
