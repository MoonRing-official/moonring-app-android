package com.moonring.ring.ui.dialogfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.module.common.weight.toast.ToastSingleton
import com.moonring.ring.R
import com.moonring.ring.base.BaseBsheetDialogFragment
import com.moonring.ring.databinding.DialogLoginBinding
import com.moonring.ring.utils.CountdownUtils
import com.moonring.ring.utils.testEmailList
import com.moonring.ring.viewmodule.state.CheckingHeartRateViewModel
import com.partice.theme.utils.PnThemeUtils
import com.particle.auth.AuthCore
import com.particle.auth.customview.PNPasswordEditText
import com.particle.auth.data.AuthCoreServiceCallback
import com.particle.auth.data.req.LoginReq
import com.particle.auth.utils.setSafeOnClickListener
import com.particle.base.ParticleNetwork
import com.particle.base.analytics.AnalyticsAction
import com.particle.base.analytics.AnalyticsService
import com.particle.base.data.ErrorInfo
import com.particle.base.ext.isEmail
import com.particle.base.model.CodeReq
import com.particle.base.model.Result1Callback
import com.particle.base.model.UserInfo
import com.particle.base.utils.GsonUtils
import com.particle.base.utils.PrefUtils
import com.particle.base.utils.UserRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 *    author : Administrator

 *    time   : 2024/10/14/014
 *    desc   :
 */
class DialogLoginBottomSheet(private val onSuccess: (UserInfo) -> Unit) : BaseBsheetDialogFragment<DialogLoginBinding>(R.layout.dialog_login) {
    var errorInfo: ErrorInfo? = null
    var primary: Int = 0
    var secondary: Int = 0

    var codeReq: CodeReq? = null


    override fun initView() {
        super.initView()
        binding.click = ProxyClick()
        binding.lifecycleOwner = viewLifecycleOwner
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)


        createObserver()

        setEmailPhone()
        initLoadingAnim()
        primary = Color.parseColor("#181B1E")
        secondary =Color.parseColor("#8B8EA1")
        step1()
        binding.mcvSend.setSafeOnClickListener {
            val phoneOrEmail = binding.etPhoneOrEmail.text.toString().trim()
            showErrorMessage1("")

            if (phoneOrEmail.isEmpty()) {
                showErrorMessage1(getString(com.particle.auth.R.string.ac_input_valid_email))
                return@setSafeOnClickListener
            }
            if (!phoneOrEmail.isEmail()) {
                showErrorMessage1(getString(com.particle.auth.R.string.ac_email_format_error))
                return@setSafeOnClickListener
            }

            sendCode(phoneOrEmail)
        }

        binding.mcvVerify.setSafeOnClickListener {
            binding.etCode.text?.let {
                if (it.length < 6) {
                    showErrorMessage2(getString(com.particle.auth.R.string.ac_input_valid_code))
                    return@setSafeOnClickListener
                }
                startLoadingVerify()
                verifyCode(it.toString().trim())
                binding.tvErrorMsg2.text = ""
            }
        }

        binding.etCode.setOnTextChangeListener { text, isComplete ->
            if (isComplete) {
                startLoadingVerify()
                verifyCode(text)
                KeyboardUtils.hideSoftInput(binding.etCode)
            }
            binding.tvErrorMsg2.text = ""
        }
        binding.ivBack.setSafeOnClickListener {
            step1()
            binding.etCode.setText("")
            KeyboardUtils.hideSoftInput(binding.etCode)
        }

        binding.tvSendAgain.setSafeOnClickListener {
            binding.tvSendAgain.isEnabled = false
            binding.tvErrorMsg1.text = ""

            val phoneOrEmail = binding.etPhoneOrEmail.text.toString().trim()
            val findEmail = testEmailList.find {
                it.email == phoneOrEmail
            }
            if (findEmail!=null){
                lifecycleScope.launch {
                    delay(1000)
                    stopLoading()
                    step2()
                }
                return@setSafeOnClickListener
            }
            AuthCore.sendCode(codeReq!!, object : Result1Callback {
                override fun success() {
                    binding.etCode.setText("")
                    sendAgainIn1Minute()
                }

                override fun failure(error: ErrorInfo) {
                    errorInfo = error
                    binding.tvSendAgain.isEnabled = true
                }
            })
        }

        layoutEtCode()
    }


    private fun setEmailPhone() {
        binding.etPhoneOrEmail.setText("")
        TransitionManager.beginDelayedTransition(binding.mcvPhoneEmail, AutoTransition())
        binding.etPhoneOrEmail.hint = getString(com.particle.auth.R.string.ac_email_address)
        binding.etPhoneOrEmail.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        binding.etPhoneOrEmail.setPadding(0, 0, 0, 0)
    }

    private fun showErrorMessage1(msg: String) {
        if (isAdded)
            binding.tvErrorMsg1.text = msg
    }

    private fun startLoadingVerify() {
        binding.lottieLoadingVerify.visibility = View.VISIBLE
        binding.mcvVerify.isClickable = false
        binding.mcvVerify.alpha = 0.4f
    }

    private fun initLoadingAnim() {

        binding.lottieLoading.setAnimation(com.particle.base.R.raw.circle_light)
        binding.lottieLoadingVerify.setAnimation(com.particle.base.R.raw.circle_light)
    }


    private fun layoutEtCode() {
        val etCode: PNPasswordEditText = binding.etCode
        val height =
            (ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(76f) - ConvertUtils.dp2px(8 * 5F)) / 6

        etCode.setItemMargin(ConvertUtils.dp2px(8F).toFloat())
        etCode.setViewHeight(height)
    }
    private fun sendCode(phoneOrEmail: String) {
        startLoading()
        val findEmail = testEmailList.find {
            it.email == phoneOrEmail
        }
        if (findEmail!=null){
            lifecycleScope.launch {
                delay(1000)
                stopLoading()
                step2()
            }
            return
        }
        lifecycleScope.launch {

            codeReq = CodeReq(email = phoneOrEmail)
            AuthCore.sendCode(codeReq!!, object : Result1Callback {
                override fun success() {
                    stopLoading()
                    step2()
                }

                override fun failure(error: ErrorInfo) {
                    errorInfo = error
                    if (!isAdded) return
                    stopLoading()
                    if (error.code == ErrorInfo.userCancelError.code) {


                    } else {
                        showErrorMessage1(
                            error.message ?: getString(com.particle.auth.R.string.ac_email_format_error)
                        )
                    }
                }
            })
        }
    }

    private fun verifyCode(text: String) {
        val phoneOrEmail = binding.etPhoneOrEmail.text.toString().trim()
        val findEmail = testEmailList.find {
            it.email == phoneOrEmail
        }
        if (findEmail!=null){
            lifecycleScope.launch {
                delay(1000)
                if (text == "666666"){
                    val findEmailStr =  com.module.common.util.GsonUtils.vo2Json(findEmail)
                    val findEmailUserInfo = com.module.common.util.GsonUtils.json2VO(findEmailStr,UserInfo::class.java)
                    onSuccess.invoke(findEmailUserInfo)
                    stopLoadingVerify()
                    dismiss()

                }else{
                    showErrorMessage2("invalid verify code")
                    stopLoadingVerify()
                }
            }

            return
        }
        val loginReq = LoginReq(email = phoneOrEmail, code = text)
        AuthCore.connect(loginReq, object : AuthCoreServiceCallback<UserInfo> {
            override fun success(output: UserInfo) {
                onSuccess.invoke(output)
                logWithParticleAuthCore(output)
                stopLoadingVerify()
                dismiss()
            }

            override fun failure(errMsg: ErrorInfo) {
                errorInfo = errMsg
                if (errMsg.code == ErrorInfo.masterPasswordRestoreError.code || errMsg.code == ErrorInfo.userCancelError.code) {
                    ToastSingleton.show(errMsg.message)
                } else {
                    showErrorMessage2(errMsg.message)
                }
                stopLoadingVerify()
            }
        })
    }


    fun logWithParticleAuthCore(userInfo: UserInfo) {
        PrefUtils.setSettingString(UserRepo.USER_INFO, GsonUtils.toJson(userInfo))
        val address = if (ParticleNetwork.chainInfo.isEvmChain()) {
            AuthCore.evm.getAddress()
        } else {
            AuthCore.solana.getAddress()
        }
        LogUtils.d("onConnected address:$address")
        AnalyticsService.logWithParticleAuthCore(AnalyticsAction.LOGIN, address!!, userInfo)
    }
    private fun startLoading() {
        binding.lottieLoading.visibility = View.VISIBLE
        binding.mcvSend.isClickable = false
        binding.mcvSend.alpha = 0.4f
    }

    private fun stopLoading() {
        try {
            binding.lottieLoading.visibility = View.INVISIBLE
            binding.mcvSend.alpha = 1f
            binding.mcvSend.isClickable = true
        } catch (_: Exception) {
        }
    }

    var countDownJob: Job? = null
    private fun sendAgainIn1Minute() {

        countDownJob = CountdownUtils.countDownCoroutines(60, lifecycleScope, onTick = { second ->
            binding.tvSendAgain.text = "${resources.getString(com.particle.auth.R.string.ac_send_again)} (${second}s)"
        }, onFinish = {
            try {
                binding.tvSendAgain.setTextColor(primary)
                binding.tvSendAgain.isEnabled = true
                binding.tvSendAgain.text = getString(com.particle.auth.R.string.ac_send_again)
            } catch (_: Exception) {
            }
        }, onStart = {
            binding.tvSendAgain.setTextColor(secondary)
            binding.tvSendAgain.isEnabled = false
        })

    }

    private fun step1() {
        binding.ivBack.visibility = View.INVISIBLE
        countDownJob?.cancel()
        binding.llStep1.visibility = View.VISIBLE
        binding.llStep2.visibility = View.GONE
        resetLoginPageInfoDesc()
    }

    private fun resetLoginPageInfoDesc() {

    }
    private fun step2() {
        binding.ivBack.visibility = View.VISIBLE
        binding.llStep1.visibility = View.GONE
        binding.llStep2.visibility = View.VISIBLE

        binding.tvProjectDesc.text = binding.etPhoneOrEmail.text.trim()

        sendAgainIn1Minute()
        KeyboardUtils.showSoftInput(binding.etCode)

    }

    private fun showErrorMessage2(msg: String) {
        if (isAdded)
            binding.tvErrorMsg2.text = msg
    }


    private fun stopLoadingVerify() {
        try {
            binding.lottieLoadingVerify.visibility = View.INVISIBLE
            binding.mcvVerify.alpha = 1f
            binding.mcvVerify.isClickable = true
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
    fun createObserver() {

    }


    inner class ProxyClick {

        fun cancel() {

            dismiss()

        }
        fun stop() {

            dismiss()

        }
    }
}
