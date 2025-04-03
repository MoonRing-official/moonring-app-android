package com.module.common.network

import android.content.Intent
import android.util.Log
import com.module.common.data.moudle.bean.ApiResponse
import com.module.common.util.GsonUtils
import com.google.gson.Gson
import com.module.common.app.appContext
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException


class TokenOutInterceptor : Interceptor {

    val gson: Gson by lazy { Gson() }

    @kotlin.jvm.Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return if (response.body() != null && response.body()!!.contentType() != null) {
            val mediaType = response.body()!!.contentType()
            val string = response.body()!!.string()
            val responseBody = ResponseBody.create(mediaType, string)

            val apiResponse =  GsonUtils.json2VO(string,ApiResponse::class.java)

            if (apiResponse!=null&&apiResponse.code != 0) {

                if (!response.request().url().toString().contains("z/v1/version")){
                    ResponseInstance.callResponse(apiResponse)
                }





            }
            response.newBuilder().body(responseBody).build()
        } else {
            response
        }
    }
}