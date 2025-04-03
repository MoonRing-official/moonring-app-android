package com.moonring.ring.ui.fragment

import android.os.Bundle
import com.module.common.base.BaseBarFragment
import com.moonring.ring.databinding.FragmentUploadLogsBinding
import com.moonring.ring.viewmodule.state.UploadLogsViewModel

/**
 *    author : Administrator

 *    time   : 2024/10/19/019
 *    desc   :
 */
class UploadLogsFragment : BaseBarFragment<UploadLogsViewModel, FragmentUploadLogsBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel


    }

    override fun toolbarBackVisity(): Boolean {
        return true
    }

    override fun createObserver() {

    }



    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {

    }
}
