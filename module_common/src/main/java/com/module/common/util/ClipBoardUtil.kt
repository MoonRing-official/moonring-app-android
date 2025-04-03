package com.module.common.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.text.TextUtils


object ClipBoardUtil {
    /**
     * 获取剪切板内容
     *
     * @return
     */
    fun paste(context: Context): String {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (manager != null) {
            if (manager.hasPrimaryClip() && manager.primaryClip!!.itemCount > 0) {
                val addedText = manager.primaryClip?.getItemAt(0)?.text
                val addedTextString = addedText?.toString()?:""
                if (!TextUtils.isEmpty(addedTextString)) {
                    return addedTextString
                }
            }
        }
        return ""
    }

    /**
     * 清空剪切板
     */
    fun clear(context: Context) {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (manager != null) {
            try {
                manager.setPrimaryClip(manager.primaryClip!!)
                manager.setPrimaryClip(ClipData.newPlainText("", ""))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    /**
     * 复制到剪切板内容
     */

    fun copyToClipboard(context: Context,text: CharSequence,callback:()->Unit){
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.text.ClipboardManager
            clipboard?.also {
                it.text = text
                callback.invoke()
            }
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard?.setPrimaryClip(clip)
            callback.invoke()
        }
//        if (clipboard!=null){
//            val clip = ClipData.newPlainText("label",text)
//            clipboard.setPrimaryClip(clip)
//        }

    }
}