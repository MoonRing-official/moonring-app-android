package com.module.common.network

import com.module.common.data.moudle.bean.ApiResponse

/**
 *    author : Administrator

 *    time   : 2022/8/11/011
 *    desc   :
 */
interface IResponseError {
    fun callback(api: ApiResponse<*>)
}