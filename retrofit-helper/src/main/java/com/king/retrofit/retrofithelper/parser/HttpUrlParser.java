package com.king.retrofit.retrofithelper.parser;

import androidx.annotation.NonNull;

import okhttp3.HttpUrl;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface HttpUrlParser {

    /**
     * 解析 httpUrl, 将 httpUrl 的 baseUrl 换成 domainUrl
     *
     * @param domainUrl 将 httpUrl 的 baseUrl 换成 domainUrl
     * @param httpUrl 原始地址 {@link HttpUrl}
     * @param pathSegmentOffsets URL路径片段偏移量
     * @return
     */
    HttpUrl parseHttpUrl(@NonNull HttpUrl domainUrl, @NonNull HttpUrl httpUrl, int pathSegmentOffsets);
}
