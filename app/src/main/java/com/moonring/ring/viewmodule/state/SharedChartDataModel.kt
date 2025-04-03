package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.module.common.bean.UserProfileMoonRing
import com.module.common.ext.toThousandSeparator
import com.moonring.ring.enums.DateType
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.utils.calculateMiles
import com.moonring.ring.utils.getFatigueLevel
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.databind.BooleanObservableField
import com.moonring.jetpackmvvm.callback.databind.IntObservableField
import com.moonring.jetpackmvvm.callback.databind.LongObservableField
import com.moonring.jetpackmvvm.callback.databind.StringObservableField

class SharedChartDataModel : BaseViewModel(){

    var flag = ""
    var max = StringObservableField("0")
    var min = StringObservableField("0")



    var restHR = StringObservableField("0")

    var unit =  StringObservableField("kcal")

    val userProfile = MutableLiveData<UserProfileMoonRing>()







    var date = StringObservableField("09/23/2024")

    var dateCaliShow  =  object : ObservableField<String>(max,date){
        override fun get(): String {
            if (max.get()=="0"){
                return ""
            }
            return date.get()
        }
    }


    var range = object : ObservableField<String>(min,max,unit){
        override fun get(): String {
            return "${min.get()}~${max.get()}"
        }
    }

    var rangeHeartRate = object : ObservableField<String>(min,max,restHR){
        override fun get(): String {
            if (flag == DateType.Day.name){
                return restHR.get()
            }
            return "${min.get()}~${max.get()}"
        }
    }


    var lastSynHeartRate = StringObservableField("0")
    var lastSynHeartRateTime = LongObservableField()


    var lastSyndateCaliShow  =  object : ObservableField<String>(max,lastSynHeartRate){
        override fun get(): String {
            return date.get()
        }
    }


    var title = StringObservableField("")
    var total = StringObservableField("0")

    var totalStepsShow = object : ObservableField<String>(total){
        override fun get(): String {
            return total.get().toThousandSeparator()
        }
    }

    var isDay = BooleanObservableField()
    var goal = StringObservableField("3000")
    var offValue = object : ObservableField<String>(goal){
        override fun get(): String {
            return "of ${goal.get().toThousandSeparator()} ${unit.get()}"
        }
    }
    var per = object : ObservableField<String>(total, goal) {
        override fun get(): String {
            val totalValue = total.get()?.toDoubleOrNull() ?: 0.0
            val goalValue = goal.get()?.toDoubleOrNull() ?: 1.0


            val percentage = if (goalValue != 0.0) (totalValue / goalValue) * 100 else 0.0


            return String.format("%d%%", percentage.toInt())

        }
    }
    var moveingTime = StringObservableField("0")
    var distance = StringObservableField("0")

    var distanceDisplay = object : ObservableField<String>(distance) {
        override fun get(): String {
            val rDistance = homeAppViewModel.userProfile.value?.let { profile ->
                val distanceInm = distance.get()?.toDoubleOrNull() ?: 0.0
                if (profile.unit == 0) {

                    val distanceInKm = distanceInm / 1000
                    String.format("%.2f", distanceInKm)
                } else {

                    val distanceInKm = distanceInm / 1000
                    val distanceInMiles = calculateMiles(distanceInKm)
                    String.format("%.2f", distanceInMiles)
                }
            } ?: ""
            return rDistance
        }
    }

    var distanceUnit = object : ObservableField<String>(distance) {
        override fun get(): String {
            val rDistance = homeAppViewModel.userProfile.value?.let { profile ->

                if (profile.unit == 0) {

                    "km"
                } else {

                    "miles"
                }
            } ?: ""
            return rDistance
        }
    }


    var oxgentitle = StringObservableField("")
    var oxgenValue = StringObservableField("")
    var fatigue = IntObservableField()

    var fatigueShow = object : ObservableField<String>(fatigue){
        override fun get(): String {
            try {
                return  getFatigueLevel(fatigue.get())
            }catch (e:Exception){

            }
            return  ""
        }
    }






    var deepSleepTitle =  StringObservableField("")
    var lightSleepTitle =  StringObservableField("")
    var deepSleepTime = StringObservableField("")
    var lightSleepTime = StringObservableField("")

    init {

        userProfile.value = homeAppViewModel.userProfile.value
    }
}