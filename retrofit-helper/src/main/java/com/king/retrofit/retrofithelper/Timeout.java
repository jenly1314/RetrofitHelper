package com.king.retrofit.retrofithelper;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标记超时时长，用于支持动态改变超时时长
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Timeout {

    TimeUnit timeUnit() default TimeUnit.SECONDS;
    /**
     * 连接超时 时长（默认单位：秒）
     * @return
     */
    int connectTimeout() default 10;
    /**
     * 读取超时 时长（默认单位：秒）
     * @return
     */
    int readTimeout() default  10;
    /**
     * 写入超时 时长（默认单位：秒）
     * @return
     */
    int writeTimeout() default 10;


}
