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

package tech.neverzore.common.gateway.filter.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.neverzore.common.gateway.filter.support.FilterConst;
import tech.neverzore.common.gateway.filter.support.FilterOrder;
import tech.neverzore.common.gateway.filter.support.RequestPartHashPair;
import tech.neverzore.common.gateway.filter.support.RequestSignature;
import tech.neverzore.common.logging.core.LogBuilder;
import tech.neverzore.common.logging.core.LogContent;
import tech.neverzore.common.logging.core.LogType;
import tech.neverzore.common.logging.Logger;
import tech.neverzore.common.util.lang.Character;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author zhouzb
 * @date 2019/3/25
 */
public abstract class AbstractSignatureVerifyGatewayFilter extends BaseAuthGatewayFilter {
    private static final String APP = "app";
    private static final String SECRET = "secret";
    private static final String NONCESTR = "noncestr";
    private static final String SIGNATURE = "signature";
    private static final String TIMESTAMP = "timestamp";
    private static final String CONTENT = "content";

    private int multiPartReaderMaxInMemorySize;

    public AbstractSignatureVerifyGatewayFilter(int multiPartReaderMaxInMemorySize) {
        this.multiPartReaderMaxInMemorySize = multiPartReaderMaxInMemorySize;
    }

    @Override
    public Mono<Void> doFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        HttpMethod method = request.getMethod();

        if (HttpMethod.GET.equals(method)) {
            boolean succeed = signatureVerifyGet(exchange);
            if (!succeed) {
                return unAuthorizedResponse(exchange.getResponse());
            }

            return chain.filter(exchange);
        } else {
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .map(dataBuffer -> {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        DataBufferUtils.release(dataBuffer);

                        return content;
                    })
                    .defaultIfEmpty(new byte[]{})
                    .flatMap(content -> {
                        if (!signatureVerifyOther(exchange, content)) {
                            return unAuthorizedResponse(exchange.getResponse());
                        }

                        final ServerWebExchange mutatedExchange = generateMutateExchange(exchange, content);
                        return chain.filter(mutatedExchange);
                    })
                    .doOnError((throwable) -> {
                        String content = LogBuilder.generate(FilterConst.SIGNATURE_FILTER, throwable.getMessage());
                        Logger.error(getClass(), content, throwable);
                    })
                    .onErrorResume(Throwable.class, (content) -> unAuthorizedResponse(exchange.getResponse()));
        }
    }

    private ServerHttpRequest generateMutateRequest(ServerHttpRequest request, byte[] content) {
        return new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
                if (content != null && content.length > 0) {
                    return DataBufferUtils.read(new ByteArrayResource(content), new DefaultDataBufferFactory(), content.length);
                } else {
                    return Flux.empty();
                }
            }
        };
    }

    private ServerWebExchange generateMutateExchange(ServerWebExchange exchange, byte[] content) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest mutatedRequest = generateMutateRequest(request, content);
        return exchange.mutate().request(mutatedRequest).build();
    }

    /**
     * 通过AppID获取AppSecret
     *
     * @param appId 应用ID
     * @return 应用密钥
     */
    protected abstract String getAppSecret(String appId);

    private boolean isFixedKey(String key) {
        return AbstractSignatureVerifyGatewayFilter.APP.equals(key)
                || AbstractSignatureVerifyGatewayFilter.SIGNATURE.equals(key)
                || AbstractSignatureVerifyGatewayFilter.NONCESTR.equals(key)
                || AbstractSignatureVerifyGatewayFilter.TIMESTAMP.equals(key);
    }

    private StringBuilder generateSignaturePart(String key, Object value, boolean multiValue) {
        if (value == null) {
            return new StringBuilder(0);
        }

        Object v = value;

        if (List.class.isAssignableFrom(value.getClass())) {
            if (CollectionUtils.isEmpty((List) value)) {
                return new StringBuilder("[]");
            }

            if (multiValue) {
                if (((List<?>) value).size() == 1) {
                    v = ((List<?>) value).get(0);
                }
            } else {
                List<StringBuilder> collect = (List<StringBuilder>) ((List) value).stream()
                        .filter(x -> x != null)
                        .map(item -> {
                            if (Map.class.isAssignableFrom(item.getClass())) {
                                Map<String, Object> mItem = (Map<String, Object>) item;
                                List<StringBuilder> stringBuilders = generateSignatureParts(mItem);
                                return stringBuilders;
                            }

                            return new StringBuilder();
                        })
                        .flatMap(item -> ((List) item).stream())
                        .collect(Collectors.toList());

                v = generateSignatureContent(collect);

                // TODO
                // Other type
            }
        }

        return new StringBuilder(key.length() + String.valueOf(v).length() + 2).append(key).append(Character.EQUAL).append(v).append(Character.AND);
    }

    private List<StringBuilder> generateSignatureParts(List<RequestPartHashPair> requestPartHashPairs) {
        List<StringBuilder> parts = new ArrayList<>(requestPartHashPairs.size());

        requestPartHashPairs.stream().sorted().forEachOrdered(requestPartHashPair -> {
            if (this.isFixedKey(requestPartHashPair.getKey())) {
                return;
            }

            StringBuilder part = generateSignaturePart(requestPartHashPair.getKey(), requestPartHashPair.getHash(), false);
            parts.add(part);
        });

        return parts;
    }

    private <T> List<StringBuilder> generateSignatureParts(Map<String, T> body) {
        List<StringBuilder> parts = new ArrayList<>(body.keySet().size());

        body.keySet().stream().sorted().forEachOrdered(key -> {
            if (this.isFixedKey(key)) {
                return;
            }

            T values = body.get(key);
            StringBuilder part = generateSignaturePart(key, values, false);
            parts.add(part);
        });

        return parts;
    }

    private <T> List<StringBuilder> generateSignatureParts(MultiValueMap<String, T> body) {
        List<StringBuilder> parts = new ArrayList<>(body.keySet().size());

        body.keySet().stream().sorted().forEachOrdered(key -> {
            if (this.isFixedKey(key)) {
                return;
            }

            List<T> values = body.get(key);
            StringBuilder part = generateSignaturePart(key, values, true);
            parts.add(part);
        });

        return parts;
    }

    private String generateSignatureContent(List<StringBuilder> signatureParts) {
        StringBuilder content = new StringBuilder(0);
        for (StringBuilder signaturePart : signatureParts) {
            content.append(signaturePart);
        }

        return content.toString();
    }

    private MultiValueMap<String, Object> convertFormUrlBody2Map(String body, Charset charset) throws UnsupportedEncodingException {
        String[] pairs = StringUtils.tokenizeToStringArray(body, Character.AND);
        MultiValueMap<String, Object> result = new LinkedMultiValueMap<>(pairs.length);

        for (String pair : pairs) {
            int idx = pair.indexOf(Character.EQUAL);
            if (idx == -1) {
                result.add(URLDecoder.decode(pair, charset.name()), null);
            } else {
                String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                result.add(name, value);
            }
        }

        return result;
    }

    private RequestSignature getSignatureFromFormUrl(MultiValueMap<String, Object> body) {
        String app = body.getFirst(APP) == null ? null : String.valueOf(body.getFirst(APP));
        String noncestr = body.getFirst(NONCESTR) == null ? null : String.valueOf(body.getFirst(NONCESTR));
        String signature = body.getFirst(SIGNATURE) == null ? null : String.valueOf(body.getFirst(SIGNATURE));
        String timestamp = body.getFirst(TIMESTAMP) == null ? null : String.valueOf(body.getFirst(TIMESTAMP));

        return new RequestSignature(app, noncestr, timestamp, signature);
    }

    private RequestSignature getSignatureFromMap(Map<String, Object> query) {
        String app = query.get(APP) == null ? null : String.valueOf(query.get(APP));
        String noncestr = query.get(NONCESTR) == null ? null : String.valueOf(query.get(NONCESTR));
        String signature = query.get(SIGNATURE) == null ? null : String.valueOf(query.get(SIGNATURE));
        String timestamp = query.get(TIMESTAMP) == null ? null : String.valueOf(query.get(TIMESTAMP));

        return new RequestSignature(app, noncestr, timestamp, signature);
    }

    private RequestSignature getSignature(ServerHttpRequest request) {
        RequestSignature requestSignature = getSignatureFromQuery(request.getQueryParams());

        if (requestSignature.incomplete()) {
            requestSignature = getSignatureFromHeader(request.getHeaders());
        }

        return requestSignature;
    }

    private RequestSignature getSignatureFromHeader(HttpHeaders headers) {
        String app = headers.getFirst(APP);
        String noncestr = headers.getFirst(NONCESTR);
        String signature = headers.getFirst(SIGNATURE);
        String timestamp = headers.getFirst(TIMESTAMP);

        return new RequestSignature(app, noncestr, timestamp, signature);
    }

    private RequestSignature getSignatureFromQuery(MultiValueMap<String, String> query) {
        String app = query.getFirst(APP);
        String noncestr = query.getFirst(NONCESTR);
        String signature = query.getFirst(SIGNATURE);
        String timestamp = query.getFirst(TIMESTAMP);

        return new RequestSignature(app, noncestr, timestamp, signature);
    }

    private boolean signatureCompletionVerify(ServerHttpRequest request, RequestSignature requestSignature) {
        if (requestSignature.incomplete()) {
            String happening = String.format("request %s, uri %s, remote %s, RequestSignature parameters incomplete",
                    request.getId(), String.valueOf(request.getURI()), request.getRemoteAddress());
            LogContent content = LogBuilder.builder()
                    .setTag(FilterConst.SIGNATURE_FILTER)
                    .setHappening(happening)
                    .build();
            Logger.warn(getClass(), content.toString());

            return true;
        }

        return false;
    }

    private boolean signatureVerifyOther(ServerWebExchange exchange, byte[] requestBody) {
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();

        if (HttpMethod.POST.equals(method)) {
            try {
                return signatureVerifyPost(exchange, requestBody);
            } catch (IOException e) {
                String content = LogBuilder.generate(FilterConst.SIGNATURE_FILTER, e.getMessage());
                Logger.error(getClass(), content, e);

                return false;
            }
        }

        return false;
    }

    private boolean signatureVerify(ServerWebExchange exchange, RequestSignature requestSignature, String content) {
        String appId = requestSignature.getAppId();
        String noncestr = requestSignature.getNoncestr();
        String timestamp = requestSignature.getTimestamp();
        String rSignature = requestSignature.getSignature();
        String appSecret = getAppSecret(appId);
        StringBuilder concatContent = new StringBuilder(APP.length() + appId.length() + 2
                + SECRET.length() + appSecret.length() + 2
                + NONCESTR.length() + noncestr.length() + 2
                + TIMESTAMP.length() + timestamp.length() + 2
                + CONTENT.length() + content.length() + 1)
                .append(APP).append(Character.EQUAL).append(appId).append(Character.AND)
                .append(SECRET).append(Character.EQUAL).append(appSecret).append(Character.AND)
                .append(NONCESTR).append(Character.EQUAL).append(noncestr).append(Character.AND)
                .append(TIMESTAMP).append(Character.EQUAL).append(timestamp).append(Character.AND)
                .append(CONTENT).append(Character.EQUAL).append(content);

        String cSignature = DigestUtils.md5Hex(concatContent.toString());

        String happening = String.format("requestSignature content %s, requestSignature original %s, requestSignature hash %s", concatContent, rSignature, cSignature);
        LogContent logContent = LogBuilder.builder()
                .setTag(FilterConst.SIGNATURE_FILTER)
                .setHappening(happening)
                .setType(LogType.MONITOR)
                .build();
        Logger.debug(getClass(), logContent.toString());

        boolean matched = rSignature.equalsIgnoreCase(cSignature);
        if (!matched) {
            ServerHttpRequest request = exchange.getRequest();
            happening = String.format("request %s, uri %s, remote %s, requestSignature verified failed due to requestSignature mismatch.",
                    request.getId(), String.valueOf(request.getURI()), request.getRemoteAddress());
            logContent = LogBuilder.builder()
                    .setTag(FilterConst.SIGNATURE_FILTER)
                    .setHappening(happening)
                    .build();
            Logger.warn(getClass(), logContent.toString());
        }

        return matched;
    }

    protected boolean signatureVerifyGet(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        if (Objects.isNull(queryParams) || queryParams.isEmpty()) {
            String happening = String.format("request %s, uri %s, remote %s, GET request query parameter is empty.",
                    request.getId(), String.valueOf(request.getURI()), request.getRemoteAddress());
            Logger.warn(getClass(), LogBuilder.generate(FilterConst.SIGNATURE_FILTER, happening));

            return false;
        }

        RequestSignature requestSignature = getSignature(exchange.getRequest());

        if (signatureCompletionVerify(request, requestSignature)) {
            return false;
        }

        List<StringBuilder> signatureParts = generateSignatureParts(queryParams);
        String content = generateSignatureContent(signatureParts);

        return signatureVerify(exchange, requestSignature, content);
    }

    private boolean signatureVerifyPost(ServerWebExchange exchange, byte[] requestBody) throws IOException, JSONException {
        ServerHttpRequest request = exchange.getRequest();
        MediaType contentType = request.getHeaders().getContentType();

        if (contentType == null) {
            String happening = String.format("request %s, uri %s, remote %s, request content-type is null, resolved to TEXT_PLAIN",
                    request.getId(), String.valueOf(request.getURI()), request.getRemoteAddress());
            Logger.warn(getClass(), LogBuilder.generate(FilterConst.SIGNATURE_FILTER, happening));

            contentType = MediaType.TEXT_PLAIN;
        }

        Charset charset = contentType.getCharset();
        if (charset == null) {
            String happening = String.format("request %s, uri %s, remote %s, request charset is null, resolved to UTF-8",
                    request.getId(), String.valueOf(request.getURI()), request.getRemoteAddress());
            Logger.warn(getClass(), LogBuilder.generate(FilterConst.SIGNATURE_FILTER, happening));

            charset = StandardCharsets.UTF_8;
        }

        if (contentType.isCompatibleWith(MediaType.MULTIPART_FORM_DATA)) {
            return signatureVerifyPostMultiPart(exchange, requestBody);
        }

        String body = StreamUtils.copyToString(new ByteArrayInputStream(requestBody), charset);

        LogContent content = LogBuilder.builder().setType(LogType.MONITOR).setHappening(String.format("request body %s", body)).build();
        Logger.debug(getClass(), content.toString());

        if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            return signatureVerifyPostJson(exchange, JSON.parseObject(body, new TypeReference<Map<String, Object>>() {
            }));
        } else if (contentType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED)) {
            return signatureVerifyPostFormUrl(exchange, convertFormUrlBody2Map(body, charset));
        } else {
            return signatureVerifyPostOther(exchange, body);
        }
    }

    protected boolean signatureVerifyPostMultiPart(ServerWebExchange exchange, byte[] requestBody) throws IOException {
        RequestSignature requestSignature = getSignature(exchange.getRequest());
        if (signatureCompletionVerify(exchange.getRequest(), requestSignature)) {
            return false;
        }

        SynchronossPartHttpMessageReader synchronossPartHttpMessageReader = new SynchronossPartHttpMessageReader();
        if (this.multiPartReaderMaxInMemorySize != -1) {
            synchronossPartHttpMessageReader.setMaxInMemorySize(this.multiPartReaderMaxInMemorySize);
        }
        MultipartHttpMessageReader reader = new MultipartHttpMessageReader(synchronossPartHttpMessageReader);
        ServerHttpRequest mutateRequest = generateMutateRequest(exchange.getRequest(), requestBody);
        Mono<MultiValueMap<String, Part>> multiValueMapMono = reader.readMono(ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, Part.class), mutateRequest, Hints.from(Hints.LOG_PREFIX_HINT, exchange.getLogPrefix()));

        AtomicReference<MultiValueMap<String, Part>> atomicMultiParts = new AtomicReference<>();
        multiValueMapMono.doOnNext(atomicMultiParts::set).subscribe();

        MultiValueMap<String, Part> multiParts = atomicMultiParts.get();
        List<RequestPartHashPair> requestPartHashPairs = new CopyOnWriteArrayList<>();
        multiParts.keySet().stream().sorted().forEachOrdered(key -> {
            if (this.isFixedKey(key)) {
                return;
            }

            List<Part> parts = multiParts.get(key);
            if (CollectionUtils.isEmpty(parts)) {
                return;
            }

            for (Part part : parts) {
                Flux<DataBuffer> content = part.content();
                DataBufferUtils.join(content)
                        .filter(dataBuffer -> dataBuffer.capacity() > 0)
                        .map(dataBuffer -> dataBuffer.asByteBuffer().array())
                        .doOnNext(b -> {
                            RequestPartHashPair requestPartHashPair = new RequestPartHashPair(part.name(), DigestUtils.md5Hex(b));
                            requestPartHashPairs.add(requestPartHashPair);
                        })
                        .subscribe();
            }
        });

        List<StringBuilder> signatureParts = generateSignatureParts(requestPartHashPairs);
        String content = generateSignatureContent(signatureParts);

        return signatureVerify(exchange, requestSignature, content);
    }

    protected boolean signatureVerifyPostOther(ServerWebExchange exchange, String body) {
        ServerHttpRequest request = exchange.getRequest();
        RequestSignature requestSignature = getSignature(request);
        if (signatureCompletionVerify(request, requestSignature)) {
            return false;
        }

        return signatureVerify(exchange, requestSignature, body);
    }

    protected boolean signatureVerifyPostFormUrl(ServerWebExchange exchange, MultiValueMap<String, Object> body) {
        ServerHttpRequest request = exchange.getRequest();
        RequestSignature requestSignature = getSignature(request);
        if (requestSignature.incomplete()) {
            requestSignature = getSignatureFromFormUrl(body);
        }

        if (signatureCompletionVerify(request, requestSignature)) {
            return false;
        }

        List<StringBuilder> signatureParts = generateSignatureParts(body);
        String content = generateSignatureContent(signatureParts);

        return signatureVerify(exchange, requestSignature, content);
    }

    protected boolean signatureVerifyPostJson(ServerWebExchange exchange, Map<String, Object> body) {
        ServerHttpRequest request = exchange.getRequest();
        RequestSignature requestSignature = getSignature(request);
        if (requestSignature.incomplete()) {
            requestSignature = getSignatureFromMap(body);
        }

        if (signatureCompletionVerify(request, requestSignature)) {
            return false;
        }

        List<StringBuilder> signatureParts = generateSignatureParts(body);
        String content = generateSignatureContent(signatureParts);

        return signatureVerify(exchange, requestSignature, content);
    }

    @Override
    public int getOrder() {
        return FilterOrder.VERIFY_FILTER_ORDER + 1;
    }
}
