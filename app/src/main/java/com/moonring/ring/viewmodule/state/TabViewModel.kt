package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.moonring.ring.enums.DateType
import com.moonring.ring.homeAppViewModel
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.databind.IntObservableField
import com.moonring.jetpackmvvm.callback.databind.StringObservableField

/**
 *    author : Administrator

 *    time   : 2024/10/8/008
 *    desc   :
 */
open class TabViewModel : BaseViewModel() {



    var recordIndex = IntObservableField(0)
    var thresholdIndex = IntObservableField(0)

    var rightBtnEnable = object : ObservableField<Boolean>(recordIndex,thresholdIndex){
        override fun get(): Boolean {

            return   recordIndex.get()<thresholdIndex.get()
        }
    }


}


