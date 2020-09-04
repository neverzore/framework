package tech.neverzore.framework.gateway.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tech.neverzore.framework.gateway.filter.base.BaseGlobalFilter;
import tech.neverzore.framework.gateway.filter.support.FilterConst;
import tech.neverzore.framework.gateway.filter.support.FilterOrder;
import tech.neverzore.framework.logging.core.LogBuilder;
import tech.neverzore.framework.logging.core.LogContent;

import java.net.InetSocketAddress;

/**
 * @author zhouzb
 * @date 2019/5/23
 */
@Slf4j
public class LoggingGlobalFilter extends BaseGlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (log.isInfoEnabled()) {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().pathWithinApplication().value();
            String scheme = request.getURI().getScheme();
            HttpMethod method = request.getMethod();
            HttpHeaders headers = request.getHeaders();

            String happening = String.format("request %s, uri %s, path %s, scheme %s, headers %s, remote %s7",
                    request.getId(), String.valueOf(request.getURI()), path, scheme, method, headers, request.getRemoteAddress());

            LogContent content = LogBuilder.builder()
                    .setHappening(happening).build();

            log.info(content.toString());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrder.PRE_FILTER_ORDER + 1;
    }
}
