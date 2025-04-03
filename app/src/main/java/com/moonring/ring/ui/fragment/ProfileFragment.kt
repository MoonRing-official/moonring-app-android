package com.moonring.ring.ui.fragment

import android.os.Bundle
import android.view.MotionEvent
import com.module.common.base.BaseBarFragment
import com.moonring.ring.R
import com.moonring.ring.databinding.FragmentProfileBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.ui.dialogfragment.DialogUpairBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogWebviewSheet
import com.moonring.ring.utils.agreement_url
import com.moonring.ring.utils.privacy_url
import com.moonring.ring.viewmodule.state.ProfileViewModel
import com.particle.auth.AuthCore
import com.particle.base.data.ErrorInfo
import com.particle.base.data.WebOutput
import com.particle.base.data.WebServiceCallback
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateAction

/**
 *    author : Administrator

 *    time   : 2024/10/8/008
 *    desc   :
 */
class ProfileFragment : BaseBarFragment<ProfileViewModel, FragmentProfileBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }
    val userProfile by lazy {
        homeAppViewModel.userProfile.value!!
    }
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel
        mViewModel.userProfile.value = userProfile

    }

    override fun toolbarBackVisity(): Boolean {
        return true
    }

    override fun createObserver() {
        // 观察 ViewModel 中的数据变化
        homeAppViewModel.userProfile.observe(viewLifecycleOwner){
            mViewModel.userProfile.value = it
        }
    }

    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {
        fun saveProfile() {


        }

        fun openAccountAndSecurity(){

            nav().navigateAction(R.id.action_mainfragment_to_accountSecurityFragment)
        }

        fun wallet(){
            nav().navigateAction(R.id.action_mainfragment_to_walletFragment)
        }


        fun myMoonRing(){
            nav().navigateAction(R.id.action_mainfragment_to_myMoonRingFragment)
        }

        fun profile(){
            nav().navigateAction(R.id.action_mainfragment_to_personalProfileFragment)
        }

        fun termsService(){
            val dialogFragment = DialogWebviewSheet("",agreement_url){ height,unit ->

            }
            dialogFragment.show(childFragmentManager, "webview")
        }

        fun privacy(){
            val dialogFragment = DialogWebviewSheet("",
                privacy_url){ height, unit ->

            }
            dialogFragment.show(childFragmentManager, "webview")
        }
    }
}
