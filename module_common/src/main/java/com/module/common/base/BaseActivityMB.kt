package com.module.common.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.moonring.jetpackmvvm.base.activity.BaseVmDbActivity
import com.moonring.jetpackmvvm.base.activity.BaseVmVbActivity
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.module.common.ext.dismissLoadingExt
import com.module.common.ext.showLoadingExt


abstract class BaseActivityMB<VM : BaseViewModel, VB : ViewBinding> : BaseVmVbActivity<VM, VB>() {

    abstract override fun initView(savedInstanceState: Bundle?)

    /**
     * 创建liveData观察者
     */
    override fun createObserver() {}

    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {
        showLoadingExt(message)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
        dismissLoadingExt()
    }


}