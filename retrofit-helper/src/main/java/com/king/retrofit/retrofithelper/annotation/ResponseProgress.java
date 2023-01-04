package com.king.retrofit.retrofithelper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ResponseProgress: 标记响应进度，在定义的接口上使用 {@link ResponseProgress} 相当于为接口配置了自定义的key，
 * 自定义配置的key优先级高于默认的url；在添加响应进度监听时，只需根据当前配置的key值进行监听，即可监听对应的接口进度。
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseProgress {
    String value() default "";
}
