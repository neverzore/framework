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
