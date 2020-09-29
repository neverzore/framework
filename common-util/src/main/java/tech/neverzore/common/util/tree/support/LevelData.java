package tech.neverzore.common.util.tree.support;

import java.util.List;

/**
 * 父子数据接口
 *
 * @Author: zhouzb
 * @Date: 2019/6/10
 */
public interface LevelData {
    /**
     * 获取当前自身标识符，也可以可以考虑复合标识
     *
     * @return 返回Comparable的目的是通过TreeMap来实现默认排序
     */
    Comparable<?> getSelf();

    Comparable<?> getParent();

    List<LevelData> getChildren();

    default boolean notExistsAncestor() {
        if (this.getParent() == null) {
            return true;
        }

        return false;
    }
}
