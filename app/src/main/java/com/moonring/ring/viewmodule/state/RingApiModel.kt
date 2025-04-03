package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.common.bean.BoundRingResponse
import com.module.common.bean.UpgradeRingResponse
import com.module.common.bean.login.PasswordReqBean
import com.module.common.bean.login.PasswordResponse
import com.module.common.bean.login.RingInfo
import com.module.common.data.moudle.bean.ApiResponse
import com.module.common.data.repository.HttpRequestCoroutine
import com.module.common.network.apiService
import com.module.common.support.log.LogInstance
import com.module.common.util.CacheUtil
import com.moonring.ring.homeAppViewModel
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.ext.requestNoCheck
import com.moonring.jetpackmvvm.ext.util.loge
import com.moonring.jetpackmvvm.state.ResultState
import com.moonring.jetpackmvvm.state.paresException
import com.moonring.jetpackmvvm.state.paresResult

/**
 *    author : Administrator

 *    time   : 2024/3/6/006
 *    desc   :
 */
class RingApiModel : BaseViewModel() {




    val boundRingResult = MutableLiveData<ResultState<ApiResponse<*>>>()

    val getBoundRingResult = MutableLiveData<ResultState<BoundRingResponse>>()

    fun afterBondRing(){
        requestNoCheck({ HttpRequestCoroutine.getBoundRing()}, getBoundRingResult,false)

    }


   fun getBoundRing() {
    viewModelScope.launch {
        runCatching {
            val boundRingResponse = apiService.getBoundRing()

            homeAppViewModel.ringInfo.value =  boundRingResponse.data
        }.onSuccess {

        }.onFailure {

            it.printStackTrace()
        }
    }
   }


    fun boundRing(ringInfo: RingInfo) {
        requestNoCheck({ HttpRequestCoroutine.boundRing(ringInfo)}, boundRingResult,false)

    }

    fun unBoundRing(){
        viewModelScope.launch {
            runCatching {
                val response = apiService.deleteBoundRing()
                LogInstance.i("deleteBoundRing ${response.code()}")
                if (response.code() == 204){

                    homeAppViewModel.ringInfo.value =  null
                }

            }.onSuccess {

            }.onFailure {

                it.printStackTrace()
            }
        }
    }






}
