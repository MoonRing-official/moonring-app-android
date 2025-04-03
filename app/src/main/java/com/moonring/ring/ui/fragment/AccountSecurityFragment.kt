package com.moonring.ring.ui.fragment

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.module.common.base.BaseBarFragment
import com.module.common.bean.CommonDialogBean
import com.module.common.network.apiService
import com.module.common.util.CacheUtil
import com.moonring.ring.R
import com.moonring.ring.databinding.FragmentAccountSecurityBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.ui.dialogfragment.DialogUpairBottomSheet
import com.moonring.ring.utils.StorageUtil
import com.moonring.ring.viewmodule.state.AccountSecurityViewModel
import com.particle.auth.AuthCore
import com.particle.auth.data.MasterPwdServiceCallback
import com.particle.base.data.ErrorInfo
import com.particle.base.data.WebOutput
import com.particle.base.data.WebServiceCallback
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.callback.databind.StringObservableField
import com.moonring.jetpackmvvm.ext.parseState
import com.moonring.jetpackmvvm.state.ResultState
import com.moonring.jetpackmvvm.support.logout.restartApp

/**
 *    author : Administrator

 *    time   : 2024/10/18/018
 *    desc   :
 */
class AccountSecurityFragment : BaseBarFragment<AccountSecurityViewModel, FragmentAccountSecurityBinding>() {


    private val mClick by lazy {
        ProxyClick()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel


       val email =  homeAppViewModel.userProfile.value?.email?:""
        if (!email.isNullOrBlank()){
            mViewModel.email.set(email)
        }else{
            AuthCore.getUserInfo()?.email?.let {
                mViewModel.email.set(it)
            }

        }

    }

    override fun toolbarBackVisity(): Boolean {
        return true
    }

    override fun createObserver() {


        mViewModel.accountDeletionResult.observe(this){


            when (it) {
                is ResultState.Success -> {
                    mClick.logout()
                }

                is ResultState.Error -> {

                    toastError(it.error.msg)
                }

                else -> {}
            }
        }
    }

    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {
        fun changePassword() {
            mViewModel.changePassword()
        }

        fun enableTwoFactorAuth() {
            mViewModel.toggleTwoFactorAuth()
        }

        fun changeMasterPassword(){
            AuthCore.changeMasterPassword(object : MasterPwdServiceCallback {
                override fun failure(errMsg: ErrorInfo) {

                }

                override fun success() {
                    println("changeMasterPassword:success")
                }

            })
        }

        fun setPhone(){
            AuthCore.openAccountAndSecurity(requireContext(),
                object : WebServiceCallback<WebOutput> {
                    override fun success(output: WebOutput) {

                    }

                    override fun failure(errMsg: ErrorInfo) {
                        if (errMsg.code == 10005 || errMsg.code == 8005) {
                            //You've been knocked out.
                        }
                    }
                })
        }

        fun setEmail(){
            AuthCore.openAccountAndSecurity(requireContext(),
                object : WebServiceCallback<WebOutput> {
                    override fun success(output: WebOutput) {

                    }

                    override fun failure(errMsg: ErrorInfo) {
                        if (errMsg.code == 10005 || errMsg.code == 8005) {

                        }
                    }
                })
        }

        fun logout(){


            val dialogFragment = DialogUpairBottomSheet(CommonDialogBean().apply {
                content = "Do you wish to log out?"
                confirm =  getString(R.string.confirm)
                cancel = getString(R.string.cancel)

            }){
                StorageUtil.setUserAccount(null)
                restartApp(requireContext())
            }
            dialogFragment.show(childFragmentManager, "uppair")

        }
        fun deleteAccount(){

            val dialogFragment = DialogUpairBottomSheet(CommonDialogBean().apply {
                content = "Deleting your account will also delete all health data saved in the app, and this cannot be restored. Do you want to continue?"
                confirm = "Delete"
                cancel = getString(R.string.cancel)

            }){
                mViewModel.accountDeletion()
            }
            dialogFragment.show(childFragmentManager, "uppair")


        }
    }
}
