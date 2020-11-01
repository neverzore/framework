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

package tech.neverzore.common.domain.support.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import tech.neverzore.common.domain.model.TraceableModel;

/**
 * @author: zhouzb
 * @date: 2020/9/2
 */
public abstract class UserMetaObjectHandler implements MetaObjectHandler {
    /**
     * 获取操作用户标识
     * @return  用户标识
     */
    abstract Long getUserId();

    private final String cid;
    private final String uid;

    public UserMetaObjectHandler() {
        this("cid", "uid");
    }

    public UserMetaObjectHandler(String cid, String uid) {
        this.cid = cid;
        this.uid = uid;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.getOriginalObject() instanceof TraceableModel) {
            this.strictUpdateFill(metaObject, this.cid, this::getUserId, Long.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.getOriginalObject() instanceof TraceableModel) {
            this.strictUpdateFill(metaObject, this.uid, this::getUserId, Long.class);
        }
    }
}
