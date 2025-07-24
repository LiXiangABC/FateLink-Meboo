# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#
#-optimizationpasses 5                       # 代码混淆的压缩比例，值介于0-7，默认5
#-verbose                                    # 混淆时记录日志
#-dontoptimize                               # 不优化输入的类文件
#-dontpreverify                              # 关闭预校验(作用于Java平台，Android不需要，去掉可加快混淆)
#-ignorewarnings                             # 忽略警告
#-dontwarn com.squareup.okhttp.**            # 指定类不输出警告信息
#-dontusemixedcaseclassnames                 # 混淆后类型都为小写
#-dontskipnonpubliclibraryclasses            # 不跳过非公共的库的类
#-useuniqueclassmembernames                  # 把混淆类中的方法名也混淆
-allowaccessmodification                    # 优化时允许访问并修改有修饰符的类及类的成员
#-renamesourcefileattribute SourceFile       # 将源码中有意义的类名转换成SourceFile，用于混淆具体崩溃代码
#-keepattributes SourceFile,LineNumberTable  # 保留行号
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod # 避免混淆注解、内部类、泛型、匿名类
-optimizations !code/simplification/cast,!field/ ,!class/merging/   # 指定混淆时采用的算法
#

#融云
-keepattributes Exceptions,InnerClasses

-keepattributes Signature

-keep class io.rong.** {*;}
-keep class cn.rongcloud.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent {*;}
-dontwarn io.rong.push.**
-dontnote com.xiaomi.**
-dontnote com.google.android.gms.gcm.**
-dontnote io.rong.**


-keep public class com.google.firebase.* {*;}
-keep class com.google.android.gms.** { *; }


-keep class com.luck.picture.lib.** { *; }

# 如果引入了Camerax库请添加混淆
-keep class com.luck.lib.camerax.** { *; }

# 如果引入了Ucrop库请添加混淆
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#appsflyer混淆
-keep class com.appsflyer.** { *; }
-keep public class com.android.installreferrer.** { *; }


-keep class xyz.doikki.videoplayer.** { *; }
-dontwarn xyz.doikki.videoplayer.**

# IjkPlayer
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

# ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**


-keep class com.adjust.sdk.**{ *; }
-keep class com.google.android.gms.common.ConnectionResult {
    int SUCCESS;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
    com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
    java.lang.String getId();
    boolean isLimitAdTrackingEnabled();
}
-keep public class com.android.installreferrer.**{ *; }

# Kotlin specific rules
-dontwarn org.jetbrains.**
-keep class kotlin.** { *; }
-keepclasseswithmembers class * {
    @org.jetbrains.annotations.NotNull <methods>;
    @org.jetbrains.annotations.Nullable <methods>;
}


-keepclassmembers class * {
    public <init> (org.json.JSONObject);
}
 -keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet);
 }

 # support-design
 -dontwarn android.support.design.**
 -keep class android.support.design.** { *; }
 -keep interface android.support.design.** { *; }
 -keep public class android.support.design.R$* { *; }

 #base库
 -keep class com.custom.base.entity.** {*;}
# -keep class com.custom.base.base.** {*;}
 -keep public class com.custom.base.mvp.** { *; }
 -keep public class com.custom.base.R$* {
     public static final int *;
 }
 -keep public class * extends com.custom.base.mvp.** { *; }


-keep class com.crush.entity.** {*;}
-keep class com.crush.bean.** {*;}
-keep class com.crush.socket.** {*;}

# 保留所有Bundle类及其方法
-keep public class android.os.Bundle {
    public <init>(...);
    public <methods>;
    public static ** createBundle(...);
    public void put*(...);
    public *** get*(...);
    public *** remove*(...);
    public void setClassLoader(...);
    public void clear();
}

# 保留与String处理相关的常用类和方法
-keep public class java.lang.String {
    public <init>(...);
    public static *** valueOf(...);
    public static *** format(...);
    public static boolean isEmpty(...);
    public *** length();
    public *** charAt(...);
    public *** substring(...);
    public *** equals(...);
    public *** hashCode(...);
    # 其他你认为重要的String方法
}

# 如果你使用了反射来操作Bundle中的String，确保相关的类和方法不被混淆
# 注意：这可能需要更具体的规则，取决于你的代码如何使用反射
-keepclasseswithmembers class * {
    public <init>(android.os.Bundle);
}

# 如果你使用了Gson、Jackson或其他JSON库来处理Bundle中的数据，确保这些库的相关类和方法不被混淆
# 例如，对于Gson：
-keep class com.google.gson.** { *; }
# 或者对于Jackson：
-keep class com.fasterxml.jackson.** { *; }

# 如果你使用了注解来标记需要序列化的字段，确保这些注解及其处理器不被混淆
-keepattributes *Annotation*
-keepclasseswithmembers class * {
    @* <fields>;
    @* <methods>;
}





# 保留Netty所需的所有类和成员
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers public class * {
    public static void main(java.lang.String[]);
}

# 保留所有的MODULE类，以防止R8在Android上删除它们
-keepclassmembers class * {
    java.lang.Module module$(java.lang.String, boolean);
}

# 保留所有Netty的Native类和成员
-keepclassmembers class * extends io.netty.channel.ChannelInboundHandler {
   public void userEventTriggered(java.lang.Object, java.lang.Object);
   public void channelRead(java.lang.Object, java.lang.Object);
   public void channelReadComplete(java.lang.Object);
   public void userEventTriggered(java.lang.Object, java.lang.Object);
   public void channelWritabilityChanged(java.lang.Object);
}

# 保留所有SPI（Service Provider Interface）实现类
-keepclassmembers class * implements java.util.ServiceLoaderProvider {
    private static java.util.ServiceLoaderProvider$Providers access$100(java.lang.Class);
    public static java.lang.Class class$(java.lang.String);
}

# 保留所有的EventLoopGroup类，因为它们通常在应用程序的静态初始化中创建
-keepclassmembers class * {
    public *;
}

# 保留所有的内部类和内部枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留所有的Optional类和它们的方法
-keepclassmembers class * {
    public java.util.Optional*(***);
}

# 保留所有的Logging Framework类和成员
-keepattributes Deprecated, SourceFile, LineNumberTable, *Annotation*
-keep public class ch.qos.logback.**
-keep public class org.slf4j.**
-keep public class org.apache.log4j.**

# 保留所有的Netty的所有类和成员
-keep class io.netty.** { *; }
-dontwarn io.netty.**

#MMKV
-keep class com.tencent.** {*;}
-keep class com.tencent.mmkv.** {*;}



#===============================================================================================
######################################
# MVP架构基础混淆保护（防止ClassCastException）
######################################

# 保留MVP基础类和Presenter，不要被混淆
-keep class com.crush.mvp.** { *; }
-keep class com.crush.ui.start.** { *; }
-keep class * extends com.crush.mvp.BasePresenterImpl { *; }
-keep class * extends com.crush.mvp.MVPBaseActivity { *; }

# 保留构造函数（反射常用）
-keepclassmembers class * {
    public <init>(...);
}

# 保留泛型信息与注解
-keepattributes Signature
-keepattributes *Annotation*

# 如果用到了反射工厂创建Presenter（推荐也保留实现类）
-keep class com.crush.mvp.BasePresenterImpl { *; }
-keep class com.crush.mvp.MVPBaseActivity { *; }

# 保留所有带有特定包名的类和方法（可根据实际情况调整包名）
-keep class com.crush.** { *; }

######################################
# Android通用配置（已混淆就不用再重复）
######################################
# Gson、反射等
-keep class com.google.gson.** { *; }
-keep class sun.misc.Unsafe { *; }
# 保证WebView能用JS接口
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# 友盟、极光、推送、三方库（如有根据需求添加）

# 忽略警告
-dontwarn com.crush.**

# 其它已有规则可保留

# Google Sign-In
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.common.api.ResultCallback { *; }
-keep class com.google.android.gms.tasks.** { *; }


# Firebase Auth
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.android.gms.** { *; }

#===============================================================================================


-obfuscationdictionary bt-proguard.txt
-classobfuscationdictionary bt-proguard.txt
-packageobfuscationdictionary bt-proguard.txt

