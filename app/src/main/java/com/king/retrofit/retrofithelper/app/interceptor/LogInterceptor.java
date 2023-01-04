package com.king.retrofit.retrofithelper.app.interceptor;

import android.util.Log;

import com.king.retrofit.retrofithelper.app.Constants;

import java.io.EOFException;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Invocation;
import retrofit2.http.Streaming;

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

        RequestBody requestBody = request.body();
        if(requestBody != null){
            final Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            if(isPlaintext(buffer)){
                Log.d(Constants.TAG,"RequestBody:" + buffer.readUtf8());
            }else{
                Log.d(Constants.TAG, "RequestBody:(binary " + requestBody.contentLength() + "-byte body omitted)");
            }

        }

        Invocation invocation = request.tag(Invocation.class);
        if(invocation != null){
            Streaming streaming = invocation.method().getAnnotation(Streaming.class);
            if(streaming != null){
                Log.d(Constants.TAG, "Streaming...");
                return chain.proceed(chain.request());
            }
        }
        Response response = chain.proceed(chain.request());
        MediaType mediaType = response.body().contentType();
        String responseBody = response.body().string();
        Log.d(Constants.TAG,"ResponseBody:" + responseBody);

        return response.newBuilder()
                .body(ResponseBody.create(mediaType, responseBody))
                .build();
    }

    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            Log.w(Constants.TAG, e);
            return false;
        }
    }
}
