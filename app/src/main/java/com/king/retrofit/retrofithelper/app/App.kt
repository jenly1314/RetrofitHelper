package com.king.retrofit.retrofithelper.app

import android.app.Application
import com.king.retrofit.retrofithelper.RetrofitHelper

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class App : Application() {


    override fun onCreate() {
        super.onCreate()

        //添加多个 BaseUrl 支持
        RetrofitHelper.getInstance().apply {
            //GitHub baseUrl
            putDomain(Constants.DOMAIN_GITHUB,Constants.GITHUB_BASE_URL)
            //Google baseUrl
            putDomain(Constants.DOMAIN_GOOGLE,Constants.GOOGLE_BASE_URL)
            //Add header
//            addHeader("seq",System.currentTimeMillis().toString())
        }
    }


}