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

package tech.neverzore.common.gateway.filter.factory;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.util.Assert;
import tech.neverzore.common.gateway.filter.impl.JwtAuthGatewayFilter;
import tech.neverzore.common.gateway.filter.support.AuthTokenSource;
import tech.neverzore.common.security.jwt.DefaultJwtManager;

/**
 * @author zhouzb
 * @date 2019/5/27
 */
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {

    public JwtAuthGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        String secret = config.getSecret();
        Assert.notNull(secret, "JwtAuthGatewayFilter's secret cannot be null.");

        DefaultJwtManager jwtManager;
        String algorithm = config.getAlgorithm();
        if (StringUtils.isEmpty(algorithm)) {
            jwtManager = new DefaultJwtManager(secret);
        } else {
            jwtManager = new DefaultJwtManager(secret, algorithm);
        }

        JwtAuthGatewayFilter jwtFilter = new JwtAuthGatewayFilter(jwtManager);

        String key = config.getKey();
        if (!StringUtils.isEmpty(key)) {
            jwtFilter.setTokenKey(key);
        }

        boolean enabled = config.isEnabled();
        jwtFilter.setEnabled(enabled);

        String source = config.getSource();
        AuthTokenSource sourceByValue = AuthTokenSource.getSourceByValue(source);
        if (sourceByValue == null) {
            jwtFilter.setTokenSource(AuthTokenSource.HEADER);
        } else {
            jwtFilter.setTokenSource(sourceByValue);
        }

        return jwtFilter;
    }

    @Getter
    @Setter
    public static class Config {
        private boolean enabled;
        private String key;
        private String source;
        private String secret;
        private String algorithm;
    }
}
