package com.moonring.ring.viewmodule.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.moonring.ring.bean.OtaUpdateState
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel

class UpgradeFirmWareViewModel : BaseViewModel(){
    val otaUpdateState = MutableLiveData<OtaUpdateState>()
    init {
        otaUpdateState.value = OtaUpdateState(0,0)
    }




}