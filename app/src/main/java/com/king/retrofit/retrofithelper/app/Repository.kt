package com.king.retrofit.retrofithelper.app

import com.king.retrofit.retrofithelper.RetrofitHelper
import com.king.retrofit.retrofithelper.app.api.ApiService
import com.king.retrofit.retrofithelper.app.factory.StringConverterFactory
import com.king.retrofit.retrofithelper.app.interceptor.LogInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
object Repository {

    private val mRetrofit by lazy {
        val okHttpClient = RetrofitHelper.getInstance()
            .createClientBuilder()
            .addInterceptor(LogInterceptor())
            .build()
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(StringConverterFactory()) //这里主要是方便用来演示多个BaseUrl
            .build()
    }

    private val mApiService by lazy {
        mRetrofit.create(ApiService::class.java)
    }

    fun getRequest1() = mApiService.getRequest1()

    fun getRequest2() = mApiService.getRequest2()

    fun getRequest3() = mApiService.getRequest3()

    fun getRequest4() = mApiService.getRequest4()


}