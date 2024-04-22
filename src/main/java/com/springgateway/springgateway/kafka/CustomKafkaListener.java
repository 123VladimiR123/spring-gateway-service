package com.springgateway.springgateway.kafka;

import com.springgateway.springgateway.responseDistributor.CustomDistributor;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CustomKafkaListener {

    private final CustomDistributor distributor;

    @KafkaListener(topics = "#{'${custom.kafka.topics.response}'.split(',')}")
    void listener(String response) {
        String[] parsed = response.split(" ");
        distributor.putResponse(parsed[0], (parsed.length > 1) ? parsed[1] : "");
    }


}
