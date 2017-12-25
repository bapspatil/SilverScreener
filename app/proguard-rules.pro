# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\bapoo\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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

# For Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# For Realm
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *
-dontwarn javax.**
-dontwarn io.realm.**

# For Retrofit
-dontwarn okio.**
-dontwarn javax.annotation.**

-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }

-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }

-keep class javax.inject.** { *; }
-keep class javax.xml.stream.** { *; }

-keep class retrofit.** { *; }

-keep class com.google.appengine.** { *; }

-keepattributes *Annotation*
-keepattributes Signature

-dontwarn com.squareup.okhttp.*
-dontwarn rx.**

-dontwarn javax.xml.stream.**
-dontwarn com.google.appengine.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.**