package com.moonring.ring


import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.Utils
import com.module.common.app.App
import com.module.common.app.application.BaseApplicationImp
import com.module.common.app.application.ModuleConfig.Companion.MODULELIST
import com.module.common.base.action.ToastAction

import com.hm.lifecycle.api.ApplicationLifecycleManager
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.module.common.data.moudle.bean.ApiResponse
import com.module.common.network.IResponseError
import com.module.common.network.ResponseInstance
import com.module.common.weight.loadCallBack.EmptyCallback
import com.module.common.weight.loadCallBack.ErrorCallback
import com.module.common.weight.loadCallBack.LoadingCallback
import com.moonring.ring.support.manager.ActivityManager
import com.moonring.ring.support.partical.ParticleInitUtils
import com.moonring.ring.viewmodule.event.HomeAppViewModel
import com.moonring.ring.viewmodule.event.UploadDataModel
import com.moonring.jetpackmvvm.support.logout.restartApp
import network.particle.chains.ChainInfo

val homeAppViewModel:HomeAppViewModel by lazy {
    MyApplication.homeAppViewModelInstance
}

val uploadDataModel:UploadDataModel by lazy {
    MyApplication.uploadDataModelInstance
}
class MyApplication : App() ,ToastAction{
    companion object {
        lateinit var homeAppViewModelInstance: HomeAppViewModel
        lateinit var uploadDataModelInstance: UploadDataModel
    }
    override fun onCreate() {
        super.onCreate()


        if (BuildConfig.DEBUG) {

            ARouter.openLog()

            ARouter.openDebug()
        }





        ActivityManager.getInstance().init(this)
        ApplicationLifecycleManager.init()
        ApplicationLifecycleManager.onCreate(this)
        LoadSir.beginBuilder()
            .addCallback(LoadingCallback())
            .addCallback(ErrorCallback())
            .addCallback(EmptyCallback())
            .setDefaultCallback(SuccessCallback::class.java)
            .commit()

        initModuleConfit()

        homeAppViewModelInstance = getAppViewModelProvider()[HomeAppViewModel::class.java]
        uploadDataModelInstance = getAppViewModelProvider()[UploadDataModel::class.java]












        ResponseInstance.addResponseListener(object : IResponseError {
            override fun callback(api: ApiResponse<*>) {

                if (api.status_code == 401){
                    restartApp(App.instance)
                }

            }})
        ParticleInitUtils.initWallet(this, ChainInfo.Ethereum)

        Utils.init(this)
    }

    private fun initModuleConfit() {
        for (modules in MODULELIST) {
            try {
                val clz = Class.forName(modules)
                val obj = clz.newInstance()
                if (obj is BaseApplicationImp) {
                    obj.onCreate(this)
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }
        }
    }


    override fun onTerminate() {
        super.onTerminate()
        ApplicationLifecycleManager.onTerminate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        ApplicationLifecycleManager.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        ApplicationLifecycleManager.onTrimMemory(level)
    }
}