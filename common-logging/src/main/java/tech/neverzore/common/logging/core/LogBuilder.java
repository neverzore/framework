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

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhouzb
 * @date 2019/5/23
 */
public class LogBuilder {
    private LogType type;
    private String tag;
    private String happening;
    private String error;
    private String suggestion;

    public String getTag() {
        return tag;
    }

    public LogBuilder setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public LogType getType() {
        return type;
    }

    public LogBuilder setType(LogType type) {
        this.type = type;
        return this;
    }

    public String getHappening() {
        return happening;
    }

    public LogBuilder setHappening(String happening) {
        this.happening = happening;
        return this;
    }

    public String getError() {
        return error;
    }

    public LogBuilder setError(String error) {
        this.error = error;
        return this;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public LogBuilder setSuggestion(String suggestion) {
        this.suggestion = suggestion;
        return this;
    }

    private LogBuilder() {
        this.tag = StringUtils.EMPTY;
        this.type = LogType.NORMAL;
    }

    public static LogBuilder builder() {
        return new LogBuilder();
    }

    public LogContent build() {
        return new LogContent(this.tag, this.happening, this.error, this.suggestion, this.type);
    }

    public static String generate(String happening) {
        return generate(StringUtils.EMPTY, happening);
    }

    public static String generate(String tag, String happening) {
        return generate(tag, happening, StringUtils.EMPTY);
    }

    public static String generate(String tag, String happening, String error) {
        return generate(tag, happening, error, StringUtils.EMPTY);
    }

    public static String generate(String tag, String happening, String error, String suggestion) {
        return generate(tag, happening, error, suggestion, LogType.NORMAL);
    }

    public static String generate(String tag, String happening, String error, String suggestion, LogType logType) {
        return LogBuilder.builder().setTag(tag).setType(logType).setHappening(happening).setError(error).setSuggestion(suggestion).build().toString();
    }
}
