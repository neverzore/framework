package tech.neverzore.common.domain.support.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import tech.neverzore.common.domain.model.TraceableModel;

import java.time.LocalDateTime;

public class DateTimeMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.getOriginalObject() instanceof TraceableModel) {
            this.strictUpdateFill(metaObject, "cdt", () -> LocalDateTime.now(), LocalDateTime.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.getOriginalObject() instanceof TraceableModel) {
            this.strictUpdateFill(metaObject, "udt", () -> LocalDateTime.now(), LocalDateTime.class);
        }
    }
}
