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

package tech.neverzore.common.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Author: zhouzb
 * @Date: 2019/12/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TraceableModel extends BaseModel {
    public static Integer DELETED = 1;
    public static Integer UN_DELETED = 0;

    /**
     * 创建者ID
     */
    private Long cid;
    private Long uid;
    private LocalDateTime cdt;
    private LocalDateTime udt;
    private Integer del;
}
