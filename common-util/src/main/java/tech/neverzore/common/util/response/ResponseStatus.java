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

package tech.neverzore.common.util.response;

/**
 * @author: zhouzb
 * @date: 2019/7/19
 */
public enum ResponseStatus {
    SUCCESS("200", "成功"),
    AUTH_ERROR("400", "校验失败"),
    ERROR("500", "失败"),
    DATA_EMPTY("600", "数据为空"), DATA_NOT_UNIQUE("601", "数据不唯一"), DATA_UPDATE_FAILED("602", "数据更新失败"),
    PARAM_MISS("700", "参数缺失");

    private String code;
    private String desc;

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    ResponseStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
