package tech.neverzore.common.domain.support.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import tech.neverzore.common.domain.model.TraceableModel;

/**
 * @author: zhouzb
 * @date: 2020/9/2
 */
public abstract class UserMetaObjectHandler implements MetaObjectHandler {
    abstract Long getUserId();

    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.getOriginalObject() instanceof TraceableModel) {
            this.strictUpdateFill(metaObject, "cid", () -> getUserId(), Long.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.getOriginalObject() instanceof TraceableModel) {
            this.strictUpdateFill(metaObject, "uid", () -> getUserId(), Long.class);
        }
    }
}
