package com.king.retrofit.retrofithelper.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BaseUrl: 用于支持动态改变并指定 BaseUrl。优先级高于{@link DomainName}
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseUrl {
    String value();
}
