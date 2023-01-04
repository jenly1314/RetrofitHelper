package com.king.retrofit.retrofithelper.interceptor;

import com.king.retrofit.retrofithelper.RetrofitHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 进度拦截器
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class ProgressInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        return RetrofitHelper.getInstance().wrapProgressResponse(
                chain.proceed(RetrofitHelper.getInstance().wrapProgressRequest(chain.request()))
        );
    }

}
