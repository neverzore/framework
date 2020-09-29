/*
 * Copyright (c) 2020 neverzore (https://github.com/neverzore).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.neverzore.common.gateway.filter.base;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tech.neverzore.common.gateway.filter.support.AuthTokenSource;
import tech.neverzore.common.gateway.filter.support.FilterConst;
import tech.neverzore.common.logging.core.LogBuilder;
import tech.neverzore.common.logging.core.LogContent;
import tech.neverzore.common.logging.core.LogType;
import tech.neverzore.common.security.filter.support.ServiceAccessConst;

import java.net.InetSocketAddress;

/**
 * @author zhouzb
 * @date 2019/3/23
 */
@Setter
@Getter
@Slf4j
public abstract class AbstractJwtAuthGatewayFilter extends BaseAuthGatewayFilter {
    private static final String AUTHORIZE_TOKEN = "X-Auth-Token";

    private String tokenKey;
    private AuthTokenSource authTokenSource;

    /**
     * 获取鉴权Token
     *
     * @param exchange 请求信息
     * @return 请求Token
     */
    protected String getAuthorizeToken(ServerWebExchange exchange) {
        String authToken = StringUtils.EMPTY;
        AuthTokenSource authTokenSource = this.getAuthTokenSource();
        String tokenKey = getTokenKey();
        if (AuthTokenSource.HEADER.equals(authTokenSource)) {
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = request.getHeaders();
            authToken = headers.getFirst(tokenKey);
        } else if (AuthTokenSource.QUERY.equals(authTokenSource)) {
            ServerHttpRequest request = exchange.getRequest();
            authToken = request.getQueryParams().getFirst(tokenKey);
        }

        return authToken;
    }

    /**
     * 解析
     *
     * @param authToken 请求Token
     * @return 请求载荷
     */
    protected abstract Claims decode(String authToken);

    /**
     * 验证签名格式
     *
     * @param authToken 请求Token
     * @return 请求Token是否符合规范
     */
    protected abstract boolean isSigned(String authToken);

    @Override
    public Mono<Void> doFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authToken = getAuthorizeToken(exchange);

        if (StringUtils.isEmpty(authToken)) {
            if (log.isWarnEnabled()) {
                ServerHttpRequest request = exchange.getRequest();
                InetSocketAddress remoteAddress = request.getRemoteAddress();
                String happening = String.format("request %s, uri %s, remote %s, JWT token is missing",
                        request.getId(), String.valueOf(request.getURI()), remoteAddress);

                LogContent content = LogBuilder.builder()
                        .setSource(FilterConst.JWT_FILTER)
                        .setType(LogType.NORMAL)
                        .setHappening(happening)
                        .build();

                log.warn(content.toString());
            }

            return unAuthorizedResponse(exchange.getResponse());
        }

        if (!isSigned(authToken)) {
            if (log.isWarnEnabled()) {
                ServerHttpRequest request = exchange.getRequest();
                InetSocketAddress remoteAddress = request.getRemoteAddress();
                String happening = String.format("request %s, uri %s, remote %s, JWT token is unsigned",
                        request.getId(), String.valueOf(request.getURI()), remoteAddress);

                LogContent content = LogBuilder.builder()
                        .setSource(FilterConst.JWT_FILTER)
                        .setType(LogType.NORMAL)
                        .setHappening(happening)
                        .build();

                log.warn(content.toString());
            }

            return unAuthorizedResponse(exchange.getResponse());
        }

        Claims claims = decode(authToken);
        if (claims == null) {
            return unAuthorizedResponse(exchange.getResponse());
        }

        String audience = claims.getAudience();

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().header(ServiceAccessConst.SERVICE_ACCESS_USER, audience).build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
        mutatedExchange.getAttributes().put(ServiceAccessConst.SERVICE_ACCESS_USER, audience);

        return chain.filter(mutatedExchange)
                .then(Mono.fromRunnable(() -> mutatedExchange.getAttributes().remove(ServiceAccessConst.SERVICE_ACCESS_USER)));
    }
}
