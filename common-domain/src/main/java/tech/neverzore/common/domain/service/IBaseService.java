package tech.neverzore.common.domain.service;

import tech.neverzore.common.util.response.Response;

import java.util.Collection;

/**
 * @Author: zhouzb
 * @Date: 2019/7/19
 */
public interface IBaseService<E, T> {
    /**
     * 实体对象
     *
     * @return
     */
    default E entityInstance() {
        throw new IllegalAccessError("please implement this method in subclass");
    }

    default E entityInstance(T id) {
        throw new IllegalAccessError("please implement this method in subclass");
    }

    /**
     * 通过主键获取对象
     *
     * @param id 主键
     * @return
     */
    Response<E> get(T id);

    /**
     * 通过主键集合获取对象集合
     *
     * @param ids 主键集合
     * @return
     */
    Response<Collection<E>> get(Collection<T> ids);

    /**
     * 插入对象
     *
     * @param entity 实体对象
     * @return
     */
    Response<E> insert(E entity);

    /**
     * 批量插入对象
     *
     * @param entities 对象集合
     * @return
     */
    Response<Collection<E>> insert(Collection<E> entities);

    /**
     * 更新对象
     *
     * @param entity 实体对象
     * @return
     */
    Response<E> update(E entity);

    /**
     * 更新对象集合
     *
     * @param entities 对象集合
     * @return
     */
    Response<Collection<E>> update(Collection<E> entities);

    /**
     * 删除对象
     *
     * @param id 对象主键
     * @return
     */
    Response<Void> delete(T id);

    /**
     * 删除对象集合
     *
     * @param ids 对象主键集合
     * @return
     */
    Response<Void> delete(Collection<T> ids);

    /**
     * 获取对象列表
     *
     * @return
     */
    Response<Collection<E>> list();
}
