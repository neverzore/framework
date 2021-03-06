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

package tech.neverzore.common.security.feign.base;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import tech.neverzore.common.security.filter.support.ServiceAccessConst;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouzb
 * @date 2020/6/22
 */
public abstract class AbstractServiceAccessInterceptor implements RequestInterceptor {
    private RSA rsa;
    private Charset charset;
    private String principal;

    public RSA getRsa() {
        return rsa;
    }

    public void setRsa(RSA rsa) {
        this.rsa = rsa;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public AbstractServiceAccessInterceptor(String privateKey, String principal) {
        this(privateKey, principal, StandardCharsets.UTF_8);
    }

    public AbstractServiceAccessInterceptor(String privateKey, String principal, Charset charset) {
        if (StringUtils.isEmpty(privateKey)) {
            throw new IllegalArgumentException("PrivateKey cannot be null.");
        }

        if (StringUtils.isEmpty(principal)) {
            throw new IllegalArgumentException("Principal cannot be null.");
        }

        if (charset == null) {
            throw new IllegalArgumentException("Charset cannot be null.");
        }

        this.charset = charset;
        this.rsa = SecureUtil.rsa(privateKey, null);
        this.principal = principal;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String accessTag = generateAccessTag(requestTemplate);
        String accessToken = generateAccessToken(accessTag);

        Map<String, Collection<String>> headers = new HashMap<>(3);

        headers.put(ServiceAccessConst.SERVICE_ACCESS_VERIFY_TAG, Collections.singletonList(accessTag));
        headers.put(ServiceAccessConst.SERVICE_ACCESS_VERIFY_TOKEN, Collections.singletonList(accessToken));
        headers.put(ServiceAccessConst.SERVICE_ACCESS_VERIFY_PRINCIPLE, Collections.singletonList(principal));

        requestTemplate.headers(headers);
    }

    /**
     * 生成服务访问标识
     *
     * @param requestTemplate 请求
     * @return 服务访问标识
     */
    protected abstract String generateAccessTag(RequestTemplate requestTemplate);

    /**
     * 生成服务访问Token
     *
     * @param accessTag 访问标识
     * @return 服务访问Token
     */
    protected abstract String generateAccessToken(String accessTag);
}
