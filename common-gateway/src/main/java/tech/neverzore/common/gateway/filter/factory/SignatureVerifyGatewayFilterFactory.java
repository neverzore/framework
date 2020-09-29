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
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import tech.neverzore.common.gateway.filter.base.AbstractSignatureVerifyGatewayFilter;

/**
 * 签名校验过滤器
 *
 * @author zhouzb
 * @date 2019/6/1
 */
public class SignatureVerifyGatewayFilterFactory extends AbstractGatewayFilterFactory<SignatureVerifyGatewayFilterFactory.Config> {

    private AbstractSignatureVerifyGatewayFilter signatureFilter;

    public SignatureVerifyGatewayFilterFactory(AbstractSignatureVerifyGatewayFilter filter) {
        super(Config.class);

        this.signatureFilter = filter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        boolean enabled = config.isEnabled();
        this.signatureFilter.setEnabled(enabled);

        return this.signatureFilter;
    }

    @Getter
    @Setter
    public static class Config {
        private boolean enabled;
    }
}
