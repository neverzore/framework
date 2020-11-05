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

package tech.neverzore.common.logging;

import org.slf4j.LoggerFactory;
import tech.neverzore.common.logging.aspect.LogAspect;
import tech.neverzore.common.logging.core.LogHolder;

import java.util.Objects;

/**
 * @author: zhouzb
 * @date: 2020/10/29
 */
public class Logger {
    public static String DEFAULT_LOGGER = "NEVERZORE-DEFAULT";

    private static org.slf4j.Logger getLogger(Class<?> caller) {
        if (caller == null) {
            return LoggerFactory.getLogger(DEFAULT_LOGGER);
        }

        return LoggerFactory.getLogger(caller);
    }

    private static org.slf4j.Logger getLogger(String name) {
        if (Objects.isNull(name)) {
            return LoggerFactory.getLogger(DEFAULT_LOGGER);
        }

        return LoggerFactory.getLogger(name);
    }

    public static void debug(Class<?> caller, String message) {
        org.slf4j.Logger logger = getLogger(caller);
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public static void info(Class<?> caller, String message) {
        org.slf4j.Logger logger = getLogger(caller);
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    public static void warn(Class<?> caller, String message) {
        org.slf4j.Logger logger = getLogger(caller);
        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    public static void error(Class<?> caller, String message) {
        org.slf4j.Logger logger = getLogger(caller);
        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
    }

    public static void error(Class<?> caller, String message, Throwable cause) {
        org.slf4j.Logger logger = getLogger(caller);
        if (logger.isErrorEnabled()) {
            logger.error(message, cause);
        }
    }

    public static boolean isDebugEnabled() {
        return getLogger(LogHolder.currentLogger()).isDebugEnabled();
    }

    public static boolean isInfoEnabled() {
        return getLogger(LogHolder.currentLogger()).isInfoEnabled();
    }

    public static boolean isWarnEnabled() {
        return getLogger(LogHolder.currentLogger()).isWarnEnabled();
    }

    public static boolean isErrorEnabled() {
        return getLogger(LogHolder.currentLogger()).isErrorEnabled();
    }
}
