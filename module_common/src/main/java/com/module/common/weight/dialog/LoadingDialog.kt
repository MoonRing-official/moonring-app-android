package com.module.common.weight.dialog

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.airbnb.lottie.LottieAnimationView
import com.blankj.utilcode.util.ConvertUtils
import com.module.common.R
import com.module.common.databinding.LayoutCustomProgressDialogViewBinding
import com.module.common.util.time.RxTimer
import com.moonring.jetpackmvvm.enum.LoadingEunm

/**
 *    author : Administrator

 *    time   : 2022/8/18/018
 *    desc   :loading dialog
 */

class LoadingDialog(context: Context): Dialog(context, R.style.LoadingDialogStyle){
    init {

        val window = window
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        window.decorView.setPadding(0, 0, 0, 0)

        setCancelable(false)
        setCanceledOnTouchOutside(false)
        val attributes = window.attributes
        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT
        attributes.gravity = Gravity.CENTER

        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setDimAmount(0.5f)

        window.attributes = attributes
    }
    private lateinit var binding: LayoutCustomProgressDialogViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_custom_progress_dialog_view,
            null,
            false
        )

        setContentView(binding.root)

    }
   val timer by lazy {
       RxTimer()
   }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        timer.cancel()
    }
    /**
     * 根据LoadingEunm 决定显示
     * @param loadingEunm 枚举
     */
    fun showByLoadingEunm(loadingEunm: LoadingEunm,autoClose:Boolean = false){
        show()

        binding.ivStatusIcon.imageAssetsFolder ="images/"
        if (loadingEunm==LoadingEunm.robot ){
            binding.ivStatusIcon.setAnimation("robot_loading.json")
        }else{
            val layoutParms =   binding.ivStatusIcon.layoutParams
            layoutParms.width = ConvertUtils.dp2px((172f*1.5).toFloat())
            layoutParms.height = ConvertUtils.dp2px((62f*1.5).toFloat())
            binding.ivStatusIcon.setAnimation("loadingv3.json")
        }
        binding.ivStatusIcon.repeatCount = ValueAnimator.INFINITE
        binding.ivStatusIcon.playAnimation()
        timer.cancel()
        if (autoClose) {

            timer.timer(15000) {
                dismiss()
            }
        }

    }


}