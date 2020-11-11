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

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tech.neverzore.common.gateway.filter.support.AuthTokenSource;
import tech.neverzore.common.gateway.filter.support.FilterConst;
import tech.neverzore.common.logging.core.LogBuilder;
import tech.neverzore.common.logging.Logger;
import tech.neverzore.common.security.filter.support.ServiceAccessConst;

import java.net.InetSocketAddress;

/**
 * @author zhouzb
 * @date 2019/3/23
 */
public abstract class AbstractJwtAuthGatewayFilter extends BaseAuthGatewayFilter {
    private static final String AUTHORIZE_TOKEN = "X-Auth-Token";

    private String tokenKey;
    private AuthTokenSource tokenSource;

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public AuthTokenSource getTokenSource() {
        return tokenSource;
    }

    public void setTokenSource(AuthTokenSource tokenSource) {
        this.tokenSource = tokenSource;
    }

    /**
     * 获取鉴权Token
     *
     * @param exchange 请求信息
     * @return 请求Token
     */
    protected String getAuthorizeToken(ServerWebExchange exchange) {
        String authToken = StringUtils.EMPTY;
        AuthTokenSource authTokenSource = this.getTokenSource();
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
     * 获取token颁发对象
     *
     * @param authToken 请求Token
     * @return 请求对象
     */
    protected abstract String getAudience(String authToken);

    /**
     * 验证签名格式
     *
     * @param authToken 请求Token
     * @return 请求Token是否符合规范
     */
    protected abstract boolean isSigned(String authToken);

    /**
     * 签名验证通过后回调接口
     * @param exchange  请求exchange
     * @param authToken 请求token
     */
    protected void authorizationSuccess(ServerWebExchange exchange, String authToken) {

    }

    @Override
    public Mono<Void> doFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authToken = getAuthorizeToken(exchange);

        if (StringUtils.isEmpty(authToken)) {
            if (Logger.isWarnEnabled(getClass())) {
                ServerHttpRequest request = exchange.getRequest();
                InetSocketAddress remoteAddress = request.getRemoteAddress();
                String happening = String.format("request %s, uri %s, remote %s, JWT token is missing",
                        request.getId(), request.getURI(), remoteAddress);
                Logger.warn(getClass(), LogBuilder.generate(FilterConst.JWT_FILTER, happening));
            }

            return unAuthorizedResponse(exchange.getResponse());
        }

        if (!isSigned(authToken)) {
            if (Logger.isWarnEnabled(getClass())) {
                ServerHttpRequest request = exchange.getRequest();
                InetSocketAddress remoteAddress = request.getRemoteAddress();
                String happening = String.format("request %s, uri %s, remote %s, JWT token is unsigned",
                        request.getId(), request.getURI(), remoteAddress);
                Logger.warn(getClass(), LogBuilder.generate(FilterConst.JWT_FILTER, happening));
            }

            return unAuthorizedResponse(exchange.getResponse());
        }

        String audience = null;
        try {
            audience = getAudience(authToken);
        } catch (Throwable e) {
            ServerHttpRequest request = exchange.getRequest();
            InetSocketAddress remoteAddress = request.getRemoteAddress();
            String happening = String.format("request %s, uri %s, remote %s, JWT token can not obtain audience",
                    request.getId(), request.getURI(), remoteAddress);
            Logger.error(getClass(), LogBuilder.generate(FilterConst.JWT_FILTER, happening, e.getMessage()), e);
        }

        if (StringUtils.isEmpty(audience)) {
            return unAuthorizedResponse(exchange.getResponse());
        }

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().header(ServiceAccessConst.SERVICE_ACCESS_USER, audience).build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
        mutatedExchange.getAttributes().put(ServiceAccessConst.SERVICE_ACCESS_USER, audience);

        try {
            authorizationSuccess(mutatedExchange, authToken);
        } catch (Throwable e) {
            ServerHttpRequest request = exchange.getRequest();
            InetSocketAddress remoteAddress = request.getRemoteAddress();
            String happening = String.format("request %s, uri %s, remote %s, authorization success handler execution failed",
                    request.getId(), request.getURI(), remoteAddress);
            Logger.error(getClass(), LogBuilder.generate(FilterConst.JWT_FILTER, happening, e.getMessage()), e);
        }

        return chain.filter(mutatedExchange)
                .then(Mono.fromRunnable(() -> mutatedExchange.getAttributes().remove(ServiceAccessConst.SERVICE_ACCESS_USER)));
    }
}
