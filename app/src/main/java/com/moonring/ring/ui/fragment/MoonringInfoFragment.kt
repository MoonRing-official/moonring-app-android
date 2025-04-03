package com.moonring.ring.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.module.common.base.BaseBarFragment
import com.module.common.support.log.LogInstance
import com.module.common.util.GsonUtils
import com.module.common.weight.toast.ToastSingleton
import com.moonring.ring.BuildConfig
import com.moonring.ring.R
import com.moonring.ring.bean.DayData
import com.moonring.ring.bean.HeartRateStats
import com.moonring.ring.bean.SleepDataStats
import com.moonring.ring.databinding.FragmentMooringInfoBinding
import com.moonring.ring.enums.ConnectionState
import com.moonring.ring.enums.GetDataByDay
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.moonring.DataFetcherHeartBeat
import com.moonring.ring.support.moonring.DataFetcherSleep
import com.moonring.ring.support.moonring.DataFetcherStep
import com.moonring.ring.support.moonring.LoadingManager
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.ui.dialogfragment.DialogPermissionBottomSheet
import com.moonring.ring.uploadDataModel
import com.moonring.ring.utils.bluePermission
import com.moonring.ring.utils.calculateDistanceAndCalories
import com.moonring.ring.utils.getDayStartEnd
import com.moonring.ring.utils.getYesterdayStartTimestamp
import com.moonring.ring.utils.isDaysDifferenceValid
import com.moonring.ring.utils.timestampToDateYMD
import com.moonring.ring.viewmodule.state.MoonringInfoModel
import com.moonring.ring.viewmodule.state.RingApiModel
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateAction
import java.util.Calendar


class MoonringInfoFragment : BaseBarFragment<MoonringInfoModel, FragmentMooringInfoBinding>() {

    private val click = ProxyClick()
    val  ringApiModel: RingApiModel by activityViewModels()

    private val loadingManager by lazy {
        LoadingManager()
    }
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = click
        mDatabind.viewModel = mViewModel





        loadingManager.configureRotationVerseAnimator(mDatabind.ivRefresh)






    }

    override fun lazyLoadData() {
        super.lazyLoadData()



    }



    fun synMRData(){
        mViewModel.viewModelScope.launch {
            kotlin.runCatching {
                val yes = getYesterdayStartTimestamp()

                val (start, end) = getDayStartEnd(-1)

                val stepRecords = DatabaseUtils.fetchStepsData(fromTimestamp = start, toTimestamp = end)
                val sleepRecords = DatabaseUtils.fetchSleepData(fromTimestamp = start, toTimestamp = end)
                val hasSteps = stepRecords.isNotEmpty()
                val hasSleeps = sleepRecords.isNotEmpty()

                LogInstance.i("hasSteps:${hasSteps}  hasSleeps:${hasSleeps}")


                if (hasSteps||hasSleeps) {

                    val latestDate = timestampToDateYMD(yes)
                    val lastSyncMillis = homeAppViewModel.lastSynTime.value ?: 0L
                    val lastSyncDate = timestampToDateYMD(lastSyncMillis / 1000)
                    LogInstance.i("hasSteps - latestDate: $latestDate, lastSyncMillis: $lastSyncMillis, lastSyncDate: $lastSyncDate")
                    if (latestDate == lastSyncDate) {
                        homeAppViewModel.callRemoteGetData(GetDataByDay.Step_Sleep.value, 0)
                        homeAppViewModel.callRemoteGetData(GetDataByDay.Step_Sleep.value, 1)
                    } else {
                        homeAppViewModel.callRemoteGetData(GetDataByDay.Step_Sleep.value, 0)
                    }
                } else {
                    DataFetcherSleep.startFetching(GetDataByDay.Step_Sleep.value)
                }



                val heartRecords = DatabaseUtils.fetchHeartRateData(fromTimestamp = start, toTimestamp = end)
                val hasHearts = heartRecords.isNotEmpty()

                LogInstance.i("hasHearts:${hasHearts}")
                if (hasHearts) {
                    val latestDate = timestampToDateYMD(yes)
                    val lastSyncMillis = homeAppViewModel.lastSynTime.value ?: 0L
                    val lastSyncDate = timestampToDateYMD(lastSyncMillis / 1000)
                    LogInstance.i("hasHearts - latestDate: $latestDate, lastSyncMillis: $lastSyncMillis, lastSyncDate: $lastSyncDate")
                    if (latestDate == lastSyncDate) {
                        homeAppViewModel.callRemoteGetData(GetDataByDay.HeartBeat.value, 0)
                        homeAppViewModel.callRemoteGetData(GetDataByDay.HeartBeat.value, 1)
                    } else {
                        homeAppViewModel.callRemoteGetData(GetDataByDay.HeartBeat.value, 0)
                    }
                }else {
                    DataFetcherHeartBeat.startFetching(GetDataByDay.HeartBeat.value)
                }
            }.onSuccess {
                LogInstance.i("synMRData - onSuccess")

            }.onFailure {
                LogInstance.i("synMRData - onFailure")
            }


        }

    }




    override fun createObserver() {
        super.createObserver()
        lifecycleScope.launchWhenStarted {
            homeAppViewModel.connectionState.collect { connectionState ->
                LogInstance.i("connectionState result: $connectionState")
                if (homeAppViewModel.isClearMR.value == true){
                    return@collect
                }
                mViewModel.connectionState.set(connectionState)
                when (connectionState) {
                    2 -> {
                        mViewModel.afterConnect()
                        synMRData()

                    }
                    1 -> {


                    }
                    0 -> {

                        mViewModel.initDefaultData()



                    }
                    else -> {

                    }
                }
            }
        }
        homeAppViewModel.bleDeviceItem.observe(viewLifecycleOwner){
            mViewModel.bleDeviceItem.value = it
        }
        homeAppViewModel.macid.observe(viewLifecycleOwner){
            mViewModel.bleDeviceAddress.set(it)
        }
        lifecycleScope.launchWhenStarted {
            homeAppViewModel.oxygenSensorData.collect { data ->
                mViewModel.oxygenSensorData.value = data
            }
        }
        lifecycleScope.launchWhenStarted {
            homeAppViewModel.otaInfo.collect { otaInfo ->
                mViewModel.otaInfo.value= otaInfo
            }
        }

        homeAppViewModel.batteryStatus.observe(viewLifecycleOwner){
                (batteryLevel, status) ->

            mViewModel.batteryLevel.set(batteryLevel)
            mViewModel.batteryStatus.set(status)


        }

        homeAppViewModel.sportData.observe(viewLifecycleOwner) { dataMap ->

            if (dataMap.type == 0){
                val pair = calculateDistanceAndCalories(dataMap.step)
                pair?.let {
                    dataMap.distance = it.first.toInt()
                    dataMap.cal = it.second.toInt()
                }


                mViewModel.sportData.value = dataMap

            }

            else if (dataMap.type == 1){

                mViewModel.sportRunData.value = dataMap
            }


        }

        homeAppViewModel.deviceInfo.observe(viewLifecycleOwner) { deviceInfo ->

            mViewModel.deviceInfo.value = deviceInfo
        }

        homeAppViewModel.sensorState.observe(viewLifecycleOwner) { sensorStatus ->

            if (sensorStatus.type == 1 ){
                mViewModel.sensorState.value = sensorStatus


            }else if (sensorStatus.type == 2 ){


            }

        }


        homeAppViewModel.stepDataListLiveData.observe(viewLifecycleOwner){ stepDataList->



            if (isDaysDifferenceValid(stepDataList,0)){

                mViewModel.dayData.value = stepDataList

                mViewModel.calMasStepTime()
            }


        }


        homeAppViewModel.dayDataListLiveData.observe(viewLifecycleOwner) { dayDataList ->


            val allHeartRatesDataList = homeAppViewModel.allHeartRates.map { sensor ->
                DayData(
                    type = sensor.result,
                    timestamp = sensor.timestamp,
                    step = 0,
                    heartrate = sensor.heartrate
                )
            }


            val newDayDataList = dayDataList.filter { it.heartrate>0}
            val combinedList = (newDayDataList + allHeartRatesDataList).filter { it.heartrate > 0 }

            val minHeartRate = combinedList.minByOrNull { it.heartrate }?.heartrate ?: 0
            val maxHeartRate = combinedList.maxByOrNull { it.heartrate }?.heartrate ?: 0
            val averageHeartRate = combinedList.map { it.heartrate }.average().toInt()
            val heartRate = newDayDataList.lastOrNull()?.heartrate?:0

            val timestamp = newDayDataList.lastOrNull()?.timestamp?:0



            mViewModel.heartRateStats.value = HeartRateStats(heartRate,minHeartRate, maxHeartRate, averageHeartRate,timestamp)
        }

        homeAppViewModel.sleepDataListLiveData.observe(viewLifecycleOwner) { sleepData ->
            setSleep(sleepData)
        }

        DataFetcherStep.allStepDataListLiveData.observe(this){
                sleepData ->

            setSleep(sleepData.sortedBy { it.timestamp })
        }

        homeAppViewModel.userProfile.observe(viewLifecycleOwner){
            mViewModel.userProfile.value = it
        }

        homeAppViewModel.isLoadingData.observe(viewLifecycleOwner){
            LogInstance.i("m:${it}")

            mViewModel.isLoadingData.value = it
            if (it){
                loadingManager.startRotation()
            }else{
                loadingManager.stopRotation()
            }
        }

        homeAppViewModel.toForeground.observe(viewLifecycleOwner){
            LogInstance.i("toForeground:${it}")

            if (it){

                mViewModel.checkIsSameDay()

                if ( homeAppViewModel.connectionState.value == ConnectionState.Connected.value){
                    synMRData()
                }else{

                    val macAddress = homeAppViewModel.ringInfo.value?.mac_address?:""
                    val name =  homeAppViewModel.bleDeviceItem.value?.bleDeviceName?:""
                    LogInstance.i("toForeground macAddress:${macAddress} name:${name}")

                }
            }
        }


    }

    fun setSleep(sleepData:List<DayData>){

        val sleepStats = SleepDataStats()

        var prevRecord = sleepData.firstOrNull() ?: DayData(0, 0, 0, 0)

        for (record in sleepData) {
            val interval = (record.timestamp - prevRecord.timestamp).toInt()


            if (interval == 60) {
                when (prevRecord.step) {
                    99 -> sleepStats.deepSleepTime += interval
                    40 -> sleepStats.lightSleepTime += interval
                }
                prevRecord = record
                continue
            }


            if (interval > 60) {
                sleepStats.awakeTime += interval
                sleepStats.awakeTime -= 60


                if (sleepStats.deepSleepTime > 0 && prevRecord.step == 99) {
                    sleepStats.deepSleepTime += 60
                }
                if (sleepStats.lightSleepTime > 0 && prevRecord.step == 40) {
                    sleepStats.lightSleepTime += 60
                }
                prevRecord = record
                continue
            }
        }


        if (sleepStats.deepSleepTime > 0 && prevRecord.step == 99) {
            sleepStats.deepSleepTime += 60
        }
        if (sleepStats.lightSleepTime > 0 && prevRecord.step == 40) {
            sleepStats.lightSleepTime += 60
        }


        sleepStats.totalSleepTime = sleepStats.deepSleepTime + sleepStats.lightSleepTime

        sleepStats.timestamp = sleepData.lastOrNull()?.timestamp?:0
        LogInstance.i("sleepStats:$${GsonUtils.vo2Json(sleepStats)}")


        mViewModel.sleepDataStats.value = sleepStats
    }


    override fun onResume() {
        super.onResume()

        mViewModel.checkIsSameDay()
    }

    override fun toolbarBackVisity(): Boolean {
        return false
    }
    inner class ProxyClick {
        fun test() {



        }


        fun synMoonringData(){
            if (homeAppViewModel.isLoadingData.value==false){
                synMRData()
                homeAppViewModel.isLoadingData.value  = true
            }

        }

        fun pair(){

            if (!XXPermissions.isGranted(requireContext(), Permission.ACCESS_FINE_LOCATION,
                    Permission.BLUETOOTH_SCAN,
                    Permission.BLUETOOTH_CONNECT,
                    Permission.BLUETOOTH_ADVERTISE)) {


                val map: Map<String, String> = mapOf(
                    "title" to getString(R.string.permission_title),
                    "content" to getString(R.string.permission_content),
                    "confirm" to "Accept",
                    "cancel" to "Reject"
                )

                val dialogFragment = DialogPermissionBottomSheet(
                    map,
                    onItemAcceptClick = {
                        LogInstance.i("onItemAcceptClick")
                        bluePermission( {
                            nav().navigateAction(R.id.action_mainfragment_to_searchMoonRingFragment)
                        })
                    },
                    onItemRejectClick = {
                        LogInstance.i("onItemRejectClick")
                    })
                dialogFragment.show(childFragmentManager, "permission")
            } else {
                bluePermission( {
                    nav().navigateAction(R.id.action_mainfragment_to_searchMoonRingFragment)
                })

            }

        }

        fun activeEnergy(){
            nav().navigateAction(R.id.action_mainfragment_to_calorieDetailFragment)
        }
        fun heartBeatDetail(){
            nav().navigateAction(R.id.action_mainfragment_to_heartRateDetailFragment)
        }

        fun stepDetail(){
            nav().navigateAction(R.id.action_mainfragment_to_stepDetailFragment)
        }


        fun sleep(){
            nav().navigateAction(R.id.action_mainfragment_to_sleepDetailFragment)
        }
    }

    override fun getToolbarTitle(): String = ""
}
