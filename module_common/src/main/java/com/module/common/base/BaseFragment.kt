package com.module.common.base

import android.R
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import com.module.common.base.action.ObserverBugAction
import com.module.common.base.action.ToolBarAction
import com.module.common.base.action.ToastAction
import com.module.common.bean.FragmentJumpBean
import com.module.common.ext.dismissLoadingExt
import com.module.common.ext.hideSoftKeyboard
import com.module.common.ext.showLoadingExt
import com.module.common.util.GsonUtils
import com.module.common.util.key_fragmentJumpVO
import com.gyf.immersionbar.ImmersionBar
import com.moonring.jetpackmvvm.base.fragment.BaseVmDbFragment
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.enum.LoadingEunm



abstract class BaseFragment<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmDbFragment<VM, DB>(),
    ToolBarAction, ToastAction , ObserverBugAction {


    private var mImmersionBar: ImmersionBar? = null


    private var mTitleBar: Toolbar? = null


    protected open fun getStatusBarConfig(): ImmersionBar {
        if (mImmersionBar == null) {
            mImmersionBar = createStatusBarConfig()

        }
        return mImmersionBar!!
    }


    protected open fun createStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this)
            .statusBarDarkFont(isDarkFont())
            .keyboardEnable(isKeyboardEnable())
            .navigationBarColor(R.color.white)
            .autoDarkModeEnable(true, 0.2f)
    }


    open fun isKeyboardEnable():Boolean{
        return false
    }

    open fun isDarkFont():Boolean{
        return false
    }

    abstract override fun initView(savedInstanceState: Bundle?)


    override fun lazyLoadData() {}


    override fun createObserver() {}


    override fun initData() {



    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (isStatusBarEnabled()) {
            getStatusBarConfig().init()
        }


        if (isStatusBarEnabled() && getTitleBar() != null) {
            ImmersionBar.setTitleBar(this, getTitleBar())
        }
        super.onViewCreated(view, savedInstanceState)
    }


    override fun getTitleBar(): Toolbar? {
        if (mTitleBar == null) {
            mTitleBar = obtainTitleBar(view as ViewGroup)
        }
        return mTitleBar
    }


    open fun isStatusBarEnabled(): Boolean {
        return true
    }


    open fun isUserListener(): Boolean {
        return false
    }



    override fun showLoading(message: String, loadingEunm: LoadingEunm, autoColose: Boolean) {
        showLoadingExt(message,loadingEunm,autoColose)
    }


    override fun dismissLoading() {
        dismissLoadingExt()
    }

    fun dismissLoading(long: Long) {
        Handler().postDelayed( {
            dismissLoadingExt()
        },long)
    }

    override fun onPause() {
        super.onPause()
        hideSoftKeyboard(activity)
    }


    override fun lazyLoadTime(): Long {
        return 300
    }


    override fun fixObserverErrorLifecycle() {

    }


    fun getBundle():Bundle{
       return arguments ?: Bundle()
    }



    val mfragmentJumpBean  by lazy{
        val json = arguments?.getString(key_fragmentJumpVO)
        println("mfragmentJumpVO:${json?:""}")
        GsonUtils.json2VO(json, FragmentJumpBean::class.java)?: FragmentJumpBean()
    }
    fun getFragmentJumpVO(): FragmentJumpBean {
        return mfragmentJumpBean
    }

}