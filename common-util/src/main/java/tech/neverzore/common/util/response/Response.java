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

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author: zhouzb
 * @date: 2019/7/19
 */
public final class Response<E> implements Serializable {

    private static final long serialVersionUID = 2081864398765097311L;

    @JsonIgnore
    @JSONField(serialize = false)
    private ResponseStatus status;

    private String code;
    private String message;
    private E payload;

    private Response(ResponseStatus status, String message, E payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
        this.code = status.getCode();
    }

    private Response(String code, String message, E payload) {
        this.code = code;
        this.message = message;
        this.payload = payload;
    }

    public boolean isSuccess() {
        return ResponseStatus.SUCCESS.getCode().equals(this.status.getCode());
    }

    public static final <T> Response<T> success() {
        return success(ResponseStatus.SUCCESS.getDesc());
    }

    public static final <T> Response<T> success(String message) {
        return success(message, null);
    }

    public static final <T> Response<T> success(T payload) {
        return success(ResponseStatus.SUCCESS.getDesc(), payload);
    }

    public static final <T> Response<T> success(String message, T payload) {
        Response<T> response = new Response<>(ResponseStatus.SUCCESS, message, payload);
        return response;
    }

    public static final <T> Response<T> error() {
        return error(ResponseStatus.ERROR.getDesc());
    }

    public static final <T> Response<T> error(String message) {
        return error(message, null);
    }

    public static final <T> Response<T> error(T payload) {
        return error(ResponseStatus.ERROR.getDesc(), payload);
    }

    public static final <T> Response<T> error(String message, T payload) {
        Response<T> response = new Response<>(ResponseStatus.ERROR, message, payload);
        return response;
    }

    public static final <T> Response<T> generate(ResponseStatus status) {
        return generate(status, null);
    }

    public static final <T> Response<T> generate(ResponseStatus status, T payload) {
        Response<T> response = new Response<>(status, status.getDesc(), payload);
        return response;
    }

    public static final <T> Response<T> generate(ResponseStatus status, String message) {
        return generate(status, message, null);
    }

    public static final <T> Response<T> generate(ResponseStatus status, String message, T payload) {
        Response<T> response = new Response<>(status, message, payload);
        return response;
    }

    public static final <T> Response<T> generate(String code) {
        return generate(code, StringUtils.EMPTY, null);
    }

    public static final <T> Response<T> generate(String code, String message) {
        return generate(code, message, null);
    }

    public static final <T> Response<T> generate(String code, String message, T payload) {
        Response<T> response = new Response<>(code, message, payload);
        return response;
    }

}
