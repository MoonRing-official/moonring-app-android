package com.module.common.weight.dialogfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment


open class BaseDialogFragment: DialogFragment(){







    override fun onStart() {
        super.onStart()
        val win = dialog!!.window

        win?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        val params = win?.attributes
        params?.gravity = Gravity.BOTTOM

        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win?.attributes = params


    }
}

