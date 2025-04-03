package com.module.common.weight.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import com.module.common.R
import com.module.common.base.action.ToastAction


open class BaseDialog(context: Context):  Dialog(context, R.style.BottomDialogStyle) , ToastAction {
    init {
        // 拿到Dialog的Window, 修改Window的属性
        val window = window
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        window.decorView.setPadding(0, 0, 0, 0)

        setCancelable(true)
        setCanceledOnTouchOutside(true)
        val attributes = window.attributes
        window.setGravity(Gravity.CENTER_VERTICAL);
        window.setWindowAnimations(R.style.main_menu_animStyle)
        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT
        attributes.gravity = Gravity.CENTER




        window.attributes = attributes
    }


    fun setALLBg(){

        val window = window
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        window.decorView.setPadding(0, 0, 0, 0)

        setCancelable(true)
        setCanceledOnTouchOutside(true)
        window.setGravity(Gravity.CENTER_VERTICAL);
        window.setWindowAnimations(R.style.main_menu_animStyle)
        val attributes = window.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT




        window.attributes = attributes
    }

    fun setALLBgForBottom(){

        val window = window
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        window.decorView.setPadding(0, 0, 0, 0)

        setCancelable(true)
        setCanceledOnTouchOutside(true)
        window.setGravity(Gravity.CENTER_VERTICAL);
        window.setWindowAnimations(R.style.BottomDialogAnimation)
        val attributes = window.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT




        window.attributes = attributes
    }

}