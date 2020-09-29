package tech.neverzore.common.logging.core;

/**
 * 日志分类
 * @author zhouzb
 * @date 2019/4/25
 */
public enum LogType {
    /**
     * 执行日志
     */
    NORMAL("NORMAL"),
    /**
     * 监控日志
     */
    MONITOR("MONITOR"),
    /**
     * 审计日志
     */
    AUDIT("AUDIT"),
    /**
     * 统计日志
     */
    STATS("STATS");

    private String desc;
    public String getDesc() {
        return this.desc;
    }

    LogType(String desc) {
        this.desc = desc;
    }
}
