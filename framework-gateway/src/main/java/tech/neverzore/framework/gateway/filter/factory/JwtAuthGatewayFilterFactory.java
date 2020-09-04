package tech.neverzore.framework.gateway.filter.factory;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.util.Assert;
import tech.neverzore.framework.gateway.filter.impl.JwtAuthGatewayFilter;
import tech.neverzore.framework.gateway.filter.support.AuthTokenSource;
import tech.neverzore.framework.security.jwt.DefaultJwtManager;

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
            jwtFilter.setAuthTokenSource(AuthTokenSource.HEADER);
        } else {
            jwtFilter.setAuthTokenSource(sourceByValue);
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
