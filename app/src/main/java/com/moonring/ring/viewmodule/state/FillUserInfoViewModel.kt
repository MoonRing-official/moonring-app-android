package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableField
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.databind.IntObservableField
import com.moonring.jetpackmvvm.callback.databind.StringObservableField

/**
 *    author : Administrator

 *    time   : 2024/9/26/026
 *    desc   :
 */
open class FillUserInfoViewModel : BaseViewModel() {
    val monthsArray = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    var name = StringObservableField()


    var gender = StringObservableField()


    var height= StringObservableField()
    var heightUnit = StringObservableField()

    var feet = StringObservableField()
    var inches = StringObservableField()


    var weight= StringObservableField()

    var weightUnit = StringObservableField()

    var birthday = StringObservableField()

    var year = IntObservableField()
    var month= IntObservableField()
    var day = IntObservableField()


    val pageStatus = IntObservableField()


    var usarName = StringObservableField()

    init {



    }


    var title = object : ObservableField<String>(pageStatus){
        override fun get(): String {
            if (pageStatus.get() == 1){
                return  "Let’s get to know \nyou better"
            }
            return  "What would you \nlike to be called?"
        }
    }


    var genderShow = object : ObservableField<String>(gender){
        override fun get(): String {
            if (gender.get().isNullOrEmpty()){
                return  "Gender"
            }
            return "${gender.get()}"
        }
    }










    var heightShow = object : ObservableField<String>(height,heightUnit,feet,inches){
        override fun get(): String {

            if (heightUnit.get() == "ft"){
                if (feet.get().isNullOrEmpty()||inches.get().isNullOrEmpty()){
                    return  "Height"
                }
                return "${feet.get()}' ${inches.get()}\" ${heightUnit.get()}"
            }else{
                if (height.get().isNullOrEmpty()){
                    return  "Height"
                }
                return "${height.get()} ${heightUnit.get()}"
            }

        }
    }

    var weightShow = object : ObservableField<String>(weight,weightUnit){
        override fun get(): String {
            if (weight.get().isNullOrEmpty()){
                return  "Weight"
            }
            return "${weight.get()} ${weightUnit.get()}"
        }
    }



    var birdaythShow = object : ObservableField<String>(year,month,day){
        override fun get(): String {
            if (year.get() == 0 ){
                return  "Date of Birth"
            }

            return "${monthsArray.get(month.get()-1)} ${day.get()} ${ year.get()}"
        }
    }


    var totalNum = IntObservableField(16)

    var errorText = object : ObservableField<String>(totalNum){
        override fun get(): String {
            return "*The name must be ${totalNum.get()} characters or fewer."
        }
    }

    var isNameErrorVisible = object : ObservableField<Boolean>(name){
        override fun get(): Boolean {
            return name.get().length > totalNum.get()
        }
    }

    var isConfirm = object : ObservableField<Boolean>(name){
        override fun get(): Boolean {
            return name.get().isNotEmpty() &&  name.get().length <= totalNum.get()

        }
    }


    var isNextButtonEnabled = object : ObservableField<Boolean>(pageStatus,name,gender,height,heightUnit,weight,year,month,day){
        override fun get(): Boolean {
            if (pageStatus.get() == 0){
               return !name.get().isNullOrEmpty() && name.get().length <= totalNum.get()
            }else{
                val genderFilled = !gender.get().isNullOrEmpty()
                val heightFilled = !heightUnit.get().isNullOrEmpty()
                val weightFilled = !weight.get().isNullOrEmpty()
                val birthdayFilled = year.get()>0 && month.get()>0 && day.get()>0
                return genderFilled && heightFilled && weightFilled && birthdayFilled
            }

        }
    }










    fun submitUserData() {

    }
}
