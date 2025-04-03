package com.module.common.base

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.module.common.R
import com.module.common.ext.initHomeModlueClose
import com.module.common.ext.loadServiceInit
import com.module.common.support.log.LogInstance
import com.module.common.weight.loadCallBack.EmptyCallback
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateUpNoRepeat


abstract class BaseBarFragment <VM : BaseViewModel, DB : ViewDataBinding> : BaseFragment<VM, DB>() {

    private lateinit var mLoadsir: LoadService<Any>



    override fun initData() {
        super.initData()


        obtainTitleBar(view as ViewGroup)?.let { toolbar ->
            when (toolbar.id) {

                R.id.toolbar -> {
                    toolbar.initHomeModlueClose(mDatabind, getToolbarTitle(), backImg = R.drawable.toolbar_back) {
                        nav().navigateUpNoRepeat()
                    }
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }

        bindRecycleView()?.let {
            mLoadsir = loadServiceInit(
                it, LoadSir.Builder()
                    .addCallback(emptyDataCallback())
                    .build()
            ) {


            }
        }

    }
    open fun toolbarBackVisity():Boolean{
        return true
    }
    open fun toolbarNav():Boolean{
        return false
    }


    fun  bindRecycleView(): RecyclerView?{
        return null
    }

    fun  emptyDataCallback(): Callback{
        return EmptyCallback()
    }

    protected fun showByData(dataList:MutableList<*>){

        if (this::mLoadsir.isInitialized){
            if (dataList.size>0){
                mLoadsir.showSuccess()
            }else{
                mLoadsir.showCallback(EmptyCallback::class.java)
            }
        }

    }

    protected open fun openBack(){
        mDatabind.root.setFocusableInTouchMode(true)
        mDatabind.root.requestFocus()
        mDatabind.root.setOnKeyListener({ v, keyCode, event ->

            LogInstance.i("root=======${keyCode}====${event.getAction()}")
            return@setOnKeyListener true
        })
    }

    override fun onResume() {
        super.onResume()

    }


    open fun isDarkMode(): Boolean {
        return true
    }

    open fun getToolbarTitle(): String {
        return ""
    }

    open fun getTitleTextSize(): Float {
        return 24f
    }

    open fun getTitleTextColor(): Int {
        return Color.parseColor("#FFFFFF")
    }
}