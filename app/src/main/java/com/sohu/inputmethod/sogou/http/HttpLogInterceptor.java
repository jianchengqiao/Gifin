/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sohu.inputmethod.sogou.http;

import android.support.annotation.NonNull;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

public final class HttpLogInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String NEW_LINE = "\n                  ";
    private static final String NEW_LINE_STAR = "\n****************";

    private final Logger logger;
    private final StringBuffer log = new StringBuffer();
    private volatile Level level = Level.NONE;

    public HttpLogInterceptor(Logger logger) {
        this.logger = logger;
    }

    public Level getLevel() {
        return level;
    }

    /**
     * Change the level at which this interceptor logs.
     */
    public HttpLogInterceptor setLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        log.delete(0, log.length());
        log.append(NEW_LINE_STAR);
        Level level = this.level;

        Request request = chain.request();
        
        if (level == Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = level == Level.BODY;
        boolean logHeaders = logBody || level == Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        String requestStartMessage = "--> "
                + request.method()
                + ' ' + request.url()
                + (connection != null ? " " + connection.protocol() : "");
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        log.append(requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    log.append(NEW_LINE);
                    log.append("Content-Type: ").append(requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    log.append(NEW_LINE);
                    log.append("Content-Length: ").append(requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    log.append(NEW_LINE);
                    log.append(name).append(": ").append(headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                log.append(NEW_LINE_STAR);
                log.append("--> END ").append(request.method());
            } else if (bodyHasUnknownEncoding(request.headers())) {
                log.append(NEW_LINE_STAR);
                log.append("--> END ").append(request.method()).append(" (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (isPlaintext(buffer)) {
                    log.append('\n').append(NEW_LINE);
                    log.append(buffer.readString(charset));
                    log.append(NEW_LINE_STAR);
                    log.append("--> END ").append(request.method()).append(" (")
                            .append(requestBody.contentLength()).append("-byte body)");
                } else {
                    log.append(NEW_LINE_STAR);
                    log.append("--> END ").append(request.method()).append(" (binary ")
                            .append(requestBody.contentLength()).append("-byte body omitted)");
                }
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log.append(NEW_LINE_STAR);
            log.append("<-- HTTP FAILED: ").append(e);
            throw e;
        }
        log.append("\n");
        logger.log(log.toString());
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        log.delete(0, log.length());
        log.append(NEW_LINE_STAR);
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        log.append("<-- ").append(response.code())
                .append(response.message().isEmpty() ? "" : ' ' + response.message())
                .append(' ').append(response.request().url())
                .append(" (").append(tookMs).append("ms")
                .append(!logHeaders ? ", " + bodySize + " body" : "").append(')');
        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                log.append(NEW_LINE);
                log.append(headers.name(i)).append(": ").append(headers.value(i));
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                log.append(NEW_LINE_STAR);
                log.append("<-- END HTTP");
            } else if (bodyHasUnknownEncoding(response.headers())) {
                log.append(NEW_LINE_STAR);
                log.append("<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Long gzippedLength = null;
                if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
                    gzippedLength = buffer.size();
                    GzipSource gzippedResponseBody = null;
                    try {
                        gzippedResponseBody = new GzipSource(buffer.clone());
                        buffer = new Buffer();
                        buffer.writeAll(gzippedResponseBody);
                    } finally {
                        if (gzippedResponseBody != null) {
                            gzippedResponseBody.close();
                        }
                    }
                }

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (!isPlaintext(buffer)) {
                    log.append(NEW_LINE_STAR);
                    log.append("<-- END HTTP (binary ").append(buffer.size()).append("-byte body omitted)");
                    return response;
                }

                if (contentLength != 0) {
                    log.append('\n').append(NEW_LINE);
                    log.append(buffer.clone().readString(charset));
                }

                if (gzippedLength != null) {
                    log.append(NEW_LINE_STAR);
                    log.append("<-- END HTTP (").append(buffer.size()).append("-byte, ")
                            .append(gzippedLength).append("-gzipped-byte body)");
                } else {
                    log.append(NEW_LINE_STAR);
                    log.append("<-- END HTTP (").append(buffer.size()).append("-byte body)");
                }
            }
        }
        logger.log(log.toString());
        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !contentEncoding.equalsIgnoreCase("identity")
                && !contentEncoding.equalsIgnoreCase("gzip");
    }

    public enum Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    public interface Logger {
        void log(String message);
    }
}
