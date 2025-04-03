package com.moonring.ring.viewmodule.state

import android.bluetooth.BluetoothDevice
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.databind.IntObservableField

/**
 *    author : Administrator

 *    time   : 2024/10/15/015
 *    desc   :
 */
class SearchMoonRingViewModel : BleViewModel() {

    val moonringszie = IntObservableField(-1)
    var titleShow = object : ObservableField<String>(moonringszie){
        override fun get(): String {
            if (moonringszie.get()>0){

                return  "Devices found"
            }
            return  "Searching for your Moon Ring"
        }
    }

    fun startDeviceSearch() {

    }


}
