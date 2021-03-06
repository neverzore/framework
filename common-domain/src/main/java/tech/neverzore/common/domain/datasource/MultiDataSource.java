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

import javax.sql.DataSource;

/**
 * 多数据源接口
 * @author: zhouzb
 * @date: 2021/1/9
 */
public interface MultiDataSource {

    /**
     * 通过标识获取对应数据源
     * @param tag   标识
     * @return  数据源
     */
    DataSource obtainDataSource(String tag);
}
