package com.moonring.ring.viewmodule.state

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.module.common.bean.UserProfileMoonRing

/**
 *    author : Administrator

 *    time   : 2024/10/19/019
 *    desc   :
 */
class PersonalProfileViewModel : FillUserInfoViewModel() {
    val userProfile = MutableLiveData<UserProfileMoonRing>()






    fun saveUserProfile(name: String, email: String, phone: String) {

        println("Saving user profile: $name, $email, $phone")
    }
}

