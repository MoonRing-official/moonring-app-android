package com.module.common.bean.login

/**
 *    author : Administrator

 *    time   : 2024/3/13/013
 *    desc   :
 */
data class LoginResponse(
    val phone: String,
    val token: String,
    val code: Int
)
