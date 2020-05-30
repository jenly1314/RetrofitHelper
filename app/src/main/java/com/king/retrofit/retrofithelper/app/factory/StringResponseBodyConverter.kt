package com.king.retrofit.retrofithelper.app.factory

import okhttp3.ResponseBody
import retrofit2.Converter

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class StringResponseBodyConverter() : Converter<ResponseBody,String>{

    override fun convert(value: ResponseBody): String? {
        value.use {
            return it.string()
        }
    }

}