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

package tech.neverzore.common.gateway.filter.support;

import org.springframework.core.Ordered;

import static org.springframework.cloud.gateway.filter.NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER;

/**
 * @author zhouzb
 * @date 2019/5/23
 */
public interface FilterOrder {
    Integer LOWEST_PRECEDENCE = Ordered.LOWEST_PRECEDENCE - 1;
    Integer HIGHEST_PRECEDENCE = Ordered.HIGHEST_PRECEDENCE + 1;
    Integer PRE_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 100;
    Integer AUTH_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 500;
    Integer VERIFY_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 10000;
    Integer ENCRYPT_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 20000;
    Integer FORWARD_FILTER_ORDER = 0;
    Integer ROUTING_FILTER_ORDER = 20000;
    Integer POST_FILTER_ORDER = WRITE_RESPONSE_FILTER_ORDER;
}
