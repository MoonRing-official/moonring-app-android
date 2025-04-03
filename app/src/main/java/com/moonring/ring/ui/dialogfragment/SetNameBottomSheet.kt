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
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.module.common.ext.clickNoRepeat
import com.module.common.ext.setNbOnItemClickListener
import com.moonring.ring.R
import com.moonring.ring.databinding.DialogSetNameBinding
import com.moonring.ring.viewmodule.state.FillUserInfoViewModel

/**
 *    author : Administrator

 *    time   : 2024/3/20/020
 *    desc   :
 */

class SetNameBottomSheet(private val btnTitle:String = "Next",private val onItemClick: (String) -> Unit): BottomSheetDialogFragment() {

    private lateinit var binding: DialogSetNameBinding


    val mViewModel: FillUserInfoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_set_name, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.click = ProxyClick()
        binding.viewModel = mViewModel
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
        binding.btLogin.text = btnTitle

        binding.etName.addTextChangedListener {
            mViewModel.name.set(it.toString())
        }

    }


    inner class ProxyClick {
        fun next(){

            dismiss()
            onItemClick.invoke(binding.etName.text.toString())
        }
    }

}