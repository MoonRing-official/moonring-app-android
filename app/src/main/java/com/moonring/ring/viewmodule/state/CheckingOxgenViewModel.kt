package com.moonring.ring.viewmodule.state

import androidx.lifecycle.MutableLiveData
import com.moonring.ring.bean.HeartRateStats
import com.moonring.ring.bean.OxygenSensorData
import com.moonring.ring.bean.SensorData
import com.moonring.ring.homeAppViewModel
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel

class CheckingOxgenViewModel : BaseViewModel() {
    val oxygenSensorData = MutableLiveData<OxygenSensorData>()

    val allHeartRates = mutableListOf<SensorData>()
    init {
        oxygenSensorData.value = OxygenSensorData()
    }
    fun startTest(isOpen:Boolean){
        homeAppViewModel.callRemoteOpenBlood(isOpen)
    }


}