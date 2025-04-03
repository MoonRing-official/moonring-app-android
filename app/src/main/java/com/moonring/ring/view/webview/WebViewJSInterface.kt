package com.moonring.ring.view.webview

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.util.Base64
import android.webkit.JavascriptInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.VibrateUtils

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.module.common.support.log.LogInstance
import com.module.common.util.CacheUtil
import com.module.common.util.GsonUtils
import com.moonring.ring.bean.CallBackNativeVO
import com.moonring.ring.bean.CallNativeVO
import com.moonring.ring.bean.CommonDialogBean
import com.moonring.ring.bean.HeadersVO
import com.moonring.ring.bean.HeartBeatH5VO
import com.moonring.ring.bean.ShowInfoPopupVO
import com.moonring.ring.bean.StatusBarHeightVO
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.ui.activity.MainActivity
import com.moonring.ring.ui.dialogfragment.DialogUpairBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogWebviewCommonBottomSheet
import com.moonring.ring.ui.fragment.FullBrowserFragment
import com.moonring.ring.utils.getTodayEndTimestamp
import com.moonring.ring.utils.getTodayStartTimestamp
import com.particle.gui.ParticleWallet.getWalletAddress
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream



class WebViewJSInterface(val fragment: Fragment?=null,val activity: Activity?=null) {



    @JavascriptInterface
    fun callNative(callNative: String?): String {
        LogInstance.i("WebViewJSInterface===========${callNative}")
        val callNativeVO = GsonUtils.json2VO(callNative, CallNativeVO::class.java)
        callNativeVO?.apply {
            type?.let {
                when (it) {
                    WebViewJSInterfaceType.getStatusBarHeight.name -> {

                        return CallBackNativeVO().let {
                            it.type = WebViewJSInterfaceType.getStatusBarHeight.name
                            it.content = StatusBarHeightVO()
                                .let { vo ->
                                vo.statusBarHeight = BarUtils.getStatusBarHeight()?.let {

                                    ConvertUtils.px2dp(it.toFloat())
                                } ?: 0
                                GsonUtils.vo2Json(vo)
                            }

                            var json = GsonUtils.vo2Json(it)
                            LogInstance.i("getStatusBarHeight===============${json}")
                            json
                        }

                    }
                    WebViewJSInterfaceType.getHeaders.name -> {
                        return CallBackNativeVO().let { vo ->
                            vo.type = WebViewJSInterfaceType.getHeaders.name
                            vo.content = HeadersVO().let {
                                it.device = "android"
                                AppUtils.getAppVersionName()?.apply {
                                    it.version = this
                                }
                                it.cookieId = CacheUtil.getCookie()

                                it.isEffect = CacheUtil.getEffect()
                                it.isMusic = CacheUtil.getMusic()

                                it.address = homeAppViewModel.userProfile.value?.wallet_info?.address?:""
                                it.setxAcceptLanguage(CacheUtil.getSettingLanguage())
                                val str = GsonUtils.vo2Json(it)
                                LogInstance.i("getHeaders===============${str}")
                                str

                            }
                            GsonUtils.vo2Json(vo)
                        }
                    }


                    WebViewJSInterfaceType.UILoadSuccess.name -> {



                    }
                    WebViewJSInterfaceType.shareImage.name -> {


                    }
                    WebViewJSInterfaceType.shareByNative.name -> {


                    }
                    WebViewJSInterfaceType.handleVibrate.name -> {
                        VibrateUtils.vibrate(500)
                    }
                    WebViewJSInterfaceType.closePage.name -> {
                        LogInstance.i("=====closePage=====")

                    }
                    WebViewJSInterfaceType.closeLoading.name -> {
                        LogInstance.i("=====closeLoading=====")

                    }
                    WebViewJSInterfaceType.img2base64.name -> {

                    }
                    WebViewJSInterfaceType.getTokenIds.name -> {

                    }

                    WebViewJSInterfaceType.getMeasureHeartRateCount.name -> {


                        val heartBeatH5VO = HeartBeatH5VO(homeAppViewModel.heartBeatcount.value?:0)
                        val backNativeVO =  GsonUtils.vo2Json(CallBackNativeVO().apply {
                            type = WebViewJSInterfaceType.getMeasureHeartRateCount.name
                            content = GsonUtils.vo2Json(heartBeatH5VO)
                        })
                        LogInstance.i("backNativeVO:${backNativeVO}")
                        return backNativeVO
                    }

                    WebViewJSInterfaceType.showInfoPopup.name -> {
                        ThreadUtils.runOnUiThread {
                            val showInfoPopupVO = GsonUtils.json2VO(content, ShowInfoPopupVO::class.java)

                            fragment?.let {
                                DialogWebviewCommonBottomSheet(showInfoPopupVO){}.show(it.childFragmentManager, "ShowInfoPopupVO")
                            }
                        }


                    }
                    WebViewJSInterfaceType.getUserInfo.name -> {


                        return GsonUtils.vo2Json(CallBackNativeVO().apply {
                            type = WebViewJSInterfaceType.getUserInfo.name
                            content = GsonUtils.vo2Json(homeAppViewModel.userProfile.value)
                        })

                    }

                    WebViewJSInterfaceType.toFunction.name -> {



                    }
                    WebViewJSInterfaceType.refreshCache.name -> {

                    }
                    WebViewJSInterfaceType.getSettingLaungage.name -> {


                    }
                    else -> {
                    }
                }
            }
        }

        return ""
    }


}

public enum class WebViewJSInterfaceType(var value: String) {
    getStatusBarHeight(""),
    closePage(""),
    getHeaders(""),
    toFunction(""),
    getTokenIds(""),
    closeLoading(""),
    getUserInfo(""),
    refreshCache(""),
    UILoadSuccess(""),
    shareImage(""),
    getSettingLaungage(""),
    shareByNative(""),
    handleVibrate(""),
    img2base64(""),
    showInfoPopup(""),
    getMeasureHeartRateCount("")

    ;

}