package com.module.common.weight.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.AppUtils;
import com.module.common.BuildConfig;

public final class BrowserView extends NestedScrollWebView
        implements LifecycleEventObserver {

    static {
        
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
    }

    public BrowserView(Context context) {
        this(context, null);
    }

    public BrowserView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public BrowserView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(getFixedContext(context), attrs, defStyleAttr, 0);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public BrowserView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        WebSettings settings = getSettings();
        
        settings.setAllowFileAccess(true);
        
        settings.setGeolocationEnabled(true);
        
        
        
        settings.setJavaScriptEnabled(true);
        
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        
        settings.setLoadsImagesAutomatically(true);
        
        settings.setDomStorageEnabled(true);
        
        String userAgent = settings.getUserAgentString();
        StringBuilder sb = new StringBuilder();
        sb.append(userAgent);

        sb.append(AppUtils.getAppVersionName());
        settings.setUserAgentString(sb.toString());


        

        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        
        settings.setTextZoom(100);
        
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        
        settings.setDisplayZoomControls(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }


    private static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            
            
            
            
            
            
            return new ContextThemeWrapper(context, context.getTheme());
        }
        return context;
    }


    @Override
    public String getUrl() {
        String originalUrl = super.getOriginalUrl();
        
        if (originalUrl != null) {
            return originalUrl;
        }
        return super.getUrl();
    }


    public void setLifecycleOwner(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_RESUME:
                onResume();
                resumeTimers();
                break;
            case ON_PAUSE:
                onPause();
                pauseTimers();
                break;
            case ON_DESTROY:
                onDestroy();
                break;
            default:
                break;
        }
    }


    public void onDestroy() {
        
        stopLoading();
        
        clearHistory();
        
        setBrowserChromeClient(null);
        setBrowserViewClient(null);
        
        removeAllViews();
        
        destroy();
    }


    @Deprecated
    @Override
    public void setWebViewClient(@NonNull WebViewClient client) {
        super.setWebViewClient(client);
    }

    public void setBrowserViewClient(BrowserViewClient client) {
        super.setWebViewClient(client);
    }


    @Deprecated
    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    public void setBrowserChromeClient(BrowserChromeClient client) {
        super.setWebChromeClient(client);
    }

    public static class BrowserViewClient extends WebViewClient {


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Context context = view.getContext();
            if (context == null) {
                return;
            }



















        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (request.isForMainFrame()) {
                onReceivedError(view,
                        error.getErrorCode(), error.getDescription().toString(),
                        request.getUrl().toString());
            }
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String scheme = Uri.parse(url).getScheme();
            if (scheme == null) {
                return true;
            }
            switch (scheme) {
                
                case "http":
                case "https":
                    view.loadUrl(url);
                    break;
                
                case "tel":
                    dialing(view, url);
                    break;
                default:
                    break;
            }
            
            return true;
        }


        protected void dialing(WebView view, String url) {
            Context context = view.getContext();
            if (context == null) {
                return;
            }













        }
    }

    public static class BrowserChromeClient extends WebChromeClient {

        private final BrowserView mWebView;

        public BrowserChromeClient(BrowserView view) {
            mWebView = view;
            if (mWebView == null) {
                throw new IllegalArgumentException("are you ok?");
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {











            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {





















            return true;
        }


        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {






















            return true;
        }


        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {


































        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> callback, FileChooserParams params) {






















            return true;
        }





    }
}