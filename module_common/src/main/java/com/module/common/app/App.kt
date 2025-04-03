package com.module.common.app

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.webkit.WebView
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.ProcessUtils
import com.module.common.BuildConfig
import com.module.common.R
import com.module.common.app.event.AppViewModel
import com.module.common.ext.getProcessName
import com.module.common.weight.loadCallBack.EmptyCallback
import com.module.common.weight.loadCallBack.ErrorCallback
import com.module.common.weight.loadCallBack.LoadingCallback
import com.module.common.weight.toast.ToastInterceptor
import com.module.common.weight.toast.ToastUtils
import com.module.common.weight.toast.ToastYuliV3TextView
import com.module.common.weight.toast.style.ToastBlackStyle
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.tencent.mmkv.MMKV
import com.moonring.jetpackmvvm.base.BaseApp
import com.tencent.bugly.crashreport.CrashReport




val appViewModel: AppViewModel by lazy { App.appViewModelInstance }

val appContext: Application by lazy { App.instance }

open class App : BaseApp() {

    companion object {
        lateinit var instance: App
        lateinit var appViewModelInstance: AppViewModel
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this.filesDir.absolutePath + "/mmkv")
        instance = this
        appViewModelInstance = getAppViewModelProvider().get(AppViewModel::class.java)
        MultiDex.install(this)



        LoadSir.beginBuilder()
            .addCallback(LoadingCallback())
            .addCallback(ErrorCallback())
            .addCallback(EmptyCallback())
            .setDefaultCallback(SuccessCallback::class.java)
            .commit()

        val context = applicationContext

        val packageName = context.packageName
        if (BuildConfig.PACKAGING_CONFIG!=4){

            val processName = getProcessName(android.os.Process.myPid())

            val strategy = CrashReport.UserStrategy(context)
            strategy.isUploadProcess = processName == null || processName == packageName
        }




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = ProcessUtils.getCurrentProcessName()
            if (packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
            }
        }



        ToastUtils.init(instance, object : ToastBlackStyle(instance) {
            override fun getCornerRadius(): Int {
                return instance.getResources()?.getDimension(R.dimen.button_round_size1)?.toInt()?:30
            }
            override fun getYOffset(): Int {
                return 0
            }
        })


        ToastUtils.setView(ToastYuliV3TextView(instance))





        ToastUtils.setToastInterceptor(ToastInterceptor())

    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.fontScale !== 1f) {

            resources
        }
        super.onConfigurationChanged(newConfig)
    }


    override fun getResources(): Resources? {
        val res: Resources? = super.getResources()

        if (res != null) {
            if (res.getConfiguration().fontScale !== 1f) {
                val config: Configuration = res.getConfiguration()
                if (config != null && config.fontScale !== 1.0f) {
                    config.fontScale = 1.0f
                    res.updateConfiguration(config, res.getDisplayMetrics())
                }
            }
        }
        return res
    }

}
