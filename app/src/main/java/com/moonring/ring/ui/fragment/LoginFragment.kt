package com.moonring.ring.ui.fragment


import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.FragmentUtils
import com.connect.common.ConnectKitCallback
import com.connect.common.model.Account

import com.moonring.ring.R
import com.module.common.base.BaseBarFragment
import com.module.common.bean.UpgradeBean
import com.module.common.bean.login.LoginMoonringReqBean
import com.module.common.network.apiService
import com.module.common.util.CacheUtil
import com.module.common.util.GsonUtils
import com.module.common.util.time.RxTimer
import com.moonring.ring.bean.TestEmailModel

import com.moonring.ring.databinding.FragmentLoginBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.moonring.LoadingManager
import com.moonring.ring.ui.dialogfragment.DialogCheckHeartrateBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogLoginBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogWebviewSheet
import com.moonring.ring.utils.StorageUtil
import com.moonring.ring.utils.agreement_url
import com.moonring.ring.utils.privacy_url
import com.moonring.ring.utils.showUpgradleDialog
import com.moonring.ring.viewmodule.state.LoginViewModel
import com.particle.auth.AuthCore
import com.particle.auth.data.AuthCoreServiceCallback
import com.particle.auth.data.MasterPwdServiceCallback
import com.particle.base.data.ErrorInfo
import com.particle.base.model.LoginPageConfig
import com.particle.base.model.LoginType
import com.particle.base.model.UserInfo
import com.particle.connectkit.AdditionalLayoutOptions
import com.particle.connectkit.ConnectKitConfig
import com.particle.connectkit.ConnectOption
import com.particle.connectkit.EnableWallet
import com.particle.connectkit.EnableWalletProvider
import com.particle.connectkit.ParticleConnectKit
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateAction


/**
 *    author : Administrator

 *    time   : 2024/3/6/006
 *    desc   :
 */
class LoginFragment : BaseBarFragment<LoginViewModel, FragmentLoginBinding>() {






    private val mClick by lazy {
        ProxyClick()
    }

    private val loadingManager by lazy {
        LoadingManager()
    }
    override fun initView(savedInstanceState: Bundle?) {


        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel

        loadingManager.configureRotationAnimator(mDatabind.ivStatusIcon)

        setAgreementShow()

    }

    fun setAgreementShow(){

        val tip = "Agree to MOONRING's Terms of service & Privacy Policy"
        val textClickable1 = "Terms of service"
        val textClickable2 = "Privacy Policy"
        val ssb = SpannableStringBuilder()
        ssb.append(tip)
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                termsService()
                mDatabind.tvAgreeTo.text = ssb
            }
        }
        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                privacy()
                mDatabind.tvAgreeTo.text = ssb
            }
        }
        val start = tip.indexOf(textClickable1)
        val start2 = tip.indexOf(textClickable2)
        if (start > -1) {
            ssb.setSpan(
                clickableSpan1,
                start,
                start + textClickable1.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#F45EC1"))
            ssb.setSpan(
                foregroundColorSpan,
                start,
                start + textClickable1.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            mDatabind.tvAgreeTo.movementMethod = LinkMovementMethod.getInstance()
        }

        if (start2 > -1) {
            ssb.setSpan(
                clickableSpan2,
                start2,
                start2 + textClickable2.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#F45EC1"))
            ssb.setSpan(
                foregroundColorSpan,
                start2,
                start2 + textClickable2.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            mDatabind.tvAgreeTo.movementMethod = LinkMovementMethod.getInstance()
        }

        mDatabind.tvAgreeTo.text = ssb
    }

    fun termsService(){
        val dialogFragment = DialogWebviewSheet("",
            agreement_url
        ){ height, unit ->

        }
        dialogFragment.show(childFragmentManager, "webview")
    }

    fun privacy(){
        val dialogFragment = DialogWebviewSheet("",
            privacy_url
        ){ height, unit ->

        }
        dialogFragment.show(childFragmentManager, "webview")
    }

    override fun toolbarBackVisity(): Boolean {
        return false
    }
    override fun createObserver() {

    }


    override fun getToolbarTitle(): String {
        return getString(R.string.login)
    }


    fun loginSuccess(output:UserInfo){
        mDatabind.loading.visibility = View.VISIBLE
        loadingManager.startRotation()
        StorageUtil.setUserAccount(output)

        mViewModel.viewModelScope.launch {
            kotlin.runCatching {
                val response =  apiService.login(LoginMoonringReqBean(output.uuid, output.token,output.email?:""))
                if (response.code == 0) {
                    val userResponse = apiService.getUserInfo()
                    homeAppViewModel.userProfile.value = userResponse.data
                    CacheUtil.setUser(userResponse.data)
                    if (userResponse.data?.birthday?:0>0){
                        nav().navigateAction(R.id.action_loginfragment_to_mainFragment)
                    }else{
                        nav().navigateAction(R.id.action_loginfragment_to_fillUserInfoFragment)
                    }
                }
            }.onSuccess {
                mDatabind.loading.visibility = View.GONE
                loadingManager.stopRotation()


            }.onFailure {
                mDatabind.loading.visibility = View.GONE
                loadingManager.stopRotation()
            }

        }
    }

    inner class ProxyClick {

        fun test(){

        }
        fun login(){

            val dialogFragment = DialogLoginBottomSheet{ output->
                loginSuccess(output)

            }
            dialogFragment.show(childFragmentManager, "login")



        }

        fun setmasterMaster(){
            mViewModel.viewModelScope.launch {
                AuthCore.setMasterPassword(object : MasterPwdServiceCallback {
                    override fun failure(errMsg: ErrorInfo) {

                    }

                    override fun success() {
                        nav().navigateAction(R.id.action_loginfragment_to_fillUserInfoFragment)

                    }

                })
            }
        }

        fun forgetpsw(){
            toastError("")
        }

        fun setAgree(){
            mViewModel.isAgree.set(!mViewModel.isAgree.get())
        }

        fun  user_agreement(){

        }

        fun user_privacy(){

        }
    }
}
