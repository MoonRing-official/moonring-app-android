package com.moonring.jetpackmvvm.ext

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment



fun Fragment.nav(): NavController {
    return NavHostFragment.findNavController(this)
}

fun nav(view: View): NavController {
    return Navigation.findNavController(view)
}

var lastNavTime = 0L

/**
 * 防止短时间内多次快速跳转Fragment出现的bug
 * @param resId 跳转的action Id
 * @param bundle 传递的参数
 * @param interval 多少毫秒内不可重复点击 默认0.5秒
 */
fun NavController.navigateAction(resId: Int,
                                 bundle: Bundle? = null,
                                 interval: Long = 500,
                                 popUpToId:Int = -1,
                                 inclusive:Boolean = true) {
    val currentTime = System.currentTimeMillis()
    if (currentTime >= lastNavTime + interval) {
        lastNavTime = currentTime
        try {
            //a->b->c  b->c的时候需要把b和a是否在stack清掉
            if (popUpToId!=-1){
                val navOptions = NavOptions.Builder().setPopUpTo(popUpToId, inclusive).build()
                navigate(resId, bundle, navOptions)
                return
            }
            navigate(resId, bundle)
        }catch (ignore:Exception){
            //防止出现 当 fragment 中 action 的 duration设置为 0 时，连续点击两个不同的跳转会导致如下崩溃 #issue53
            ignore.printStackTrace()
        }
    }
}

var lastNavBackTime = 0L

fun NavController.navigateUpNoRepeat(interval: Long = 500){
    val currentTime = System.currentTimeMillis()
    if (currentTime >= lastNavBackTime + interval) {
        lastNavBackTime = currentTime
        navigateUp()
    }
}

//判断是否在堆里
fun NavController.isInBackStack(@IdRes id: Int): Boolean = runCatching {
    getBackStackEntry(id)
}.isSuccess


