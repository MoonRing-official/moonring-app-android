package com.moonring.ring.ui.activity

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.ServiceUtils
import com.moonring.ring.R
import com.module.common.app.isLogin
import com.module.common.base.action.ObserverBugAction
import com.module.common.bean.UpgradeBean
import com.module.common.support.RoutePath
import com.module.common.support.log.LogInstance
import com.module.common.util.time.RxTimer
import com.moonring.ring.BuildConfig
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.moonring.BluetoothBroadcastReceiver
import com.moonring.ring.support.moonring.CheckUtil
import com.moonring.ring.support.router.interceptBackPressed
import com.moonring.ring.utils.isLoginAccount
import com.moonring.ring.utils.showUpgradleDialog
import com.moonring.ring.viewmodule.state.MainViewModel
import com.moonring.jetpackmvvm.state.ResultState

/**
 * author : Administrator
 *
 *
 * time   : 2024/3/6/006
 * desc   :
 */

@Route(path = RoutePath.PATH_HOME)
class MainActivity : AppCompatActivity(), ObserverBugAction {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.main_navation)

        if (isLoginAccount()) {
            graph.startDestination = R.id.mainfragment
        } else {
            graph.startDestination = R.id.loginfragment
        }


        val navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)


        mainViewModel.getVersion()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {



            override fun handleOnBackPressed() {
                val nav = Navigation.findNavController(this@MainActivity, R.id.host_fragment)

                if (nav.currentDestination != null && (nav.currentDestination!!.id != R.id.mainfragment)) {

                    nav.navigateUp()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()


        val serviceExit = ServiceUtils.isServiceRunning(SampleBleService::class.java)
        LogInstance.i("serviceExit:${serviceExit}")
        if (!serviceExit) {

            homeAppViewModel.startAndBindService(this)
        }
    }
    fun createObserver(){


        mainViewModel.upgradeResponse.observe(this, Observer {
            fixObserverErrorLifecycle()
            when (it) {
                is ResultState.Success -> {
                    LogInstance.i("upgradeResponse=============${it}")
                    it?.data?.let{
                        showUpgradleDialog(this@MainActivity,it)
                    }
                }
                is ResultState.Error ->{

                }
                else -> {}
            }
        })
    }


    override fun fixObserverErrorLifecycle() {

    }

    override fun onDestroy() {
        super.onDestroy()

    }
}