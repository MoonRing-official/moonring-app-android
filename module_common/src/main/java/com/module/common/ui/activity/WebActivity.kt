package com.module.common.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.activity.OnBackPressedCallback
import com.alibaba.android.arouter.facade.annotation.Route
import com.module.common.base.BaseActivity
import com.module.common.base.BaseFragment
import com.module.common.databinding.ActivityWebBinding
import com.module.common.ext.hideSoftKeyboard
import com.module.common.support.RoutePath
import com.module.common.viewmodel.state.WebViewModel
import com.just.agentweb.AgentWeb
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.util.get


@Route(path = RoutePath.PATH_WEB)
class WebActivity : BaseActivity<WebViewModel, ActivityWebBinding>(){

    private var mAgentWeb: AgentWeb? = null

    private var preWeb: AgentWeb.PreAgentWeb? = null

    override fun initView(savedInstanceState: Bundle?) {
        val intent = intent
        intent.extras?.run {
            getString("url")?.let {
                mViewModel.url = it
            }
        }

        preWeb = AgentWeb.with(this)
            .setAgentWebParent(mDatabind.webcontent, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()

        mAgentWeb = preWeb?.go(mViewModel.url)
    }


    override fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
        setSupportActionBar(null)
        super.onDestroy()
    }


}