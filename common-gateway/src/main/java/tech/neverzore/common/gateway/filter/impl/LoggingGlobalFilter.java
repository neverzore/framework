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

package tech.neverzore.common.gateway.filter.impl;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tech.neverzore.common.gateway.filter.base.BaseGlobalFilter;
import tech.neverzore.common.gateway.filter.support.FilterOrder;
import tech.neverzore.common.logging.core.LogBuilder;
import tech.neverzore.common.logging.Logger;

/**
 * @author zhouzb
 * @date 2019/5/23
 */
public class LoggingGlobalFilter extends BaseGlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().pathWithinApplication().value();
        String scheme = request.getURI().getScheme();
        HttpMethod method = request.getMethod();
        HttpHeaders headers = request.getHeaders();

        if (Logger.isInfoEnabled(getClass())) {
            String happening = String.format("request %s, uri %s, path %s, scheme %s, headers %s, remote %s7",
                    request.getId(), String.valueOf(request.getURI()), path, scheme, method, headers, request.getRemoteAddress());

            Logger.info(getClass(), LogBuilder.generate("GlobalLoggingFilter", happening));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrder.PRE_FILTER_ORDER + 1;
    }
}
