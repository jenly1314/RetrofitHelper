package com.king.retrofit.retrofithelper.listener;

/**
 * 进度监听
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface ProgressListener {

    /**
     * 进度
     *
     * @param currentBytes
     * @param contentLength
     * @param completed
     */
    void onProgress(long currentBytes, long contentLength, boolean completed);

    /**
     * 异常
     *
     * @param e
     */
    void onException(Exception e);
}
