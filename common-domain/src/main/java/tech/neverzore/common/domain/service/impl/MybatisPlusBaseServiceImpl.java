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
