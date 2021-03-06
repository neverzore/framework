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

    /**
     * 通过主键获取对象
     * @param id    主键
     * @return  对象
     */
    protected abstract E getById(I id);

    /**
     * 通过主键集获取对象集
     * @param ids   主键集
     * @return  对象集
     */
    protected abstract Collection<E> listByIds(Collection<I> ids);

    /**
     * 保存领域对象
     * @param entity    领域对象
     * @return  保存结果
     */
    protected abstract boolean save(E entity);

    /**
     * 批量保存领域对象
     * @param entities  领域对象集
     * @return  保存结果
     */
    protected abstract boolean saveBatch(Collection<E> entities);

    /**
     * 通过主键更新领域对象
     * @param entity    领域对象
     * @return  更新结果
     */
    protected abstract boolean updateById(E entity);

    /**
     * 批量更新领域对象
     * @param entities  领域对象集
     * @return  更新结果
     */
    protected abstract boolean updateBatchById(Collection<E> entities);


    /**
     * 通过主键删除对象
     * @param id    主键
     * @return  删除结果
     */
    protected abstract boolean removeById(I id);

    /**
     * 通过主键集删除领域对象集
     * @param ids   主键集
     * @return  删除结果
     */
    protected abstract boolean removeByIds(Collection<I> ids);

    @Override
    public abstract Response<Collection<E>> list();

    /**
     * 获取当前领域对象Class
     *
     * @return  当前领域对象类型
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
