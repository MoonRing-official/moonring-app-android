package com.module.common.base

import android.R
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import com.module.common.base.action.ToolBarAction
import com.module.common.base.action.ToastAction
import com.moonring.jetpackmvvm.base.activity.BaseVmDbActivity
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.module.common.ext.dismissLoadingExt
import com.module.common.ext.showLoadingExt
import com.module.common.util.StatusBarUtil
import com.gyf.immersionbar.ImmersionBar

abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmDbActivity<VM, DB>(),
    ToolBarAction, ToastAction {

    /** 状态栏沉浸  */
    private var mImmersionBar: ImmersionBar? = null

    /** 标题栏对象  */
    private var mTitleBar: Toolbar? = null

    /**
     * 获取状态栏沉浸的配置对象
     */
    protected open fun getStatusBarConfig(): ImmersionBar {
        if (mImmersionBar == null) {
            mImmersionBar = createStatusBarConfig()
        }

        return mImmersionBar!!
    }

    override fun onResume() {
        super.onResume()
        StatusBarUtil.setDarkMode(this)
    }

    /**
     * 初始化沉浸式
     */
    protected open fun createStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this) // 默认状态栏字体颜色为黑色
            .statusBarDarkFont(isDarkFont()) // 指定导航栏背景颜色
            .keyboardEnable(isKeyboardEnable())
            .navigationBarColor(R.color.white) // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
            .autoDarkModeEnable(true, 0.2f)
    }
    /**
     * 解决keyborad与状态栏的冲突问题
     */
    open fun isKeyboardEnable():Boolean{
        return false
    }
    /**
     * 导航栏背景颜色 默认是黑色
     */
    open fun isDarkFont():Boolean{
        return false
    }


     open fun initData() {
        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            getStatusBarConfig().init()
        }

        // 设置标题栏沉浸
        if (isStatusBarEnabled() && getTitleBar() != null) {
            ImmersionBar.setTitleBar(this, getTitleBar())
        }

    }

    /**
     * 是否在 Fragment 使用沉浸式
     */
    open fun isStatusBarEnabled(): Boolean {
        return true
    }

     override fun initView(savedInstanceState: Bundle?){
         initData()
     }

    override fun getTitleBar(): Toolbar? {
        return mTitleBar
    }


    /**
     * 创建liveData观察者
     */
    override fun createObserver() {}

    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {
        showLoadingExt(message)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
        dismissLoadingExt()
    }


}