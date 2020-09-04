package tech.neverzore.framework.common.threadfactory;

import lombok.extern.slf4j.Slf4j;
import tech.neverzore.framework.logging.core.LogBuilder;
import tech.neverzore.framework.logging.core.LogContent;

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
