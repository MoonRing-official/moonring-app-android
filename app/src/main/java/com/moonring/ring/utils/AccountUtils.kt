package com.moonring.ring.utils

import com.module.common.util.CacheUtil

/**
 *    author : Administrator

 *    time   : 2024/10/15/015
 *    desc   :
 */


fun isLoginAccount(): Boolean {
    return StorageUtil.getUserAccount()!= null
//    return !CacheUtil.getCookie().isNullOrEmpty()
}

