# RetrofitHelper

![Image](app/src/main/ic_launcher-playstore.png)

[![Download](https://img.shields.io/badge/download-App-blue.svg)](https://raw.githubusercontent.com/jenly1314/RetrofitHelper/master/app/release/app-release.apk)
[![JCenter](https://img.shields.io/badge/JCenter-1.0.0-46C018.svg)](https://bintray.com/beta/#/jenly/maven/retrofit-helper)
[![JitPack](https://jitpack.io/v/jenly1314/RetrofitHelper.svg)](https://jitpack.io/#jenly1314/RetrofitHelper)
[![CI](https://travis-ci.org/jenly1314/RetrofitHelper.svg?branch=master)](https://travis-ci.org/jenly1314/RetrofitHelper)
[![CircleCI](https://circleci.com/gh/jenly1314/RetrofitHelper.svg?style=svg)](https://circleci.com/gh/jenly1314/RetrofitHelper)
[![API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/mit-license.php)
[![Blog](https://img.shields.io/badge/blog-Jenly-9933CC.svg)](https://jenly1314.github.io/)
[![QQGroup](https://img.shields.io/badge/QQGroup-20867961-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=8fcc6a2f88552ea44b1411582c94fd124f7bb3ec227e2a400dbbfaad3dc2f5ad)

RetrofitHelper for Android 是一个为 Retrofit 提供便捷配置多个BaseUrl相关的扩展帮助类。

## 主要功能介绍
- [x] 支持配置多个BaseUrl
- [x] 支持动态改变BaseUrl
- [x] 支持动态配置超时时长
- [x] 支持添加公共请求头

## Gif 展示
![Image](GIF.gif)


## 引入

> 由于2021年2月3日 **JFrog宣布将关闭Bintray和JCenter，计划在2022年2月完全关闭。** 所以后续版本不再发布至 **JCenter**

1. 在Project的 **build.gradle** 里面添加远程仓库  
          
```gradle
allprojects {
    repositories {
        //...
        maven { url 'https://jitpack.io' }
    }
}
```

2. 在Module的 **build.gradle** 里面添加引入依赖项
```gradle
//AndroidX 版本
implementation 'com.github.jenly1314:RetrofitHelper:1.0.1'

```

### RetrofitHelper引入的库（具体对应版本请查看 [Versions](versions.gradle)）
```gradle
    compileOnly "androidx.appcompat:appcompat:$versions.appcompat"
    compileOnly "com.squareup.retrofit2:retrofit:$versions.retrofit"
```

> 因为 **RetrofitHelper** 的依赖只在编译时有效，并未打入包中，所以您的项目中必须依赖上面列出相关库

## 示例

主要集成步骤代码示例

Step.1 需使用JDK8编译，在你项目中的build.gradle的android{}中添加配置：
```gradle
compileOptions {
    targetCompatibility JavaVersion.VERSION_1_8
    sourceCompatibility JavaVersion.VERSION_1_8
}

```

Step.2 通过RetrofitUrlManager初始化OkHttpClient，进行初始化配置
```kotlin
//通过RetrofitHelper创建一个支持多个BaseUrl的 OkHttpClient
//方式一
val okHttpClient = RetrofitHelper.getInstance()
            .createClientBuilder()
            //...你自己的其他配置
            .build()
//方式二
val okHttpClient = RetrofitHelper.getInstance()
            .with(builder)
            //...你自己的其他配置
            .build()

```

```kotlin
//完整示例
val okHttpClient = RetrofitHelper.getInstance()
            .createClientBuilder()
            .addInterceptor(LogInterceptor())
            .build()
val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson())) 
            .build()
```

Step.3 定义接口时，通过注解标记对应接口，支持动态改变 BaseUrl相关功能
```kotlin
 interface ApiService {
    
     /**
      * 接口示例，没添加任何标识，和常规使用一致
      * @return
      */
     @GET("api/user")
     fun getUser(): Call<User>


     /**
      * Retrofit默认返回接口定义示例，添加 DomainName 标示 和 Timeout 标示
      * @return
      */
     @DomainName(domainName) // domainName 域名别名标识，用于支持切换对应的 BaseUrl
     @Timeout(connectTimeout = 15,readTimeout = 15,writeTimeout = 15,timeUnit = TimeUnit.SECONDS) //超时标识，用于自定义超时时长
     @GET("api/user")
     fun getUser(): Call<User>

     /**
      * 动态改变 BaseUrl
      * @return
      */
     @BaseUrl(baseUrl) //baseUrl 标识，用于支持指定 BaseUrl
     @GET("api/user")
     fun getUser(): Call<User>


     //--------------------------------------

     /**
      * 使用RxJava返回接口定义示例，添加 DomainName 标示 和 Timeout 标示
      * @return
      */
     @DomainName(domainName) // domainName 域名别名标识，用于支持切换对应的 BaseUrl
     @Timeout(connectTimeout = 20,readTimeout = 10) //超时标识，用于自定义超时时长
     @GET("api/user")
     fun getUser(): Observable<User>

 }

```

Step.4 添加多个 BaseUrl 支持
```kotlin
        //添加多个 BaseUrl 支持 ，domainName为域名别名标识，domainUrl为域名对应的 BaseUrl，与上面的接口定义表示一致即可生效
        RetrofitHelper.getInstance().putDomain(domainName,domainUrl)
```
```kotlin
        //添加多个 BaseUrl 支持 示例
        RetrofitHelper.getInstance().apply {
            //GitHub baseUrl
            putDomain(Constants.DOMAIN_GITHUB,Constants.GITHUB_BASE_URL)
            //Google baseUrl
            putDomain(Constants.DOMAIN_GOOGLE,Constants.GOOGLE_BASE_URL)
        }
```

RetrofitHelper
```java
/**
 * Retrofit帮助类
 * <p>
 * 主要功能介绍：
 *      1.支持管理多个 BaseUrl，且支持运行时动态改变
 *      2.支持接口自定义超时时长，满足每个接口动态定义超时时长
 *      3.支持添加公共请求头
 * <p>
 *
 * RetrofitHelper中的核心方法
 *
 * {@link #createClientBuilder()} 创建 {@link OkHttpClient.Builder}初始化一些配置参数，用于支持多个 BaseUrl
 *
 * {@link #with(OkHttpClient.Builder)} 传入 {@link OkHttpClient.Builder} 配置一些参数，用于支持多个 BaseUrl
 *
 * {@link #setBaseUrl(String)} 和 {@link #setBaseUrl(HttpUrl)} 主要用于设置默认的 BaseUrl。
 *
 * {@link #putDomain(String, String)} 和 {@link #putDomain(String, HttpUrl)} 主要用于支持多个 BaseUrl，且支持 BaseUrl 动态改变。
 *
 * {@link #setDynamicDomain(boolean)} 设置是否支持 配置多个BaseUrl，且支持动态改变，一般会通过其他途径自动开启，此方法一般不会主动用到，只有在特殊场景下可能会有此需求，所以提供此方法主要用于提供更多种可能。
 *
 * {@link #setHttpUrlParser(HttpUrlParser)} 设置 HttpUrl解析器 , 当前默认采用的 {@link DomainParser} 实现类，你也可以自定义实现 {@link HttpUrlParser}
 *
 * {@link #setAddHeader(boolean)} 设置是否添加头，一般会通过{@link #addHeader(String, String)}相关方法自动开启，此方法一般不会主动用到，只有特殊场景下会有此需求，主要用于提供统一控制。
 *
 * {@link #addHeader(String, String)} 设置头，主要用于添加公共头消息。
 *
 * {@link #addHeaders(Map)} 设置头，主要用于设置公共头消息。
 *
 * 这里只是列出一些对外使用的核心方法，和相关的简单说明。如果想了解更多，可以查看对应的方法和详情。
 *
 * <p>
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public final class RetrofitHelper{
    //...
}

```

特别说明
```kotlin
        //通过setBaseUrl可以动态改变全局的 BaseUrl，优先级比putDomain(domainName,domainUrl)低，谨慎使用
        RetrofitHelper.getInstance().setBaseUrl(dynamicUrl)
```

更多使用详情，请查看[Demo](app)中的源码使用示例或直接查看[API帮助文档](https://jitpack.io/com/github/jenly1314/RetrofitHelper/latest/javadoc/)


## 版本记录

#### v1.0.1：2021-4-23
*  新增支持添加公共请求头
*  新增 **@BaseUrl** 注解

#### v1.0.0：2020-5-30
*  RetrofitHelper初始版本

## 赞赏
如果你喜欢RetrofitHelper，或感觉RetrofitHelper帮助到了你，可以点右上角“Star”支持一下，你的支持就是我的动力，谢谢 :smiley:<p>
你也可以扫描下面的二维码，请作者喝杯咖啡 :coffee:
    <div>
        <img src="https://jenly1314.github.io/image/pay/wxpay.png" width="280" heght="350">
        <img src="https://jenly1314.github.io/image/pay/alipay.png" width="280" heght="350">
        <img src="https://jenly1314.github.io/image/pay/qqpay.png" width="280" heght="350">
        <img src="https://jenly1314.github.io/image/alipay_red_envelopes.jpg" width="233" heght="350">
    </div>

## 关于我
   Name: <a title="关于作者" href="https://about.me/jenly1314" target="_blank">Jenly</a>

   Email: <a title="欢迎邮件与我交流" href="mailto:jenly1314@gmail.com" target="_blank">jenly1314#gmail.com</a> / <a title="给我发邮件" href="mailto:jenly1314@vip.qq.com" target="_blank">jenly1314#vip.qq.com</a>

   CSDN: <a title="CSDN博客" href="http://blog.csdn.net/jenly121" target="_blank">jenly121</a>

   CNBlogs: <a title="博客园" href="https://www.cnblogs.com/jenly" target="_blank">jenly</a>

   GitHub: <a title="GitHub开源项目" href="https://github.com/jenly1314" target="_blank">jenly1314</a>
   
   Gitee: <a title="Gitee开源项目" href="https://gitee.com/jenly1314" target="_blank">jenly1314</a>

   加入QQ群: <a title="点击加入QQ群" href="http://shang.qq.com/wpa/qunwpa?idkey=8fcc6a2f88552ea44b1411582c94fd124f7bb3ec227e2a400dbbfaad3dc2f5ad" target="_blank">20867961</a>
   <div>
       <img src="https://jenly1314.github.io/image/jenly666.png">
       <img src="https://jenly1314.github.io/image/qqgourp.png">
   </div>


   
