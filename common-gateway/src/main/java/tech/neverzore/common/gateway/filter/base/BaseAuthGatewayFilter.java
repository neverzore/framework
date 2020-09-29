package tech.neverzore.common.gateway.filter.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

/**
 * @author zhouzb
 * @date 2019/3/23
 */
public abstract class BaseAuthGatewayFilter extends BaseGatewayFilter {
    public Mono<Void> unAuthorizedResponse(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
