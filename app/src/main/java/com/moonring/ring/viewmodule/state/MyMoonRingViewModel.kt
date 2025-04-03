package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.blankj.utilcode.util.AppUtils
import com.module.common.util.CacheUtil
import com.moonring.ring.bean.DeviceInfo
import com.moonring.ring.enums.ConnectionState
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.utils.formatTimestampMyMoonring
import com.moonring.jetpackmvvm.callback.databind.IntObservableField
import com.moonring.jetpackmvvm.callback.databind.StringObservableField

/**
 *    author : Administrator

 *    time   : 2024/10/19/019
 *    desc   :
 */
class MyMoonRingViewModel : BleViewModel() {
    var batteryLevel = IntObservableField()
    var batteryStatus = IntObservableField()
    var isCharging = object : ObservableField<Boolean>(batteryStatus){
        override fun get(): Boolean {
            return batteryStatus.get() == 1
        }
    }

    var lastSynTime = StringObservableField("")

    var appVersion = StringObservableField("")


    var version =  StringObservableField("")


    val deviceInfo = MutableLiveData<DeviceInfo>()
    init {
        appVersion.set("v${AppUtils.getAppVersionName()}" )
        deviceInfo.value = DeviceInfo()


        lastSynTime.set(formatTimestampMyMoonring(homeAppViewModel.lastSynTime?.value?:0))

    }

    var lastSynTimeShow = object : ObservableField<String>(lastSynTime,connectionState){
        override fun get(): String {
            if (connectionState.get() == ConnectionState.Connected.value){
                return lastSynTime.get()
            }
            return  "--"
        }
    }


    var bleDeviceAddressShow  = object : ObservableField<String>(bleDeviceAddress,connectionState){
        override fun get(): String {
            if (connectionState.get() == ConnectionState.Connected.value){
                return bleDeviceAddress.get()
            }
            return  "--"
        }
    }




    val versionShow = object : ObservableField<String>(version,connectionState){
        override fun get(): String {
            if (connectionState.get() == ConnectionState.Connected.value){
                return "v${version.get()}"
            }
            return  "--"
        }
    }

    fun refreshDeviceInfo() {

    }
}
