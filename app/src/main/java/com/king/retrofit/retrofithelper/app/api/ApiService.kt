package com.king.retrofit.retrofithelper.app.api

import com.king.retrofit.retrofithelper.annotation.*
import com.king.retrofit.retrofithelper.app.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
interface ApiService {

    /**
     * 原始请求
     * @return
     */
    @GET("index.html")
    fun getRequest1(): Call<String>

    /**
     * BaseUrl切换成 GitHub Url
     * @return
     */
    @DomainName(Constants.DOMAIN_GITHUB) // 域名别名标识，用于支持切换对应的 BaseUrl
    @GET("index.html")
    fun getRequest2(): Call<String>

    /**
     * 自定义超时
     * BaseUrl切换成 Google Url
     * @return
     */
    @DomainName(Constants.DOMAIN_GOOGLE) // 域名别名标识，用于支持切换对应的 BaseUrl
    @Timeout(connectTimeout = 15,readTimeout = 15,writeTimeout = 15) // 超时标识，用于自定义超时时长
    @GET("index.html")
    fun getRequest3(): Call<String>

    /**
     * 动态改变 BaseUrl
     * @return
     */
    @BaseUrl(Constants.BAIDU_BASE_URL) // BaseUrl标识，用于支持指定 BaseUrl
    @GET("index.html")
    fun getRequest4(): Call<String>


    /**
     * 动态改变 BaseUrl
     * @return
     */
    @ResponseProgress(Constants.RESPONSE_PROGRESS_1) // 支持响应进度监听，自定义配置监听的key
    @DomainName(Constants.DOMAIN_DYNAMIC) // 域名别名标识，用于支持切换对应的 BaseUrl
    @GET("index.html")
    fun getRequest5(): Call<String>

    /**
     * 下载；可通过 RetrofitHelper.getInstance().addResponseListener(key, listener) 监听下载进度
     */
    @ResponseProgress(Constants.RESPONSE_PROGRESS_2) // 支持响应进度监听，自定义配置监听的key
    @Streaming
    @GET
    fun download(@Url url: String): Call<ResponseBody>

}