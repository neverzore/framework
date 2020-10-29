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

import org.slf4j.LoggerFactory;

/**
 * @author: zhouzb
 * @date: 2020/10/29
 */
public class Logger {

    private static org.slf4j.Logger getLogger(Class<?> caller) {
        if (caller == null) {
            return LoggerFactory.getLogger("DEFAULT");
        }

        return LoggerFactory.getLogger(caller);
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
}
