package com.moonring.ring

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.launcher.ARouter
import com.moonring.ring.databinding.ActivityWelcomeBinding


import com.module.common.base.BaseActivity
import com.module.common.support.RoutePath

import com.module.common.util.*
import com.moonring.ring.ui.activity.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.ext.delay

import java.util.*


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class WelcomeActivity : BaseActivity<BaseViewModel, ActivityWelcomeBinding>() {








    var count = 0

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)


        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT !== 0) {
            finish()
            return
        }
        mDatabind.click = ProxyClick()
        mDatabind.welcomeBaseview.visibility = View.GONE


        homeAppViewModel.startAndBindService(this)

        mViewModel.viewModelScope.launch {
            delay(3000)
            toHomePage()
        }





    }








    private fun toHomePage() {





        startActivity(Intent(this,MainActivity::class.java))
        finishWithTransition()
    }

    override fun createObserver() {
        super.createObserver()





    }


    override fun initData() {
        super.initData()


    }









    inner class ProxyClick {
        fun toMain() {








        }
    }

    fun finishWithTransition() {
        finish()

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }


}