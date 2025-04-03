package com.module.common.network

import android.webkit.WebSettings
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.Utils
import com.module.common.util.CacheUtil
import com.module.common.app.appContext
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class MyHeadInterceptor : Interceptor {


    val packName by lazy {
        AppUtils.getAppPackageName()
    }
    val appName by lazy {
        AppUtils.getAppName()
    }

    val languageStr by lazy {
        CacheUtil.getSettingLanguage()
    }
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        return chain.proceed(builder.build())
    }

}