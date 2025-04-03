# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

-keep class com.huantansheng.easyphotos.models.** { *; }

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule



# AOP
-adaptclassstrings
-keepattributes InnerClasses, EnclosingMethod, Signature, *Annotation*

-keepnames @org.aspectj.lang.annotation.Aspect class * {
    public <methods>;
}

# OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**


#PictureSelector 2.0
-keep class com.luck.picture.lib.** { *; }

#Ucrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#Okio
-dontwarn org.codehaus.mojo.animal_sniffer.*


#---------------------------------第三方包-------------------------

#okhttp3.x
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.{*;}
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

#retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okio.**

#Rxjava RxAndroid
-dontwarn rx.*
-dontwarn sun.misc.**

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#Gson
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

#Lambda表达式
-keep class java.lang.invoke.** {*;}
-dontwarn java.lang.invoke.**



#eventbus 3.0
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}




-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}


-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider



################retrofit###############
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

################RxPermissions#################
-keep class com.tbruyelle.rxpermissions2.** { *; }
-keep interface com.tbruyelle.rxpermissions2.** { *; }



-dontwarn com.kingja.loadsir.**
-keep class com.kingja.loadsir.** {*;}

-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**

-keep public class * extends com.chad.library.adapter.base.viewholder.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.viewholder.BaseViewHolder {
     <init>(...);
}
-keep class com.chad.library.adapter.base.viewholder.BaseDataBindingHolder {*;}

-keep class com.stx.xhb.xbanner.**{*;}


-keep class com.tencent.imsdk.** { *; }


-keep class org.bitcoinj.crypto.** { *; }

-keep class org.web3j.** { *; }




# Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keep class com.github.aachartmodel.aainfographics.** { *; }
