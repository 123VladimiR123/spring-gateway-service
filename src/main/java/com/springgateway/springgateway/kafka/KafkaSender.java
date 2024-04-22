package com.springgateway.springgateway.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaSender {

    @Value("${custom.kafka.topics.request}")
    private String requestTopic;
    private final KafkaTemplate<String, String> template;

    public void sendMessage(String msg) {
        template.send(requestTopic, msg);
    }

}
