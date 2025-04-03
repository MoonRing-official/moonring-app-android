package com.module.common.weight.dialog

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 *    author : Administrator

 *    time   : 2022/5/6/006
 *    desc   :基本dialog 含dabtabing
 */
abstract class BaseDialogDB<DB : ViewDataBinding>(context: Context) : BaseDialog(context) {
    protected lateinit var mDatabing: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDatabing = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutID(), null, false)
        setContentView(mDatabing.root)
    }


    abstract fun getLayoutID(): Int

    override fun show() {

        this.getWindow()?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        getWindow()?.getDecorView()?.let { fullScreenImmersive(it) }
        this.getWindow()?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    private fun fullScreenImmersive(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
            view.systemUiVisibility = uiOptions
        }
    }
}