package com.moonring.jetpackmvvm.base.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.ext.getVmClazz
import com.moonring.jetpackmvvm.ext.util.notNull
import com.moonring.jetpackmvvm.network.manager.NetState
import com.moonring.jetpackmvvm.network.manager.NetworkStateManager


abstract class BaseVmActivity<VM : BaseViewModel> : AppCompatActivity() {

    lateinit var mViewModel: VM

    abstract fun layoutId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun showLoading(message: String = "loading...")

    abstract fun dismissLoading()

    override fun onCreate(savedInstanceState: Bundle?) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState)
        initDataBind().notNull({
            setContentView(it)
        }, {
            setContentView(layoutId())
        })
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        mViewModel = createViewModel()
        registerUiChange()
        initView(savedInstanceState)
        createObserver()
        NetworkStateManager.instance.mNetworkStateCallback.observe(this, Observer {
            onNetworkStateChanged(it)
        })
    }
//
//    // 字体大小不跟随系统
//    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(newBase)
//        overrideFontScale(newBase)
//    }
//
//    /**
//     * 重置配置 fontScale：保持字体比例不变，始终为 1.
//     */
//    private fun overrideFontScale(context: Context?) {
//        if (context == null) return
//        context.resources.configuration.let {
//            it.fontScale = 1f // 保持字体比例不变，始终为 1.
//            applyOverrideConfiguration(it) // 应用新的配置
//        }
//    }

    /**
     * 网络变化监听 子类重写
     */
    open fun onNetworkStateChanged(netState: NetState) {}

    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    /**
     * 创建LiveData数据观察者
     */
    abstract fun createObserver()

    /**
     * 注册UI 事件
     */
    private fun registerUiChange() {
        //显示弹窗
        mViewModel.loadingChange.showDialog.observe(this, Observer {
            showLoading(it)
        })
        //关闭弹窗
        mViewModel.loadingChange.dismissDialog.observe(this, Observer {
            dismissLoading()
        })
    }

    /**
     * 将非该Activity绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: BaseViewModel) {
        viewModels.forEach { viewModel ->
            //显示弹窗
            viewModel.loadingChange.showDialog.observe(this, Observer {
                showLoading(it)
            })
            //关闭弹窗
            viewModel.loadingChange.dismissDialog.observe(this, Observer {
                dismissLoading()
            })
        }
    }

    /**
     * 供子类BaseVmDbActivity 初始化Databinding操作
     */
    open fun initDataBind(): View? {
        return null
    }
}