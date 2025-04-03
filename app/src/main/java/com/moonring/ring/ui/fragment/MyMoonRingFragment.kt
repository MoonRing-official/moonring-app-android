package com.moonring.ring.ui.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.module.common.base.BaseBarFragment
import com.module.common.bean.CommonDialogBean
import com.module.common.bean.UpgradeBean
import com.module.common.support.log.LogInstance
import com.moonring.ring.BuildConfig
import com.moonring.ring.R
import com.moonring.ring.databinding.FragmentMyMoonringBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.ui.dialogfragment.DialogCommonBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogNotifyBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogPermissionBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogUpairBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogUpgradeFirmwareBottomSheet
import com.moonring.ring.utils.bluePermission
import com.moonring.ring.utils.formatTimestampMyMoonring
import com.moonring.ring.viewmodule.state.MyMoonRingViewModel
import com.moonring.ring.viewmodule.state.RingApiModel
import com.moonring.ring.viewmodule.state.UpgradeModel
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateAction
import com.moonring.jetpackmvvm.ext.parseState

/**
 *    author : Administrator

 *    time   : 2024/10/19/019
 *    desc   :
 */
class MyMoonRingFragment : BaseBarFragment<MyMoonRingViewModel, FragmentMyMoonringBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }
    private var animator: ObjectAnimator? = null
    private var imageViewReference: ImageView? = null
    val  ringApiModel: RingApiModel by activityViewModels()

    private val upgradeModel: UpgradeModel by viewModels()
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel


        displayDeviceDetails()

        configureRotationAnimator(mDatabind.ivFirmware)

    }

    private fun displayDeviceDetails() {

    }

    override fun toolbarBackVisity(): Boolean {
        return true
    }

    override fun createObserver() {


        lifecycleScope.launchWhenStarted {
            homeAppViewModel.connectionState.collect { connectionState ->
                LogInstance.i("connectionState result: $connectionState")
                if (homeAppViewModel.isClearMR.value == true){
                    return@collect
                }
                mViewModel.connectionState.set(connectionState)
                when (connectionState) {
                    2 -> {

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
        homeAppViewModel.batteryStatus.observe(viewLifecycleOwner){
                (batteryLevel, status) ->

            mViewModel.batteryLevel.set(batteryLevel)
            mViewModel.batteryStatus.set(status)

        }

        homeAppViewModel.macid.observe(viewLifecycleOwner){
            mViewModel.bleDeviceAddress.set(it)
        }

        homeAppViewModel.deviceInfo.observe(viewLifecycleOwner) { deviceInfo ->
            mViewModel.deviceInfo.value = deviceInfo
            mViewModel.version.set("${deviceInfo.version}")
        }

        homeAppViewModel.lastSynTime.observe(viewLifecycleOwner){

            mViewModel.lastSynTime.set( formatTimestampMyMoonring(it))
        }

        upgradeModel.upgradeRingResponse.observe(this){
            parseState(it,{
                stopRotation()
                val productCode = homeAppViewModel.deviceInfo.value?.productCode?:0
                val firmWare = it.data?.find { it.firmware_type == productCode }
                firmWare?.let {

                    val deviceNewVersion = it.version
                        ?.split(".")
                        ?.joinToString("")
                        ?.toIntOrNull()
                        ?: 0


                    val deviceNow =  homeAppViewModel.deviceInfo.value?.version?:-1
                    if (deviceNewVersion>deviceNow && deviceNow>-1){
                        val content = "New version of Moon Ring \n" +
                                "firmware detected (v${it.version}). \n" +
                                "Would you like to update now?"

                        val map: Map<String, String> = mapOf(
                            "title" to "Upgrade Firmware",
                            "content" to content,
                            "confirm" to "Update",
                            "cancel" to "later"
                        )
                        val dialogFragment = DialogCommonBottomSheet(map,{
                            DialogUpgradeFirmwareBottomSheet(it){}.show(childFragmentManager, "firmware")
                        },{

                        })
                        dialogFragment.show(childFragmentManager, "newfirmware")
                    }else{
                        val dialogFragment = DialogNotifyBottomSheet("Update \nNotification","You're on the latest version, \nno update needed."){

                        }
                        dialogFragment.show(childFragmentManager, "uppair")
                    }

                }


            },{
                stopRotation()
            })
        }

    }



    private fun configureRotationAnimator(imageView: ImageView) {
        imageViewReference = imageView
        animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    private fun startRotation() {

        animator?.start()
    }

    private fun stopRotation() {
        animator?.cancel()
        imageViewReference?.rotation = 0f
    }

    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {
        fun test() {

        }
        fun disConnect(){
            homeAppViewModel.callRemoteDisconnect()
        }

        fun pair(){



            if (!XXPermissions.isGranted(requireContext(), Permission.ACCESS_FINE_LOCATION,
                    Permission.BLUETOOTH_SCAN,
                    Permission.BLUETOOTH_CONNECT,
                    Permission.BLUETOOTH_ADVERTISE)) {

                val map: Map<String, String> = mapOf(
                    "title" to getString(R.string.permission_title),
                    "content" to getString(R.string.permission_content),
                    "confirm" to "Accept",
                    "cancel" to "Reject"
                )
                val dialogFragment = DialogPermissionBottomSheet(
                    map,
                    onItemAcceptClick = {
                        bluePermission ({
                            nav().navigateAction(R.id.action_myMoonRingFragment_to_searchMoonRingFragment)
                        })
                    },
                    onItemRejectClick = {

                    })
                dialogFragment.show(childFragmentManager, "permission")
            } else {
                bluePermission ({
                    nav().navigateAction(R.id.action_myMoonRingFragment_to_searchMoonRingFragment)
                })

            }


        }

        fun unpair(){
            val dialogFragment = DialogUpairBottomSheet(CommonDialogBean().apply {
                content = "You are about to unpair your Moon Ring. Once the process is complete, any unsynced data will be permanently lost. Do you wish to continue?"
                confirm = "Unpair"
                cancel = getString(R.string.cancel)

            }){
                disConnect()
                ringApiModel.unBoundRing()
            }
            dialogFragment.show(childFragmentManager, "uppair")
        }

        fun  getOtaInfo(){
            startRotation()

            upgradeModel.getRingVersion()




        }

    }
}
