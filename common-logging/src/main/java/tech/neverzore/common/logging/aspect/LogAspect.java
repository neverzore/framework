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

package tech.neverzore.common.logging.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import tech.neverzore.common.logging.Logger;
import tech.neverzore.common.logging.aspect.support.Log;
import tech.neverzore.common.logging.core.LogBuilder;
import tech.neverzore.common.logging.core.LogHolder;
import tech.neverzore.common.logging.core.LogType;

/**
 * @author: zhouzb
 * @date: 2020/10/29
 */
public class LogAspect {
    @Pointcut("@annotation(tech.neverzore.common.logging.aspect.support.Log)")
    public void execute() {

    }

    protected void doAroundInternal(JoinPoint joinPoint, Log log, Object retVal) {
        LogType type = log.type();
        Class<?> declaringType = joinPoint.getSignature().getDeclaringType();
        String name = joinPoint.getSignature().getName();

        if (Logger.isInfoEnabled(getClass())) {

        }
        String happening = null;
        if (LogType.MONITOR.equals(type)) {
            happening = String.format("class [%s] method [%s] parameter [%s] return [%s]",
                    declaringType.getSimpleName(),
                    name,
                    JSON.toJSONString(joinPoint.getArgs()),
                    JSON.toJSONString(retVal));
        }

        Logger.info(LogHolder.exists() ? LogHolder.currentLogger() : declaringType.getName(), LogBuilder.generate(log.tag(), happening));
    }

    @Around(value = "execute() && @annotation(log)")
    public Object doAround(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        Object retVal = joinPoint.proceed();
        doAroundInternal(joinPoint, log, retVal);
        return retVal;
    }

    @AfterThrowing(value = "execute() && @annotation(log)", throwing = "t")
    public void doAfterThrowing(JoinPoint joinPoint, Log log, Throwable t) {
        LogType type = log.type();

        Class<?> declaringType = joinPoint.getSignature().getDeclaringType();
        String name = joinPoint.getSignature().getName();
        String happening = String.format("method [%s] parameter [%s] failed", name, JSON.toJSONString(joinPoint.getArgs()));

        Logger.error(declaringType,
                LogBuilder.generate(log.tag(), happening, t.getMessage(), "", type),
                t);
    }

    @Before(value = "execute() && @annotation(log)")
    public void doBefore(JoinPoint joinPoint, Log log) {
        String loggerName;
        if (!log.value().isEmpty()) {
            loggerName = log.value();
        } else {
            loggerName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        }
        LogHolder.currentLogger(loggerName);
    }

    @After(value = "execute()")
    public void doAfter(JoinPoint joinPoint) {
        LogHolder.reset();
    }
}
