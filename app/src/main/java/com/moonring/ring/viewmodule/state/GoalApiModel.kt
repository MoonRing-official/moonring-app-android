package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.common.bean.MovementGoalBean
import com.module.common.bean.UpgradeRingResponse
import com.module.common.bean.login.PasswordReqBean
import com.module.common.bean.login.PasswordResponse
import com.module.common.bean.login.RingInfo
import com.module.common.data.moudle.bean.ApiResponse
import com.module.common.data.repository.HttpRequestCoroutine
import com.module.common.network.apiService
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
class GoalApiModel : BaseViewModel() {




   fun getMovementGoal() {
    viewModelScope.launch {
        runCatching {
            val boundRingResponse = apiService.getMovementGoal()

            homeAppViewModel.movementGoalBean.value =  boundRingResponse
        }.onSuccess {

        }.onFailure {

            it.printStackTrace()
        }
    }
   }


    fun postMovementGoal(bean: MovementGoalBean) {

        viewModelScope.launch {
            kotlin.runCatching {
                val response = apiService.postMovementGoal(bean)
                if (response.code == 0){
                    homeAppViewModel.movementGoalBean.value =  bean
                }
            }.onSuccess {

            }.onFailure {

            }

        }
    }




}
