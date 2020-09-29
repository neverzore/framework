package tech.neverzore.common.util.response;

/**
 * @Author: zhouzb
 * @Date: 2019/7/19
 */
public enum ResponseStatus {
    SUCCESS("200", "成功"),
    AUTH_ERROR("400", "校验失败"),
    ERROR("500", "失败"),
    DATA_EMPTY("600", "数据为空"), DATA_NOT_UNIQUE("601", "数据不唯一"), DATA_UPDATE_FAILED("602", "数据更新失败"),
    PARAM_MISS("700", "参数缺失");

    private String code;
    private String desc;

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    ResponseStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
