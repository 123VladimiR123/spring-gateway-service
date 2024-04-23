package com.springgateway.springgateway.filters;

import com.springgateway.springgateway.kafka.KafkaSender;
import com.springgateway.springgateway.responseDistributor.CustomDistributor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CustomGlobalRedirectFilter implements GlobalFilter, Ordered {

    @Value("${jwt.cookie.name}")
    private String cookie;
    private final CustomDistributor distributor;
    private final KafkaSender sender;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (ServerWebExchangeUtils.isAlreadyRouted(exchange))
            return chain.filter(exchange);

        if (!exchange.getRequest().getCookies().containsKey(cookie)) {
            redirectToLogin(exchange);
            return chain.filter(exchange);
        }

        String id = UUID.randomUUID().toString();
        sender.sendMessage(id + ' ' + exchange.getRequest().getCookies().get(cookie));

        try {
            distributor.getLock(id).tryAcquire(2, TimeUnit.SECONDS);
            exchange.getRequest().getHeaders().add("email", distributor.getResult(id));
        } catch (InterruptedException ignored) {
            redirectToLogin(exchange);
        }

        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return 0;
    }

    private void redirectToLogin(ServerWebExchange exchange) {
        exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
        exchange.getResponse().setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        ServerWebExchangeUtils.setAlreadyRouted(exchange);
    }
}