package com.king.retrofit.retrofithelper.interceptor;


import com.king.retrofit.retrofithelper.RetrofitHelper;
import com.king.retrofit.retrofithelper.Timeout;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Invocation;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class TimeoutInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        //如果支持动态配置超时时长
        if(RetrofitHelper.getInstance().isDynamicTimeout()){
            Request request = chain.request();
            Invocation invocation = request.tag(Invocation.class);
            if(invocation != null){
                Timeout timeout = invocation.method().getAnnotation(Timeout.class);
                if(timeout != null){
                    return chain.withConnectTimeout(timeout.connectTimeout(),timeout.timeUnit())
                                .withReadTimeout(timeout.readTimeout(),timeout.timeUnit())
                                .withWriteTimeout(timeout.writeTimeout(),timeout.timeUnit())
                                .proceed(request);

                }
            }
        }

        return chain.proceed(chain.request());
    }

}
