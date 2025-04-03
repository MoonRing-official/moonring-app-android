package com.moonring.ring.viewmodule.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.moonring.ring.bean.OtaUpdateState
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 *    author : Administrator

 *    time   : 2024/10/15/015
 *    desc   :
 */
class ConfirmPairingViewModel : BleViewModel() {


val title: LiveData<String> = bleDeviceItem.map{ state ->
    "Ready to pair your Moon Ring\n${ state.bleDeviceAddress}?"
}
    fun pairDevice() {



    }
}
