package com.springgateway.springgateway.gatewayConfig;

import com.springgateway.springgateway.filters.CustomGlobalRedirectFilter;
import com.springgateway.springgateway.kafka.KafkaSender;
import com.springgateway.springgateway.responseDistributor.CustomDistributor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public GlobalFilter globalFilter(CustomDistributor distributor, KafkaSender sender) {
        return new CustomGlobalRedirectFilter(distributor, sender);
    }
}
