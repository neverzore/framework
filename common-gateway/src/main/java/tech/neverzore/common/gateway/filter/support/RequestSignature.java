package tech.neverzore.common.gateway.filter.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhouzb
 * @date 2019/6/10
 */
@Getter
@Setter
@AllArgsConstructor
public class RequestSignature {
    private String appId;
    private String noncestr;
    private String timestamp;
    private String signature;

    public boolean incomplete() {
        return StringUtils.isAnyEmpty(appId, noncestr, timestamp, signature);
    }
}
