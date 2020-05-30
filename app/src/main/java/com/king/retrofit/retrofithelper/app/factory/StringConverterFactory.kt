package com.king.retrofit.retrofithelper.app.factory

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class StringConverterFactory internal constructor() : Converter.Factory() {



    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<String, RequestBody> {
        return StringRequestBodyConverter()
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, String> {
        return StringResponseBodyConverter()
    }

}
