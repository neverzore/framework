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
