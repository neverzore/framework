package tech.neverzore.common.gateway.filter.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author zhouzb
 * @date 2019/3/23
 */
@Getter
@Setter
public abstract class BaseGatewayFilter implements GatewayFilter, Ordered {
    private boolean enabled = true;

    /**
     * 具体拦截方法
     *
     * @param exchange 请求信息
     * @param chain    过滤链
     * @return Void
     */
    public abstract Mono<Void> doFilter(ServerWebExchange exchange, GatewayFilterChain chain);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!isEnabled()) {
            return chain.filter(exchange);
        }

        return doFilter(exchange, chain);
    }
}
