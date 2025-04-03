package com.moonring.ring.viewmodule.state

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.module.common.app.appContext
import com.module.common.bean.UserProfileMoonRing
import com.module.common.ext.adapterChildLastClickTime
import com.module.common.ext.adapterLastClickTime
import com.module.common.ext.lastClickTime
import com.module.common.support.log.LogInstance
import com.module.common.util.AppCacheKeyEnum
import com.module.common.util.CacheUtil
import com.module.common.util.GsonUtils
import com.moonring.ring.bean.AutoHeartRateTestSettings
import com.moonring.ring.bean.BleDeviceItem
import com.moonring.ring.bean.DayData
import com.moonring.ring.bean.DeviceInfo
import com.moonring.ring.bean.HeartRateStats
import com.moonring.ring.bean.OxygenSensorData
import com.moonring.ring.bean.SensorData
import com.moonring.ring.bean.SensorStatus
import com.moonring.ring.bean.SleepDataStats
import com.moonring.ring.bean.SportData
import com.moonring.ring.bean.room.HeartRateData
import com.moonring.ring.bean.room.OxygenData
import com.moonring.ring.bean.room.SleepData

import com.moonring.ring.enums.ConnectionState
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.moonring.getDefaultProfile
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.utils.HealthDataDetailsViewModel
import com.moonring.ring.utils.calculateMiles
import com.moonring.ring.utils.formatSecondsToHourMinute
import com.moonring.ring.utils.formatTimestampToHourMinute
import com.moonring.ring.utils.getCal
import com.moonring.ring.utils.getTodayEndTimestamp
import com.moonring.ring.utils.getTodayStartTimestamp
import com.moonring.ring.utils.getYesterdayEndTimestamp
import com.moonring.ring.utils.getYesterdayStartTimestamp
import com.moonring.ring.utils.isToday
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.callback.databind.IntObservableField
import com.moonring.jetpackmvvm.ext.lastNavBackTime
import com.moonring.jetpackmvvm.ext.lastNavTime
import com.moonring.jetpackmvvm.ext.requestNoCheck
import com.moonring.jetpackmvvm.state.ResultState
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class MoonringInfoModel : BleViewModel() {


    val currentTime = MutableLiveData<Long>()



    val sportData = MutableLiveData<SportData>()


    val sportRunData = MutableLiveData<SportData>()
    val sensorData = MutableLiveData<SensorData>()

    val dayData = MutableLiveData<List<DayData>>()

    val maxStepTime = MutableLiveData<Int>()




    var batteryLevel = IntObservableField()
    var batteryStatus = IntObservableField()

    var isCharging = object : ObservableField<Boolean>(batteryStatus){
        override fun get(): Boolean {
            return batteryStatus.get() == 1
        }
    }


    val deviceInfo = MutableLiveData<DeviceInfo>()


    val sensorState = MutableLiveData<SensorStatus>()


    val heartRateStats = MutableLiveData<HeartRateStats>()
    val sleepDataStats = MutableLiveData<SleepDataStats>()
    val oxygenSensorData = MutableLiveData<OxygenSensorData>()

    val userProfile = MutableLiveData<UserProfileMoonRing>()

    val rateSetting = MutableLiveData<AutoHeartRateTestSettings>()
    val isLoadingData = MutableLiveData<Boolean>()



    val switchState = MutableLiveData<Boolean>()







    val heartRateData: LiveData<HeartRateData?> = currentTime.switchMap { fetchLatestHeartRateData() }


    val todayTotalSteps: LiveData<Int> = currentTime.switchMap {fetchTodayTotalSteps()}



    private val todayLastStepTimestamp: LiveData<Long?> =  currentTime.switchMap {fetchTodayLastStepTimestamp()}


    private val yesterdayTotalSteps: LiveData<Int> = currentTime.switchMap {fetchYesterdayTotalSteps()}



    private val todayTotalCalories: LiveData<Int> = currentTime.switchMap {fetchTodayTotalCalories()}
    private val yesterdayTotalCalories: LiveData<Int> = currentTime.switchMap {fetchYesterdayTotalCalories()}








    var titleShow = object : ObservableField<String>(connectionState) {
        override fun get(): String {
            return when (connectionState.get()) {
                ConnectionState.Connected.value -> "Today’s Health \nData"
                else -> "Today’s Health \nData"
            }
        }
    }




    fun initDefaultData(){
        batteryLevel.set(0)
        batteryStatus.set(0)

        deviceInfo.value = DeviceInfo()

        sportData.value = SportData()
        sportRunData.value = SportData()
        dayData.value = emptyList()
        maxStepTime.value = 0

        sleepDataStats.value = SleepDataStats()
        heartRateStats.value = HeartRateStats()
        oxygenSensorData.value = OxygenSensorData()
        switchState.value = false







    }

    private fun startTimeTracker() {
        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                val currentMillis = System.currentTimeMillis()
                Log.d("TimeTracker", "Current time in milliseconds: $currentMillis")

                if (!isSameDay(currentMillis, currentTime.value ?: 0)) {
                    Log.d("TimeTracker", "Day has changed, updating LiveData.")
                    currentTime.value = currentMillis
                } else {
                    Log.d("TimeTracker", "No change in day, no update needed.")
                }
                handler.postDelayed(this, 60000)
            }
        }
        handler.postDelayed(updateRunnable, 100)
    }


    fun checkIsSameDay(){
        val currentMillis = System.currentTimeMillis()
        Log.d("checkIsSameDay", "Current time in milliseconds: $currentMillis")


        lastNavTime = System.currentTimeMillis()
        lastNavBackTime = System.currentTimeMillis()
        lastClickTime = System.currentTimeMillis()
        adapterLastClickTime = System.currentTimeMillis()
        adapterChildLastClickTime = System.currentTimeMillis()

        if (!isSameDay(currentMillis, currentTime.value ?: 0)) {
            Log.d("checkIsSameDay", "Day has changed, updating LiveData.")
            currentTime.value = currentMillis
        } else {
            Log.d("checkIsSameDay", "No change in day, no update needed.")
        }
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val calendar1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val calendar2 = Calendar.getInstance().apply { timeInMillis = time2 }

        return (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR))
    }

    fun afterConnect(){

        val cacheBleDevice = GsonUtils.json2VO(CacheUtil.getCommonJson(AppCacheKeyEnum.bleDevice.name),
            BleDeviceItem::class.java)?: BleDeviceItem()
        bleDeviceItem.value = cacheBleDevice
        userProfile.value = homeAppViewModel.userProfile.value

        homeAppViewModel.callRemoteGetDeviceInfo()

        homeAppViewModel.callRemoteSetAutoHeartMode(true,0,0,23,59,15, 2)
    }


    private fun fetchLatestOxygenData(): LiveData<OxygenData?> {
        return DatabaseUtils.getDatabase(appContext, homeAppViewModel.getUserId()).oxygenDao().fetchLatestOxygenData(getTodayStartTimestamp())
    }


    private fun fetchLatestHeartRateData(): LiveData<HeartRateData?> {
        return DatabaseUtils.getDatabase(appContext, homeAppViewModel.getUserId())
            .heartRateDao()
            .fetchLatestHeartRateData(getTodayStartTimestamp())
    }

    private fun fetchTodayTotalSteps(): LiveData<Int> {
        return DatabaseUtils.getDatabase(appContext, homeAppViewModel.getUserId())
            .stepDao()
            .getTodayTotalSteps(getTodayStartTimestamp())
    }


    private fun fetchYesterdayTotalSteps(): LiveData<Int> {
        return DatabaseUtils.getDatabase(appContext, homeAppViewModel.getUserId())
            .stepDao()
            .getYesterdayTotalSteps(getYesterdayStartTimestamp(), getYesterdayEndTimestamp())
    }


    private fun fetchTodayLastStepTimestamp(): LiveData<Long?> {
        return DatabaseUtils.getDatabase(appContext, homeAppViewModel.getUserId())
            .stepDao()
            .getTodayLastStepTimestamp(getTodayStartTimestamp())
    }

    private fun fetchTodayTotalCalories(): LiveData<Int> {
        val liveData = MediatorLiveData<Int>()
        liveData.value = 0

        val sourceLiveData = DatabaseUtils.getDatabase(appContext, homeAppViewModel.getUserId())
            .calDataDao()
            .getTodayTotalCalories(getTodayStartTimestamp(), getTodayEndTimestamp())

        liveData.addSource(sourceLiveData) { value ->
            liveData.value = value ?: 0
        }

        return liveData
    }


    private fun fetchYesterdayTotalCalories(): LiveData<Int> {
        return DatabaseUtils.getDatabase(appContext, homeAppViewModel.getUserId())
            .calDataDao()
            .getYesterdayTotalCalories(getYesterdayStartTimestamp(), getYesterdayEndTimestamp())
    }




















    val calShow: LiveData<String> = todayTotalCalories.map { totalCalories ->
        val calories = totalCalories ?: 0
        if (calories > 0) {

            (calories / 1000.0).toInt().toString()
        } else {
            "--"
        }
    }



    val calVisibility: LiveData<Boolean> = calShow.map { it != "--" }



    val calComparison: LiveData<String> = MediatorLiveData<String>().apply {
        addSource(todayTotalCalories) { todayCalories ->
            updateComparisonKal(todayCalories, yesterdayTotalCalories.value)
        }
        addSource(yesterdayTotalCalories) { yesterdayCalories ->
            updateComparisonKal(todayTotalCalories.value, yesterdayCalories)
        }
    }

    private fun MediatorLiveData<String>.updateComparisonKal(todayCalories: Int?, yesterdayCalories: Int?) {
        value = if (todayCalories != null ) {

            val todayKcal = todayCalories / 1000.0
            val yesterdayKcal = yesterdayCalories?.div(1000.0)?:0.0
            val difference = todayKcal - yesterdayKcal

            when {
                difference > 0 -> "More than yesterday ${difference.toInt()} kcal"
                difference < 0 -> "Less than yesterday ${-difference.toInt()} kcal"
                else -> "Same as yesterday"
            }
        } else {
            ""
        }
    }



    val calLatestTime: LiveData<String> = currentTime.switchMap { fetchTodayLatestTimestamp()}.map { timestamp ->
        timestamp?.let { formatTimestampToHourMinute(it) } ?: ""
    }

    private fun fetchTodayLatestTimestamp(): LiveData<Long?> {
        return DatabaseUtils.getDatabase(appContext, homeAppViewModel.getUserId())
            .calDataDao()
            .getTodayLatestTimestamp(getTodayStartTimestamp(), getTodayEndTimestamp())
    }



    val heartRateShow: LiveData<String> = heartRateData.map { data ->
        data?.let { if (it.value > 0) it.value.toString() else "--" } ?: "--"
    }


    val heartRateVisibility: LiveData<Boolean> = heartRateData.map { data ->
        data?.value ?: 0 > 0
    }


    val heartRateLastTime: LiveData<String> = heartRateData.map { data ->
        data?.getTime() ?: ""
    }



    val stepShow: LiveData<String> = MediatorLiveData<String>().apply {









        addSource(todayTotalSteps) { steps ->


            val tDSteps = steps ?: 0
            value = if (tDSteps > 0) {
                tDSteps?.toString()
            } else {
                "--"
            }


        }
    }


    val stepShowLastTime: LiveData<String> = MediatorLiveData<String>().apply {








        addSource(todayLastStepTimestamp) { timestamp ->

            value = timestamp?.let { formatTimestampToHourMinute(it) } ?: ""
        }
    }


    val stepVisibility: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(todayTotalSteps) { steps ->
            value =  steps?:0 > 0
        }
    }


    val stepComparison: LiveData<String> = MediatorLiveData<String>().apply {

        addSource(todayTotalSteps) { todaySteps ->
            val finalTodaySteps =  todaySteps?:0
            updateComparison(finalTodaySteps, yesterdayTotalSteps.value)
        }
        addSource(yesterdayTotalSteps) { yesterdaySteps ->
            val todaySteps =  todayTotalSteps.value ?: 0
            updateComparison(todaySteps, yesterdaySteps)
        }
    }

    private fun MediatorLiveData<String>.updateComparison(todaySteps: Int, yesterdaySteps: Int?) {
        value = if (todaySteps != null) {
            val yesterdayStepsin = yesterdaySteps?:0
            val difference = todaySteps - yesterdayStepsin
            when {
                difference > 0 -> "More than yesterday $difference steps"
                difference < 0 -> "Less than yesterday ${-difference} steps"
                else -> "Same as yesterday"
            }
        } else {
            ""
        }
    }






    private val recentSleepData = currentTime.switchMap {fetchRecentSleepData()}


    val sleepShow: LiveData<String> = recentSleepData.map { sleepDataList ->
        val calendarToday = Calendar.getInstance()
        val dataModel = HealthDataDetailsViewModel()
        val sleepItemToday = dataModel.sleepItemByDay(calendarToday, sleepDataList)

        if (sleepItemToday.totalDurationSecond == 0) {
            "--"
        } else {
            formatSecondsToHourMinute(sleepItemToday.totalDurationSecond)
        }

    }
    val sleepLastTime: LiveData<String> = recentSleepData.map { dataList ->
        val latestSleepData = dataList.maxByOrNull { it.timestamp }
        if (latestSleepData != null) {

            if (isToday(latestSleepData.timestamp)) {
                latestSleepData.getTime()
            } else {
                "--"
            }
        } else {
            "--"
        }
    }





    val sleepShowVisibility: LiveData<Boolean> = sleepShow.map { it != "--" }


    private fun fetchRecentSleepData(): LiveData<List<SleepData>> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis/1000
        val startTime = calendar.apply {
            add(Calendar.DATE, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis/1000

        return DatabaseUtils.getDatabase(appContext, homeAppViewModel.getUserId()).sleepDao().fetchSleepDataByTimestampLive(startTime, endTime)
    }


    val maxStepTimeDisplay: LiveData<String> = maxStepTime.map { seconds ->
        convertSecondsToHMS(seconds ?: 0)
    }






    val heartRateTextColor: LiveData<Int> = heartRateStats.map{ data ->
        when {
            data.heartrate == 0 -> Color.BLACK
            data.heartrate in 60..100 -> Color.BLACK
            data.heartrate in 50..59 || data.heartrate in 101..110 -> Color.parseColor("#FFA500")
            else -> Color.RED
        }
    }



    val systolicPressureTextColor: LiveData<Int> = oxygenSensorData.map { data ->
        when {
            data.systolicPressure == 0 -> Color.BLACK
            data.systolicPressure in 90..120 -> Color.BLACK
            data.systolicPressure in 80..89 || data.systolicPressure in 121..130 -> Color.parseColor("#FFA500")
            else -> Color.RED
        }
    }


    val diastolicPressureTextColor: LiveData<Int> = oxygenSensorData.map { data ->
        when {
            data.diastolicPressure == 0 -> Color.BLACK
            data.diastolicPressure in 60..90 -> Color.BLACK
            data.diastolicPressure in 50..59 || data.diastolicPressure in 91..110 -> Color.parseColor("#FFA500")
            else -> Color.RED
        }
    }


    private val _isMetric = MutableLiveData<Boolean>(true)
    val isMetric: LiveData<Boolean> = _isMetric

    fun onMetricChecked(isChecked: Boolean) {
        if (isChecked) {
            _isMetric.value = true
        }
    }

    fun onImperialChecked(isChecked: Boolean) {
        if (isChecked) {
            _isMetric.value = false
        }
    }

    fun calMasStepTime() {

        val historyStepTime = dayData.value?.count { it.step > 0 } ?: 0


        val currentStepTime = sportRunData.value?.steptime ?: 0


        val maxime = maxOf(historyStepTime, currentStepTime, maxStepTime.value ?: 0)


        maxStepTime.value = maxime
    }

fun convertSecondsToHMS(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60


    return   String.format("%02d:%02d", hours, minutes)
}



    init {
        userProfile.value = homeAppViewModel.userProfile.value
        rateSetting.value =  AutoHeartRateTestSettings()
        sportData.value = SportData()
        sleepDataStats.value = SleepDataStats()
        heartRateStats.value = HeartRateStats()
        isLoadingData.value = false
        switchState.value = false






    }
}
