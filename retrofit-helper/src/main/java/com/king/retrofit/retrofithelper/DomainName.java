package com.king.retrofit.retrofithelper;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标记域名别名，用于支持动态改变 BaseUrl
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface DomainName {
    String value();
}
