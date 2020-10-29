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
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import tech.neverzore.common.logging.core.LogBuilder;
import tech.neverzore.common.logging.core.LogType;
import tech.neverzore.common.logging.core.Logger;

/**
 * @author: zhouzb
 * @date: 2020/10/29
 */
@Aspect
public class LogAspect {
    @Pointcut("@annotation(tech.neverzore.common.logging.aspect.Log)")
    public void execute() {

    }

    @Around(value = "execute() && @annotation(log)")
    public Object around(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        LogType type = log.type();

        long begin = System.currentTimeMillis();
        Object retVal = joinPoint.proceed();
        long cost = System.currentTimeMillis() - begin;

        Class<?> declaringType = joinPoint.getSignature().getDeclaringType();

        String happening = StringUtils.EMPTY;

        if (type.equals(LogType.MONITOR)) {
            String name = joinPoint.getSignature().getName();
            happening = String.format("Method %s, Parameter %s, Cost %s", name, JSON.toJSONString(joinPoint.getArgs()), cost);
        }

        Logger.info(declaringType, LogBuilder.generate(log.value(), happening));

        return retVal;
    }

    @AfterThrowing(value = "execute() && @annotation(log)", throwing = "t")
    public void afterThrowing(JoinPoint joinPoint, Log log, Throwable t) {
        LogType type = log.type();

        Class<?> declaringType = joinPoint.getSignature().getDeclaringType();
        String name = joinPoint.getSignature().getName();
        String happening = String.format("Method %s, Parameter %s executed failed", name, JSON.toJSONString(joinPoint.getArgs()));

        Logger.error(declaringType,
                LogBuilder.generate(log.value(), happening, t.getMessage(), "", type),
                t);
    }

}
