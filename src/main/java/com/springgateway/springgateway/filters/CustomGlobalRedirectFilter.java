package com.springgateway.springgateway.filters;

import com.springgateway.springgateway.kafka.KafkaSender;
import com.springgateway.springgateway.responseDistributor.CustomDistributor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.print.DocFlavor;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CustomGlobalRedirectFilter implements GlobalFilter, Ordered {

    private final List<String> paths = List.of("/login", "/registration", "/logout");

    @Value("${jwt.cookie.name}")
    private String cookie;
    private final CustomDistributor distributor;
    private final KafkaSender sender;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (ServerWebExchangeUtils.isAlreadyRouted(exchange))
            return chain.filter(exchange);

        if (!exchange.getRequest().getCookies().containsKey(cookie)) {
            if (!paths.contains(exchange.getRequest().getPath().toString())) {
                redirectToLogin(exchange);
            }
            return chain.filter(exchange);
        }

        String id = UUID.randomUUID().toString();
        sender.sendMessage(id + ' ' + exchange.getRequest().getCookies().get(cookie).getFirst().getValue());

        String result = "";

        try {
            distributor.getLock(id).tryAcquire(2, TimeUnit.SECONDS);
            result = distributor.getResult(id);

            if (result == null || result.equals(""))
                redirectToLogout(exchange);
            else
                exchange.getRequest().mutate().header("email", result).build();
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

    private void redirectToLogout(ServerWebExchange exchange) {
        exchange.getResponse().getHeaders().setLocation(URI.create("/logout"));
        exchange.getResponse().setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        ServerWebExchangeUtils.setAlreadyRouted(exchange);
    }
}