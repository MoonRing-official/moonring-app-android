package com.module.common.weight.toast

import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import com.module.common.R
import com.module.common.weight.toast.ToastUtils

/**
 *    author : Administrator

 *    time   : 2022/8/18/018
 *    desc   :
 */
object ToastSingleton {
    val view  by lazy{
//        val toastView = ToastYuliV3TextView(appContext)
//        ToastUtils.setView(R.layout.yuli_toast_v3)

        ToastUtils.getView<ToastYuliV3TextView>()
    }
    val iconStatus by lazy {
//        ToastUtils.setMargin(ConvertUtils.dp2px(16f).toFloat(),0f)
        view.findViewById<ImageView>(R.id.iv_status)
    }
    fun show(any:Any?){
        iconStatus.setImageResource(R.drawable.icon_v3_toast_warning)
        ToastUtils.getToast()?.duration = Toast.LENGTH_SHORT
        ToastUtils.show(any?:"null")
    }
    fun show(@StringRes id:Int){
        iconStatus.setImageResource(R.drawable.icon_v3_toast_warning)
        ToastUtils.getToast()?.duration = Toast.LENGTH_SHORT
        ToastUtils.show(id)
    }

    fun show(text:CharSequence?){
        iconStatus.setImageResource(R.drawable.icon_v3_toast_warning)
        ToastUtils.getToast()?.duration = Toast.LENGTH_SHORT
        ToastUtils.show(text?:"null")
    }

    fun toastSuccess(text:CharSequence?){
        show(ToastVOType.success,text)
    }
    fun toastWarning(text:CharSequence?){
        show(ToastVOType.warning,text)
    }

    fun toastError(text:CharSequence?){
        show(ToastVOType.error,text)
    }

    fun toastSuccess(@StringRes id:Int){
        show(ToastVOType.success,id)
    }
    fun toastWarning(@StringRes id:Int){
        show(ToastVOType.warning,id)
    }

    fun toastError(@StringRes id:Int){
        show(ToastVOType.error,id)
    }

    fun show(type:ToastVOType,text:CharSequence?,duration: Long = 2000){
        when(type){
            ToastVOType.success->{
                iconStatus.setImageResource(R.drawable.icon_v3_toast_success)

            }
            ToastVOType.warning->{
                iconStatus.setImageResource(R.drawable.icon_v3_toast_warning)
            }
            ToastVOType.error->{
                iconStatus.setImageResource(R.drawable.icon_v3_toast_error)
            }
        }
        ToastUtils.getToast().duration = duration.toInt()
        ToastUtils.show(text)
    }

    fun show(type:ToastVOType,@StringRes id:Int,duration: Long = 2000){
        when(type){
            ToastVOType.success->{
                iconStatus.setImageResource(R.drawable.icon_v3_toast_success)

            }
            ToastVOType.warning->{
                iconStatus.setImageResource(R.drawable.icon_v3_toast_warning)
            }
            ToastVOType.error->{
                iconStatus.setImageResource(R.drawable.icon_v3_toast_error)
            }
        }
        ToastUtils.getToast().duration = duration.toInt()
        ToastUtils.show(id)
    }


    fun show(type:String,text:CharSequence?,duration: Long = 2000){
        when(type){
            ToastVOType.success.name->{
                iconStatus.setImageResource(R.drawable.icon_v3_toast_success)
            }
            ToastVOType.warning.name->{
                iconStatus.setImageResource(R.drawable.icon_v3_toast_warning)
            }
            ToastVOType.error.name->{
                iconStatus.setImageResource(R.drawable.icon_v3_toast_error)
            }
        }
        ToastUtils.getToast().duration = duration.toInt()
        ToastUtils.show(text)
    }


}

enum class ToastVOType {
    error, success, warning
}