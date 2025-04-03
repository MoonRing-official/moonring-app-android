package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.github.aachartmodel.aainfographics.aachartcreator.AAOptions
import com.moonring.ring.bean.ChartSeriesSleepItem
import com.moonring.ring.enums.DateType
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.utils.calculateMiles
import com.moonring.ring.utils.distanceInKilometers
import com.moonring.ring.utils.formatTime
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.databind.DoubleObservableField
import com.moonring.jetpackmvvm.callback.databind.IntObservableField
import com.moonring.jetpackmvvm.callback.databind.StringObservableField

/**
 *    author : Administrator

 *    time   : 2024/10/12/012
 *    desc   :
 */
class SleepChartViewModel : TabViewModel() {
    var avg = StringObservableField("0")
    var max = StringObservableField("0")
    var min = StringObservableField("0")
    var total = StringObservableField("0")


    var totalDistance = DoubleObservableField()
    var totalStepTime = IntObservableField()

    var unitName = ""
    var goalSteps = StringObservableField("1000")

    var goalPer = StringObservableField("130%")

    var goalStepsShow = object : ObservableField<String>(goalSteps){
        override fun get(): String {
            return "Target: ${goalSteps.get()}"
        }
    }


    var totalStepShow = object : ObservableField<String>(total){
        override fun get(): String {
            return "${total.get()} "
        }
    }


    var totalStepTimeShow =  object : ObservableField<String>(totalStepTime){
        override fun get(): String {
            return formatTime(totalStepTime.get()*60)
        }
    }

    var type = StringObservableField("")

    var range = object : ObservableField<String>(min,max){
        override fun get(): String {
            return "${min.get()}~${max.get()}"
        }
    }

    /**
     * Avg Sleep Time:
     * 08h 29m
     * 09/12~19/2024
     */
    var title =  object : ObservableField<String>(type){
        override fun get(): String {
            return when (type.get()) {
                DateType.Day.name -> "Total Sleep Time:"
                DateType.Week.name -> "Avg Sleep Time:"
                DateType.Month.name -> "Avg Sleep Time:"
                else -> ""
            }
        }
    }



    val titleValue = object : ObservableField<String>(type,avg,total){
        override fun get(): String {
            val value =  when (type.get()) {
                DateType.Day.name -> total.get()
                DateType.Week.name -> avg.get()
                DateType.Month.name -> "0"
                else -> "0"
            }
            return  "${value}"
        }
    }

    var toolTipTitle = StringObservableField("Total:")
    var toolTipValue = StringObservableField("0")
    var toolTipUnit = StringObservableField(unitName)
    var toolTipTime = StringObservableField("")
    var time = StringObservableField("0")
    var timeShow = object : ObservableField<String>(time){
        override fun get(): String {
            return time.get()
        }
    }

    var caloriesData: List<Int> = arrayListOf()
    var categories: List<String> = arrayListOf()
    var categoriesOrign: List<String> = arrayListOf()


    var sleepItem: ChartSeriesSleepItem ?= null

    var sleepItemList: List<ChartSeriesSleepItem> ?=null

    var chartOptions: AAOptions?=null
    init {
        loadSleepChartData()
    }

    fun loadSleepChartData() {

    }


}
