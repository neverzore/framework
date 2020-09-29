

package tech.neverzore.common.domain.support.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import tech.neverzore.common.domain.model.TraceableModel;

import java.time.LocalDateTime;

public class DateTimeMetaObjectHandler implements MetaObjectHandler {
    private String cdt;
    private String udt;

    public DateTimeMetaObjectHandler() {
        this("cdt", "udt");
    }

    public DateTimeMetaObjectHandler(String cdt, String udt) {
        this.cdt = cdt;
        this.udt = udt;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.getOriginalObject() instanceof TraceableModel) {
            this.strictUpdateFill(metaObject, this.cdt, () -> LocalDateTime.now(), LocalDateTime.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.getOriginalObject() instanceof TraceableModel) {
            this.strictUpdateFill(metaObject, this.udt, () -> LocalDateTime.now(), LocalDateTime.class);
        }
    }
}
