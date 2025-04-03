package com.moonring.jetpackmvvm.callback.livedata

import androidx.lifecycle.MutableLiveData


class ByteLiveData : MutableLiveData<Byte>() {
    override fun getValue(): Byte {
        return super.getValue() ?: 0
    }
}