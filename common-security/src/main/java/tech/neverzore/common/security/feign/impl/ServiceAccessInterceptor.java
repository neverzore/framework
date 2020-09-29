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
