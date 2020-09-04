package tech.neverzore.framework.gateway.filter.support;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouzb
 * @date 2019/5/25
 */
public enum AuthTokenSource {
    /**
     * 请求Header部分
     */
    HEADER("HEADER"),
    /**
     * 请求Query部分
     */
    QUERY("QUERY");

    private String value;

    AuthTokenSource(String value) {
        this.value = value;
    }

    private static final Map<String, AuthTokenSource> map = new HashMap<>();

    static {
        for (AuthTokenSource source : AuthTokenSource.values()) {
            map.put(source.value, source);
        }
    }

    public static AuthTokenSource getSourceByValue(String value) {
        return map.get(value);
    }
}
