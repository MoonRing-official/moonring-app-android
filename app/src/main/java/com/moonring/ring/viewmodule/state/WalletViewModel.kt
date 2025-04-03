package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableField
import com.moonring.ring.utils.toWalletSimaple
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.databind.StringObservableField

/**
 *    author : Administrator

 *    time   : 2024/10/19/019
 *    desc   :
 */
class WalletViewModel : BaseViewModel() {

    var walletStr = StringObservableField("")

    var walletStrShow = object : ObservableField<String>(walletStr){
        override fun get(): String {
            return walletStr.get().toWalletSimaple()
        }
    }

}
