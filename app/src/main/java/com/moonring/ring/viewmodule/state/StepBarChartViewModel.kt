package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableField
import com.github.aachartmodel.aainfographics.aachartcreator.AAOptions
import com.module.common.ext.toThousandSeparator
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
class StepBarChartViewModel :  TabViewModel() {

    var avg = StringObservableField("0")
    var max = StringObservableField("0")
    var min = StringObservableField("0")
    var total = StringObservableField("0")


    var totalDistance = DoubleObservableField()
    var totalStepTime = IntObservableField()

    var unitName = "steps"

    var goalSteps = StringObservableField("1000")

    var goalPer = StringObservableField("130%")

    var goalStepsShow = object : ObservableField<String>(goalSteps){
        override fun get(): String {
            return "Target: ${goalSteps.get()} ${unitName}"
        }
    }


    var totalStepShow = object : ObservableField<String>(total){
        override fun get(): String {
            return "${total.get()} ${unitName}"
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
            return "${min.get()}~${max.get()} kcal"
        }
    }

    var title =  object : ObservableField<String>(type){
        override fun get(): String {
            return when (type.get()) {
                DateType.Day.name -> "Total:"
                DateType.Week.name -> "Avg:"
                DateType.Month.name -> "Avg:"
                else -> ""
            }
        }
    }



    val titleValue = object : ObservableField<String>(type,avg,total){
        override fun get(): String {
            val value =  when (type.get()) {
                DateType.Day.name -> total.get().toThousandSeparator()
                DateType.Week.name -> avg.get().toThousandSeparator()
                DateType.Month.name -> avg.get().toThousandSeparator()
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


    var chartOptions: AAOptions?=null
    init {
        loadStepData()
    }

    fun loadStepData() {


    }
}

