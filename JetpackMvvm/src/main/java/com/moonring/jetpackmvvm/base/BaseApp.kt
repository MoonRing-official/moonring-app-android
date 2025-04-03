package com.moonring.jetpackmvvm.base

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner



open class BaseApp : Application() {

    private lateinit var mAppViewModelStore: ViewModelStore

    private var mFactory: ViewModelProvider.Factory? = null



    override fun onCreate() {
        super.onCreate()
        mAppViewModelStore = ViewModelStore()
    }

    /**
     * 获取一个全局的ViewModel
     */
    fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(mAppViewModelStore, this.getAppFactory())
    }

    private fun getAppFactory(): ViewModelProvider.Factory {
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        }
        return mFactory as ViewModelProvider.Factory
    }
}