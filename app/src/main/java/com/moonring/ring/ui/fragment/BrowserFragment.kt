package com.moonring.ring.ui.fragment

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import com.module.common.base.BaseBarFragment
import com.module.common.util.GsonUtils
import com.module.common.weight.webview.BrowserView
import com.moonring.ring.bean.Article
import com.moonring.ring.databinding.FragmentBrowserBinding
import com.moonring.ring.viewmodule.state.BrowserModel
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateUpNoRepeat


/**
 *    author : Administrator

 *    time   : 2024/3/20/020
 *    desc   :
 */
class BrowserFragment : BaseBarFragment<BrowserModel, FragmentBrowserBinding>() {

    override fun initView(savedInstanceState: Bundle?) {


    }


    override fun initData() {
        super.initData()

        mDatabind.wvBrowserView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        mDatabind.wvBrowserView.setBrowserViewClient(MyBrowserViewClient())

        val articleSr =   GsonUtils.json2VO(arguments?.getString("article") ?: "",Article::class.java)

        val baseHtml = """
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <style>
            body {
                font-size: 16px; /* 设置字体大小 */
                line-height: 1.6; /* 增加行间距 */
            }
            img {
                max-width: 100%;
                height: auto;
            }
        </style>
    </head>
    <body>
        ${articleSr.content}
    </body>
</html>
"""


        mDatabind.wvBrowserView.loadDataWithBaseURL(null, baseHtml, "text/html", "UTF-8", null);




    }





    override fun onDestroy() {
        super.onDestroy()

    }


    /**
     * 重新加载当前页
     */
    private fun reload() {
        mDatabind.wvBrowserView.reload()
    }

    fun loadUrl(url: String?) {
        url?.let {
            mDatabind.wvBrowserView.loadUrl(url)
        }
    }



    override fun getToolbarTitle(): String {
        return ""
    }



    private inner class MyBrowserViewClient : BrowserView.BrowserViewClient() {

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?,
        ) {



        }


        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String,
        ) {

        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {}


        override fun onPageFinished(view: WebView, url: String) {


        }
    }

}