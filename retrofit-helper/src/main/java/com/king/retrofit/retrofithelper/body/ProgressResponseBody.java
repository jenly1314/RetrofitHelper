package com.king.retrofit.retrofithelper.body;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.king.retrofit.retrofithelper.listener.ProgressListener;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class ProgressResponseBody extends ResponseBody {

    private ResponseBody responseBody;

    private List<ProgressListener> progressListeners;

    private long updateIntervalTime;

    private Handler handler;

    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, List<ProgressListener> progressListeners, long updateIntervalTime, Handler handler) {
        this.responseBody = responseBody;
        this.progressListeners = progressListeners;
        this.updateIntervalTime = updateIntervalTime;
        this.handler = handler;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            private long contentLength;
            private long totalBytesRead;
            private long lastUpdateTime;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                try {
                    if(contentLength <= 0){
                        contentLength = contentLength();
                    }
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    if (progressListeners != null) {
                        long currentTime = SystemClock.elapsedRealtime();
                        if (currentTime - lastUpdateTime > updateIntervalTime || bytesRead == -1) {
                            runMainThread(() -> {
                                for (ProgressListener progressListener : progressListeners) {
                                    progressListener.onProgress(totalBytesRead, contentLength, bytesRead == -1);
                                }
                            });
                            lastUpdateTime = currentTime;
                        }
                    }
                    return bytesRead;
                } catch (IOException e) {
                    if(progressListeners != null){
                        runMainThread(() -> {
                            for (ProgressListener progressListener : progressListeners) {
                                progressListener.onException(e);
                            }
                        });
                    }
                    throw new IOException(e);
                }
            }
        };
    }

    /**
     * 在主线程中执行
     * @param runnable
     */
    private void runMainThread(Runnable runnable){
        handler.post(runnable);
    }

}
