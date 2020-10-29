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

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhouzb
 * @date 2019/6/10
 */
public class RequestSignature {
    private String appId;
    private String noncestr;
    private String timestamp;
    private String signature;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean incomplete() {
        return StringUtils.isAnyEmpty(appId, noncestr, timestamp, signature);
    }

    public RequestSignature(String appId, String noncestr, String timestamp, String signature) {
        this.appId = appId;
        this.noncestr = noncestr;
        this.timestamp = timestamp;
        this.signature = signature;
    }
}
