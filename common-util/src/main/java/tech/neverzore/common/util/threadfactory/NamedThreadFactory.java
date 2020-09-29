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

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: zhouzb
 * @Date: 2019/12/19
 */
public class NamedThreadFactory implements ThreadFactory {
    private String app;
    private String namePrefix;
    private final AtomicInteger poolNumber;

    public NamedThreadFactory(String app, String namePrefix) {
        this.app = app;
        this.namePrefix = namePrefix;
        this.poolNumber = new AtomicInteger(1);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread nThread = new Thread(r, new StringBuilder().append(this.namePrefix).append("-thread-").append(poolNumber.getAndIncrement()).toString());
        nThread.setUncaughtExceptionHandler(new DefaultThreadExceptionHandler(app));
        return nThread;
    }
}
