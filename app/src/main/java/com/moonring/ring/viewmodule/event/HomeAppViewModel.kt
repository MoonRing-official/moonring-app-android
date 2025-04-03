package com.moonring.ring.viewmodule.event

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.RemoteException
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.hutool.core.util.HexUtil
import com.blankj.utilcode.util.ActivityUtils
import com.module.common.app.appContext
import com.module.common.bean.MovementGoalBean
import com.module.common.bean.UserProfileMoonRing
import com.module.common.bean.login.RingInfo
import com.module.common.support.log.LogInstance
import com.module.common.util.AppCacheKeyEnum
import com.module.common.util.CacheUtil
import com.module.common.util.GsonUtils
import com.module.common.weight.toast.ToastSingleton
import com.moonring.ring.bean.BleDeviceItem
import com.moonring.ring.bean.ComparatorBleDeviceItem
import com.moonring.ring.bean.DayData
import com.moonring.ring.bean.DeviceInfo
import com.moonring.ring.bean.OtaInfo
import com.moonring.ring.bean.OtaUpdateState
import com.moonring.ring.bean.OxygenSensorData
import com.moonring.ring.bean.SensorData
import com.moonring.ring.bean.SensorStatus
import com.moonring.ring.bean.SportData
import com.moonring.ring.bean.room.CalData
import com.moonring.ring.bean.room.HeartRateData
import com.moonring.ring.bean.room.OxygenData
import com.moonring.ring.bean.room.SleepData
import com.moonring.ring.bean.room.StepData
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.moonring.CommonAttributes
import com.moonring.ring.support.moonring.DataFetcherHeartBeat
import com.moonring.ring.support.moonring.DataFetcherSleep
import com.moonring.ring.support.moonring.DataFetcherStep
import com.moonring.ring.support.moonring.MRRingManager
import com.moonring.ring.support.moonring.getDefaultProfile
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.ui.activity.SampleBleService
import com.moonring.ring.uploadDataModel
import com.moonring.ring.utils.calculateDistanceAndCalories
import com.moonring.ring.utils.currentTimeInSec
import com.moonring.ring.utils.getCal
import com.sxr.sdk.ble.keepfit.aidl.BleClientOption
import com.sxr.sdk.ble.keepfit.aidl.ECardInfo
import com.sxr.sdk.ble.keepfit.aidl.ECardInfoItem
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback
import com.sxr.sdk.ble.keepfit.aidl.SmsRspInfo
import com.sxr.sdk.ble.keepfit.aidl.SmsRspInfoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.livedata.event.EventLiveData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random


class HomeAppViewModel : BaseViewModel(){

    var changeTabIndex = EventLiveData<Int>()
    val bleDeviceItem = MutableLiveData<BleDeviceItem>()

    val userProfile = EventLiveData<UserProfileMoonRing>()

    val allHeartRates = mutableListOf<SensorData>()

    val handler by lazy {
        Handler(Looper.getMainLooper())
    }
    val ringInfo = EventLiveData<RingInfo?>()
    val movementGoalBean = EventLiveData<MovementGoalBean>()



    val _connectionState = MutableStateFlow<Int>(0)
    val connectionState: StateFlow<Int> = _connectionState.asStateFlow()


    val _uuid = MutableStateFlow<String>("")
    val uuid: StateFlow<String> = _uuid.asStateFlow()


    private val _otaInfo = MutableStateFlow(OtaInfo(false, "", ""))
    val otaInfo = _otaInfo.asStateFlow()


    private val _scanResult = MutableStateFlow<List<BleDeviceItem>>(emptyList())
    val scanResult: StateFlow<List<BleDeviceItem>> = _scanResult.asStateFlow()

    val _oxygenSensorData = MutableStateFlow(OxygenSensorData(0, 0, 0, 0, 0))
    val oxygenSensorData = _oxygenSensorData.asStateFlow()


    val batteryStatus = MutableLiveData<Pair<Int, Int>>()
    val sportData = MutableLiveData<SportData>()
    val sensorData = MutableLiveData<SensorData>()

    val deviceInfo = MutableLiveData<DeviceInfo>()


    val sensorState = MutableLiveData<SensorStatus>()

    private val dayDataList = mutableListOf<DayData>()

    val dayDataListLiveData = MutableLiveData<List<DayData>>()

    private val sleepDataList = mutableListOf<DayData>()
    val sleepDataListLiveData = MutableLiveData<List<DayData>>()


    private val stepDataList = mutableListOf<DayData>()
    val stepDataListLiveData = MutableLiveData<List<DayData>>()




    val setUserInfoResult = EventLiveData<Int>()


    val _otaUpdateStateFlow = MutableStateFlow(OtaUpdateState(0, 0))
    val otaUpdateStateFlow = _otaUpdateStateFlow.asStateFlow()

    var mService: IRemoteService? = null



     var bScan = false

    var isLoadingData = EventLiveData<Boolean>()

    var isLoadingSleepData = EventLiveData<Boolean>()
    var isLoadingStepData = EventLiveData<Boolean>()
    var isLoadingCurSportData = EventLiveData<Boolean>()
    var isLoadingHeartRateData = EventLiveData<Boolean>()

    var lastSynTime = EventLiveData<Long>()






    val stepDataChangeList = mutableListOf<StepData>()



    val heartBeatcount = EventLiveData<Int>()


    private var previousStepCount: Int = 0


    var toForeground = EventLiveData<Boolean>()


    var isClearMR = EventLiveData<Boolean>()

    init {
        changeTabIndex.value = -1
        val cacheBleDevice = GsonUtils.json2VO(CacheUtil.getCommonJson(AppCacheKeyEnum.bleDevice.name),BleDeviceItem::class.java)?:BleDeviceItem()
        bleDeviceItem.value = cacheBleDevice
        dayDataListLiveData.value = arrayListOf()
        isLoadingSleepData.value = false
        isLoadingStepData.value = false
        isLoadingCurSportData.value = false
        isLoadingHeartRateData.value = false

        isLoadingData.value = false

        lastSynTime.value =  CacheUtil.getLastSynTime(cacheBleDevice.bleDeviceAddress)?.toLong()?:0L

        userProfile.value = CacheUtil.getUser()?: getDefaultProfile()


        deviceInfo.value =  GsonUtils.json2VO(CacheUtil.getCommonJson(AppCacheKeyEnum.deviceInfo.name),DeviceInfo::class.java)?:DeviceInfo()
        heartBeatcount.value = 0

        toForeground.value = false

        isClearMR.value = false
    }

    fun startAndBindService(context: Context) {
        val gattServiceIntent = Intent(
            context,
            SampleBleService::class.java
        )

        context.startService(gattServiceIntent)
        context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }



    fun getConnectMac():String{
      return  homeAppViewModel.bleDeviceItem.value?.bleDeviceAddress?:""
    }
    fun callRemoteScanDevice(){
        bScan = true
        mService?.scanDevice(bScan)
    }


    fun callRemoteDisconnect(enable:Boolean = true) {
        if (mService != null) {
            try {
                LogInstance.i("callRemoteDisconnect")
                mService!!.disconnectBt(enable)
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }
    fun callRemoteGetDeviceBatery() {
        val result: Int
        if (mService != null) {
            try {
                result = mService!!.deviceBatery
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }

    fun callgetCurSportData() {
        val result: Int
        if (mService != null) {
            try {
                result = mService!!.curSportData
                isLoadingCurSportData.value = true
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }
    fun callRemoteSetHeartRateMode(enable: Boolean) {
        val result: Int
        if (mService != null) {
            try {
                result = mService!!.setHeartRateMode(enable, 60, 0)
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }

    fun callRemoteConnect(name: String, mac: String?) {
        if (mac.isNullOrEmpty()) {
            ToastSingleton.toastWarning("ble device mac address is not correctly!")
            return
        }
        if (mService != null) {
            try {
                macid.value = mac?:""
                mService!!.connectBt(name, mac)
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }
    fun callRemoteGetDeviceInfo() {
        val result: Int
        if (mService != null) {
            try {
                result = mService!!.deviceInfo
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }

    fun callRemoteGetData(type: Int, day: Int) {
        val result: Int
        if (mService != null) {
            try {
                result = mService!!.getDataByDay(type, day)
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }

    fun callRemoteOpenBlood(enable: Boolean) {
        val result: Int
        if (mService != null) {
            try {
                result = mService!!.setBloodPressureMode(enable)
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }


    fun callRemoteSetAutoHeartMode(enable: Boolean,startHour:Int,startMin:Int,endHour:Int,endMin:Int,interval:Int,duration:Int) {
        val result: Int
        if (mService != null) {
            try {
                result = mService!!.setAutoHeartMode(enable, startHour, startMin, endHour, endMin, interval, duration)
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }



     fun setDeviceName(name: String) {
        if (mService != null) {
            try {
                mService!!.setDeviceName(name)
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }




    fun callRemoteSetphoto(photoMode :Boolean = true) {
        val result: Int
        if (mService != null) {
            try {
                result = mService!!.setPhontMode(photoMode)
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }


    fun callRemoteSetOption(opt: BleClientOption): Int {
        var result = 0
        if (mService != null) {
            try {
                result = mService!!.setOption(opt)
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
        return result
    }


    fun callRemoteSetUserInfo(): Int {
        var result = 0
        if (mService != null) {
            try {
                result = mService!!.setUserInfo()
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
        return result
    }

    fun test(){

    }

    fun generateRandomMacAddress(): String {
        val random = Random()
        return (1..6).joinToString(":") {
            "%02x".format(random.nextInt(256))
        }
    }

    fun clearBleList(){
        _scanResult.value = emptyList()
    }
    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {


            LogInstance.i("Service connected")


            mService = IRemoteService.Stub.asInterface(service)
            try {

                mService?.registerCallback(mServiceCallback)




                val isConnected = callRemoteIsConnected()
                macid.value = callRemoteGetConnectedDevice()

                if (!isConnected) {


                } else {

                    val authrize = callRemoteIsAuthrize()
                    if (authrize == 200) {

                        macid.value = callRemoteGetConnectedDevice()
                    }
                }
            } catch (e: RemoteException) {

                e.printStackTrace()
            }
        }


        override fun onServiceDisconnected(name: ComponentName) {

            LogInstance.i("Service disconnected")
            mService = null
        }
    }


    private val mServiceCallback: IServiceCallback = object : IServiceCallback.Stub() {
        @Throws(RemoteException::class)
        override fun onConnectStateChanged(state: Int) {
            showToast("onConnectStateChanged", macid.value + " state " + state)

            _connectionState.value = state
        }


        override fun onScanCallback(
            deviceName: String?,
            deviceMacAddress: String?,
            rssi: Int,
            ver: String?,
            cid: String?,
            did: String?,
            bindFlag: String?,
            bindState: String?,
            additionalParam: String?,
        ) {


    if (ver.isNullOrBlank() || ver == "0000"){
        return
    }
    if (cid.isNullOrBlank()||cid == "0000"){
        return
    }
    if (did.isNullOrBlank()|| did == "0000"){
        return
    }
    if (bindFlag.isNullOrBlank()||bindFlag == "0000"){
        return
    }
    if (bindState.isNullOrBlank()|| bindFlag == "00"){
        return
    }
            if (!bScan || deviceName.isNullOrBlank()) return

            val newList = _scanResult.value.toMutableList()

            var bExist = false
            val index =
                newList.indexOfFirst { it.bleDeviceAddress.equals(deviceMacAddress, ignoreCase = true) }
            if (index != -1) {
                bExist = true
                newList[index].rssi = rssi
                newList[index].bleDeviceName = deviceName
            }

            if (!bExist) {
                val newItem = BleDeviceItem(
                    deviceName ?: "",
                    deviceMacAddress ?: "",
                    "",
                    "",
                    rssi,"",did
                )
                newList.add(newItem)
            }

            newList.sortWith(ComparatorBleDeviceItem())
            _scanResult.value = newList
        }

        @Throws(RemoteException::class)
        override fun onSetNotify(result: Int) {
            showToast("onSetNotify", result.toString())
        }

        @Throws(RemoteException::class)
        override fun onSetUserInfo(result: Int) {
            showToast("onSetUserInfo", "" + result)
            handler.post {
                setUserInfoResult.postValue(result)
            }

        }

        @Throws(RemoteException::class)
        override fun onAuthSdkResult(errorCode: Int) {
            showToast("onAuthSdkResult", errorCode.toString() + "")
        }

        @Throws(RemoteException::class)
        override fun onGetDeviceTime(result: Int, time: String) {
            showToast("onGetDeviceTime", time)
        }

        @Throws(RemoteException::class)
        override fun onSetDeviceTime(arg0: Int) {
            showToast("onSetDeviceTime", arg0.toString() + "")
        }

        @Throws(RemoteException::class)
        override fun onSetDeviceInfo(arg0: Int) {
            showToast("onSetDeviceInfo", arg0.toString() + "")
        }

        @Throws(RemoteException::class)
        override fun onAuthDeviceResult(arg0: Int) {
            showToast("onAuthDeviceResult", arg0.toString() + "")
        }

        @Throws(RemoteException::class)
        override fun onSetAlarm(arg0: Int) {
            showToast("onSetAlarm", arg0.toString() + "")
        }

        @Throws(RemoteException::class)
        override fun onSendVibrationSignal(arg0: Int) {
            showToast("onSendVibrationSignal", "result:$arg0")
        }

        @Throws(RemoteException::class)
        override fun onGetDeviceBatery(arg0: Int, arg1: Int) {
            showToast("onGetDeviceBatery", "batery:$arg0, statu $arg1")
            handler.post {
                batteryStatus.value = Pair(arg0, arg1)
            }

        }

        @Throws(RemoteException::class)
        override fun onSetDeviceMode(arg0: Int) {
            showToast("onSetDeviceMode", "result:$arg0")
        }

        @Throws(RemoteException::class)
        override fun onSetHourFormat(arg0: Int) {
            showToast("onSetHourFormat ", "result:$arg0")
        }

        @Throws(RemoteException::class)
        override fun setAutoHeartMode(arg0: Int) {
            showToast("setAutoHeartMode ", "result:$arg0")
        }

        @Throws(RemoteException::class)
        override fun onGetCurSportData(
            type: Int, timestamp: Long, step: Int, distance: Int,
            cal: Int, cursleeptime: Int, totalrunningtime: Int, steptime: Int
        ) {
            val date = Date(timestamp * 1000)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val time = sdf.format(date)
            showToast(
                "onGetCurSportData",
                "type : $type , time :$time , step: $step, distance :$distance, cal :$cal, cursleeptime :$cursleeptime, totalrunningtime:$totalrunningtime  steptime:$steptime"
            )

            val updatedSportData = SportData(
                type = type,
                timestamp = timestamp,
                step = step,
                distance = distance,
                cal = cal,
                cursleeptime = cursleeptime,
                totalrunningtime = totalrunningtime,
                steptime = steptime
            )

            handler.post{
                isLoadingCurSportData.value = false
                if (updatedSportData.type == 0){
                    sportData.value = updatedSportData
                }

            }

        }

        @Throws(RemoteException::class)
        override fun onGetSenserData(
            result: Int,
            timestamp: Long,
            heartrate: Int,
            sleepstatu: Int
        ) {
            val date = Date(timestamp * 1000)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val time = sdf.format(date)
            showToast(
                "onGetSenserData",
                "result: $result,time:$time,heartrate:$heartrate,sleepstatu:$sleepstatu"
            )
            handler.post{
                val  data = SensorData(
                result = result,
                timestamp = timestamp,
                heartrate = heartrate,
                sleepStatus = sleepstatu
                )
                sensorData.value = data
                if (heartrate>0){
                    homeAppViewModel.allHeartRates.add(data)
                }

            }

        }

        @Throws(RemoteException::class)
        override fun onGetDataByDay(type: Int, timestamp: Long, step: Int, heartrate: Int) {
            val date = Date(timestamp * 1000)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val recorddate = sdf.format(date)
            showToast(
                "onGetDataByDay",
                "type:$type,time::$recorddate,step:$step,heartrate:$heartrate"
            )
            if (type == 2) {
                sleepcount++
            }


            handler.post {
                isLoadingData.value = true
                CacheUtil.setLastSynTime(getConnectMac(),"${System.currentTimeMillis()}")
                lastSynTime.value = System.currentTimeMillis()
                when (type) {

                    1 -> {

                        homeAppViewModel.isLoadingStepData.value = true
                        val dayData = DayData(type, timestamp, step, heartrate)
                        if (stepDataList.none { it.timestamp == dayData.timestamp }) {
                            stepDataList.add(dayData)
                        }

                        DataFetcherStep.addData(DayData(type, timestamp, step, heartrate))

                        if (step>0){
                            val stepData = StepData(timestamp = timestamp,value = step)
                            DatabaseUtils.insertStepData(stepData = stepData)




                            val calData = CalData(timestamp = timestamp,value =   getCal(step))
                            DatabaseUtils.insertCalData(calData = calData)
                        }

                    }

                    2 -> {
                        homeAppViewModel.isLoadingSleepData.value = true

                        val dayData = DayData(type, timestamp, step, heartrate)
                        if (sleepDataList.none { it.timestamp == dayData.timestamp }) {
                            sleepDataList.add(dayData)
                        }

                        DataFetcherSleep.addData(DayData(type, timestamp, step, heartrate))



                    }

                    3 -> {
                        homeAppViewModel.isLoadingHeartRateData.value = true
                        val dayData = DayData(type, timestamp, step, heartrate)
                        if (dayDataList.none { it.timestamp == dayData.timestamp }) {
                            dayDataList.add(dayData)
                        }


                        DataFetcherHeartBeat.addData(DayData(type, timestamp, step, heartrate))

                        if (heartrate>0){
                            val heartRateData = HeartRateData(timestamp = timestamp,value  = heartrate)
                            DatabaseUtils.insertHeartRateData(heartRateData = heartRateData)
                        }

                    }
                }
            }

        }

        @Throws(RemoteException::class)
        override fun onGetDataByDayEnd(type: Int, timestamp: Long) {
            val date = Date(timestamp * 1000)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val recorddate = sdf.format(date)
            showToast("onGetDataByDayEnd", "$type time:$recorddate,sleepcount:$sleepcount")
            sleepcount = 0

            handler.post {
                isLoadingData.value = false
                when (type) {

                    1,2 -> {
                        stepDataListLiveData.value = stepDataList.toList()
                        stepDataList.clear()



                        if (sleepDataList.isNotEmpty()){
                            val sleepList = sleepDataList.map {
                                SleepData(timestamp = it.timestamp, value = it.step)
                            }
                            val saveList = MRRingManager.conversionSleepLineList(sleepList)
                            saveList.forEach { data ->
                                DatabaseUtils.insertSleepData(sleepData = data)
                            }
                        }

                        sleepDataListLiveData.value = sleepDataList.toList()
                        sleepDataList.clear()




                        DataFetcherStep.onGetDataByDayEnd()
                        DataFetcherSleep.onGetDataByDayEnd()

                        homeAppViewModel.isLoadingSleepData.value = false
                        homeAppViewModel.isLoadingStepData.value = false
                    }

                    3 -> {

                        handler.postDelayed({
                            dayDataListLiveData.value = dayDataList.toList()
                            dayDataList.clear()
                            homeAppViewModel.isLoadingHeartRateData.value = false


                        },1000)

                        DataFetcherHeartBeat.onGetDataByDayEnd()
                    }
                }

            }

            uploadDataModel.checkUploadData()




        }

        @Throws(RemoteException::class)
        override fun onSetPhontMode(arg0: Int) {
            showToast("onSetPhontMode", "result:$arg0")
        }

        @Throws(RemoteException::class)
        override fun onSetSleepTime(arg0: Int) {
            showToast("onSetSleepTime", "result:$arg0")
        }

        @Throws(RemoteException::class)
        override fun onSetIdleTime(arg0: Int) {
            showToast("onSetIdleTime", "result:$arg0")
        }

        @Throws(RemoteException::class)
        override fun onGetDeviceInfo(
            version: Int, macaddress: String, vendorCode: String,
            productCode: String, result: Int
        ) {
            showToast(
                "onGetDeviceInfo",
                "version :$version,macaddress : $macaddress,vendorCode : $vendorCode,productCode :$productCode , CRCresult :$result"
            )


            val fetchedDeviceInfo = DeviceInfo(version, macaddress, vendorCode, productCode, result)
            handler.post {
                deviceInfo.value = fetchedDeviceInfo
                CacheUtil.setCommonJson(AppCacheKeyEnum.deviceInfo.name,GsonUtils.vo2Json(fetchedDeviceInfo))
            }

        }

        @Throws(RemoteException::class)
        override fun onGetDeviceAction(type: Int) {
            showToast("onGetDeviceAction", "type:$type")
            if (type == 5) {






            }
        }

        @Throws(RemoteException::class)
        override fun onGetBandFunction(result: Int, results: BooleanArray) {
            showToast("onGetBandFunction", "result : " + result + ", results :" + results.size)
            var function = ""
            for (i in results.indices) {
                function += (i + 1).toString() + "=" + results[i] + " "
            }
            showToast("onGetBandFunction", function)


        }

        @Throws(RemoteException::class)
        override fun onSetLanguage(arg0: Int) {
            showToast("onSetLanguage", "result:$arg0")
        }

        @Throws(RemoteException::class)
        override fun onSendWeather(arg0: Int) {
            showToast("onSendWeather", "result:$arg0")
        }

        @Throws(RemoteException::class)
        override fun onSetAntiLost(arg0: Int) {
            showToast("onSetAntiLost", "result:$arg0")
        }

        @Throws(RemoteException::class)
        override fun onReceiveSensorData(heartrate: Int, systolicPressure: Int, diastolicPressure: Int, oxygen: Int, fatigueValue: Int) {


            handler.post {


                if (oxygen>0){
                    val oxygenData =  OxygenSensorData(heartrate, systolicPressure, diastolicPressure, oxygen, fatigueValue)
                    _oxygenSensorData.value = oxygenData


                    callRemoteOpenBlood(false)
                    val timestamp =  System.currentTimeMillis()/1000
                    val newOxygenData = OxygenData(timestamp =timestamp, value = oxygenData.oxygen ,fatigueLevel = oxygenData.fatigueValue)
                    DatabaseUtils.insertOxygenData(oxygenData = newOxygenData){}
                }


            }

        }

        @Throws(RemoteException::class)
        override fun onSetBloodPressureMode(arg0: Int) {
            showToast("onSetBloodPressureMode", "result:$arg0")
        }

        @Throws(RemoteException::class)
        override fun onGetMultipleSportData(flag: Int, recorddate: String, mode: Int, value: Int) {

            showToast(
                "onGetMultipleSportData",
                "flag:$flag , mode :$mode recorddate:$recorddate , value :$value"
            )
        }

        @Throws(RemoteException::class)
        override fun onSetGoalStep(result: Int) {
            showToast("onSetGoalStep", "result:$result")
        }

        @Throws(RemoteException::class)
        override fun onSetDeviceHeartRateArea(result: Int) {
            showToast("onSetDeviceHeartRateArea", "result:$result")
        }

        @Throws(RemoteException::class)
        override fun onSensorStateChange(type: Int, state: Int) {
            showToast("onSensorStateChange", "type:$type , state : $state")
            handler.post {
                sensorState.value = SensorStatus(type, state)
            }

        }

        @Throws(RemoteException::class)
        override fun onReadCurrentSportData(
            mode: Int, time: String, step: Int,
            cal: Int
        ) {
            showToast(
                "onReadCurrentSportData",
                "mode:$mode , time : $time , step : $step cal :$cal"
            )
        }

        @Throws(RemoteException::class)
        override fun onGetOtaInfo(isUpdate: Boolean, version: String, path: String) {
            showToast("onGetOtaInfo", "isUpdate $isUpdate version $version path $path")
            handler.post {
                _otaInfo.value = OtaInfo(isUpdate, version, path)
            }
        }

        @Throws(RemoteException::class)
        override fun onGetOtaUpdate(step: Int, progress: Int) {
            showToast("onGetOtaUpdate", "step $step progress $progress")
            handler.post {
                _otaUpdateStateFlow.value = OtaUpdateState(step, progress)
            }
        }

        @Throws(RemoteException::class)
        override fun onSetDeviceCode(result: Int) {
            showToast("onSetDeviceCode", "result $result")
        }

        @Throws(RemoteException::class)
        override fun onGetDeviceCode(bytes: ByteArray) {
            showToast("onGetDeviceCode", "bytes " +  HexUtil.encodeHexStr(bytes))

        }

        @Throws(RemoteException::class)
        override fun onCharacteristicChanged(uuid: String, bytes: ByteArray) {
            showToast("onCharacteristicChanged uuid", uuid + " " + HexUtil.encodeHexStr(bytes))


            handler.post {
                _uuid.value = uuid
            }

        }

        @Throws(RemoteException::class)
        override fun onCharacteristicWrite(uuid: String, bytes: ByteArray, status: Int) {

            showToast("onCharacteristicWrite uuid", uuid + " " + HexUtil.encodeHexStr(bytes))
        }

        @Throws(RemoteException::class)
        override fun onSetEcgMode(result: Int, state: Int) {
            showToast("onSetEcgMode", "result $result state $state")
        }

        @Throws(RemoteException::class)
        override fun onGetEcgValue(state: Int, values: IntArray) {
            showToast("onGetEcgValue", "state " + state + " value " + values.size)

        }

        @Throws(RemoteException::class)
        override fun onGetEcgHistory(timestamp: Long, number: Int) {
            showToast("onGetEcgHistory", "timestamp $timestamp number $number")
        }

        @Throws(RemoteException::class)
        override fun onGetEcgStartEnd(id: Int, state: Int, timestamp: Long) {
            showToast("onGetEcgStartEnd", "id $id state $state timestamp $timestamp")
        }

        @Throws(RemoteException::class)
        override fun onGetEcgHistoryData(id: Int, values: IntArray) {
            showToast("onGetEcgHistoryData", "id " + id + " values " + values.size)
        }

        @Throws(RemoteException::class)
        override fun onSetDeviceName(result: Int) {
            showToast("onSetDeviceName", "result $result")
        }

        @Throws(RemoteException::class)
        override fun onGetDeviceRssi(rssi: Int) {
            showToast("onGetDeviceRssi", "rssi $rssi")
        }

        @Throws(RemoteException::class)
        override fun onSetReminder(result: Int) {
            showToast("onSetReminder", "result $result")
        }

        @Throws(RemoteException::class)
        override fun onSetReminderText(result: Int) {
            showToast("onSetReminderText", "result $result")
        }

        @Throws(RemoteException::class)
        override fun onSetBPAdjust(result: Int) {
            showToast("onSetBPAdjust", "result $result")
        }

        @Throws(RemoteException::class)
        override fun onSetTemperatureMode(result: Int) {
            showToast("onSetTemperatureMode", "result $result")
        }

        @Throws(RemoteException::class)
        override fun onGetTemperatureData(surfaceTemp: Int, bodyTemp: Int) {
            showToast("onGetTemperatureData", "surfaceTemp $surfaceTemp, bodyTemp$bodyTemp")
        }

        @Throws(RemoteException::class)
        override fun onTemperatureModeChange(enable: Int) {
            showToast("onTemperatureModeChange", "enable $enable")
        }

        @Throws(RemoteException::class)
        override fun onGetDeviceDial(
            productType: String,
            productId: String,
            watchWidth: Int,
            watchHeight: Int,
            unitWidth: Int,
            colorMode: Int,
            isCustom: Int,
            dialId: Int,
            reviewWatchWidth: Int,
            reviewWatchHeight: Int,
            shapeType: Int
        ) {
            showToast(
                "onGetDeviceDial",
                "$productType,$productId,$watchWidth,$watchHeight,$unitWidth,$colorMode,$isCustom,$reviewWatchWidth,$reviewWatchHeight,$shapeType"
            )

        }

        @Throws(RemoteException::class)
        override fun onSetDeviceDialState() {
            showToast("onSetDeviceDialState", "")
        }

        @Throws(RemoteException::class)
        override fun onSetDeviceWallpaperState() {
            showToast("onSetDeviceWallpaperState", "")
        }

        @Throws(RemoteException::class)
        override fun onEditDeviceDialCustom() {
            showToast("onEditDeviceDialCustom", "")
        }

        @Throws(RemoteException::class)
        override fun onGetDeviceDialCustom(
            timePos: Int,
            timeAboveContent: Int,
            timeBelowContent: Int,
            fontColorType: Int
        ) {
            showToast(
                "onGetDeviceDialCustom",
                "$timePos,$timeAboveContent,$timeBelowContent,$fontColorType"
            )
        }

        @Throws(RemoteException::class)
        override fun onSetFemaleReminder() {
            showToast("onSetFemaleReminder", "")
        }

        @Throws(RemoteException::class)
        override fun onNotifyClassicBtName(deviceBtName: String) {
            showToast("onNotifyClassicBtName", deviceBtName)
            mClassicBtDeviceBtName = deviceBtName
        }

        @Throws(RemoteException::class)
        override fun onNotifyClassicBtInfo(
            btState: Int,
            pareState: Int,
            deviceMac: String,
            phoneMac: String
        ) {
            var deviceMac = deviceMac
            showToast("onNotifyClassicBtInfo", "$btState,$pareState,$deviceMac,$phoneMac")
            mClassicBtDeviceBtName = ""
            if (btState == 1) {

                var bBonded = false

                val adapter = BluetoothAdapter.getDefaultAdapter()
                val devices = if (ActivityCompat.checkSelfPermission(
                        ActivityUtils.getTopActivity(),
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    null
                } else {
                    adapter.bondedDevices
                }

                if (devices != null && devices.size > 0) {
                    val iterator: Iterator<*> = devices.iterator()
                    while (iterator.hasNext()) {
                        val bluetoothDevice = iterator.next() as BluetoothDevice

                        if (bluetoothDevice.address.equals(deviceMac, ignoreCase = true)) {
                            bBonded = true
                        }
                    }
                }
                if (pareState != 1) {
                    val regex = "(.{2})"
                    deviceMac = deviceMac.replace(regex.toRegex(), "$1:")
                    deviceMac = deviceMac.substring(0, deviceMac.length - 1)
                    val bd = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceMac)
                    val intent = Intent(CommonAttributes.ACTION_NOTIFY_CLASSIC_BT_CREATE_BOND)
                    intent.putExtra("device", bd)
                    sendBroadcastWithPackage(intent)
                }
            }
        }

        @Throws(RemoteException::class)
        override fun onNotifyContactCrc(contactCrc: String) {
            showToast("onNotifyContactCrc", contactCrc)
        }

        @Throws(RemoteException::class)
        override fun onNotifyAppId(appId: String) {
            showToast("onNotifyAppId", appId)
        }

        @Throws(RemoteException::class)
        override fun onGetPhoneVolume() {
            showToast("onGetPhoneVolume", "")
        }

        @Throws(RemoteException::class)
        override fun onNotifyBindedInfo(action: Int, state: Int) {
            showToast("onNotifyBindedInfo", "$action, $state")
            when (action) {
                CommonAttributes.BOND_ACTION_INIT -> if (state == CommonAttributes.BOND_STATE_NO) {
                    try {
                        mService!!.setBindedInfo(
                            CommonAttributes.BOND_ACTION_APP_START,
                            CommonAttributes.BOND_STATE_NO,
                            CommonAttributes.OS_TYPE
                        )
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                } else if (state == CommonAttributes.BOND_STATE_YES) {
                }

                CommonAttributes.BOND_ACTION_ACK -> try {
                    mService!!.setBindedInfo(
                        CommonAttributes.BOND_ACTION_SUCCESS,
                        CommonAttributes.BOND_STATE_NO,
                        CommonAttributes.OS_TYPE
                    )
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

                CommonAttributes.BOND_ACTION_ACK_CANCEL -> {


                    val intent = Intent(CommonAttributes.ACTION_NOTIFY_UNBOND_ACK)
                    sendBroadcastWithPackage(intent)
                }

                CommonAttributes.BOND_ACTION_SUCCESS -> {}
                CommonAttributes.BOND_ACTION_UNBOND_ACK -> {

                }

                else -> {}
            }
        }

        @Throws(RemoteException::class)
        override fun onGetDeviceState(
            bHandOfflight: Boolean,
            bVibrate: Boolean,
            bNoDisturb: Boolean
        ) {
            showToast("onGetDeviceState", "$bHandOfflight, $bVibrate, $bNoDisturb")
        }

        @Throws(RemoteException::class)
        override fun onNotifyECardNeedUpdate(bytes: ByteArray) {
            val alItem = ArrayList<ECardInfoItem>()
            val item = ECardInfoItem()
            item.ecardId = 1
            item.name = "111"
            item.content = "111..."
            alItem.add(item)

            val info = ECardInfo(alItem)
            mService!!.setECardInfoContent(info)
        }

        @Throws(RemoteException::class)
        override fun onNotifySmsRspNeedUpdate(bytes: ByteArray) {
            val alItem = ArrayList<SmsRspInfoItem>()
            var item = SmsRspInfoItem()
            item.smsRspId = 1
            item.content = "111"
            alItem.add(item)
            item = SmsRspInfoItem()
            item.smsRspId = 2
            item.content = "222"
            alItem.add(item)
            item = SmsRspInfoItem()
            item.smsRspId = 3
            item.content = "333"
            alItem.add(item)
            val info = SmsRspInfo(alItem)
            mService!!.setSmsRspInfoContent(info)
        }

        @Throws(RemoteException::class)
        override fun onNotifySmsRspSend(id: Int, phoneNum: String) {


        }

        @Throws(RemoteException::class)
        override fun onGetChatgptAction(type: Int) {

        }

        @Throws(RemoteException::class)
        override fun onGetFactoryTestData(bytes: ByteArray) {

        }

        @Throws(RemoteException::class)
        override fun onNotifyDialJsonContent(content: String) {

        }

        @Throws(RemoteException::class)
        override fun onGetSportSteps(steps: Int) {
        }
    }


    private fun showToast(tag: String, msg: String) {
        LogInstance.i(tag, msg)

    }

    private var mClassicBtDeviceBtName = ""
    val macid = MutableLiveData<String>()




    private fun callRemoteIsAuthrize(): Int {
        var isAuthrize = 0

        if (mService != null) {
            try {

                isAuthrize = mService!!.isAuthrize
            } catch (e: RemoteException) {

                e.printStackTrace()



            }
        } else {


        }
        return isAuthrize
    }


    private fun callRemoteGetConnectedDevice(): String? {
        var deviceMac: String? = ""

        if (mService != null) {
            try {

                deviceMac = mService!!.connectedDevice
            } catch (e: RemoteException) {

                e.printStackTrace()


            }
        } else {


        }
        return deviceMac
    }


    private fun callRemoteIsConnected(): Boolean {
        var isConnected = false

        if (mService != null) {
            try {

                isConnected = mService!!.isConnectBt
            } catch (e: RemoteException) {

                e.printStackTrace()


            }
        } else {


        }
        return isConnected
    }
    fun ByteArray.toHexString(): String =
        joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }






    fun startFileOta( mode:Int =1,  filePath:String = ""){
        val result: Int
        if (mService != null) {
            try {

                result = mService!!.startFileOta(mode,filePath)
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }


    fun callRemoteSetDeviceMode(mode: Int) {
        val result: Int
        if (mService != null) {
            try {
                result = mService!!.setDeviceMode(mode)
            } catch (e: RemoteException) {
                e.printStackTrace()
                ToastSingleton.toastWarning("Remote call error!")
            }
        } else {
            ToastSingleton.toastWarning("Service is not available yet!")
        }
    }

    private var sleepcount = 0

















    fun sendBroadcastWithPackage(intent: Intent) {

    }



    fun getBleName():String{
        val deviceAddress = homeAppViewModel.bleDeviceItem.value?.bleDeviceAddress?:""
        val bluetoothDevice = BluetoothAdapter.getDefaultAdapter()?.getRemoteDevice(deviceAddress)
        val name = if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ""
        } else {
            bluetoothDevice?.name?:""
        }

        val pairedDevices: Set<BluetoothDevice>? =  BluetoothAdapter.getDefaultAdapter()?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceUuids = device.uuids
            deviceUuids?.forEach { uuid ->
                LogInstance.d("BluetoothUUID", "UUID: ${uuid.uuid}")
            }
        }
        LogInstance.i("bluetoothDevice name:${name}")
        return  name
    }


    fun getUserId():String{
        return userProfile.value?.user_id?:""
    }


}