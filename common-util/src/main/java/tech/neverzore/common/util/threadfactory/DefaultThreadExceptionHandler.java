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

package tech.neverzore.common.util.threadfactory;

import lombok.extern.slf4j.Slf4j;
import tech.neverzore.common.logging.core.LogBuilder;
import tech.neverzore.common.logging.core.LogContent;

/**
 * @Author: zhouzb
 * @Date: 2019/12/18
 */
@Slf4j
public class DefaultThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
    private String app;

    public DefaultThreadExceptionHandler(String app) {
        this.app = app;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        String happening = String.format("thread %s, error %s", t.getName(), e.getMessage());
        LogContent content = LogBuilder.generate(app, happening);
        log.error(content.toString(), e);
    }
}
