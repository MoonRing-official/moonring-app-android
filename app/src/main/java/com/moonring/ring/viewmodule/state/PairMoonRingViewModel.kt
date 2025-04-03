package com.moonring.ring.viewmodule.state

import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 *    author : Administrator

 *    time   : 2024/10/15/015
 *    desc   :
 */
class PairMoonRingViewModel : BaseViewModel() {


    fun startPairingProcess() {

    }
}

enum class PairingStatus {
    IN_PROGRESS,
    SUCCESS,
    FAILURE
}
