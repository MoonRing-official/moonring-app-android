package com.moonring.ring.support.room

import android.content.Context
import com.module.common.app.appContext
import com.module.common.support.log.LogInstance
import com.moonring.ring.bean.room.CalData
import com.moonring.ring.bean.room.HeartRateData
import com.moonring.ring.bean.room.OxygenData
import com.moonring.ring.bean.room.RingBaseDataModel
import com.moonring.ring.bean.room.SleepData
import com.moonring.ring.bean.room.StepData
import com.moonring.ring.enums.HealthDataType
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.utils.getDayStartEnd
import com.moonring.ring.utils.getWeekStartEnd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object DatabaseUtils {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)


    fun getDatabase(context: Context = appContext, userId: String = getUseId()): AppDatabase {
        return AppDatabase.getDatabase(context, userId)
    }


    fun insertStepData(context: Context = appContext, userId: String = getUseId(), stepData: StepData) {
        applicationScope.launch {
            val dao = getDatabase(context, userId).stepDao()
            val insertResult = dao.insert(stepData)
            LogInstance.i("insertStepData:${insertResult}")
            if (insertResult == -1L) {
                dao.updateValue(stepData.timestamp, stepData.value)
            }
        }
    }


    fun insertSleepData(context: Context = appContext, userId: String= getUseId(), sleepData: SleepData) {
        applicationScope.launch {
            val dao = getDatabase(context, userId).sleepDao()
            val insertResult = dao.insert(sleepData)
            LogInstance.i("insertSleepData: $insertResult")
            if (insertResult == -1L) {
                dao.updateValue(sleepData.timestamp, sleepData.value)
            }
        }
    }


    fun insertHeartRateData(context: Context = appContext, userId: String= getUseId(), heartRateData: HeartRateData,callback: (HeartRateData) -> Unit={}) {
        applicationScope.launch {
            val dao = getDatabase(context, userId).heartRateDao()
            val insertResult = dao.insert(heartRateData)
            if (insertResult == -1L) {
                dao.updateValue(heartRateData.timestamp, heartRateData.value)

            }

            withContext(Dispatchers.Main) {
                callback.invoke(heartRateData)
            }

        }
    }


    fun insertOxygenData(context: Context = appContext, userId: String= getUseId(), oxygenData: OxygenData,callback: (OxygenData) -> Unit) {
        applicationScope.launch {
            val dao = getDatabase(context, userId).oxygenDao()
            val insertResult = dao.insert(oxygenData)
            if (insertResult == -1L) {
                dao.updateValue(oxygenData.timestamp, oxygenData.value)
            }

            withContext(Dispatchers.Main) {
                callback.invoke(oxygenData)
            }
        }
    }

    fun insertCalData(context: Context = appContext, userId: String = getUseId(), calData: CalData) {
        applicationScope.launch {

            val calDao = getDatabase(context, userId).calDataDao()

            val id = calDao.insert(calData)



        }
    }


    suspend fun fetchCalData(context: Context = appContext, userId: String = getUseId(), fromTimestamp: Long, toTimestamp: Long): List<CalData> {
        return withContext(Dispatchers.IO) {

            val result = getDatabase(context, userId).calDataDao().fetchCalDataByTimestamp(fromTimestamp, toTimestamp)
            withContext(Dispatchers.Main) {
                result
            }
        }
    }



    fun updateStepData(context: Context = appContext, userId: String = getUseId(), stepData: StepData) {
        applicationScope.launch {
            getDatabase(context, userId).stepDao().update(stepData)
        }
    }


    fun updateSleepData(context: Context = appContext, userId: String = getUseId(), sleepData: SleepData) {
        applicationScope.launch {
            getDatabase(context, userId).sleepDao().update(sleepData)
        }
    }


    fun updateHeartRateData(context: Context = appContext, userId: String =getUseId(), heartRateData: HeartRateData) {
        applicationScope.launch {
            getDatabase(context, userId).heartRateDao().update(heartRateData)
        }
    }


    fun updateOxygenData(context: Context = appContext, userId: String = getUseId(), oxygenData: OxygenData) {
        applicationScope.launch {
            getDatabase(context, userId).oxygenDao().update(oxygenData)
        }
    }


    fun updateCalData(context: Context = appContext, userId: String = getUseId(), calData: CalData) {
        applicationScope.launch {
            getDatabase(context, userId).calDataDao().update(calData)
        }
    }





    suspend fun fetchCalDataBefore(context: Context = appContext, userId: String = getUseId(), toTimestamp: Long): List<CalData> {
        return withContext(Dispatchers.IO) {
            getDatabase(context, userId).calDataDao().fetchCalDataBeforeTimestamp(toTimestamp)
        }
    }


    suspend fun fetchStepsData(context: Context = appContext, userId: String = getUseId(), fromTimestamp: Long, toTimestamp: Long): List<StepData> {
        return withContext(Dispatchers.IO) {
            getDatabase(context, userId).stepDao().fetchStepsByTimestamp(fromTimestamp, toTimestamp)
        }
    }

    suspend fun fetchStepDataBefore(context: Context = appContext, userId: String = getUseId(), toTimestamp: Long): List<StepData> {
        return withContext(Dispatchers.IO) {
            val result = getDatabase(context, userId).stepDao().fetchStepDataBeforeTimestamp(toTimestamp)
            result
        }
    }
    suspend fun fetchSleepDataBefore(context: Context = appContext, userId: String = getUseId(), toTimestamp: Long): List<SleepData> {
        return withContext(Dispatchers.IO) {
            getDatabase(context, userId).sleepDao().fetchSleepDataBeforeTimestamp(toTimestamp)
        }
    }





    suspend fun fetchSleepData(context: Context = appContext, userId: String = getUseId(), fromTimestamp: Long, toTimestamp: Long): List<SleepData> {
        return withContext(Dispatchers.IO) {
            getDatabase(context, userId).sleepDao().fetchSleepDataByTimestamp(fromTimestamp, toTimestamp)
        }
    }



    fun fetchAllSleepData(context: Context = appContext, userId: String= getUseId(), callback: (List<SleepData>) -> Unit) {
        applicationScope.launch {
            val result = getDatabase(context, userId).sleepDao().fetchAllSleepData()
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }


    suspend fun fetchHeartRateData(context: Context = appContext, userId: String = getUseId(), fromTimestamp: Long, toTimestamp: Long): List<HeartRateData> {
        return withContext(Dispatchers.IO) {
            getDatabase(context, userId).heartRateDao().fetchHeartRateDataByTimestamp(fromTimestamp, toTimestamp)
        }
    }

    suspend fun fetchHeartRateDataBefore(context: Context = appContext, userId: String = getUseId(), toTimestamp: Long): List<HeartRateData> {
        return withContext(Dispatchers.IO) {

            getDatabase(context, userId).heartRateDao().fetchHeartRateDataBeforeTimestamp(toTimestamp)
        }
    }



    suspend fun fetchOxygenData(context: Context = appContext, userId: String = getUseId(), fromTimestamp: Long, toTimestamp: Long): List<OxygenData> {
        return withContext(Dispatchers.IO) {
            getDatabase(context, userId).oxygenDao().fetchOxygenByTimestamp(fromTimestamp, toTimestamp)
        }
    }


    suspend fun fetchOxygenDataBefore(context: Context = appContext, userId: String = getUseId(), toTimestamp: Long): List<OxygenData> {
        return withContext(Dispatchers.IO) {

            getDatabase(context, userId).oxygenDao().fetchOxygenDataBeforeTimestamp(toTimestamp)
        }
    }





    fun getUseId():String{
       return homeAppViewModel.userProfile.value?.user_id?:""
    }

    fun updateByModel(model: RingBaseDataModel) {
        when (model) {
            is CalData -> updateCalData(calData = model)
            is HeartRateData -> updateHeartRateData(heartRateData = model)
            is OxygenData -> updateOxygenData(oxygenData = model)
            is StepData -> updateStepData(stepData = model)
            is SleepData -> updateSleepData(sleepData = model)
        }
    }

    suspend fun fetchDataByTypes(type: HealthDataType, fromTimestamp: Long, toTimestamp: Long) : List<RingBaseDataModel>{
        return  when (type) {
            HealthDataType.calorie -> fetchCalData(fromTimestamp = fromTimestamp, toTimestamp = toTimestamp)
            HealthDataType.heart_rate -> fetchHeartRateData(fromTimestamp = fromTimestamp, toTimestamp = toTimestamp)
            HealthDataType.blood_oxygen -> fetchOxygenData(fromTimestamp = fromTimestamp, toTimestamp = toTimestamp)
            HealthDataType.movement -> fetchStepsData(fromTimestamp = fromTimestamp, toTimestamp = toTimestamp)
            HealthDataType.sleep -> fetchSleepData(fromTimestamp = fromTimestamp, toTimestamp = toTimestamp)
            else -> {emptyList()}
        }

    }


    suspend fun fetchDataByTypesBefore(type: HealthDataType, toTimestamp: Long): List<RingBaseDataModel> {
        return when (type) {
            HealthDataType.calorie -> fetchCalDataBefore(toTimestamp = toTimestamp)
            HealthDataType.heart_rate -> fetchHeartRateDataBefore(toTimestamp = toTimestamp)
            HealthDataType.blood_oxygen -> fetchOxygenDataBefore(toTimestamp = toTimestamp)
            HealthDataType.movement -> fetchStepDataBefore(toTimestamp = toTimestamp)
            HealthDataType.sleep -> fetchSleepDataBefore(toTimestamp = toTimestamp)
            else -> emptyList()
        }
    }
    suspend fun fetchActiveHeartRateTestCount(context: Context = appContext, userId: String = getUseId(), fromTimestamp: Long, toTimestamp: Long): Int {

        return withContext(Dispatchers.IO) {
            getDatabase(context, userId).heartRateDao().countActiveHeartRateTests(fromTimestamp, toTimestamp)
        }
    }






}