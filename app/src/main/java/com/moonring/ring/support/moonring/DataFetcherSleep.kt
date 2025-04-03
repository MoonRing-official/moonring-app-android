package com.moonring.ring.support.moonring

import com.module.common.support.log.LogInstance
import com.module.common.util.time.RxTimer
import com.moonring.ring.bean.DayData
import com.moonring.ring.bean.room.SleepData
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.jetpackmvvm.callback.livedata.event.EventLiveData




object DataFetcherSleep {

    private var dayOffset = 0
    private val maxDays = 6
    var type: Int = -1

    var isFetching = false



    private val allSleepDataList = mutableListOf<DayData>()
    val allSleepDataListLiveData = EventLiveData<List<DayData>>()

    fun startFetching(type: Int) {
        this.type = type
        this.dayOffset = 0
        this.allSleepDataList.clear()
        fetchNextDayData()
        isFetching = true
    }

    fun addData(data:DayData){
        if (!isFetching){
            return
        }
        allSleepDataList.add(data)
    }

    private fun fetchNextDayData() {
        LogInstance.i("type:${type} dayOffset:${dayOffset}  maxDays:${maxDays}")
        if (dayOffset <= maxDays) {

            RxTimer().timer(50,){
                homeAppViewModel.callRemoteGetData(type, dayOffset)
                dayOffset++
            }

        } else {
            isFetching =  false


            val sleepList = allSleepDataList.map {
                SleepData(timestamp = it.timestamp, value = it.step)
            }
            val saveList = MRRingManager.conversionSleepLineList(sleepList)
            saveList.forEach { data ->
                DatabaseUtils.insertSleepData(sleepData = data)
            }



            allSleepDataListLiveData.postValue(allSleepDataList.toList())
            allSleepDataList.clear()
        }
    }

    fun onGetDataByDayEnd() {
        if (!isFetching){
            return
        }

        fetchNextDayData()
    }
}