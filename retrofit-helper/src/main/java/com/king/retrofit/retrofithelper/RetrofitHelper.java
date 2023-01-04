package com.king.retrofit.retrofithelper;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.king.retrofit.retrofithelper.annotation.DomainName;
import com.king.retrofit.retrofithelper.annotation.ResponseProgress;
import com.king.retrofit.retrofithelper.annotation.RequestProgress;
import com.king.retrofit.retrofithelper.body.ProgressRequestBody;
import com.king.retrofit.retrofithelper.body.ProgressResponseBody;
import com.king.retrofit.retrofithelper.interceptor.DomainInterceptor;
import com.king.retrofit.retrofithelper.interceptor.HeaderInterceptor;
import com.king.retrofit.retrofithelper.interceptor.ProgressInterceptor;
import com.king.retrofit.retrofithelper.interceptor.TimeoutInterceptor;
import com.king.retrofit.retrofithelper.listener.ProgressListener;
import com.king.retrofit.retrofithelper.parser.DomainParser;
import com.king.retrofit.retrofithelper.parser.HttpUrlParser;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Invocation;

/**
 * Retrofit帮助类
 * <p>
 * 主要功能介绍：
 * 1.支持管理多个 BaseUrl，且支持运行时动态改变
 * 2.支持接口自定义超时时长，满足每个接口动态定义超时时长
 * 3.支持添加公共请求头
 * <p>
 * RetrofitHelper中的核心方法
 * <p>
 * {@link #createClientBuilder()} 创建 {@link OkHttpClient.Builder}初始化一些配置参数，用于支持多个 BaseUrl
 * <p>
 * {@link #with(OkHttpClient.Builder)} 传入 {@link OkHttpClient.Builder} 配置一些参数，用于支持多个 BaseUrl
 * <p>
 * {@link #setBaseUrl(String)} 和 {@link #setBaseUrl(HttpUrl)} 主要用于设置默认的 BaseUrl。
 * <p>
 * {@link #putDomain(String, String)} 和 {@link #putDomain(String, HttpUrl)} 主要用于支持多个 BaseUrl，且支持 BaseUrl 动态改变。
 * <p>
 * {@link #setDynamicDomain(boolean)} 设置是否支持 配置多个BaseUrl，且支持动态改变，一般会通过其他途径自动开启，此方法一般不会主动用到，只有在特殊场景下可能会有此需求，所以提供此方法主要用于提供更多种可能。
 * <p>
 * {@link #setHttpUrlParser(HttpUrlParser)} 设置 HttpUrl解析器 , 当前默认采用的 {@link DomainParser} 实现类，你也可以自定义实现 {@link HttpUrlParser}
 * <p>
 * {@link #setAddHeader(boolean)} 设置是否添加头，一般会通过{@link #addHeader(String, String)}相关方法自动开启，此方法一般不会主动用到，只有特殊场景下会有此需求，主要用于提供统一控制。
 * <p>
 * {@link #addHeader(String, String)} 设置头，主要用于添加公共头消息。
 * <p>
 * {@link #addHeaders(Map)} 设置头，主要用于设置公共头消息。
 * <p>
 * {@link #addRequestListener(String, ProgressListener)} 添加请求监听。
 * <p>
 * {@link #addResponseListener(String, ProgressListener)} 添加响应监听。
 * <p>
 * 这里只是列出一些对外使用的核心方法，和相关的简单说明。如果想了解更多，可以查看对应的方法和详情。
 * <p>
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public final class RetrofitHelper {

    /**
     * BaseUrl
     */
    private HttpUrl mBaseUrl;
    /**
     * 存储支持多个 BaseUrl
     */
    private final Map<String, HttpUrl> mUrlMap;
    /**
     * 是否是否支持 配置多个BaseUrl，且支持动态改变
     */
    private boolean isDynamicDomain;
    /**
     * 是否主动设置过 支持 配置多个BaseUrl，且支持动态改变
     */
    private boolean isSetDynamicDomain;
    /**
     * 是否支持 配置动态超时时长
     */
    private boolean isDynamicTimeout;
    /**
     * Url 解析器
     */
    private HttpUrlParser mHttpUrlParser;
    /**
     * 是否添加头
     */
    private boolean isAddHeader;
    /**
     * 是否主动设置过 是否添加头，如果设置过，则以主动设置的为准
     */
    private boolean isSetAddHeader;
    /**
     * 请求头
     */
    private Map<String, String> mHeaders;
    /**
     * URL路径片段偏移量
     */
    private int mPathSegmentOffsets;
    /**
     * Handler
     */
    private Handler mHandler;
    /**
     * 请求进度监听
     */
    private Map<String, List<ProgressListener>> mRequestProgressListeners;
    /**
     * 响应进度监听
     */
    private Map<String, List<ProgressListener>> mResponseProgressListeners;
    /**
     * 进度更新间隔时间
     */
    private long mProgressUpdateIntervalTime = 300L;

    /**
     * 获取单例
     *
     * @return {@link RetrofitHelper}
     */
    public static RetrofitHelper getInstance() {
        return RetrofitHelperHolder.INSTANCE;
    }

    private static class RetrofitHelperHolder {
        private static final RetrofitHelper INSTANCE = new RetrofitHelper();
    }

    private RetrofitHelper() {
        mUrlMap = new LinkedHashMap<>();
        mHttpUrlParser = new DomainParser();
        mHeaders = new LinkedHashMap<>();
        mRequestProgressListeners = new WeakHashMap<>();
        mResponseProgressListeners = new WeakHashMap<>();
        mHandler = new Handler(Looper.getMainLooper());
        isDynamicDomain = false;
        isDynamicTimeout = true;
        isAddHeader = false;
    }

    /**
     * 创建 {@link OkHttpClient.Builder}初始化一些配置参数，用于支持多个 BaseUrl
     * 相关方法 {@link #with(OkHttpClient.Builder)} 同样具备此功能，两种选其一即可
     *
     * @return
     */
    public OkHttpClient.Builder createClientBuilder() {
        return with(new OkHttpClient.Builder());
    }

    /**
     * 传入 {@link OkHttpClient.Builder} 配置一些参数，用于支持多个 BaseUrl
     * 相关方法 {@link #createClientBuilder()} 同样具备此功能，两种选其一即可
     *
     * @param builder
     * @return
     */
    public OkHttpClient.Builder with(@NonNull OkHttpClient.Builder builder) {
        return builder.addInterceptor(new DomainInterceptor())
                .addInterceptor(new TimeoutInterceptor())
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new ProgressInterceptor());
    }

    /**
     * 设置 BaseUrl，前提条件：当 {@link #isDynamicDomain()} 为{@code true} 时才支持动态改变。
     * 通过此方法，可以动态改变 {@link Request#url()}中的 baseUrl，优先级低于{@link DomainName}
     * 只有在接口没有标记{@link DomainName}或者标记了，但没找到对应的 {@code domainUrl} 时，才能动态改变。
     *
     * @param baseUrl
     */
    public void setBaseUrl(@NonNull String baseUrl) {
        setBaseUrl(HttpUrl.parse(baseUrl));
    }

    /**
     * 设置 BaseUrl，前提条件：当 {@link #isDynamicDomain()} 为{@code true} 时才支持动态改变。
     * 通过此方法，可以动态改变 {@link Request#url()}中的 baseUrl，优先级低于{@link DomainName}
     * 只有在接口没有标记{@link DomainName}或者标记了，但没找到对应的 {@code domainUrl} 时，才能动态改变。
     *
     * @param baseUrl
     */
    public void setBaseUrl(@NonNull HttpUrl baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    /**
     * 获取 BaseUrl
     *
     * @return
     */
    public HttpUrl getBaseUrl() {
        return mBaseUrl;
    }

    /**
     * 移除 BaseUrl
     */
    public void removeBaseUrl() {
        mBaseUrl = null;
    }

    /**
     * 添加动态域名，当执行此操作时，则会自动启用{@link #isDynamicDomain}，并支持多个 BaseUrl
     * 在接口方法上添加 {@link DomainName} 标记对应的 {@code domainName}即可支持动态改变，
     *
     * @param domainName 域名别名
     * @param domainUrl  域名对应的 BaseUrl
     */
    public void putDomain(@NonNull String domainName, @NonNull String domainUrl) {
        putDomain(domainName, HttpUrl.parse(domainUrl));
    }

    /**
     * 添加动态域名，当执行此操作时，则会自动启用{@link #isDynamicDomain}，并支持多个 BaseUrl
     * 在接口方法上添加 {@link DomainName} 标记对应的 {@code domainName}即可支持动态改变，
     *
     * @param domainName 域名别名
     * @param domainUrl  域名对应的 BaseUrl
     */
    public void putDomain(@NonNull String domainName, @NonNull HttpUrl domainUrl) {
        mUrlMap.put(domainName, domainUrl);
        if (!isSetDynamicDomain) {
            isDynamicDomain = true;
        }
    }

    /**
     * 移除操作
     *
     * @param domainName
     */
    public void removeDomain(@NonNull String domainName) {
        mUrlMap.remove(domainName);
    }

    /**
     * 通过 domainName 获取 {@link HttpUrl}
     *
     * @param domainName
     * @return
     */
    @Nullable
    public HttpUrl getDomainUrl(@NonNull String domainName) {
        return mUrlMap.get(domainName);
    }

    /**
     * 是否支持 配置多个 BaseUrl，且支持动态改变
     *
     * @return
     */
    public boolean isDynamicDomain() {
        return isDynamicDomain;
    }

    /**
     * 设置是否支持 配置多个 BaseUrl，且支持动态改变，一般会通过其他途径自动开启，此方法一般不会用到，只有在特殊场景下可能会有此需求，所以提供此方法主要用于提供更多种可能。
     * 特殊场景：使用了多个可切换 BaseUrl 支持功能，但突然想终止切换BaseUrl，全部使用默认的 BaseUrl 时，可通过此方法禁用对多个BaseUrl的支持。
     *
     * @param dynamicDomain
     */
    public void setDynamicDomain(boolean dynamicDomain) {
        isDynamicDomain = dynamicDomain;
        isSetDynamicDomain = true;
    }

    /**
     * 是否支持 动态配置超时时长
     *
     * @return
     */
    public boolean isDynamicTimeout() {
        return isDynamicTimeout;
    }

    /**
     * 设置是否支持 动态配置超时时长，可以通过此方法禁用所有自定义的超时时间配置
     *
     * @return 默认为{@code false}
     */
    public void setDynamicTimeout(boolean dynamicTimeout) {
        isDynamicTimeout = dynamicTimeout;
    }

    /**
     * 设置URL路径片段偏移量
     * <p>
     * 示例：当 baseUrl为：http://host/a/b/c/  domainUrl为：http://domain/时，按照路径片段偏移量转换后的URL结果如下：
     * <p>
     * pathSegmentOffsets: 0  http://host/a/b/c/  ->  http://domain/a/b/c/
     * pathSegmentOffsets: 1  http://host/a/b/c/  ->  http://domain/b/c/
     * pathSegmentOffsets: 2  http://host/a/b/c/  ->  http://domain/c/
     * pathSegmentOffsets: 3  http://host/a/b/c/  ->  http://domain/
     *
     * @param pathSegmentOffsets
     */
    public void setPathSegmentOffsets(int pathSegmentOffsets) {
        this.mPathSegmentOffsets = pathSegmentOffsets;
    }

    /**
     * s
     * 清空支持多个 BaseUrl
     */
    public void clearDomain() {
        mUrlMap.clear();
    }

    /**
     * 取切换之后的 BaseUrl，如果没有则返回 {@link #mBaseUrl}，如果 {@link #mBaseUrl}也为空，则不切换，直接返回空
     *
     * @param domainName
     * @param originUrl
     * @return
     */
    public synchronized HttpUrl obtainParserDomainUrl(@NonNull String domainName, @NonNull HttpUrl originUrl) {
        HttpUrl domainUrl = getDomainUrl(domainName);
        if (domainUrl != null) {
            return parseHttpUrl(domainUrl, originUrl);
        }
        // 如果 mBaseUrl 不为空则，切换成 mBaseUrl
        if (mBaseUrl != null) {
            return parseHttpUrl(mBaseUrl, originUrl);
        }
        return null;
    }

    /**
     * 解析 httpUrl, 将 httpUrl 的 baseUrl 换成 domainUrl
     *
     * @param domainUrl
     * @param httpUrl
     * @return
     */
    public HttpUrl parseHttpUrl(@NonNull HttpUrl domainUrl, @NonNull HttpUrl httpUrl) {
        return mHttpUrlParser.parseHttpUrl(domainUrl, httpUrl, mPathSegmentOffsets);
    }

    /**
     * 设置 HttpUrl解析器 , 当前默认采用的 {@link DomainParser} 实现类，你也可以自定义实现 {@link HttpUrlParser}
     *
     * @param httpUrlParser
     */
    public void setHttpUrlParser(@NonNull HttpUrlParser httpUrlParser) {
        this.mHttpUrlParser = httpUrlParser;
    }

    /**
     * 是否添加头
     *
     * @return
     */
    public boolean isAddHeader() {
        return isAddHeader;
    }

    /**
     * 设置是否添加头，一般会通过{@link #addHeader(String, String)}相关方法自动开启，此方法一般不会主动用到，只有特殊场景下会有此需求，提供统一控制。
     *
     * @param addHeader
     */
    public void setAddHeader(boolean addHeader) {
        isAddHeader = addHeader;
        isSetAddHeader = true;
    }

    /**
     * 获取头，只能获取通过{@link #RetrofitHelper()}添加的头
     *
     * @return
     */
    @NonNull
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    /**
     * 设置头，主要用于设置公共头消息。
     *
     * @param headers
     */
    public void setHeaders(@NonNull Map<String, String> headers) {
        this.mHeaders = headers;
        if (!isSetAddHeader) {
            isAddHeader = true;
        }
    }

    /**
     * 添加头，主要用于添加公共头消息。
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        mHeaders.put(name, value);
        if (!isSetAddHeader) {
            isAddHeader = true;
        }
    }

    /**
     * 添加头，主要用于添加公共头消息。
     *
     * @param headers
     */
    public void addHeaders(Map<String, String> headers) {
        if (headers != null) {
            mHeaders.putAll(headers);
            if (!isSetAddHeader) {
                isAddHeader = true;
            }
        }
    }

    /**
     * 清空头
     */
    public void clearHeaders() {
        mHeaders.clear();
    }

    /**
     * 设置进度更新间隔时间（单位：毫秒）；默认为：300毫秒，配置建议最小不要小于100毫秒
     *
     * @param progressUpdateIntervalTime
     */
    public void setProgressUpdateIntervalTime(long progressUpdateIntervalTime) {
        this.mProgressUpdateIntervalTime = progressUpdateIntervalTime;
    }

    /**
     * 添加请求监听
     *
     * @param key      key默认为请求的url，也可以通过 {@link RequestProgress} 来自定义配置接口对应的key；自定义配置的key优先级高于默认的url
     * @param listener 进度监听器
     */
    public synchronized void addRequestListener(@NonNull String key, @NonNull ProgressListener listener) {
        List<ProgressListener> list = mRequestProgressListeners.get(key);
        if (list == null) {
            list = new LinkedList<>();
            mRequestProgressListeners.put(key, list);
        }
        if (!list.contains(listener)) {
            list.add(listener);
        }
    }

    /**
     * 添加响应监听
     *
     * @param key      key默认为请求的url，也可以通过 {@link ResponseProgress} 来自定义配置接口对应的key；自定义配置的key优先级高于默认的url
     * @param listener 进度监听器
     */
    public synchronized void addResponseListener(@NonNull String key, @NonNull ProgressListener listener) {
        List<ProgressListener> list = mResponseProgressListeners.get(key);
        if (list == null) {
            list = new LinkedList<>();
            mResponseProgressListeners.put(key, list);
        }
        if (!list.contains(listener)) {
            list.add(listener);
        }
    }

    /**
     * 移除请求监听
     *
     * @param key
     */
    public void removeRequestListener(@NonNull String key) {
        mRequestProgressListeners.remove(key);
    }

    /**
     * 移除响应监听
     *
     * @param key
     */
    public void removeResponseListener(@NonNull String key) {
        mResponseProgressListeners.remove(key);
    }

    /**
     * 清空请求监听
     */
    public void clearRequestListener() {
        mRequestProgressListeners.clear();
    }

    /**
     * 清空响应监听
     */
    public void clearResponseListener() {
        mResponseProgressListeners.clear();
    }

    /**
     * 清空监听
     */
    public void clearListener() {
        clearRequestListener();
        clearResponseListener();
    }

    /**
     * 包装进度请求，支持监听上传进度
     *
     * @param request
     * @return
     */
    public Request wrapProgressRequest(Request request) {
        if (request.body() == null) {
            return request;
        }
        List<ProgressListener> requestProgressListeners = null;
        Invocation invocation = request.tag(Invocation.class);
        if (invocation != null) {
            RequestProgress requestProgress = invocation.method().getAnnotation(RequestProgress.class);
            if (requestProgress != null && !TextUtils.isEmpty(requestProgress.value())) {
                requestProgressListeners = mRequestProgressListeners.get(requestProgress.value());
            }
        }
        if (requestProgressListeners == null) {
            String url = request.url().toString();
            requestProgressListeners = mRequestProgressListeners.get(url);
        }
        if (requestProgressListeners != null) {
            return request.newBuilder()
                    .method(request.method(), new ProgressRequestBody(
                            request.body(),
                            requestProgressListeners,
                            mProgressUpdateIntervalTime,
                            mHandler
                    )).build();
        }

        return request;
    }

    /**
     * 包装进度响应，支持监听下载进度
     *
     * @param response
     * @return
     */
    public Response wrapProgressResponse(Response response) {
        if (response.body() == null) {
            return response;
        }
        List<ProgressListener> responseProgressListeners = null;
        Invocation invocation = response.request().tag(Invocation.class);
        if (invocation != null) {
            ResponseProgress responseProgress = invocation.method().getAnnotation(ResponseProgress.class);
            if (responseProgress != null && !TextUtils.isEmpty(responseProgress.value())) {
                responseProgressListeners = mResponseProgressListeners.get(responseProgress.value());
            }
        }
        if (responseProgressListeners == null) {
            String url = response.request().url().toString();
            responseProgressListeners = mRequestProgressListeners.get(url);
        }
        if (responseProgressListeners != null) {
            return response.newBuilder()
                    .body(new ProgressResponseBody(
                            response.body(),
                            responseProgressListeners,
                            mProgressUpdateIntervalTime,
                            mHandler
                    )).build();
        }

        return response;
    }
}
