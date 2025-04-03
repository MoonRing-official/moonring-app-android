package com.moonring.jetpackmvvm.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.ext.inflateBindingWithGeneric


abstract class BaseVmDbFragment<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmFragment<VM>() {

    override fun layoutId() = 0

    //该类绑定的ViewDataBinding
    private var _binding: DB? = null
    val mDatabind: DB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding  = inflateBindingWithGeneric(inflater,container,false)
        mDatabind.root.isClickable = true
        return mDatabind.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}