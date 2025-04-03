package com.moonring.ring.viewmodule.state

import androidx.lifecycle.MutableLiveData
import com.module.common.bean.DeleteBeanResponse
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.state.ResultState

/**
 *    author : Administrator

 *    time   : 2024/3/20/020
 *    desc   :
 */
class BrowserModel:BaseViewModel() {
    val close = MutableLiveData<Boolean>()

    init {

        close.value = false
    }
}