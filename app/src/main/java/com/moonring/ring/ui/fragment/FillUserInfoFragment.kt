package com.moonring.ring.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.viewModelScope
import com.module.common.base.BaseBarFragment
import com.module.common.bean.UserProfileMoonRing
import com.module.common.enums.Gender
import com.module.common.enums.WeightUnit
import com.module.common.ext.initHomeModlueClose
import com.module.common.util.CacheUtil
import com.module.common.util.GsonUtils
import com.moonring.ring.R
import com.module.common.network.apiService
import com.module.common.util.feetInchesToCm
import com.module.common.util.poundsToKg
import com.moonring.ring.databinding.FragmentFillUserInfoBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.ui.dialogfragment.BirthdaySelectionBottomSheet
import com.moonring.ring.ui.dialogfragment.GenderSelectionBottomSheet
import com.moonring.ring.ui.dialogfragment.HeightSelectionBottomSheet
import com.moonring.ring.ui.dialogfragment.WeightSelectionBottomSheet
import com.moonring.ring.utils.calculateBirthTimestamp
import com.moonring.ring.viewmodule.state.FillUserInfoViewModel
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateAction
import com.moonring.jetpackmvvm.ext.navigateUpNoRepeat

/**
 *    author : Administrator

 *    time   : 2024/9/26/026
 *    desc   :
 */
class FillUserInfoFragment : BaseBarFragment<FillUserInfoViewModel, FragmentFillUserInfoBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }

    private val userProfile by lazy {
        UserProfileMoonRing()
    }
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel

        mDatabind.etName.addTextChangedListener {
            mViewModel.name.set(it.toString())
        }




    }


    override fun initData() {
        super.initData()
        obtainTitleBar(view as ViewGroup)?.let { toolbar ->
            when (toolbar.id) {
                com.module.common.R.id.toolbar -> {
                    toolbar.initHomeModlueClose(mDatabind, getToolbarTitle(), backImg = com.module.common.R.drawable.toolbar_back) {
                        mViewModel.pageStatus.set(0)
                        it.visibility = View.INVISIBLE
                    }
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    toolbar.visibility = View.INVISIBLE
                }
            }
        }

    }
    override fun createObserver() {

    }

    override fun getToolbarTitle(): String {
        return ""
    }

    fun saveUserProfile(){
        CacheUtil.setUserProfile( GsonUtils.vo2Json(userProfile))
    }

    inner class ProxyClick {






        fun next(){

            if (mViewModel.pageStatus.get() == 0){
                userProfile.username = mDatabind.etName.text.toString()
                saveUserProfile()
                mViewModel.pageStatus.set(1)
                obtainTitleBar(view as ViewGroup)?.visibility = View.VISIBLE
                return
            }
            if ( mViewModel.pageStatus.get() == 1){

                mViewModel.viewModelScope.launch {
                    kotlin.runCatching {
                        userProfile.avatar_url = homeAppViewModel.userProfile.value?.avatar_url?:""
                       val response =  apiService.updateUserProfile(userProfile)
                        val userResponse =  apiService.getUserInfo()
                        if (userResponse.code == 0){
                            homeAppViewModel.userProfile.value = userResponse.data
                        }


                    }.onSuccess {
                        nav().navigateAction(R.id.action_fillUserInfoFragment_to_pairMoonRingFragment)
                    }.onFailure {


                    }
                }


            }
        }
        fun gender() {
            val dialogFragment = GenderSelectionBottomSheet(userProfile = userProfile){value, gender ->

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

            }
            dialogFragment.show(childFragmentManager, "gender")
        }
        fun height() {
            val dialogFragment = HeightSelectionBottomSheet(btnTitle = "Next",userProfile = userProfile){ height,feet,inches,unit ->


                mViewModel.heightUnit.set("${unit}")
                if (unit == "cm") {

                    mViewModel.height.set("${height}")

                    userProfile.height_unit = unit
                    userProfile.height = height.toString()
                    saveUserProfile()


                } else if (unit == "ft") {
                    mViewModel.feet.set("${feet}")
                    mViewModel.inches.set("${inches}")

                    userProfile.height_unit = unit
                    userProfile.height =  feetInchesToCm(feet,inches).toString()
                    saveUserProfile()
                }


            }
            dialogFragment.show(childFragmentManager, "height")
        }
        fun weight() {
            val dialogFragment =  WeightSelectionBottomSheet(btnTitle = "Next",userProfile){ weight,unit ->


                mViewModel.weight.set("${weight}")
                mViewModel.weightUnit.set("${unit}")

                userProfile.weight_unit = unit

                if (userProfile.weight_unit == WeightUnit.lb.name){
                    userProfile.weight ="${poundsToKg(weight.toDouble())}"
                }else{
                    userProfile.weight ="${weight}"
                }
                saveUserProfile()

            }
            dialogFragment.show(childFragmentManager, "weight")
        }
        fun birthday(){
            val dialogFragment = BirthdaySelectionBottomSheet(btnTitle = "Next",userProfile){ year,month,day ->


                mViewModel.year.set(year)
                mViewModel.month.set(month)
                mViewModel.day.set(day)



                userProfile.birthday = calculateBirthTimestamp(year,month,day)
                saveUserProfile()



            }
            dialogFragment.show(childFragmentManager, "date")
        }
    }
}
