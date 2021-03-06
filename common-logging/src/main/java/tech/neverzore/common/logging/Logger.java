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
import tech.neverzore.common.logging.core.LogHolder;

import java.util.Objects;

/**
 * @author: zhouzb
 * @date: 2020/10/29
 */
public class Logger {
    public static String DEFAULT_LOGGER = "NEVERZORE";

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
        debug(caller.getSimpleName(), message);
    }

    public static void info(Class<?> caller, String message) {
        info(caller.getSimpleName(), message);
    }

    public static void warn(Class<?> caller, String message) {
        warn(caller.getSimpleName(), message);
    }

    public static void error(Class<?> caller, String message) {
        error(caller.getSimpleName(), message);
    }

    public static void error(Class<?> caller, String message, Throwable cause) {
        error(caller.getSimpleName(), message, cause);
    }

    public static void debug(String name, String message) {
        org.slf4j.Logger logger = getLogger(name);
        logger.debug(message);
    }

    public static void info(String name, String message) {
        org.slf4j.Logger logger = getLogger(name);
        logger.info(message);
    }

    public static void warn(String name, String message) {
        org.slf4j.Logger logger = getLogger(name);
        logger.warn(message);
    }

    public static void error(String name, String message) {
        org.slf4j.Logger logger = getLogger(name);
        logger.error(message);
    }

    public static void error(String name, String message, Throwable cause) {
        org.slf4j.Logger logger = getLogger(name);
        logger.error(message, cause);
    }

    private static void checkBefore() {
        if (!LogHolder.exists()) {
            throw new RuntimeException("Current logger is empty, please check whether Log is configured.");
        }
    }

    public static boolean isDebugEnabled() {
        checkBefore();

        return getLogger(LogHolder.currentLogger()).isDebugEnabled();
    }

    public static boolean isInfoEnabled() {
        checkBefore();

        return getLogger(LogHolder.currentLogger()).isInfoEnabled();
    }

    public static boolean isWarnEnabled() {
        checkBefore();

        return getLogger(LogHolder.currentLogger()).isWarnEnabled();
    }

    public static boolean isErrorEnabled() {
        checkBefore();

        return getLogger(LogHolder.currentLogger()).isErrorEnabled();
    }

    public static boolean isDebugEnabled(Class<?> caller) {
        return getLogger(caller).isDebugEnabled();
    }

    public static boolean isInfoEnabled(Class<?> caller) {
        return getLogger(caller).isInfoEnabled();
    }

    public static boolean isWarnEnabled(Class<?> caller) {
        return getLogger(caller).isWarnEnabled();
    }

    public static boolean isErrorEnabled(Class<?> caller) {
        return getLogger(caller).isErrorEnabled();
    }
}
