package com.module.common.data.moudle.bean

import com.moonring.jetpackmvvm.network.BaseResponse



open class ApiResponse<T>(
    val code: Int = -1,
    val errorMsg: String = "",
    val msg: String = "",
    val status_code: Int = -1,
    val data: T? = null
) : BaseResponse<T>() {

    override fun isSucces() = code == 0

    override fun getResponseCode() = code

    override fun getResponseData() = data

    override fun getResponseMsg() = errorMsg

    override fun getStatusCode() = status_code
}