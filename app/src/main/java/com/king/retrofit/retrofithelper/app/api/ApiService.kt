package com.king.retrofit.retrofithelper.app.api

import com.king.retrofit.retrofithelper.DomainName
import com.king.retrofit.retrofithelper.Timeout
import com.king.retrofit.retrofithelper.app.Constants
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

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
    @DomainName(Constants.DOMAIN_GITHUB) //域名别名标识，用于支持切换对应的 BaseUrl
    @GET("index.html")
    fun getRequest2(): Call<String>

    /**
     * 自定义超时
     * BaseUrl切换成 Google Url
     * @return
     */
    @DomainName(Constants.DOMAIN_GOOGLE) //域名别名标识，用于支持切换对应的 BaseUrl
    @Timeout(connectTimeout = 15,readTimeout = 15,writeTimeout = 15) //超时标识，用于自定义超时时长
    @GET("index.html")
    fun getRequest3(): Call<String>

    /**
     * 动态改变 BaseUrl
     * @return
     */
    @DomainName(Constants.DOMAIN_DYNAMIC) //域名别名标识，用于支持切换对应的 BaseUrl
    @GET("index.html")
    fun getRequest4(): Call<String>

}