package com.moonring.ring.ui.dialogfragment

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.module.common.util.time.RxTimer
import com.module.common.weight.webview.BrowserView
import com.moonring.ring.R
import com.moonring.ring.databinding.DialogUnpairBinding
import com.moonring.ring.databinding.DialogWebBinding
import com.moonring.ring.databinding.DialogWeightSelectionBinding

/**
 *    author : Administrator

 *    time   : 2024/10/14/014
 *    desc   :
 */
class DialogWebviewSheet(private val titleStr:String,private val url:String,private val onItemClick: (Int, String) -> Unit) : BottomSheetDialogFragment() {
    private lateinit var mDatabing: DialogWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mDatabing = DataBindingUtil.inflate(inflater, R.layout.dialog_web, container, false)
        return mDatabing.root
    }

    private val behavior by lazy { (dialog as BottomSheetDialog).behavior }

    override fun onStart() {
        super.onStart()


    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDatabing.click = ProxyClick()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)


        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_DRAGGING -> {

                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }
                        }

                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })
        }
        mDatabing.wvBrowserView.visibility = View.INVISIBLE
        mDatabing.wvBrowserView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        mDatabing.wvBrowserView.settings.mediaPlaybackRequiresUserGesture=false

        mDatabing.wvBrowserView.setBrowserViewClient(MyBrowserViewClient())
        mDatabing.wvBrowserView.loadUrl(url)


        mDatabing.wvBrowserView.setWebChromeClient(object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)

            }
        })

        mDatabing.tvTitle.text = titleStr
        if (titleStr.isNullOrBlank()){
            mDatabing.tvTitle.visibility = View.GONE
        }

        RxTimer().timer(2000){
            mDatabing?.wvBrowserView?.visibility = View.VISIBLE
        }

        mDatabing.wvBrowserView.setBackgroundColor(0)
        setCancelable(false)


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
        }
    }


    inner class ProxyClick {

        fun confirm() {
            dismiss()

        }
        fun cancel() {
            dismiss()

        }
    }
}
