package tech.neverzore.framework.domain.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import tech.neverzore.framework.common.reflect.ReflectionUtil;
import tech.neverzore.framework.common.response.Response;
import tech.neverzore.framework.common.response.ResponseStatus;
import tech.neverzore.framework.domain.model.BaseModel;
import tech.neverzore.framework.domain.service.IBaseService;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Author: zhouzb
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
