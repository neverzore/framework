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
public class LogContent {
    private static final String TYPE = "[TYPE ";
    private static final String HAP = "[HAP ";
    private static final String ERR = "[ERR ";
    private static final String SUG = "[SUG ";
    private static final String CLOSURE = "] ";

    private final LogType type;
    private final String happening;
    private final String error;
    private final String suggestion;

    LogContent(String happening) {
        this(happening, StringUtils.EMPTY);
    }

    LogContent(String happening, String error) {
        this(happening, error, StringUtils.EMPTY);
    }

    LogContent(String happening, String error, String suggestion) {
        this(happening, error, suggestion, LogType.NORMAL);
    }

    LogContent(String happening, String error, String suggestion, LogType type) {
        this.type = type;
        this.happening = happening;
        this.error = error;
        this.suggestion = suggestion;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append(TYPE).append(this.type.getDesc()).append(CLOSURE);

        if (!StringUtils.isEmpty(this.happening)) {
            builder.append(HAP).append(this.happening).append(CLOSURE);
        }

        if (!StringUtils.isEmpty(this.error)) {
            builder.append(ERR).append(this.error).append(CLOSURE);
        }

        if (!StringUtils.isEmpty(this.suggestion)) {
            builder.append(SUG).append(this.suggestion).append(CLOSURE);
        }

        return builder.toString();
    }
}
