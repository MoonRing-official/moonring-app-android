package com.moonring.jetpackmvvm.network

import com.moonring.jetpackmvvm.bean.HttpExceptionBodyVO


class AppException : Exception {
//    {"code":404420,"msg":"user is not bound wallet","request_id":"","status_code":404}
    var errorMsg: String //错误消息
    var errCode: Int = 0 //错误码
    var errorLog: String? //错误日志
    var throwable: Throwable? = null

    //如果是HttpException 会有这些信息
    var code: Int = -1
    var msg: String = ""
    var status_code: Int = 0


    constructor(errCode: Int, error: String?, errorLog: String? = "", throwable: Throwable? = null) : super(error) {
        this.errorMsg = error ?: "Request failed, please try again later"
        msg = errorMsg
        this.errCode = errCode
        this.errorLog = errorLog ?: this.errorMsg
        this.throwable = throwable



    }

    constructor(error: Error, e: Throwable?) {
        errCode = error.getKey()
        errorMsg = error.getValue()
        msg = errorMsg
        errorLog = e?.message
        throwable = e
    }


    constructor(error: Error, e: Throwable?,body: HttpExceptionBodyVO) {
        errCode = error.getKey()
        errorMsg = error.getValue()
        errorLog = e?.message
        throwable = e

        code = body.code
        msg = body.msg?:""
        status_code = body.status_code
    }
}