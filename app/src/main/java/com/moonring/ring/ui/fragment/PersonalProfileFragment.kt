package com.moonring.ring.ui.fragment

import android.graphics.Gainmap
import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.module.common.base.BaseBarFragment
import com.module.common.enums.Gender
import com.module.common.enums.WeightUnit
import com.module.common.network.apiService
import com.module.common.support.log.LogInstance
import com.module.common.util.CacheUtil
import com.module.common.util.GsonUtils
import com.module.common.util.feetInchesToCm
import com.module.common.util.poundsToKg
import com.moonring.ring.R
import com.moonring.ring.databinding.FragmentPersonalProfileBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.ui.dialogfragment.BirthdaySelectionBottomSheet
import com.moonring.ring.ui.dialogfragment.GenderSelectionBottomSheet
import com.moonring.ring.ui.dialogfragment.HeightSelectionBottomSheet
import com.moonring.ring.ui.dialogfragment.SetNameBottomSheet
import com.moonring.ring.ui.dialogfragment.WeightSelectionBottomSheet
import com.moonring.ring.utils.calculateBirthTimestamp
import com.moonring.ring.utils.mapToBleUseProfile
import com.moonring.ring.viewmodule.state.PersonalProfileViewModel
import com.sxr.sdk.ble.keepfit.aidl.BleClientOption
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateAction
import java.util.Calendar

/**
 *    author : Administrator

 *    time   : 2024/10/19/019
 *    desc   :
 */
class PersonalProfileFragment : BaseBarFragment<PersonalProfileViewModel, FragmentPersonalProfileBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }

    val userProfile by lazy {
        homeAppViewModel.userProfile.value!!
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel

       mViewModel.userProfile.value = userProfile
    }

    override fun toolbarBackVisity(): Boolean {
        return true
    }



    override fun createObserver() {

        homeAppViewModel.setUserInfoResult.observe(viewLifecycleOwner){
                result->
            if (result == 1){

                saveUserProfile()
            }
        }
    }
    fun saveUserProfile(){
        CacheUtil.setUserProfile( GsonUtils.vo2Json(userProfile))
        homeAppViewModel.userProfile.value = userProfile
        mViewModel.userProfile.value = userProfile
    }
    fun sendToDevice(){
        val user = userProfile?.let { mapToBleUseProfile(it) }
        val opt = BleClientOption(user, null, null)
        val result = homeAppViewModel.callRemoteSetOption(opt)
        LogInstance.i("result:$result")
        homeAppViewModel.callRemoteSetUserInfo()
    }

    fun updateProflie(){
        mViewModel.viewModelScope.launch {
            kotlin.runCatching {
                val response =  apiService.updateUserProfile(userProfile)
                val userResponse =  apiService.getUserInfo()
                if (userResponse.code == 0){
                    homeAppViewModel.userProfile.value = userResponse.data
                }


            }.onSuccess {

            }.onFailure {


            }
        }
    }

    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {



        fun  setName(){
            val dialogFragment = SetNameBottomSheet(btnTitle = "Confirm"){ userName ->


                userProfile.username = userName
                saveUserProfile()
                updateProflie()

            }
            dialogFragment.show(childFragmentManager, "setName")
        }
        fun gender() {
            val dialogFragment = GenderSelectionBottomSheet(btnTitle = "Confirm",userProfile){value, gender ->

                mViewModel.gender.set(gender)
                when(value){
                    Gender.female.value ->{
                        userProfile.gender = Gender.female.name
                    }
                    Gender.male.value ->{
                        userProfile.gender = Gender.male.name
                    }
                    Gender.non_binary.value ->{
                        userProfile.gender = Gender.non_binary.name
                    }

                }
                saveUserProfile()
                sendToDevice()
                updateProflie()

            }
            dialogFragment.show(childFragmentManager, "gender")
        }
        fun height() {
            val dialogFragment = HeightSelectionBottomSheet(btnTitle = "Confirm",userProfile){ height,feet,inches,unit ->


                LogInstance.i("height: ${height}  ${feet}  ${inches} ${unit}")

                if (unit == "cm") {

                    mViewModel.height.set("${height}")
                    mViewModel.heightUnit.set("${unit}")

                    userProfile.height_unit = unit
                    userProfile.height = height.toString()
                    saveUserProfile()
                    sendToDevice()

                } else if (unit == "ft") {
                    userProfile.height_unit = unit
                    userProfile.height =  feetInchesToCm(feet,inches).toString()
                    saveUserProfile()
                    sendToDevice()
                }

                updateProflie()



            }
            dialogFragment.show(childFragmentManager, "height")
        }
        fun weight() {
            val dialogFragment = WeightSelectionBottomSheet(btnTitle = "Confirm",userProfile = userProfile){ weight,unit ->

                LogInstance.i("weight: ${weight} ${unit}")

                mViewModel.weight.set("${weight}")
                mViewModel.weightUnit.set("${unit}")

                userProfile.weight_unit = unit
                if (userProfile.weight_unit == WeightUnit.lb.name){
                    userProfile.weight ="${poundsToKg(weight.toDouble())}"
                }else{
                    userProfile.weight ="${weight}"
                }

                saveUserProfile()
                sendToDevice()
                updateProflie()

            }
            dialogFragment.show(childFragmentManager, "weight")
        }
        fun birthday(){
            val dialogFragment = BirthdaySelectionBottomSheet(btnTitle = "Confirm",userProfile){ year,month,day ->


                LogInstance.i("date:year ${year}-${month}-${day}")
                mViewModel.year.set(year)
                mViewModel.month.set(month)
                mViewModel.day.set(day)




                userProfile.birthday = calculateBirthTimestamp(year,month,day)
                saveUserProfile()
                sendToDevice()
                updateProflie()


            }
            dialogFragment.show(childFragmentManager, "date")
        }
    }
}
