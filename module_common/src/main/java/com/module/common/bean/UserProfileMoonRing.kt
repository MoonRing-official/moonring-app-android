package com.module.common.bean

import com.module.common.bean.login.RingInfo
import com.module.common.bean.login.WalletInfo
import com.module.common.enums.Gender
import com.module.common.enums.HeightUnit
import com.module.common.enums.WeightUnit
import com.module.common.util.calculateAge
import com.module.common.util.cmToFeetInches
import com.module.common.util.formatBirthday
import com.module.common.util.kgToPounds
import com.module.common.util.poundsToKg


class UserProfileMoonRing(
    var user_id: String = "",
    var email: String = "",
    var username: String = "",
    var avatar_url: String ?= "",
    var register_at: Long = 0L,
    var modify_at: Long = 0L,
    var birthday: Long = 0L,
    var height: String = "170",
    var height_unit: String ?= "unknown",
    var weight: String = "",
    var weight_unit: String ?= "unknown",
    var gender: String = "unknown",
    var unit:Int = 0,
    var wallet_info: WalletInfo ?= WalletInfo(""),
    var ring_info: RingInfo ?= RingInfo("",0),
) {
    val genderShow: String
        get() = if (gender == Gender.female.name){
            Gender.female.displayName
        }else if (gender == Gender.male.name){
            Gender.male.displayName
        }else{
            Gender.non_binary.displayName
        }

    val heightShow: String
        get() = if (height_unit == HeightUnit.ft.name){
            val pair = cmToFeetInches(height.toDouble())
            val feet = pair.first
            val inches = pair.second
            "${feet}' ${inches}\" ${height_unit}"
        }else{
            "${height} ${height_unit}"
        }

    val weightShow:String
        get() = if (weight_unit == WeightUnit.lb.name){
            val pWeight = kgToPounds(weight.toDouble())
            "${pWeight} ${weight_unit}"
        }else{
            "${weight} ${weight_unit}"
        }

     val age:Int
         get() = calculateAge(birthday*1000)










    val heightCM:Int
        get() = height.toInt()

    val weightKg:Int
        get() = if (weight_unit == WeightUnit.lb.name){
            poundsToKg(weight.toDouble())
        }else{
            weight.toInt()
        }

    val  birthdayShow:String
        get() = formatBirthday(birthday*1000)






















}
