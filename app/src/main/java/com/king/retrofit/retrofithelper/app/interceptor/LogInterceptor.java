package com.king.retrofit.retrofithelper.app.interceptor;

import android.util.Log;

import com.king.retrofit.retrofithelper.app.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class LogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Log.i(Constants.TAG,String.format("%1$s -> %2$s",request.method(),request.url()));

        if(request.headers() != null){
            Log.d(Constants.TAG,"Headers:" + request.headers());
        }

        if(request.body() != null){
            Log.d(Constants.TAG,"RequestBody:" + bodyToString(request.body()));
        }

        Response response = chain.proceed(request);
        MediaType mediaType = response.body().contentType();
        String responseBody = response.body().string();
        Log.d(Constants.TAG,"ResponseBody:" + responseBody);

        return response.newBuilder()
                .body(ResponseBody.create(mediaType, responseBody))
                .build();
    }

    private String bodyToString(final RequestBody request) {
        if(request != null){
            try {
                final RequestBody copy = request;
                final Buffer buffer = new Buffer();
                copy.writeTo(buffer);
                return buffer.readUtf8();
            } catch (final IOException e) {
                Log.w(Constants.TAG,"Did not work.");
                e.printStackTrace();
            }
        }
        return null;
    }
}
