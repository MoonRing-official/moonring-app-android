package com.moonring.jetpackmvvm.base.activity

import android.view.View
import androidx.databinding.ViewDataBinding
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.ext.inflateBindingWithGeneric


abstract class BaseVmDbActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmActivity<VM>() {

    override fun layoutId() = 0

    lateinit var mDatabind: DB

    /**
     * 创建DataBinding
     */
    override fun initDataBind(): View? {
        mDatabind = inflateBindingWithGeneric(layoutInflater)
        return mDatabind.root
    }
}