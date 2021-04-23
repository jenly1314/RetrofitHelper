package com.king.retrofit.retrofithelper;

import androidx.annotation.NonNull;

import com.king.retrofit.retrofithelper.annotation.DomainName;
import com.king.retrofit.retrofithelper.interceptor.DomainInterceptor;
import com.king.retrofit.retrofithelper.interceptor.HeaderInterceptor;
import com.king.retrofit.retrofithelper.interceptor.TimeoutInterceptor;
import com.king.retrofit.retrofithelper.parser.DomainParser;
import com.king.retrofit.retrofithelper.parser.HttpUrlParser;

import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * Retrofit帮助类
 * <p>
 * 主要功能介绍：
 *      1.支持管理多个 BaseUrl，且支持运行时动态改变
 *      2.支持接口自定义超时时长，满足每个接口动态定义超时时长
 *      3.支持添加公共请求头
 * <p>
 *
 * RetrofitHelper中的核心方法
 *
 * {@link #createClientBuilder()} 创建 {@link OkHttpClient.Builder}初始化一些配置参数，用于支持多个 BaseUrl
 *
 * {@link #with(OkHttpClient.Builder)} 传入 {@link OkHttpClient.Builder} 配置一些参数，用于支持多个 BaseUrl
 *
 * {@link #setBaseUrl(String)} 和 {@link #setBaseUrl(HttpUrl)} 主要用于设置默认的 BaseUrl。
 *
 * {@link #putDomain(String, String)} 和 {@link #putDomain(String, HttpUrl)} 主要用于支持多个 BaseUrl，且支持 BaseUrl 动态改变。
 *
 * {@link #setDynamicDomain(boolean)} 设置是否支持 配置多个BaseUrl，且支持动态改变，一般会通过其他途径自动开启，此方法一般不会主动用到，只有在特殊场景下可能会有此需求，所以提供此方法主要用于提供更多种可能。
 *
 * {@link #setHttpUrlParser(HttpUrlParser)} 设置 HttpUrl解析器 , 当前默认采用的 {@link DomainParser} 实现类，你也可以自定义实现 {@link HttpUrlParser}
 *
 * {@link #setAddHeader(boolean)} 设置是否添加头，一般会通过{@link #addHeader(String, String)}相关方法自动开启，此方法一般不会主动用到，只有特殊场景下会有此需求，主要用于提供统一控制。
 *
 * {@link #addHeader(String, String)} 设置头，主要用于添加公共头消息。
 *
 * {@link #addHeaders(Map)} 设置头，主要用于设置公共头消息。
 *
 * 这里只是列出一些对外使用的核心方法，和相关的简单说明。如果想了解更多，可以查看对应的方法和详情。
 *
 * <p>
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public final class RetrofitHelper implements HttpUrlParser {

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

    private boolean isSetAddHeader;

    private Map<String,String> mHeaders;

    public static RetrofitHelper getInstance(){
        return DomainHolder.INSTANCE;
    }

    private static class DomainHolder{
        private static final RetrofitHelper INSTANCE = new RetrofitHelper();
    }

    private RetrofitHelper(){
        mUrlMap = new HashMap<>();
        mHttpUrlParser = new DomainParser();
        mHeaders = new HashMap<>();
        isDynamicDomain = false;
        isDynamicTimeout = true;
        isAddHeader = false;
    }

    /**
     * 创建 {@link OkHttpClient.Builder}初始化一些配置参数，用于支持多个 BaseUrl
     * 相关方法 {@link #with(OkHttpClient.Builder)} 同样具备此功能，两种选其一即可
     * @return
     */
    public OkHttpClient.Builder createClientBuilder(){
        return with(new OkHttpClient.Builder());
    }

    /**
     * 传入 {@link OkHttpClient.Builder} 配置一些参数，用于支持多个 BaseUrl
     * 相关方法 {@link #createClientBuilder()} 同样具备此功能，两种选其一即可
     * @param builder
     * @return
     */
    public OkHttpClient.Builder with(@NonNull OkHttpClient.Builder builder){
         return builder.addInterceptor(new DomainInterceptor())
                 .addInterceptor(new TimeoutInterceptor())
                 .addInterceptor(new HeaderInterceptor());
    }

    /**
     * 设置 BaseUrl，前提条件：当 {@link #isDynamicDomain()} 为{@code true} 时才支持动态改变。
     * 通过此方法，可以动态改变 {@link Request#url()}中的 baseUrl，优先级低于{@link DomainName}
     * 只有在接口没有标记{@link DomainName}或者标记了，但没找到对应的 {@code domainUrl} 时，才能动态改变。
     * @param baseUrl
     */
    public void setBaseUrl(@NonNull String baseUrl){
        setBaseUrl(HttpUrl.parse(baseUrl));
    }

    /**
     * 设置 BaseUrl，前提条件：当 {@link #isDynamicDomain()} 为{@code true} 时才支持动态改变。
     * 通过此方法，可以动态改变 {@link Request#url()}中的 baseUrl，优先级低于{@link DomainName}
     * 只有在接口没有标记{@link DomainName}或者标记了，但没找到对应的 {@code domainUrl} 时，才能动态改变。
     * @param baseUrl
     */
    public void setBaseUrl(@NonNull HttpUrl baseUrl){
        this.mBaseUrl = baseUrl;
    }

    /**
     * 获取 BaseUrl
     * @return
     */
    public HttpUrl getBaseUrl(){
        return mBaseUrl;
    }

    /**
     * 移除 BaseUrl
     */
    public void removeBaseUrl(){
        mBaseUrl = null;
    }

    /**
     * 添加动态域名，当执行此操作时，则会自动启用{@link #isDynamicDomain}，并支持多个 BaseUrl
     * 在接口方法上添加 {@link DomainName} 标记对应的 {@code domainName}即可支持动态改变，
     * @param domainName 域名别名
     * @param domainUrl 域名对应的 BaseUrl
     */
    public void putDomain(@NonNull String domainName,@NonNull String domainUrl){
        putDomain(domainName, HttpUrl.parse(domainUrl));
    }

    /**
     * 添加动态域名，当执行此操作时，则会自动启用{@link #isDynamicDomain}，并支持多个 BaseUrl
     * 在接口方法上添加 {@link DomainName} 标记对应的 {@code domainName}即可支持动态改变，
     * @param domainName 域名别名
     * @param domainUrl 域名对应的 BaseUrl
     */
    public void putDomain(@NonNull String domainName,@NonNull HttpUrl domainUrl){
        mUrlMap.put(domainName,domainUrl);
        if(!isSetDynamicDomain){
            isDynamicDomain = true;
        }

    }

    /**
     * 移除操作
     * @param domainName
     */
    public void removeDomain(@NonNull String domainName){
        mUrlMap.remove(domainName);
    }

    /**
     * 通过 domainName 获取 {@link HttpUrl}
     * @param domainName
     * @return
     */
    public HttpUrl get(@NonNull String domainName){
        return mUrlMap.get(domainName);
    }

    /**
     * 是否支持 配置多个 BaseUrl，且支持动态改变
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
     * @return
     */
    public boolean isDynamicTimeout() {
        return isDynamicTimeout;
    }

    /**
     * 设置是否支持 动态配置超时时长，可以通过此方法禁用所有自定义的超时时间配置
     * @return 默认为{@code false}
     */
    public void setDynamicTimeout(boolean dynamicTimeout) {
        isDynamicTimeout = dynamicTimeout;
    }

    /**s
     * 清空支持多个 BaseUrl
     */
    public void clearDomain(){
        mUrlMap.clear();
    }

    /**
     * 取切换之后的 BaseUrl，如果没有则返回 {@link #mBaseUrl}，如果 {@link #mBaseUrl}也为空，则不切换，直接返回空
     * @param domainName
     * @param baseUrl
     * @return
     */
    public synchronized HttpUrl obtainParserDomainUrl(@NonNull String domainName, @NonNull HttpUrl baseUrl){
        HttpUrl domainUrl = get(domainName);
        if(domainUrl != null){
            return parseHttpUrl(domainUrl,baseUrl);
        }
        //如果 mBaseUrl 不为空则，切换成 mBaseUrl
        if(mBaseUrl != null){
            return parseHttpUrl(mBaseUrl,baseUrl);
        }
        return null;
    }

    /**
     * 解析 httpUrl, 将 httpUrl 的 baseUrl 换成 domainUrl
     * @param domainUrl
     * @param httpUrl
     * @return
     */
    @Override
    public HttpUrl parseHttpUrl(@NonNull HttpUrl domainUrl,@NonNull HttpUrl httpUrl){
        return mHttpUrlParser.parseHttpUrl(domainUrl,httpUrl);
    }

    /**
     * 设置 HttpUrl解析器 , 当前默认采用的 {@link DomainParser} 实现类，你也可以自定义实现 {@link HttpUrlParser}
     * @param httpUrlParser
     */
    public void setHttpUrlParser(@NonNull HttpUrlParser httpUrlParser){
        this.mHttpUrlParser = httpUrlParser;
    }

    /**
     * 是否添加头
     * @return
     */
    public boolean isAddHeader() {
        return isAddHeader;
    }

    /**
     * 设置是否添加头，一般会通过{@link #addHeader(String, String)}相关方法自动开启，此方法一般不会主动用到，只有特殊场景下会有此需求，提供统一控制。
     * @param addHeader
     */
    public void setAddHeader(boolean addHeader) {
        isAddHeader = addHeader;
        isSetAddHeader = true;
    }

    /**
     * 获取头，只能获取通过{@link #RetrofitHelper()}添加的头
     * @return
     */
    @NonNull
    public Map<String,String> getHeaders(){
        return mHeaders;
    }

    /**
     * 设置头，主要用于设置公共头消息。
     * @param headers
     */
    public void setHeaders(@NonNull Map<String,String> headers){
        this.mHeaders = headers;
        if(!isSetAddHeader){
            isAddHeader = true;
        }

    }

    /**
     * 添加头，主要用于添加公共头消息。
     * @param name
     * @param value
     */
    public void addHeader(String name,String value){
        mHeaders.put(name,value);
        if(!isSetAddHeader){
            isAddHeader = true;
        }
    }

    /**
     * 添加头，主要用于添加公共头消息。
     * @param headers
     */
    public void addHeaders(Map<String,String> headers){
        if(headers != null){
            mHeaders.putAll(headers);
            if(!isSetAddHeader){
                isAddHeader = true;
            }
        }
    }

    /**
     * 清空头
     */
    public void clearHeaders(){
        mHeaders.clear();
    }
}
