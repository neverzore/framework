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

package tech.neverzore.common.domain.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import tech.neverzore.common.util.reflect.ReflectionUtil;
import tech.neverzore.common.util.response.Response;
import tech.neverzore.common.util.response.ResponseStatus;
import tech.neverzore.common.domain.model.BaseModel;
import tech.neverzore.common.domain.service.IBaseService;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author: zhouzb
 * @Date: 2019/7/19
 */
public abstract class BaseServiceImpl<E extends BaseModel, I extends Serializable> implements IBaseService<E, I> {

    protected abstract E getById(I id);

    protected abstract Collection<E> listByIds(Collection<I> ids);

    protected abstract boolean save(E entity);

    protected abstract boolean saveBatch(Collection<E> entities);

    protected abstract boolean updateById(E entity);

    protected abstract boolean updateBatchById(Collection<E> entities);

    protected abstract boolean removeById(I id);

    protected abstract boolean removeByIds(Collection<I> ids);

    @Override
    public abstract Response<Collection<E>> list();

    /**
     * 获取当前实体对象Class
     *
     * @return
     */
    public Class<E> currentModelClass() {
        return (Class<E>) ReflectionUtil.getSuperClassGenericType(this.getClass(), 0);
    }

    @Override
    public Response<E> get(I id) {
        if (id == null) {
            return Response.generate(ResponseStatus.PARAM_MISS);
        }

        E entity = this.getById(id);
        if (entity == null) {
            return Response.generate(ResponseStatus.DATA_EMPTY);
        }

        return Response.success(entity);
    }

    @Override
    public Response<Collection<E>> get(Collection<I> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Response.generate(ResponseStatus.PARAM_MISS);
        }

        Collection<E> entities = this.listByIds(ids);
        if (CollectionUtils.isEmpty(entities)) {
            return Response.generate(ResponseStatus.DATA_EMPTY);
        }

        return Response.success(entities);
    }

    @Override
    public Response<E> insert(E entity) {
        if (entity == null) {
            return Response.generate(ResponseStatus.PARAM_MISS);
        }

        boolean saved = this.save(entity);
        return saved ? Response.success(entity) : Response.generate(ResponseStatus.DATA_UPDATE_FAILED);
    }

    @Override
    public Response<Collection<E>> insert(Collection<E> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Response.generate(ResponseStatus.PARAM_MISS);
        }

        boolean saved = this.saveBatch(entities);
        return saved ? Response.success(entities) : Response.generate(ResponseStatus.DATA_UPDATE_FAILED);
    }

    @Override
    public Response<E> update(E entity) {
        if (entity == null) {
            return Response.generate(ResponseStatus.PARAM_MISS);
        }

        boolean updated = this.updateById(entity);
        return updated ? Response.success(entity) : Response.generate(ResponseStatus.DATA_UPDATE_FAILED);
    }

    @Override
    public Response<Collection<E>> update(Collection<E> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Response.generate(ResponseStatus.PARAM_MISS);
        }

        boolean updated = this.updateBatchById(entities);
        return updated ? Response.success(entities) : Response.generate(ResponseStatus.DATA_UPDATE_FAILED);
    }

    @Override
    public Response<Void> delete(I id) {
        if (id == null) {
            return Response.generate(ResponseStatus.PARAM_MISS);
        }

        boolean removed = this.removeById(id);
        return removed ? Response.success() : Response.generate(ResponseStatus.DATA_UPDATE_FAILED);
    }

    @Override
    public Response<Void> delete(Collection<I> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Response.generate(ResponseStatus.PARAM_MISS);
        }

        boolean removed = this.removeByIds(ids);
        return removed ? Response.success() : Response.generate(ResponseStatus.DATA_UPDATE_FAILED);
    }

}
