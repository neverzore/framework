package tech.neverzore.common.logging.core;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhouzb
 * @date 2019/5/23
 */
@Getter
@Setter
@Accessors(chain = true)
public class LogBuilder {
    private String source;
    private LogType type;
    private String happening;
    private String suggestion;

    private LogBuilder() {
        this.source = StringUtils.EMPTY;
        this.type = LogType.NORMAL;
    }

    public static LogBuilder builder() {
        return new LogBuilder();
    }

    public LogContent build() {
        return new LogContent(this.source, this.happening, this.suggestion, this.type);
    }

    public static LogContent generate(String app, String happening) {
        return generate(app, happening, StringUtils.EMPTY);
    }

    public static LogContent generate(String app, String happening, String suggestion) {
        return generate(app, happening, suggestion, LogType.NORMAL);
    }

    public static LogContent generate(String app, String happening, String suggestion, LogType logType) {
        LogContent content = LogBuilder.builder().setSource(app).setType(logType).setHappening(happening).setSuggestion(suggestion).build();
        return content;
    }
}
