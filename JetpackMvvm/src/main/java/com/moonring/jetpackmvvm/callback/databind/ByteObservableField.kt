package com.moonring.jetpackmvvm.callback.databind

import androidx.databinding.ObservableField


class ByteObservableField(value: Byte = 0) : ObservableField<Byte>(value) {

    override fun get(): Byte {
        return super.get()!!
    }

}