package com.king.retrofit.retrofithelper.parser;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

/**
 * 域名解析器
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class DomainParser implements HttpUrlParser {

    /**
     * 缓存支持多个 BaseUrl相关的 Url
     */
    private final LruCache<String, String> mCacheUrlMap;

    public DomainParser() {
        this(128);
    }

    public DomainParser(int maxSize) {
        mCacheUrlMap = new LruCache<>(maxSize);
    }

    /**
     * 解析 httpUrl, 将 httpUrl 的 baseUrl 换成 domainUrl
     *
     * @param domainUrl
     * @param httpUrl
     * @param pathSegmentOffsets
     * @return
     */
    @Override
    public HttpUrl parseHttpUrl(@NonNull HttpUrl domainUrl, @NonNull HttpUrl httpUrl, int pathSegmentOffsets) {

        HttpUrl.Builder builder = httpUrl.newBuilder();
        // 优先从缓存里面取
        String url = mCacheUrlMap.get(getUrlKey(domainUrl, httpUrl));
        if (TextUtils.isEmpty(url)) {
            for (int i = 0; i < httpUrl.pathSize(); i++) {
                builder.removePathSegment(0);
            }
            List<String> newPathSegments = new ArrayList<>();
            newPathSegments.addAll(domainUrl.encodedPathSegments());
            if (pathSegmentOffsets > 0) {
                List<String> pathSegments = httpUrl.encodedPathSegments();
                // 按照偏移量进行截取
                newPathSegments.addAll(pathSegments.subList(pathSegmentOffsets, pathSegments.size()));
            } else {
                newPathSegments.addAll(httpUrl.encodedPathSegments());
            }
            for (String pathSegment : newPathSegments) {
                builder.addEncodedPathSegment(pathSegment);
            }
        } else {
            builder.encodedPath(url);
        }
        HttpUrl resultUrl = builder.scheme(domainUrl.scheme())
                .host(domainUrl.host())
                .port(domainUrl.port())
                .build();

        updateThreadName(resultUrl);
        if (TextUtils.isEmpty(url)) {
            // 缓存 Url
            mCacheUrlMap.put(getUrlKey(domainUrl, httpUrl), resultUrl.encodedPath());
        }
        return resultUrl;
    }

    /**
     * 更新线程名称
     *
     * @param httpUrl
     */
    private void updateThreadName(@NonNull HttpUrl httpUrl) {
        Thread.currentThread().setName("OkHttp " + httpUrl.redact());
    }

    /**
     * 获取用于缓存 Url 时的 key，
     *
     * @param domainUrl
     * @param currentUrl
     * @return
     */
    private String getUrlKey(@NonNull HttpUrl domainUrl, @NonNull HttpUrl currentUrl) {
        return String.format("%s_%s", domainUrl.encodedPath(), currentUrl.encodedPath());
    }
}
