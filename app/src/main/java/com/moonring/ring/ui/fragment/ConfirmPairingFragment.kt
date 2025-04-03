package com.moonring.ring.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.module.common.base.BaseBarFragment
import com.module.common.bean.UserProfileMoonRing
import com.module.common.bean.login.RingInfo
import com.module.common.support.config.AppConfig
import com.module.common.support.log.LogInstance
import com.module.common.util.AppCacheKeyEnum
import com.module.common.util.CacheUtil
import com.module.common.util.GsonUtils
import com.module.common.util.time.RxTimer
import com.moonring.ring.BuildConfig
import com.moonring.ring.R
import com.moonring.ring.bean.BleDeviceItem
import com.moonring.ring.databinding.FragmentConfirmPairingBinding
import com.moonring.ring.enums.ConnectionState
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.moonring.LoadingManager
import com.moonring.ring.support.moonring.getDefaultProfile
import com.moonring.ring.ui.dialogfragment.DialogCommonBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogPairTipBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogUpairBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogUpgradeFirmwareBottomSheet
import com.moonring.ring.utils.bluePermission
import com.moonring.ring.utils.mapToBleUseProfile
import com.moonring.ring.viewmodule.state.ConfirmPairingViewModel
import com.moonring.ring.viewmodule.state.RingApiModel
import com.moonring.ring.viewmodule.state.SharedChartDataModel
import com.sxr.sdk.ble.keepfit.aidl.BleClientOption
import com.moonring.jetpackmvvm.ext.isInBackStack
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateAction
import com.moonring.jetpackmvvm.ext.parseState

/**
 *    author : Administrator

 *    time   : 2024/10/15/015
 *    desc   :
 */
class ConfirmPairingFragment : BaseBarFragment<ConfirmPairingViewModel, FragmentConfirmPairingBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }
    val ble by lazy {
        GsonUtils.json2VO(  arguments?.getString("ble")?:"",BleDeviceItem::class.java)?:BleDeviceItem()
    }

    private val loadingManager by lazy {
        LoadingManager()
    }
    val  ringApiModel: RingApiModel by viewModels()
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel
        mViewModel.bleDeviceItem.value = ble

        loadingManager.configureRotationAnimator(mDatabind.ivStatusIcon)


    }



    override fun toolbarBackVisity(): Boolean {
        return true
    }

    override fun createObserver() {

        ringApiModel.boundRingResult.observe(viewLifecycleOwner) {
            parseState(it,{

                ringApiModel.afterBondRing()
            },{

                mDatabind.loading.visibility = View.GONE
                loadingManager.stopRotation()

                val dialogFragment = DialogPairTipBottomSheet {
                }
                dialogFragment.show(childFragmentManager, "pairTip")
            })

        }
        ringApiModel.getBoundRingResult.observe(viewLifecycleOwner){
            parseState(it,{

                val lastSynTime = it.data?.last_synced?:0L

                val sevenDaysInSeconds = 7 * 24 * 60 * 60
                val currentTimeInSeconds = System.currentTimeMillis() / 1000

                val isWithinLastSevenDays = lastSynTime >= (currentTimeInSeconds - sevenDaysInSeconds)

                LogInstance.i("isWithinLastSevenDays:${isWithinLastSevenDays}")


                homeAppViewModel.ringInfo.value =  it.data

                if (isWithinLastSevenDays) {

                    homeAppViewModel.isClearMR.value = true
                    surebingLogic()

                }else{
                    surebingLogic()
                }





            },{

            })

        }
        lifecycleScope.launchWhenStarted {
            homeAppViewModel.connectionState.collect { connectionState ->
                LogInstance.i("connectionState result: $connectionState ${homeAppViewModel.isClearMR.value}")

                if (homeAppViewModel.isClearMR.value == true
                    && connectionState == ConnectionState.Connected.value){

                    homeAppViewModel.callRemoteSetDeviceMode(4)
                    RxTimer().timer(2000){
                        homeAppViewModel.isClearMR.value = false
                    }
                    return@collect
                }
                mViewModel.connectionState.set(connectionState)
                when (connectionState) {
                    2 -> {

                        mViewModel.isModifyBleIng.set(false)

                        mDatabind.loading.visibility = View.GONE
                        loadingManager.stopRotation()


                        if (nav().isInBackStack(R.id.myMoonRingFragment)) {
                            nav().popBackStack(R.id.myMoonRingFragment, false)
                        }else if (nav().isInBackStack(R.id.mainfragment)){
                            nav().popBackStack(R.id.mainfragment, false)
                        }else{
                            sendToDevice()
                            nav().navigateAction(R.id.action_confirmPairingFragment_to_mainfragment)
                        }





                    }
                    1 -> {


                    }
                    0 -> {





                    }
                    else -> {

                    }
                }
            }
        }
    }


    fun surebingLogic(){

        homeAppViewModel.callRemoteConnect(ble.bleDeviceName, ble.bleDeviceAddress)
        CacheUtil.setCommonJson(AppCacheKeyEnum.bleDevice.name, GsonUtils.vo2Json(ble))
        homeAppViewModel.bleDeviceItem.value = ble
    }

    fun bingLogic(){
        ringApiModel.getBoundRing()
        homeAppViewModel.callRemoteConnect(ble.bleDeviceName, ble.bleDeviceAddress)
        CacheUtil.setCommonJson(AppCacheKeyEnum.bleDevice.name, GsonUtils.vo2Json(ble))
        homeAppViewModel.bleDeviceItem.value = ble

        homeAppViewModel.allHeartRates.clear()
    }
    fun sendToDevice(){

        val user = homeAppViewModel.userProfile.value?.let { mapToBleUseProfile(it) }
        val opt = BleClientOption(user, null, null)
        val result = homeAppViewModel.callRemoteSetOption(opt)
        LogInstance.i("result:$result")
        homeAppViewModel.callRemoteSetUserInfo()
    }
    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {
        fun confirmPairing() {
            mViewModel.pairDevice()
        }

        fun pair(){
            mDatabind.loading.visibility = View.VISIBLE
            loadingManager.startRotation()

            val macAddress = homeAppViewModel.ringInfo.value?.mac_address?:""
            if (!macAddress.isNullOrBlank()){
                bingLogic()
                return
            }
            LogInstance.i("start boundRing")
            ringApiModel.boundRing(RingInfo( ble.bleDeviceAddress,0))

        }

        fun cancel(){
            nav().navigateUp()
        }
    }
}
