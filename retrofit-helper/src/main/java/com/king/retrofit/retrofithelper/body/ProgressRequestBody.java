package com.king.retrofit.retrofithelper.body;

import android.os.Handler;
import android.os.SystemClock;

import com.king.retrofit.retrofithelper.listener.ProgressListener;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class ProgressRequestBody extends RequestBody {

    private RequestBody requestBody;

    private List<ProgressListener> progressListeners;

    private long updateIntervalTime;

    private Handler handler;

    private BufferedSink bufferedSink;

    public ProgressRequestBody(RequestBody requestBody, List<ProgressListener> progressListeners, long updateIntervalTime, Handler handler) {
        this.requestBody = requestBody;
        this.progressListeners = progressListeners;
        this.updateIntervalTime = updateIntervalTime;
        this.handler = handler;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        try {
            if (bufferedSink == null) {
                bufferedSink = Okio.buffer(new ProgressSink(sink, contentLength()));
            }
            requestBody.writeTo(bufferedSink);
            bufferedSink.flush();
        } catch (IOException e) {
            if (progressListeners != null) {
                runMainThread(() -> {
                    for (ProgressListener progressListener : progressListeners) {
                        progressListener.onException(e);
                    }
                });
            }
            throw new IOException(e);
        }
    }

    private final class ProgressSink extends ForwardingSink {

        private long contentLength;
        private long totalBytesReceived;
        private long lastUpdateTime;

        public ProgressSink(Sink delegate, long contentLength) {
            super(delegate);
            this.contentLength = contentLength;
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            this.totalBytesReceived += byteCount;
            if (progressListeners != null) {
                long currentTime = SystemClock.elapsedRealtime();
                if (currentTime - lastUpdateTime > updateIntervalTime || totalBytesReceived == contentLength) {
                    runMainThread(() -> {
                        for (ProgressListener progressListener : progressListeners) {
                            progressListener.onProgress(totalBytesReceived, contentLength, totalBytesReceived == contentLength);
                        }
                    });
                    lastUpdateTime = currentTime;
                }
            }
        }
    }

    /**
     * 在主线程中执行
     *
     * @param runnable
     */
    private void runMainThread(Runnable runnable) {
        handler.post(runnable);
    }
}
