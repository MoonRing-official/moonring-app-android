package com.moonring.jetpackmvvm.callback.databind

import androidx.databinding.ObservableField


class LongObservableField(value: Long = 0) : ObservableField<Long>(value) {

    override fun get(): Long {
        return super.get()!!
    }

}