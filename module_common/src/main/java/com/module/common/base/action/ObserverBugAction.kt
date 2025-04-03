package com.module.common.base.action

import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar

/**
 *    author : Administrator

 *    time   : 2022/5/11/011
 *    desc   :
 */
interface ObserverBugAction {

    /**
     * https://juejin.cn/post/6934606974239637511
     * 这个是为了处理Cannot add the same observer with different lifecycles错误
     * 啥也不用实现
     */
    fun fixObserverErrorLifecycle()

}