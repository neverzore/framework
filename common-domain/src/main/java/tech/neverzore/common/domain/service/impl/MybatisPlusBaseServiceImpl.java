package tech.neverzore.common.domain.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import tech.neverzore.common.util.response.Response;
import tech.neverzore.common.util.response.ResponseStatus;
import tech.neverzore.common.domain.model.BaseModel;
import tech.neverzore.common.domain.service.IBaseService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author zhouzb
 * @date 2020/6/12
 */
public class MybatisPlusBaseServiceImpl<E extends BaseModel, I extends Serializable, M extends BaseMapper<E>> extends BaseServiceImpl<E, I> implements IBaseService<E, I> {

    protected ServiceImpl<M, E> service;

    public MybatisPlusBaseServiceImpl() {
        this.service = new ServiceImpl<>();
    }

    @Override
    protected E getById(I id) {
        return this.service.getById(id);
    }

    @Override
    protected Collection<E> listByIds(Collection<I> collection) {
        return this.service.listByIds(collection);
    }

    @Override
    protected boolean save(E e) {
        return this.service.save(e);
    }

    @Override
    protected boolean saveBatch(Collection<E> collection) {
        return this.service.saveBatch(collection);
    }

    @Override
    protected boolean updateById(E e) {
        return this.service.updateById(e);
    }

    @Override
    protected boolean updateBatchById(Collection<E> ids) {
        return this.service.updateBatchById(ids);
    }

    @Override
    protected boolean removeById(I id) {
        return this.service.removeById(id);
    }

    @Override
    protected boolean removeByIds(Collection<I> ids) {
        return this.service.removeByIds(ids);
    }

    @Override
    public Response<Collection<E>> list() {
        List<E> list = this.service.list();
        return CollectionUtils.isEmpty(list) ? Response.generate(ResponseStatus.DATA_EMPTY) : Response.success(list);
    }
}
