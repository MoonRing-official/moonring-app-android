package com.moonring.jetpackmvvm.callback.databind

import androidx.databinding.ObservableField

open class IntObservableField(value: Int = 0) : ObservableField<Int>(value) {

    override fun get(): Int {
        return super.get()!!
    }

}