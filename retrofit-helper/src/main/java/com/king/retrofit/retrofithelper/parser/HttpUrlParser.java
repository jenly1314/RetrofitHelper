package com.king.retrofit.retrofithelper.parser;

import androidx.annotation.NonNull;

import okhttp3.HttpUrl;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface HttpUrlParser {


    /**
     * 解析 httpUrl, 将 httpUrl 的 baseUrl 换成 domainUrl
     * @param domainUrl
     * @param httpUrl
     * @return
     */
    HttpUrl parseHttpUrl(@NonNull HttpUrl domainUrl,@NonNull HttpUrl httpUrl);
}
