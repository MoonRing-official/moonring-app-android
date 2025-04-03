package com.moonring.ring.viewmodule.state

import android.bluetooth.BluetoothClass.Device
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.module.common.data.moudle.bean.ApiResponse
import com.module.common.data.repository.HttpRequestCoroutine
import com.module.common.util.AppCacheKeyEnum
import com.module.common.util.CacheUtil
import com.module.common.util.GsonUtils
import com.moonring.ring.bean.BleDeviceItem
import com.moonring.ring.bean.DeviceInfo
import com.moonring.ring.bean.DeviceReqBean
import com.moonring.ring.bean.OtaInfo
import com.moonring.ring.bean.OtaUpdateState
import com.moonring.ring.bean.SportData
import com.moonring.ring.data.repository.YFHttpRequestManger
import com.moonring.ring.data.repository.homeCommonHttpRequestCoroutine
import com.moonring.ring.data.repository.yfHttpRequestCoroutine
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.databind.BooleanObservableField
import com.moonring.jetpackmvvm.callback.databind.IntObservableField
import com.moonring.jetpackmvvm.callback.databind.StringObservableField
import com.moonring.jetpackmvvm.ext.requestNoCheck
import com.moonring.jetpackmvvm.state.ResultState

/**
 *    author : Administrator

 *    time   : 2024/3/7/007
 *    desc   :
 */

open class BleViewModel : BaseViewModel() {
    var connectionState = IntObservableField()

    var bleDeviceAddress = StringObservableField("")


    var uuid = StringObservableField("")

    val bleDeviceItem = MutableLiveData<BleDeviceItem>()
    val otaInfo = MutableLiveData<OtaInfo>()

    val otaUpdateState = MutableLiveData<OtaUpdateState>()

    var isModifyBleIng = BooleanObservableField()

    init {
        otaUpdateState.value = OtaUpdateState(0,0)
    }



    val submitResult = MutableLiveData<ResultState<ApiResponse<*>>>()

}


