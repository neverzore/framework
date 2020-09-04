package tech.neverzore.framework.common.tree;

import org.apache.commons.collections4.CollectionUtils;
import tech.neverzore.framework.common.tree.support.LevelData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 树形数据工具
 *
 * @Author: zhouzb
 * @Date: 2019/2/10
 */
public class TreeUtil {
    /**
     * 当前build实现，子节点排序默认采用TreeMap基于Key来排序
     * 时间 o(n) 空间 o(n)
     *
     * @param source
     * @return
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
            if (((LevelData) data).notExistsAncestor()) {
                parent.putIfAbsent(selfId, data);
                return;
            }

            if (origin.containsKey(parentId)) {
                origin.get(parentId).putIfAbsent(selfId, data);
            } else {
                origin.putIfAbsent(parentId, new ConcurrentHashMap<>());
                origin.get(parentId).putIfAbsent(selfId, data);
            }
        });

        List<LevelData> levelData = new ArrayList<>(parent.size());

        // 单纯父节点内存消耗较小
        TreeMap<Comparable<?>, LevelData> tree = new TreeMap<>(parent);

        // 此处不用并行是防止乱序
        tree.entrySet().stream().forEach(entry -> {
            LevelData iLevelData = entry.getValue();

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
                new TreeMap<Comparable<?>, LevelData>(subTree).entrySet().stream().forEach(sub -> {
                    node.getChildren().add(sub.getValue());
                    buildSubtree(sub.getValue(), origin);
                });
            }
        }
    }
}
