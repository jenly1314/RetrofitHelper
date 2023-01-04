package com.king.retrofit.retrofithelper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Timeout: 标记超时时长，用于支持动态改变超时时长
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {

    /**
     * 时间单位
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 连接超时 时长（默认：10秒），时间单位可参见：{@link #timeUnit()}
     *
     * @return
     */
    int connectTimeout() default 10;

    /**
     * 读取超时 时长（默认：10秒），时间单位可参见：{@link #timeUnit()}
     *
     * @return
     */
    int readTimeout() default 10;

    /**
     * 写入超时 时长（默认：10秒），时间单位可参见：{@link #timeUnit()}
     *
     * @return
     */
    int writeTimeout() default 10;

}
