package com.moonring.ring.ui.fragment

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.module.common.base.BaseBarFragment
import com.module.common.support.log.LogInstance
import com.module.common.util.time.RxTimer
import com.module.common.weight.webview.BrowserView
import com.moonring.ring.R
import com.moonring.ring.databinding.FragmentActivityBinding
import com.moonring.ring.support.intent.key_is_use_cache
import com.moonring.ring.support.intent.key_url
import com.moonring.ring.support.intent.web_loading
import com.moonring.ring.utils.setWebViewCacheByParam
import com.moonring.ring.view.webview.WebViewJSInterface
import com.moonring.ring.viewmodule.state.BrowserModel
import kotlinx.coroutines.delay

import com.moonring.jetpackmvvm.ext.dismissLoadingDelay
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateUpNoRepeat


open class FullBrowserFragment : BaseBarFragment<BrowserModel, FragmentActivityBinding>() {

    override fun initView(savedInstanceState: Bundle?) {

        configureRotationAnimator()
        startRotation()
        mDatabind.refreshLayout.visibility = View.VISIBLE


        mDatabind.root.setFocusableInTouchMode(true)
        mDatabind.root.requestFocus()
        mDatabind.root.setOnKeyListener({ v, keyCode, event ->

            LogInstance.i("root=======${keyCode}====${event.getAction()}")
            return@setOnKeyListener true
        })




    }



    private lateinit var animator: ObjectAnimator
    private fun configureRotationAnimator() {
        animator = ObjectAnimator.ofFloat(mDatabind.ivStatusIcon, "rotation", 0f, 360f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    private fun startRotation() {
        animator.start()
    }

    private fun stopRotation() {
        animator?.cancel()
    }

    override fun initData() {



        mDatabind.wvBrowserView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        setWebViewCacheByParam(arguments?.getString(key_url)?:"",mDatabind.wvBrowserView,arguments?.getString(key_is_use_cache)?:"true")

        mDatabind.wvBrowserView.settings.mediaPlaybackRequiresUserGesture=false

        mDatabind.wvBrowserView.setBackgroundColor(ContextCompat.getColor(requireContext(),android.R.color.transparent));
        mDatabind.wvBrowserView.setBackgroundResource(com.module.common.R.color.black);
        mDatabind.wvBrowserView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        mDatabind.wvBrowserView.setBrowserViewClient(MyBrowserViewClient())
        mDatabind.wvBrowserView.addJavascriptInterface(WebViewJSInterface(this), "BlockoorJSInterface")
        mDatabind.wvBrowserView.loadUrl(arguments?.getString(key_url) ?: "")
        mDatabind.wvBrowserView.setOnKeyListener({ v, keyCode, event ->
            LogInstance.i("wvBrowserView=======${keyCode}====${event.getAction()}")
            if (event.getAction() === KeyEvent.ACTION_DOWN) {
                if (keyCode === KeyEvent.KEYCODE_BACK) {

                    if (!mDatabind.wvBrowserView.canGoBack()) {

                        return@setOnKeyListener true
                    }
                    val backUrl = getBackUrl(mDatabind.wvBrowserView)
                    if (backUrl.isNullOrBlank()) {


                    } else {
                        mDatabind.wvBrowserView.goBack()
                    }
                    return@setOnKeyListener true
                }
            }

            activity?.onKeyDown(keyCode, event) ?: false == true
        })


        LogUtils.i("url=====1========" + arguments?.getString(key_url))
    }



    private fun closePage() {
        nav().navigateUpNoRepeat()
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.close.observe(viewLifecycleOwner){
            if (it){
                RxTimer().timer(1500){
                    dismissLoadingBg()
                }


            }
        }
    }
    fun getBackUrl(webView: WebView): String? {
        try {
            val backForwardList = webView.copyBackForwardList()
            if (backForwardList != null && backForwardList.size != 0) {

                val currentIndex = backForwardList.currentIndex
                val historyItem = backForwardList.getItemAtIndex(currentIndex - 1)
                if (historyItem != null) {

                    return historyItem.url
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }


    private fun reload() {
        mDatabind.wvBrowserView.reload()
    }

    fun loadUrl(url: String?) {
        url?.let {
            mDatabind.wvBrowserView.loadUrl(url)
        }

    }

    var resumeNum = 0
    override fun onResume() {
        super.onResume()

    }

    fun resumeBlockoorJS(){
        mDatabind?.wvBrowserView?.loadUrl("javascript:resumeBlockoorJS()")
    }

    override fun onDestroy() {


        super.onDestroy()

    }

    fun dismissLoadingBg() {
        mDatabind?.refreshLayout?.visibility = View.GONE
        stopRotation()
    }


    private inner class MyBrowserViewClient : BrowserView.BrowserViewClient() {

        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {


        }


        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {}


        override fun onPageFinished(view: WebView, url: String) {
            LogInstance.i("=========${arguments?.getBoolean(web_loading)}============${arguments?.getBoolean(web_loading) ?: false}")
            if (!(arguments?.getBoolean(web_loading) ?: false)) {

                ThreadUtils.runOnUiThread {
                    mViewModel.close.value = true
                }
            }

        }

    }



    companion object {
        fun newInstance(url: String,use_cache:String = "true"): FullBrowserFragment {
            val args = Bundle()
            args.putString(key_url, url)
            args.putString(key_is_use_cache, use_cache)
            val fragment = FullBrowserFragment()
            fragment.arguments = args
            return fragment
        }
    }
}