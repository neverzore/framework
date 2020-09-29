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

package tech.neverzore.common.security.filter.base;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.neverzore.common.security.filter.support.ServiceAccessConst;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author zhouzb
 * @date 2020/6/19
 */
public abstract class ServiceAccessVerifyFilter extends OncePerRequestFilter {
    private RSA rsa;
    private Charset charset;
    private RequestMatcher requestMatcher;

    public ServiceAccessVerifyFilter(String pattern, String publicKey) {
        this(pattern, publicKey, StandardCharsets.UTF_8);
    }

    public ServiceAccessVerifyFilter(String pattern, String publicKey, Charset charset) {
        if (StringUtils.isAnyEmpty(pattern, publicKey)) {
            throw new IllegalArgumentException("PublicKey cannot be null.");
        }

        if (charset == null) {
            throw new IllegalArgumentException("Charset cannot be null.");
        }

        this.charset = charset;
        this.rsa = SecureUtil.rsa(null, publicKey);
        this.requestMatcher = new AntPathRequestMatcher(pattern);
    }

    protected void unAuthenticateAccess(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    protected boolean requiresAuthentication(HttpServletRequest request) {
        return this.requestMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (!requiresAuthentication(httpServletRequest)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        if (!accessVerifyPassed(httpServletRequest, httpServletResponse)) {
            unAuthenticateAccess(httpServletRequest, httpServletResponse);
            return;
        }

        accessVerifySuccess(httpServletRequest, httpServletResponse, filterChain);
    }

    protected boolean accessVerifyPassed(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String accessVerifyTag = httpServletRequest.getHeader(ServiceAccessConst.SERVICE_ACCESS_VERIFY_TAG);
        String accessVerifyToken = httpServletRequest.getHeader(ServiceAccessConst.SERVICE_ACCESS_VERIFY_TOKEN);

        if (StringUtils.isAnyEmpty(accessVerifyTag, accessVerifyToken)) {
            return false;
        }

        byte[] decodeBytes = Base64.getDecoder().decode(accessVerifyToken);
        byte[] decrypt = this.rsa.decrypt(decodeBytes, KeyType.PublicKey);

        String computedTag = new String(decrypt, this.charset);

        if (!accessVerifyTag.equals(computedTag)) {
            return false;
        }

        return true;
    }



    protected void accessVerifySuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
//        String accessVerifyTag = httpServletRequest.getHeader(ServiceAccessConst.SERVICE_ACCESS_VERIFY_TAG);
//        String accessPrincipal = httpServletRequest.getHeader(ServiceAccessConst.SERVICE_ACCESS_VERIFY_PRINCIPLE);

        accessVerifySuccessHandler(httpServletRequest, httpServletResponse, filterChain);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    protected abstract void accessVerifySuccessHandler(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain);
//    {
//        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ANONYMOUS");
//        List<GrantedAuthority> authorities = Collections.singletonList(authority);
//
//        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(accessVerifyTag, accessPrincipal, authorities));
//    }
}
