package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableField
import com.github.aachartmodel.aainfographics.aachartcreator.AAOptions
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.moonring.ring.bean.DayData
import com.moonring.ring.enums.DateType
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.databind.StringObservableField

/**
 *    author : Administrator

 *    time   : 2024/10/10/010
 *    desc   :
 */
class HeartRateColumnRangeViewModel : TabViewModel() {



    var avg = StringObservableField("0")
    var max = StringObservableField("0")
    var min = StringObservableField("0")
    var total = StringObservableField("0")

    var latest = StringObservableField("0")

    var type = StringObservableField("")

    var unitName = "bpm"

    var range = object : ObservableField<String>(min,max){
        override fun get(): String {
            return "${min.get()}~${max.get()} ${unitName}"
        }
    }

    var title =  object : ObservableField<String>(type){
        override fun get(): String {
            return when (type.get()) {
                DateType.Day.name -> "Range:"
                DateType.Week.name -> "Range:"
                DateType.Month.name -> "Range:"
                else -> ""
            }
        }
    }



    val titleValue = object : ObservableField<String>(type,min,max){
        override fun get(): String {
            return "${min.get()}~${max.get()} ${unitName}"
        }
    }



    val latestShow =  object : ObservableField<String>(latest){
        override fun get(): String {
            return "${latest.get()} ${unitName}"
        }
    }



    var caloriesData: List<Array<Int>> = mutableListOf()
    var originData: LinkedHashMap<String, MutableList<DayData>> = linkedMapOf()


    var categories: List<String> = arrayListOf()
    var categoriesOrign: List<String> = arrayListOf()













    var time = StringObservableField("0")
    var timeShow = object : ObservableField<String>(time){
        override fun get(): String {
            return time.get()
        }
    }















    var toolTipTitle = StringObservableField("Range:")
    var toolTipValue = StringObservableField("0")
    var toolTipUnit = StringObservableField(unitName)
    var toolTipTime = StringObservableField("")


    var chartOptions: AAOptions?=null

    var series: Array<AASeriesElement>? = null

    init {
        loadHeartRateRangeData()
    }

    fun loadHeartRateRangeData() {







    }
}


