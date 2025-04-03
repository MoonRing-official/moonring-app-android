package com.moonring.ring.viewmodule.event

import com.module.common.network.apiService
import com.module.common.support.log.LogInstance
import com.moonring.ring.BuildConfig
import com.moonring.ring.bean.room.RingBaseDataModel
import com.moonring.ring.enums.HealthDataType
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.utils.getDayStartEnd
import com.moonring.ring.utils.timestampToDateYMD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import java.util.Calendar
import java.util.Date


class UploadDataModel : BaseViewModel(){



    var isSyncing = false

     fun checkUploadData() {

         GlobalScope.launch {
             kotlin.runCatching {
                 syncTodayData()



                 if (!isSyncing ) {
                     isSyncing = true

                     val calendar = Calendar.getInstance()
                     calendar.add(Calendar.DATE, -1)
                     syncOtherDateData(calendar)
                 }
             }.onSuccess {
                 LogInstance.i("checkUploadData success")
             }.onFailure {
                 LogInstance.i("checkUploadData failure:$${it}")
             }
         }

    }





    suspend fun uploadData(list: List<RingBaseDataModel>): Result<String> {
        if (list.isEmpty()) {
            return Result.failure(Exception("No data uploaded"))
        }

        val data = list.map { it.uploadData() }
        val total = data.size
        val params = mapOf("data" to data, "total" to total)

        return withContext(Dispatchers.IO) {
            runCatching {

                val response = apiService.postHealth(params)
                if (response != null) {
                    list.forEach { model ->
                        try {

                            model.isUpload = true

                            DatabaseUtils.updateByModel(model)
                        } catch (e: Exception) {

                            e.printStackTrace()
                        }
                    }
                } else {
                    "Failed to upload data: No response"
                }
                "Data uploaded successfully"
            }.onSuccess {
                LogInstance.i(" onSuccess")
            }.onFailure {
                LogInstance.i(" isUpload = true:$${it.printStackTrace()}")
            }
        }
    }




    suspend fun syncTodayData() {
        val types = HealthDataType.values()
        val (start, end) = getDayStartEnd()
        var list = mutableListOf<RingBaseDataModel>()
        LogInstance.i("syncTodayData types forEach start")
        types.forEach { type ->

            val results = DatabaseUtils.fetchDataByTypes(type, fromTimestamp = start, toTimestamp = end)
            val filterData = results.filter { !it.isUpload }
            LogInstance.i("syncTodayData fetchDataByTypes :${type.displayName}  ${results.size} ${filterData.size}")
            list.addAll(filterData)

        }

        LogInstance.i("syncTodayData start:${list.size}")
        uploadData(list)
    }




    suspend fun syncOtherDateData(formCalendar: Calendar) {

        if (homeAppViewModel.getUserId().isNullOrBlank()) {
            LogInstance.i("getUserId: null")
            isSyncing = false
            return
        }


        val sixMonthsAgo = Calendar.getInstance().apply {
            add(Calendar.MONTH, -6)
        }


        if (formCalendar.before(sixMonthsAgo)) {
            isSyncing = false
            return
        }


        val types = HealthDataType.values()

        val endTimestamp = formCalendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis / 1000


        val dayBefore  = if (BuildConfig.DEBUG){
            -1
        }else{
            -7
        }
        val startDate = (formCalendar.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, dayBefore)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTimestamp = startDate.timeInMillis / 1000


        val list = mutableListOf<RingBaseDataModel>()


        types.forEach { type ->
            val results = withContext(Dispatchers.IO) {
                DatabaseUtils.fetchDataByTypes(type, fromTimestamp = startTimestamp, toTimestamp = endTimestamp)
            }
            val filterData = results.filter { !it.isUpload }
            LogInstance.i("syncTodayData fetchDataByTypes :${type.displayName}  ${results.size} ${filterData.size}")
            list.addAll(filterData)
        }


        val uploadResult = uploadData(list)
        if (uploadResult.isFailure) {
            LogInstance.e("Data upload failed: ${uploadResult.exceptionOrNull()?.message}")
            return
        }

        val startDateText = timestampToDateYMD(startTimestamp)
        val endDateText = timestampToDateYMD(endTimestamp)
        println("\nUploading data: $startDateText --- $endDateText\nUpload count: ${list.size}\nUpload result: Success")


        var hasUnuploaded = false
        types.forEach { type ->
            val results = withContext(Dispatchers.IO) {
                DatabaseUtils.fetchDataByTypesBefore(type, toTimestamp = startTimestamp)
            }
            val filterData = results.filter { !it.isUpload }
            if (filterData.isNotEmpty()) {
                hasUnuploaded = true
            }
        }


        if (!hasUnuploaded) {
            isSyncing = false

            return
        }


        delay(1000)
        syncOtherDateData(startDate)
    }






}

