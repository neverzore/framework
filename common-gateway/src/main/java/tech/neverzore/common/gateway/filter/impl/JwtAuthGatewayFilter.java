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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import tech.neverzore.common.gateway.filter.base.AbstractJwtAuthGatewayFilter;
import tech.neverzore.common.gateway.filter.support.FilterOrder;
import tech.neverzore.common.logging.core.LogBuilder;
import tech.neverzore.common.logging.Logger;
import tech.neverzore.common.security.jwt.support.JwtManager;

import java.util.Objects;

/**
 * @author zhouzb
 * @date 2019/5/27
 */
public class JwtAuthGatewayFilter extends AbstractJwtAuthGatewayFilter {

    private JwtManager jwtManager;

    public JwtAuthGatewayFilter(JwtManager jwtManager) {
        Assert.notNull(jwtManager, "JWT manager cannot be null.");
        this.jwtManager = jwtManager;
    }

    protected Claims decode(String authToken) {
        if (StringUtils.isBlank(authToken)) {
            return null;
        }

        Claims claims = null;
        try {
            claims = jwtManager.decode(authToken);
        } catch (ExpiredJwtException e) {
            String content = LogBuilder.generate(JwtAuthGatewayFilter.class.getCanonicalName(), e.getMessage());
            Logger.error(getClass(), content, e);
        } catch (UnsupportedJwtException e) {
            String content = LogBuilder.generate(JwtAuthGatewayFilter.class.getCanonicalName(), e.getMessage());
            Logger.error(getClass(), content, e);
        } catch (MalformedJwtException e) {
            String content = LogBuilder.generate(JwtAuthGatewayFilter.class.getCanonicalName(), e.getMessage());
            Logger.error(getClass(), content, e);
        } catch (SignatureException e) {
            String content = LogBuilder.generate(JwtAuthGatewayFilter.class.getCanonicalName(), e.getMessage());
            Logger.error(getClass(), content, e);
        } catch (IllegalArgumentException e) {
            String content = LogBuilder.generate(JwtAuthGatewayFilter.class.getCanonicalName(), e.getMessage());
            Logger.error(getClass(), content, e);
        } catch (Throwable t) {
            String content = LogBuilder.generate(JwtAuthGatewayFilter.class.getCanonicalName(), t.getMessage());
            Logger.error(getClass(), content, t);
        }

        return claims;
    }

    @Override
    protected String getAudience(String authToken) {
        Claims claims = decode(authToken);
        if (Objects.isNull(claims)) {
            return StringUtils.EMPTY;
        }

        return claims.getAudience();
    }

    @Override
    protected boolean isSigned(String authToken) {
        return jwtManager.isSigned(authToken);
    }

    @Override
    public int getOrder() {
        return FilterOrder.AUTH_FILTER_ORDER + 1;
    }
}
