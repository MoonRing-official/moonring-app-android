package com.moonring.ring.ext

import android.graphics.Color
import android.os.Build
import android.widget.EditText
import android.widget.NumberPicker

fun NumberPicker.setTextSizeCompat(sizeInSp: Float) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textSize = sizeInSp // API 29 及以上直接设置
        } else {
            // 遍历子视图
            val count = this.childCount
            for (i in 0 until count) {
                val child = this.getChildAt(i)
                if (child is EditText) {
                    child.setTextSize(sizeInSp) // 设置字体大小
                    break
                }
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun NumberPicker.setTextColorCompat(color: Int) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textColor = color // API 29 及以上直接设置
        } else {
            // 获取 NumberPicker 的子视图
            val count = this.childCount
            for (i in 0 until count) {
                val child = this.getChildAt(i)
                if (child is EditText) {
                    child.setTextColor(color) // 设置文本颜色
                    break
                }
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
