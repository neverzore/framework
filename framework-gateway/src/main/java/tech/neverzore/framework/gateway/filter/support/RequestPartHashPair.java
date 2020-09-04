package tech.neverzore.framework.gateway.filter.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhouzb
 * @date 2019/6/11
 */
@Getter
@Setter
@AllArgsConstructor
public class RequestPartHashPair {
    private String key;
    private String hash;
}
