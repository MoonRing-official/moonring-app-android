package com.moonring.ring.utils

import androidx.databinding.BindingAdapter
import com.kyleduo.switchbutton.SwitchButton
import com.moonring.ring.view.BatteryView

object AppBindAdapter {

    @JvmStatic
    @BindingAdapter(value = ["isCharging"])
    fun isCharging(view: BatteryView, isCharging: Boolean) {
        view.setStatus(isCharging)
    }
}