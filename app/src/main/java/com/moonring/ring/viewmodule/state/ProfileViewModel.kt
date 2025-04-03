package com.moonring.ring.viewmodule.state

import androidx.lifecycle.MutableLiveData
import com.module.common.bean.UserProfileMoonRing
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 *    author : Administrator

 *    time   : 2024/10/8/008
 *    desc   :
 */
class ProfileViewModel : BaseViewModel() {

    val userProfile = MutableLiveData<UserProfileMoonRing>()
    val userName = MutableLiveData<String>()
    val userEmail = MutableLiveData<String>()
    val userPhone = MutableLiveData<String>()


}
