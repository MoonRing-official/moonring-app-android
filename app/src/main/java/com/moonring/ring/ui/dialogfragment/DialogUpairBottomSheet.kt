package com.moonring.ring.ui.dialogfragment

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.module.common.bean.CommonDialogBean
import com.moonring.ring.R
import com.moonring.ring.databinding.DialogUnpairBinding
import com.moonring.ring.databinding.DialogWeightSelectionBinding

/**
 *    author : Administrator

 *    time   : 2024/10/14/014
 *    desc   :
 */
class DialogUpairBottomSheet(val bean : CommonDialogBean, private val onItemClick: () -> Unit) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogUnpairBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_unpair, container, false)
        return binding.root
    }

    val unitOptions = arrayOf("kg", "lb")

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.click = ProxyClick()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)


        binding.tvContent.text = bean.content
        binding.tvConfirm.text = bean.confirm
        binding.tvCancle.text = bean.cancel

    }



    inner class ProxyClick {

        fun next() {
            onItemClick.invoke()
            dismiss()

        }
        fun cancel() {
            dismiss()

        }
    }
}
