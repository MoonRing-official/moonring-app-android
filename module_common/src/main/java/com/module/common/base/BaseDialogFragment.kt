package com.module.common.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.module.common.base.action.ToastAction
import com.module.common.ext.dismissLoadingExt
import com.module.common.ext.showLoadingExt
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.enum.LoadingEunm
import com.moonring.jetpackmvvm.ext.getVmClazz
import com.moonring.jetpackmvvm.ext.inflateBindingWithGeneric

/**
 *    author : Administrator

 *    time   : 2023/1/18/018
 *    desc   :
 */
abstract class BaseDialogFragment<VM : BaseViewModel, DB : ViewDataBinding>:DialogFragment(),
    ToastAction {



    private var _binding: DB? = null
    val mDatabind: DB get() = _binding!!
    lateinit var mActivity: AppCompatActivity
    lateinit var mViewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        _binding  = inflateBindingWithGeneric(inflater,container,false)
        mDatabind.root.isClickable = true
        return mDatabind.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = createViewModel()
        initView(savedInstanceState)
        createObserver()
        registorDefUIChange()
        initData()
    }


    override fun onStart() {
        super.onStart()

        val win = dialog!!.window

        win?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        val params = win?.attributes
        params?.gravity = Gravity.CENTER

        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win?.attributes = params
        dialog!!.setCancelable(isCancle())
    }
    /**
     * Fragment执行onCreate后触发的方法
     */
    open fun initData() {}

    /**
     *
     */
    open fun isCancle():Boolean{
        return false
    }


    /**
     * 创建观察者
     */
    abstract fun createObserver()

    /**
     * 初始化view
     */
    abstract fun initView(savedInstanceState: Bundle?)
    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    /**
     * 注册 UI 事件
     */
    private fun registorDefUIChange() {
        mViewModel.loadingChange.showDialog.observe(this, Observer {
            showLoading(it)
        })
        mViewModel.loadingChange.dismissDialog.observe(this, Observer {
            dismissLoading()
        })
    }

    fun showLoading(message: String = "loading...",loadingEunm: LoadingEunm = LoadingEunm.moonring){
        showLoadingExt(message,loadingEunm)
    }
    fun dismissLoading(){
        dismissLoadingExt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}