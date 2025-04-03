package com.module.common.base

import android.R
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.module.common.base.action.ObserverBugAction
import com.module.common.base.action.ToolBarAction
import com.module.common.base.action.ToastAction
import com.moonring.jetpackmvvm.base.fragment.BaseVmVbFragment
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.module.common.ext.dismissLoadingExt
import com.module.common.ext.hideSoftKeyboard
import com.module.common.ext.showLoadingExt
import com.gyf.immersionbar.ImmersionBar
import com.moonring.jetpackmvvm.enum.LoadingEunm


abstract class BaseFragmentMB<VM : BaseViewModel, VB : ViewBinding> : BaseVmVbFragment<VM, VB>(),
    ToolBarAction,
    ToastAction , ObserverBugAction {

    /** 状态栏沉浸  */
    private var mImmersionBar: ImmersionBar? = null

    /**
     * 获取状态栏沉浸的配置对象
     */
    protected open fun getStatusBarConfig(): ImmersionBar {
        if (mImmersionBar == null) {
            mImmersionBar = createStatusBarConfig()
        }
        return mImmersionBar!!
    }

    /**
     * 初始化沉浸式
     */
    protected open fun createStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this)
            .statusBarDarkFont(isDarkFont())
            .navigationBarColor(R.color.black)
            .autoDarkModeEnable(true, 0.2f)
    }

    /**
     * 状态栏字体字体颜色 默认是黑色
     */
    protected open fun isDarkFont():Boolean{
        return false
    }

    abstract override fun initView(savedInstanceState: Bundle?)

    /**
     * 懒加载 只有当前fragment视图显示时才会触发该方法
     */
    override fun lazyLoadData() {}

    /**
     * 创建LiveData观察者 Fragment执行onViewCreated后触发
     */
    override fun createObserver() {}

    /**
     * Fragment执行onViewCreated后触发
     */
    override fun initData() {

        if (isStatusBarEnabled()) {
            getStatusBarConfig().init()
        }

        if (isStatusBarEnabled() && getTitleBar() != null) {
            ImmersionBar.setTitleBar(this, getTitleBar())
        }
    }
    /** 标题栏对象  */
    private var mTitleBar: Toolbar? = null

    override fun getTitleBar(): Toolbar? {
        if (mTitleBar == null) {
            mTitleBar = obtainTitleBar(view as ViewGroup)
        }
        return mTitleBar
    }


    /**
     * 是否在 Fragment 使用沉浸式
     */
    open fun isStatusBarEnabled(): Boolean {
        return true
    }

    /**
     * 打开等待框
     */
    override fun showLoading(message: String, loadingEunm: LoadingEunm, autoColose: Boolean) {
        showLoadingExt(message,loadingEunm,autoColose)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
        dismissLoadingExt()
    }

    override fun onPause() {
        super.onPause()
        hideSoftKeyboard(activity)
    }

    /**
     * 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿  bug
     * 这里传入你想要延迟的时间，延迟时间可以设置比转场动画时间长一点 单位： 毫秒
     * 不传默认 300毫秒
     * @return Long
     */
    override fun lazyLoadTime(): Long {
        return 300
    }

    override fun fixObserverErrorLifecycle() {

    }
}