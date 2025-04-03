package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.moonring.ring.enums.DateType
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.databind.StringObservableField

/**
 *    author : Administrator

 *    time   : 2024/10/10/010
 *    desc   :
 */
open  class BaseChartLogciViewModel : BaseViewModel(){

    var caloriesData: List<Int> = arrayListOf()
    var categories: List<String> = arrayListOf()
    var categoriesOrign: List<String> = arrayListOf()
    var avg = StringObservableField("0")
    var max = StringObservableField("0")
    var min = StringObservableField("0")
    var total = StringObservableField("0")


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

    var time = StringObservableField("0")
    var timeShow = object : ObservableField<String>(time){
        override fun get(): String {
            return time.get()
        }
    }



    val titleValue = object : ObservableField<String>(type,avg,total){
        override fun get(): String {
            return when (type.get()) {
                DateType.Day.name -> total.get()
                DateType.Week.name -> avg.get()
                DateType.Month.name -> avg.get()
                else -> "0"
            }
        }
    }


    var toolTipTitle = StringObservableField("Total:")
    var toolTipValue = StringObservableField("0")
    var toolTipUnit = StringObservableField("kcal")
    var toolTipTime = StringObservableField("")
}