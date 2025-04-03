package com.module.common.network

import android.content.Intent
import android.util.Log
import com.module.common.data.moudle.bean.ApiResponse
import com.module.common.util.CacheUtil
import com.google.gson.Gson
import com.module.common.app.appContext
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

/**

 */
class CookieInterceptor : Interceptor {


    @kotlin.jvm.Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val cookieStr = response.header("x-cookie-id")
        if (!cookieStr.isNullOrBlank()){
            CacheUtil.setCookie(cookieStr.toString())
        }

        return response

    }
}