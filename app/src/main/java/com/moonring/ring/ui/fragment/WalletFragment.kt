package com.moonring.ring.ui.fragment

import android.os.Bundle
import com.module.common.base.BaseBarFragment
import com.module.common.util.ClipBoardUtil
import com.moonring.ring.databinding.FragmentWalletBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.viewmodule.state.WalletViewModel
import com.particle.auth.AuthCore
import com.particle.base.model.ChainType

/**
 *    author : Administrator

 *    time   : 2024/10/19/019
 *    desc   :
 */
class WalletFragment : BaseBarFragment<WalletViewModel, FragmentWalletBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel


        val address =  homeAppViewModel.userProfile.value?.wallet_info?.address?:""
        if (!address.isNullOrBlank()){
            mViewModel.walletStr.set(address)
        }else{
            AuthCore.getUserInfo()?.getWallet(ChainType.EVM)?.let {
                mViewModel.walletStr.set(it.publicAddress)

            }
        }

        setupWalletViews()
    }

    private fun setupWalletViews() {

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


        fun copyWalletAddress() {
            context?.let {
                ClipBoardUtil.copyToClipboard(it, mViewModel.walletStr.get(), callback = {
                    toastSuccess("Address copied")
                })
            }
        }
    }
}
