package com.moonring.ring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ColorUtils
import com.google.android.material.tabs.TabLayout
import com.module.common.base.BaseBarFragment
import com.module.common.bean.UpgradeBean
import com.module.common.network.apiService
import com.module.common.support.log.LogInstance
import com.module.common.util.CacheUtil
import com.module.common.util.time.RxTimer
import com.module.common.weight.viewpager.FragmentPagerAdapter
import com.moonring.ring.BuildConfig
import com.moonring.ring.R
import com.moonring.ring.databinding.FragmentMainBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.ui.dialogfragment.DialogCommonBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogNotifyBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogUpgradeFirmwareBottomSheet
import com.moonring.ring.utils.getTodayEndTimestamp
import com.moonring.ring.utils.getTodayStartTimestamp
import com.moonring.ring.utils.moonPoints_url
import com.moonring.ring.utils.showUpgradleDialog
import com.moonring.ring.viewmodule.state.GoalApiModel
import com.moonring.ring.viewmodule.state.MainViewModel
import com.moonring.ring.viewmodule.state.RingApiModel
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.ext.parseState
import com.moonring.jetpackmvvm.state.ResultState
import java.lang.StringBuilder


/**
 *    author : Administrator

 *    time   : 2024/3/7/007
 *    desc   :
 */
class MainFragment : BaseBarFragment<MainViewModel, FragmentMainBinding>() {
    lateinit  var mPagerAdapter: FragmentPagerAdapter<Fragment>

    val  ringApiModel: RingApiModel by activityViewModels()
    val  goalApiModel: GoalApiModel by viewModels()
    val webviewFragment = FullBrowserFragment.newInstance(moonPoints_url,"false")

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = ProxyClick()
        mPagerAdapter = FragmentPagerAdapter(this)

        mPagerAdapter.addFragment(MoonringInfoFragment(),"Health" )

        mPagerAdapter.addFragment(webviewFragment,"Moon Points" )
        mPagerAdapter.addFragment(ProfileFragment(),"Profile" )
        mDatabind.viewpager.adapter = mPagerAdapter




        mDatabind.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {

            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

        setTabLayoutInfo()


    }

    override fun initData() {
        super.initData()
        initUserData()



    }

    override fun createObserver() {
        super.createObserver()
        homeAppViewModel.userProfile.observe(this){
            if (it!=null){
                CacheUtil.setUser(it)
            }
        }

        homeAppViewModel.changeTabIndex.observe(this){
            if (it>-1 && mDatabind.tabLayout.tabCount>it){
                mDatabind.viewpager.currentItem = it

            }
        }

        mViewModel.upgradeRingResponse.observe(this){
            parseState(it,{
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
                    }
                }


            },{

            })
        }

        homeAppViewModel.ringInfo.observe(this){
            ringinfo->


            val mac_address = ringinfo?.mac_address?:""
            LogInstance.i("ringInfo observe:${mac_address}")
            if (mac_address.isNullOrBlank()){
                homeAppViewModel.callRemoteDisconnect(true)
            }else{
                if (homeAppViewModel.getConnectMac()!=mac_address){
                    homeAppViewModel.callRemoteDisconnect(true)
                }

            }
        }
    }

    fun setTabLayoutInfo(){
        for (i in 0 until  mPagerAdapter.count) {
            mDatabind.tabLayout.addTab(mDatabind.tabLayout.newTab())
        }


        mDatabind.tabLayout.tabRippleColor = null
        mDatabind.tabLayout.setupWithViewPager(mDatabind.viewpager)
        setupTabIcons(mDatabind.tabLayout)
        changeTabIcons(mDatabind.tabLayout,0)

        mDatabind.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                val position = tab.position

                changeTabIcons(mDatabind.tabLayout,position)
                if (position == 1){

                    mViewModel.viewModelScope.launch {
                        kotlin.runCatching {

                            val count = DatabaseUtils.fetchActiveHeartRateTestCount(fromTimestamp = getTodayStartTimestamp(), toTimestamp = getTodayEndTimestamp())
                            if (count>0){
                                homeAppViewModel.heartBeatcount.value = count
                            }
                        }
                    }
                    webviewFragment.resumeBlockoorJS()
                }



            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }




    private fun setupTabIcons(tabLayout: TabLayout) {
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            if (tab != null) {
                val customView = LayoutInflater.from(requireContext()).inflate(R.layout.main_tab, null)


                if (customView != null) {
                    val tabIcon = customView.findViewById<ImageView>(R.id.tab_icon)
                    val tabText = customView.findViewById<TextView>(R.id.tab_text)
                    tabText.setTextColor(ColorUtils.getColor(R.color.tab_n_sel))
                    when (i) {
                        0 -> {
                            tabIcon.setImageResource(R.drawable.tab_health)
                            tabIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color._9A9DAC), android.graphics.PorterDuff.Mode.SRC_IN)
                            tabText.text = "Health"
                        }

                        1 -> {
                            tabIcon.setImageResource(R.drawable.tab_discovery)
                            tabIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color._9A9DAC), android.graphics.PorterDuff.Mode.SRC_IN)
                            tabText.text = "Moon Points"
                        }

                        2 -> {
                            tabIcon.setImageResource(R.drawable.tab_profile)
                            tabIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color._9A9DAC), android.graphics.PorterDuff.Mode.SRC_IN)
                            tabText.text = "Profile"
                        }
                    }
                }
                tab.customView = customView
            }
        }
    }



    private fun changeTabIcons(tabLayout: TabLayout,position:Int){


        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            if (tab != null) {
                val customView = tab.customView
                if (customView != null) {
                    val tabIcon = customView.findViewById<ImageView>(R.id.tab_icon)
                    val tabText = customView.findViewById<TextView>(R.id.tab_text)
                    tabText.setTextColor(ColorUtils.getColor(R.color.tab_n_sel))
                    when (i) {
                        0 -> {
                            tabIcon.setImageResource(R.drawable.tab_health)
                            tabIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color._9A9DAC), android.graphics.PorterDuff.Mode.SRC_IN)
                            tabText.text = "Health"
                        }

                        1 -> {
                            tabIcon.setImageResource(R.drawable.tab_discovery)
                            tabIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color._9A9DAC), android.graphics.PorterDuff.Mode.SRC_IN)
                            tabText.text = "Moon Points"
                        }

                        2 -> {
                            tabIcon.setImageResource(R.drawable.tab_profile)
                            tabIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color._9A9DAC), android.graphics.PorterDuff.Mode.SRC_IN)
                            tabText.text = "Profile"
                        }
                    }
                }
            }
        }


        val tab = tabLayout.getTabAt(position)
        if (tab != null) {
            val customView = tab.customView
            if (customView != null) {
                val tabIcon = customView.findViewById<ImageView>(R.id.tab_icon)
                val tabText = customView.findViewById<TextView>(R.id.tab_text)
                when (position) {
                    0 -> {
                        tabIcon.setImageResource(R.drawable.tab_health)
                        tabIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color._5D5FEF), android.graphics.PorterDuff.Mode.SRC_IN)
                        tabText.text = "Health"
                        tabText.setTextColor(ColorUtils.getColor(R.color._5D5FEF))

                    }

                    1 -> {
                        tabIcon.setImageResource(R.drawable.tab_discovery)
                        tabIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color._FF903E), android.graphics.PorterDuff.Mode.SRC_IN)
                        tabText.text = "Moon Points"
                        tabText.setTextColor(ColorUtils.getColor(R.color._FF903E))
                    }

                    2 -> {
                        tabIcon.setImageResource(R.drawable.tab_profile)
                        tabIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color._F45EC1), android.graphics.PorterDuff.Mode.SRC_IN)
                        tabText.text = "Profile"
                        tabText.setTextColor(ColorUtils.getColor(R.color._F45EC1))
                    }
                }

            }
        }
    }


    fun initUserData() {

        mViewModel.viewModelScope.launch {
            kotlin.runCatching {
                val userResponse = apiService.getUserInfo()
                homeAppViewModel.userProfile.value = userResponse.data
                ringApiModel.getBoundRing()
            }.onSuccess {



            }.onFailure {

            }

        }
        goalApiModel.getMovementGoal()

    }

    inner class ProxyClick {

    }
}