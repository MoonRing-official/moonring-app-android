package com.moonring.ring.viewmodule.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 *    author : Administrator

 *    time   : 2024/9/26/026
 *    desc   :
 */
class FillUserNameViewModel : BaseViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _isNameErrorVisible = MutableLiveData<Boolean>(false)
    val isNameErrorVisible: LiveData<Boolean> get() = _isNameErrorVisible

    val isNextButtonEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(_name) { name ->
            value = !name.isNullOrEmpty() && name.length <= 16
        }



    }

    init {

        _name.value = ""
    }


    fun onNameChanged(newName: String) {
        _name.value = newName
        _isNameErrorVisible.value = newName.length > 16
    }
}
