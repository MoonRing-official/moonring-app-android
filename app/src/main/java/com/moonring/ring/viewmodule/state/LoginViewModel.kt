package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.common.bean.login.PasswordReqBean
import com.module.common.bean.login.PasswordResponse
import com.module.common.data.repository.HttpRequestCoroutine
import com.module.common.network.apiService
import com.module.common.util.CacheUtil
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.ext.requestNoCheck
import com.moonring.jetpackmvvm.state.ResultState

/**
 *    author : Administrator

 *    time   : 2024/3/6/006
 *    desc   :
 */
class LoginViewModel : BaseViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val loginResult = MutableLiveData<ResultState<PasswordResponse>>()


    var isAgree = object : ObservableBoolean(false) {
        override fun set(value: Boolean) {
            CacheUtil.setUserAgreement(value)
            super.set(value)
        }

    }

    init {

        isAgree.set(CacheUtil.getUserAgreement())
    }



    fun password(bean: PasswordReqBean) {
        requestNoCheck({HttpRequestCoroutine.password(bean)},loginResult)
    }
}
