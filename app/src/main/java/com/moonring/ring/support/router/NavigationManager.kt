package com.moonring.ring.support.router

import androidx.navigation.NavController
import com.module.common.support.log.LogInstance
import com.moonring.ring.R

/**
 *    author : Administrator

 *    time   : 2024/3/15/015
 *    desc   :
 */
fun interceptBackPressed(nav: NavController?):Boolean{

    if (nav?.currentDestination?.id == R.id.loginfragment) {
        return true
    }


    LogInstance.i("${nav?.currentDestination?.id}=========interceptBackPressed==========")


    val previousBackStack = nav?.previousBackStackEntry?.destination?.id
    if (previousBackStack == R.id.loginfragment) {
        return true
    }
    return false
}