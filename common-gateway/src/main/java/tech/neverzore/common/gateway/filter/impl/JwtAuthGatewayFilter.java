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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import tech.neverzore.common.gateway.filter.base.AbstractJwtAuthGatewayFilter;
import tech.neverzore.common.gateway.filter.support.FilterConst;
import tech.neverzore.common.gateway.filter.support.FilterOrder;
import tech.neverzore.common.logging.core.LogBuilder;
import tech.neverzore.common.logging.core.LogContent;
import tech.neverzore.common.security.jwt.support.JwtManager;

/**
 * @author zhouzb
 * @date 2019/5/27
 */
@Slf4j
public class JwtAuthGatewayFilter extends AbstractJwtAuthGatewayFilter {

    private JwtManager jwtManager;

    public JwtAuthGatewayFilter(JwtManager jwtManager) {
        Assert.notNull(jwtManager, "JWT manager cannot be null.");
        this.jwtManager = jwtManager;
    }

    @Override
    protected Claims decode(String authToken) {
        if (StringUtils.isBlank(authToken)) {
            return null;
        }

        Claims claims = null;
        try {
            claims = jwtManager.decode(authToken);
        } catch (ExpiredJwtException e) {
            LogContent content = LogBuilder.generate(FilterConst.JWT_FILTER, e.getMessage());
            log.error(content.toString(), e);
        } catch (UnsupportedJwtException e) {
            LogContent content = LogBuilder.generate(FilterConst.JWT_FILTER, e.getMessage());
            log.error(content.toString(), e);
        } catch (MalformedJwtException e) {
            LogContent content = LogBuilder.generate(FilterConst.JWT_FILTER, e.getMessage());
            log.error(content.toString(), e);
        } catch (SignatureException e) {
            LogContent content = LogBuilder.generate(FilterConst.JWT_FILTER, e.getMessage());
            log.error(content.toString(), e);
        } catch (IllegalArgumentException e) {
            LogContent content = LogBuilder.generate(FilterConst.JWT_FILTER, e.getMessage());
            log.error(content.toString(), e);
        } catch (Throwable t) {
            LogContent content = LogBuilder.generate(FilterConst.JWT_FILTER, t.getMessage());
            log.error(content.toString(), t);
        }

        return claims;
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