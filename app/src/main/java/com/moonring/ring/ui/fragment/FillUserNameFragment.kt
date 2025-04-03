package com.moonring.ring.ui.fragment

import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.module.common.base.BaseBarFragment
import com.moonring.ring.databinding.FragmentFillUserNameBinding
import com.moonring.ring.viewmodule.state.FillUserNameViewModel

/**
 *    author : Administrator

 *    time   : 2024/9/26/026
 *    desc   :
 */
class FillUserNameFragment : BaseBarFragment<FillUserNameViewModel, FragmentFillUserNameBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel
        mDatabind.etName.addTextChangedListener {
            mViewModel.onNameChanged(it.toString())
        }
    }


    override fun createObserver() {

    }

    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {


    }
}
