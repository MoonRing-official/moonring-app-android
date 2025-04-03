package com.moonring.jetpackmvvm.network

import android.net.ParseException
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import com.moonring.jetpackmvvm.bean.HttpExceptionBodyVO
import okhttp3.ResponseBody
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException


object ExceptionHandle {

    fun handleException(e: Throwable?): AppException {
        val ex: AppException
        e?.let {
            when (it) {
                //其他状态码
                is HttpException -> {

                    val errorBody = it.response()?.errorBody()
                    val body = parseHTTPError(errorBody)
                    if (body!=null){
                        ex = AppException(Error.NETWORK_ERROR, e,body)
                    }else{
                        ex = AppException(Error.NETWORK_ERROR, e)
                    }


                    return ex
                }
                is JsonParseException, is JSONException, is ParseException, is MalformedJsonException -> {
                    ex = AppException(Error.PARSE_ERROR, e)
                    return ex
                }
                is ConnectException -> {
                    ex = AppException(Error.NETWORK_ERROR, e)
                    return ex
                }
                is javax.net.ssl.SSLException -> {
                    ex = AppException(Error.SSL_ERROR, e)
                    return ex
                }
                is ConnectTimeoutException -> {
                    ex = AppException(Error.TIMEOUT_ERROR, e)
                    return ex
                }
                is java.net.SocketTimeoutException -> {
                    ex = AppException(Error.TIMEOUT_ERROR, e)
                    return ex
                }
                is java.net.UnknownHostException -> {
                    ex = AppException(Error.TIMEOUT_ERROR, e)
                    return ex
                }
                is AppException -> return it

                else -> {
                    ex = AppException(Error.UNKNOWN, e)
                    return ex
                }
            }
        }
        ex = AppException(Error.UNKNOWN, e)
        return ex
    }
//    {"code":404420,"msg":"user is not bound wallet","request_id":"","status_code":404}
    fun parseHTTPError(responseBody: ResponseBody?): HttpExceptionBodyVO? {
    try {
//            val jsonObject = JSONObject()
        return Gson().fromJson(responseBody?.string(), HttpExceptionBodyVO::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
    }
        return null
    }


}