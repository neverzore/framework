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

package tech.neverzore.common.util.tree;

import org.apache.commons.collections4.CollectionUtils;
import tech.neverzore.common.util.tree.support.LevelData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 树形数据工具
 *
 * @author: zhouzb
 * @date: 2019/2/10
 */
public class TreeUtil {
    /**
     * 当前build实现，子节点排序默认采用TreeMap基于Key来排序
     * 时间 o(n) 空间 o(n)
     *
     * @param source    数据集
     * @return  树形结果集
     */
    public static List<LevelData> build(Collection<? extends LevelData> source) {
        if (CollectionUtils.isEmpty(source)) {
            return new ArrayList<>();
        }

        Map<Comparable<?>, LevelData> parent = new ConcurrentHashMap<>(source.size());
        Map<Comparable<?>, Map<Comparable<?>, LevelData>> origin = new ConcurrentHashMap<>(source.size());

        source.parallelStream().forEach(data -> {
            if (data == null) {
                return;
            }

            Comparable<?> selfId = data.getSelf();
            if (selfId == null) {
                return;
            }

            Comparable<?> parentId = data.getParent();
            if (data.notExistsAncestor()) {
                parent.putIfAbsent(selfId, data);
                return;
            }

            if (!origin.containsKey(parentId)) {
                origin.putIfAbsent(parentId, new ConcurrentHashMap<>());
            }
            origin.get(parentId).putIfAbsent(selfId, data);
        });

        List<LevelData> levelData = new ArrayList<>(parent.size());

        // 单纯父节点内存消耗较小
        TreeMap<Comparable<?>, LevelData> tree = new TreeMap<>(parent);

        // 此处不用并行是防止乱序
        tree.forEach((key, iLevelData) -> {

            levelData.add(iLevelData);

            buildSubtree(iLevelData, origin);
        });

        return levelData;
    }

    private static void buildSubtree(LevelData node,
                                     Map<Comparable<?>, Map<Comparable<?>, LevelData>> origin) {
        Comparable<?> self = node.getSelf();
        if (self != null && origin.containsKey(self)) {
            Map<Comparable<?>, LevelData> subTree = origin.get(self);

            if (subTree != null && !subTree.isEmpty()) {
                new TreeMap<>(subTree).forEach((key, value) -> {
                    node.getChildren().add(value);
                    buildSubtree(value, origin);
                });
            }
        }
    }
}
