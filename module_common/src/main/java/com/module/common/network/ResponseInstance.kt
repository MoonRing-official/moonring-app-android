package com.module.common.network

import com.module.common.data.moudle.bean.ApiResponse

object ResponseInstance {
    lateinit var mIResponseError: IResponseError

    fun addResponseListener(listener:IResponseError) {
        mIResponseError = listener
    }

    fun callResponse(api: ApiResponse<*>){
        mIResponseError.callback(api)
    }

}