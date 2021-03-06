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

package tech.neverzore.common.util.tree.support;

import java.util.List;

/**
 * 父子数据接口
 *
 * @author: zhouzb
 * @date: 2019/6/10
 */
public interface LevelData {
    /**
     * 获取当前自身标识符，也可以可以考虑复合标识
     *
     * @return 返回Comparable的目的是通过TreeMap来实现默认排序
     */
    Comparable<?> getSelf();

    /**
     * 获取父级标识符
     * @return  父级标识
     */
    Comparable<?> getParent();

    /**
     * 获取子集
     * @return  子集
     */
    List<LevelData> getChildren();

    /**
     * 判断是否存在祖先
     * @return  是否存在祖先
     */
    default boolean notExistsAncestor() {
        return this.getParent() == null;
    }
}
