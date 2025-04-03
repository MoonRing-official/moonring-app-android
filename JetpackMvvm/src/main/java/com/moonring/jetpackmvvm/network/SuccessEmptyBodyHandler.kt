package com.moonring.jetpackmvvm.network

import com.google.gson.Gson
import com.moonring.jetpackmvvm.bean.HttpExceptionBodyVO
import okhttp3.ResponseBody
import retrofit2.Response

/**
 *    author : Administrator

 *    time   : 2022/10/8/008
 *    desc   :
 */


/**
 *处理200..300空問題
 */
fun parseEmptyBody(responseBody: Response<Unit>):Int{
    val code = if (responseBody?.isSuccessful?:false) {
        0
    } else {
        -1
    }
    return code
}
