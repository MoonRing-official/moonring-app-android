package com.moonring.ring.support.moonring

import com.module.common.support.log.LogInstance
import com.module.common.util.time.RxTimer
import com.moonring.ring.bean.DayData
import com.moonring.ring.homeAppViewModel
import com.moonring.jetpackmvvm.callback.livedata.event.EventLiveData




object DataFetcherStep {

    private var dayOffset = 0
    private val maxDays = 6
    var type: Int = -1

    var isFetching = false



    private val allStepDataList = mutableListOf<DayData>()
    val allStepDataListLiveData = EventLiveData<List<DayData>>()

    fun startFetching(type: Int) {
        this.type = type
        this.dayOffset = 0
        this.allStepDataList.clear()
        fetchNextDayData()
        isFetching = true
    }

    fun addData(data:DayData){
        if (!isFetching){
            return
        }
        allStepDataList.add(data)
    }

    private fun fetchNextDayData() {
        LogInstance.i("type:${type} dayOffset:${dayOffset}  maxDays:${maxDays}")
        if (dayOffset <= maxDays) {

            RxTimer().timer(50,){
                homeAppViewModel.callRemoteGetData(type, dayOffset)
                dayOffset++  // 准备获取下一天的数据
            }

        } else {
            isFetching =  false
            // 如果已经获取了所有天的数据，更新LiveData
            allStepDataListLiveData.postValue(allStepDataList.toList())
            allStepDataList.clear()
        }
    }

    fun onGetDataByDayEnd() {
        if (!isFetching){
            return
        }
        // 继续获取下一天的数据
        fetchNextDayData()
    }
}