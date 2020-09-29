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

package tech.neverzore.common.security.feign.impl;

import cn.hutool.crypto.asymmetric.KeyType;
import feign.RequestTemplate;
import tech.neverzore.common.security.feign.base.AbstractServiceAccessInterceptor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * @author zhouzb
 * @date 2020/6/22
 */
public class ServiceAccessInterceptor extends AbstractServiceAccessInterceptor {
    public ServiceAccessInterceptor(String privateKey, String principal) {
        this(privateKey, principal, StandardCharsets.UTF_8);
    }

    public ServiceAccessInterceptor(String privateKey, String principal, Charset charset) {
        super(privateKey, principal, charset);
    }

    @Override
    protected String generateAccessTag(RequestTemplate requestTemplate) {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    protected String generateAccessToken(String accessTag) {
        byte[] originBytes = accessTag.getBytes(this.getCharset());
        byte[] encryptByte = this.getRsa().encrypt(originBytes, KeyType.PrivateKey);
        return Base64.getEncoder().encodeToString(encryptByte);
    }
}
