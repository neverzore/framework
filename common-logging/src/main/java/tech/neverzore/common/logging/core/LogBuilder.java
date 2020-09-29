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
