package com.moonring.ring.ui.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.module.common.base.BaseBarFragment
import com.module.common.ext.loadServiceInit
import com.module.common.ext.showLoading
import com.module.common.support.log.LogInstance
import com.module.common.util.AppCacheKeyEnum
import com.module.common.util.CacheUtil
import com.module.common.util.GsonUtils
import com.module.common.weight.loadCallBack.EmptyCallback
import com.module.common.weight.loadCallBack.LoadingCallback
import com.moonring.ring.R
import com.moonring.ring.adapter.BleMoonringAdapter
import com.moonring.ring.bean.BleDeviceItem
import com.moonring.ring.databinding.FragmentSearchMoonringBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.moonring.CheckUtil
import com.moonring.ring.support.moonring.getDefaultProfile
import com.moonring.ring.ui.dialogfragment.DialogPairTipBottomSheet
import com.moonring.ring.viewmodule.state.SearchMoonRingViewModel
import com.moonring.ring.weight.loadCallBack.LoadingMoonringCallback
import com.sxr.sdk.ble.keepfit.aidl.UserProfile
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateAction

/**
 *    author : Administrator

 *    time   : 2024/10/15/015
 *    desc   :
 */
class SearchMoonRingFragment : BaseBarFragment<SearchMoonRingViewModel, FragmentSearchMoonringBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }

    val emptyCallback by lazy {
        EmptyCallback(HashMap<String,Any>().apply {
            put("title","")
            put("desc","")
            put("img", R.drawable.ic_empty_gantan)
            put("button","")
        })
    }
    private lateinit var loadsir: LoadService<Any>


    private val mAdapter by lazy { BleMoonringAdapter() }
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel


        setupDeviceListView()


        loadsir = loadServiceInit(
            mDatabind.recyclerView, LoadSir.Builder()
                .addCallback(LoadingMoonringCallback())
                .addCallback(emptyCallback)
                .build()
        ) {


        }
        loadsir.showCallback(LoadingMoonringCallback::class.java)
        mDatabind.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mDatabind.recyclerView.adapter = mAdapter
        mDatabind.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                outRect.bottom = ConvertUtils.dp2px(12f)
            }
        })
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val ble = mAdapter.data[position]
            LogInstance.i("ble setOnItemClickListener:${ble.bleDeviceName}")

            val macAddress = homeAppViewModel.ringInfo.value?.mac_address?:""
            if (!macAddress.isNullOrBlank()){
                if (ble.bleDeviceAddress != macAddress){

                    val dialogFragment = DialogPairTipBottomSheet {
                    }
                    dialogFragment.show(childFragmentManager, "pairTip")
                    return@setOnItemClickListener
                }
            }


            nav().navigateAction(R.id.action_searchMoonRingFragment_to_confirmPairingFragment,Bundle().apply {
                putString("ble",GsonUtils.vo2Json(ble))
            })


        }

    }



    override fun lazyLoadData() {
        super.lazyLoadData()
        homeAppViewModel.callRemoteScanDevice()
    }







    private fun setupDeviceListView() {

    }

    override fun toolbarBackVisity(): Boolean {
        return true
    }

    override fun createObserver() {

        homeAppViewModel.clearBleList()
        lifecycleScope.launchWhenStarted {
            homeAppViewModel.scanResult.collect{
                    scanResult ->

                scanResult?.let { resultList ->

                    val sortedList =resultList
                    if (sortedList.size == 0){
                        return@let
                    }
                    mViewModel.moonringszie.set(sortedList.size)

                    mAdapter.setList(sortedList)


                    loadsir.showSuccess()
                }

            }


        }
    }

    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {
        fun startSearch() {
            mViewModel.startDeviceSearch()
        }

        fun cancel() {
            nav().navigateUp()
        }
    }
}
