package com.afk.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        String remoteAddress = request.getRemoteAddress() != null 
                ? request.getRemoteAddress().getAddress().getHostAddress() 
                : "UNKNOWN";

        log.info("🌐 [GATEWAY INBOUND] Method: {}, Path: {}, IP: {}", method, path, remoteAddress);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            int statusCode = exchange.getResponse().getStatusCode() != null 
                    ? exchange.getResponse().getStatusCode().value() 
                    : 500;

            log.info("✅ [GATEWAY OUTBOUND] Path: {}, Status: {}, Süre: {} ms", path, statusCode, duration);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
