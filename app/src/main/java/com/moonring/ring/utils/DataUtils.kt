package com.moonring.ring.utils

import com.module.common.bean.UserProfileMoonRing
import com.module.common.enums.Gender
import com.moonring.ring.enums.SleepLevelType

import com.sxr.sdk.ble.keepfit.aidl.UserProfile


fun mapToBleUseProfile(userProfile: UserProfileMoonRing): UserProfile {


    val gender = if (userProfile.gender == Gender.female.name){
        Gender.female.value
    }else{
        Gender.male.value
    }
    return UserProfile(
        8000,
        userProfile.heightCM,
        userProfile.weightKg,
        50,
        userProfile.unit,
        gender,
        userProfile.age
    )
}



/**
 * Severe
 * Fatigue
 */
fun getFatigueLevel(fatigueValue: Int): String {
    return when {
        fatigueValue < 34 -> "Mild Fatigue"
        fatigueValue < 67 -> "Moderate Fatigue"
        else -> "Severe Fatigue"
    }
}

fun Int.toSleepLevelType():SleepLevelType{
   return when (this) {
        99 -> SleepLevelType.DeepSleep
        40 -> SleepLevelType.LightSleep
        else -> SleepLevelType.Awake
    }
}

