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

package tech.neverzore.common.security.jwt.support;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.util.Map;

/**
 * @author zhouzb
 * @date 2019/5/29
 */
public interface JwtManager {
    /**
     * 是否签名格式
     *
     * @param token token
     * @return 是否符合Token格式规范
     */
    boolean isSigned(String token);

    /**
     * 生成Token
     *
     * @param user     颁发对象
     * @param duration 有效时间
     * @param claims   具体项
     * @return JWT token
     */
    String encode(String user, Long duration, Map<String, Object> claims);

    /**
     * 解析Token
     *
     * @param jwt token
     * @return 解析出来的载荷
     * @throws ExpiredJwtException      失效
     * @throws UnsupportedJwtException  不支持
     * @throws MalformedJwtException
     * @throws SignatureException
     * @throws IllegalArgumentException
     */
    Claims decode(String jwt) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;
}
