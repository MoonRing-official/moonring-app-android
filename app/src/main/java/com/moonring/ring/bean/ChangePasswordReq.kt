package com.moonring.ring.bean

/**
 *    author : Administrator

 *    time   : 2024/3/19/019
 *    desc   :
 */
data class ChangePasswordReq(
    val old_password: String,
    val new_password: String
)