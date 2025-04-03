package com.moonring.ring.viewmodule.state

import androidx.lifecycle.MutableLiveData
import com.module.common.bean.UpgradeBean
import com.module.common.bean.UpgradeRingResponse
import com.module.common.data.repository.HttpRequestCoroutine
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.ext.requestNoCheck
import com.moonring.jetpackmvvm.state.ResultState

/**
 *    author : Administrator

 *    time   : 2024/3/7/007
 *    desc   :
 */
open class UpgradeModel : BaseViewModel(){
    var upgradeResponse = MutableLiveData<ResultState<UpgradeBean>>()
    var upgradeRingResponse = MutableLiveData<ResultState<UpgradeRingResponse>>()
    fun getVersion(){
        requestNoCheck({ HttpRequestCoroutine.getVersion()},upgradeResponse,false)
    }



    fun getRingVersion(){
        requestNoCheck({ HttpRequestCoroutine.getRingVersion()}, upgradeRingResponse,false)
    }


}