package com.module.common.bean.login

/**
 *    author : Administrator

 *    time   : 2024/3/13/013
 *    desc   :
 */
data class LoginReqBean(
    val username: String,
    val password: String,
    val captcha: String
)
