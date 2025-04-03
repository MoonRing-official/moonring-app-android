package com.moonring.jetpackmvvm.network.manager

import com.moonring.jetpackmvvm.callback.livedata.event.EventLiveData


class NetworkStateManager private constructor() {

    val mNetworkStateCallback = EventLiveData<NetState>()

    companion object {
        val instance: NetworkStateManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkStateManager()
        }
    }

}