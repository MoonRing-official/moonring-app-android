package com.moonring.ring.utils

import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView


/**
 *    author : Administrator

 *    time   : 2023/8/11/011
 *    desc   :
 */


fun setWebViewCacheByParam(url:String,webView: WebView,is_use_cache:String = "true"){
    try {
        val uri = Uri.parse(url)
        val isCache = uri.getQueryParameter("isCache")
        if (url== privacy_url || url == agreement_url || isCache =="false" || is_use_cache == "false") {
            webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        }
    }catch (e:Exception){
        e.printStackTrace()
    }

}
