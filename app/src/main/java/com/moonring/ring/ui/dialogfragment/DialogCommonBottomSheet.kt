package com.moonring.ring.ui.dialogfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.moonring.ring.R
import com.moonring.ring.databinding.DialogCommonBinding


/**
 *    author : Administrator

 *    time   : 2024/10/14/014
 *    desc   :
 */
class DialogCommonBottomSheet(private val map: Map<String,String>, private val onItemUpdateClick: () -> Unit,private val onItemLaterClick: () -> Unit) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogCommonBinding

    init {
        isCancelable = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_common, container, false)
        return binding.root
    }



    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.click = ProxyClick()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)


        binding.tvTitle.text = map["title"]
        binding.tvContent.text = map["content"]
        binding.btConfirm.text =  map["confirm"]
        binding.btCancel.text =  map["cancel"]


    }



    inner class ProxyClick {

        fun cancle() {
            onItemLaterClick.invoke()
            dismiss()

        }
        fun confirm() {
            onItemUpdateClick.invoke()

            dismiss()

        }
    }
}
