package com.moonring.ring.viewmodule.state

import androidx.lifecycle.MutableLiveData
import com.moonring.ring.bean.HeartRateStats
import com.moonring.ring.bean.SensorData
import com.moonring.ring.homeAppViewModel
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel

class CheckingHeartRateViewModel : BaseViewModel() {
    val heartRateStats = MutableLiveData<HeartRateStats>()

    val allHeartRates = mutableListOf<SensorData>()
    fun startTest(isOpen:Boolean){
        homeAppViewModel.callRemoteSetHeartRateMode(isOpen)
    }
}