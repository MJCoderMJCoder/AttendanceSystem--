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

##---------------Begin: proguard configuration for MJCoder  ----------
#保持 native 方法不被混淆：JNI方法不可混淆，因为这个方法需要和native方法保持一致；
#JNI 调用 Java 方法，需要通过类名和方法名构成的地址形成。
#Java 使用 Native 方法，Native 是C/C++编写的，方法是无法一同混淆的。
-keepclasseswithmembernames class * {
   native <methods>;
}
-keep class com.arcsoft.face.** {*;}
##---------------End: proguard configuration for MJCoder  ----------

##---------------Begin: proguard configuration for greenDAO 3  ----------
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties {*;}

# If you do not use SQLCipher:
-dontwarn net.sqlcipher.database.**
# If you do not use RxJava:
-dontwarn rx.**
##---------------End: proguard configuration for greenDAO 3  ----------

##---------------Begin: proguard configuration for Glide  ----------
#ProGuard：Depending on your ProGuard (DexGuard) config and usage, you may need to include the following lines in your proguard.cfg (see the Download and Setup docs page for more details):
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
##---------------End: proguard configuration for Glide  ----------


##---------------Begin: proguard configuration for Amap  ----------
# 3D 地图 V5.0.0之前：
    -keep   class com.amap.api.maps.**{*;}
    -keep   class com.autonavi.amap.mapcore.*{*;}
    -keep   class com.amap.api.trace.**{*;}

# 3D 地图 V5.0.0之后：
    -keep   class com.amap.api.maps.**{*;}
    -keep   class com.autonavi.**{*;}
    -keep   class com.amap.api.trace.**{*;}

# 定位
    -keep class com.amap.api.location.**{*;}
    -keep class com.amap.api.fence.**{*;}
    -keep class com.loc.**{*;}
    -keep class com.autonavi.aps.amapapi.model.**{*;}

# 搜索
    -keep   class com.amap.api.services.**{*;}

# 2D地图
    -keep class com.amap.api.maps2d.**{*;}
    -keep class com.amap.api.mapcore2d.**{*;}

# 导航
    -keep class com.amap.api.navi.**{*;}
    -keep class com.autonavi.**{*;}
##---------------End: proguard configuration for Amap  ----------