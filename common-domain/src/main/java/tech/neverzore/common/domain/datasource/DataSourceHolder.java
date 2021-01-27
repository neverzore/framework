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

package tech.neverzore.common.domain.datasource;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author: zhouzb
 * @date: 2021/1/9
 */
public class DataSourceHolder {
    private static final ThreadLocal<Deque<String>> holder = ThreadLocal.withInitial(LinkedList::new);

    /**
     * 查看当前栈顶数据源标识
     * @return  当前使用数据源标识
     */
    public static String obtainCurrentDataSource() {
        return holder.get().peek();
    }

    /**
     * 将当前数据源标识入栈
     * @param dataSourceTag 数据源标识
     */
    public static void push(String dataSourceTag) {
        holder.get().push(dataSourceTag);
    }

    /**
     * 当前数据源执行完毕，出栈
     */
    public static void pop() {
        holder.get().poll();
    }
}
