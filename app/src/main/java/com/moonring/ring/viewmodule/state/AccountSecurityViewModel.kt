package com.moonring.ring.viewmodule.state

import androidx.lifecycle.MutableLiveData
import com.module.common.bean.DeleteBeanResponse
import com.module.common.bean.login.RingInfo
import com.module.common.data.moudle.bean.ApiResponse
import com.module.common.data.repository.HttpRequestCoroutine
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.databind.StringObservableField
import com.moonring.jetpackmvvm.ext.requestNoCheck
import com.moonring.jetpackmvvm.state.ResultState

/**
 *    author : Administrator

 *    time   : 2024/10/18/018
 *    desc   :
 */
class AccountSecurityViewModel : BaseViewModel() {

    var email = StringObservableField("")

    val accountDeletionResult = MutableLiveData<ResultState<DeleteBeanResponse>>()
    fun changePassword() {

    }

    fun toggleTwoFactorAuth() {

    }


    fun accountDeletion() {
        requestNoCheck({ HttpRequestCoroutine.accountDeletion()}, accountDeletionResult,false)

    }
}
