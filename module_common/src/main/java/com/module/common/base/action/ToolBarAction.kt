package com.module.common.base.action

import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar

/**
 *    author : Administrator

 *    time   : 2022/5/11/011
 *    desc   :
 */
interface ToolBarAction {
    fun getTitleBar(): Toolbar?


    open fun obtainTitleBar(group: ViewGroup): Toolbar? {
        for (i in 0 until group.childCount) {
            val view = group.getChildAt(i)
            if (view is Toolbar) {
                return view
            } else if (view is ViewGroup) {
                val titleBar = obtainTitleBar(view)
                if (titleBar != null) {
                    return titleBar
                }
            }
        }
        return null
    }
}