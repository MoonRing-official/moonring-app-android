package com.module.common.weight.toast

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.module.common.R
import com.module.common.databinding.YuliToastV3Binding


class ToastYuliV3TextView@JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null) : LinearLayout(mContext, attrs) {

    private var binding: YuliToastV3Binding? = null

    private fun init(context: Context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.yuli_toast_v3, this, false)
        val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        llp.gravity= Gravity.CENTER
        addView(binding!!.getRoot())
    }



    init {
        init(mContext)
    }
}