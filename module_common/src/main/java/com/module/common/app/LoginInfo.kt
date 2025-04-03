package com.module.common.app

import com.module.common.util.CacheUtil

/**
 *    author : Administrator

 *    time   : 2022/8/2/002
 *    desc   :
 */



fun isLogin(): Boolean {
    return CacheUtil.getUser()!= null

}

