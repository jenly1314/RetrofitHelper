# RetrofitHelper

![Image](app/src/main/ic_launcher-playstore.png)

[![Download](https://img.shields.io/badge/download-App-blue.svg)](https://raw.githubusercontent.com/jenly1314/RetrofitHelper/master/app/release/app-release.apk)
[![JCenter](https://img.shields.io/badge/JCenter-1.0.0-46C018.svg)](https://bintray.com/beta/#/jenly/maven/retrofit-helper)
[![JitPack](https://jitpack.io/v/jenly1314/RetrofitHelper.svg)](https://jitpack.io/#jenly1314/RetrofitHelper)
[![CI](https://travis-ci.org/jenly1314/RetrofitHelper.svg?branch=master)](https://travis-ci.org/jenly1314/RetrofitHelper)
[![CircleCI](https://circleci.com/gh/jenly1314/RetrofitHelper.svg?style=svg)](https://circleci.com/gh/jenly1314/RetrofitHelper)
[![API](https://img.shields.io/badge/API-21%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/mit-license.php)
[![Blog](https://img.shields.io/badge/blog-Jenly-9933CC.svg)](https://jenly1314.github.io/)
[![QQGroup](https://img.shields.io/badge/QQGroup-20867961-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=8fcc6a2f88552ea44b1411582c94fd124f7bb3ec227e2a400dbbfaad3dc2f5ad)

RetrofitHelper for Android 是一个为 Retrofit 提供便捷配置多个BaseUrl相关的扩展帮助类。

## 主要功能介绍
- [x] 支持配置多个BaseUrl
- [x] 支持动态改变BaseUrl
- [x] 支持动态配置超时时长

## Gif 展示
![Image](GIF.gif)


## 引入

### Maven：
```maven
<dependency>
  <groupId>com.king.retrofit</groupId>
  <artifactId>retrofit-helper</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
### Gradle:
```gradle
//AndroidX 版本
implementation 'com.king.retrofit:retrofit-helper:1.0.0'

```
### Lvy:
```lvy
<dependency org='com.king.retrofit' name='retrofit-helper' rev='1.0.0'>
  <artifact name='$AID' ext='pom'></artifact>
</dependency>
```


### RetrofitHelper引入的库（具体对应版本请查看 [Versions](versions.gradle)）
```gradle
    compileOnly "androidx.appcompat:appcompat:$versions.appcompat"
    compileOnly "com.squareup.retrofit2:retrofit:$versions.retrofit"
```
    因为**RetrofitHelper**的依赖只在编译时有效，并未打入包中，所以您的项目中必须依赖上面列出相关库

###### 如果Gradle出现compile失败的情况，可以在Project的build.gradle里面添加如下：（也可以使用上面的GitPack来complie）
```gradle
allprojects {
    repositories {
        maven { url 'https://dl.bintray.com/jenly/maven' }
    }
}
```

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
方式二
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
```kotlin
        //通过setBaseUrl可以动态改变全局的 BaseUrl，优先级比putDomain(domainName,domainUrl)低，谨慎使用
        RetrofitHelper.getInstance().setBaseUrl(dynamicUrl)
```

更多使用详情，请查看[Demo](app)中的源码使用示例或直接查看[API帮助文档](https://jenly1314.github.io/projects/RetrofitHelper/doc/)


## 版本记录

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


   
