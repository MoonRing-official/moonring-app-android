package com.module.common.weight.dialog

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback


abstract class BaseSheetDialogDB<DB : ViewDataBinding>(context: Context) : BaseDialogDB<DB>(context),
    View.OnTouchListener, View.OnClickListener {
    private var mCancelable = true
    private var mCanceledOnTouchOutside = true
    private var mCanceledOnTouchOutsideSet = false

    private val mBottomSheetBehavior: BottomSheetBehavior<FrameLayout>by lazy {
        BottomSheetBehavior(getContext(), null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setALLBgForBottom()
        mBottomSheetBehavior.addBottomSheetCallback(myBottomSheetCallback)
        mBottomSheetBehavior.setHideable(mCancelable)

    }



    override fun cancel() {
        if (mBottomSheetBehavior == null || mBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            super.cancel()
            return
        }
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        dismiss()
    }

    override fun show() {
        super.show()
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun setContentView(@LayoutRes layoutResId: Int) {
        super.setContentView(wrapContentView(layoutInflater.inflate(layoutResId, null, false)))
    }

    override fun setContentView(view: View) {
        super.setContentView(wrapContentView(view!!))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        view.layoutParams = params
        super.setContentView(wrapContentView(view))
    }

    open fun getBottomSheetBehavior(): BottomSheetBehavior<FrameLayout> {
        return mBottomSheetBehavior
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        super.setCanceledOnTouchOutside(cancel)
        if (cancel && !mCancelable) {
            mCancelable = true
        }
        mCanceledOnTouchOutside = cancel
        mCanceledOnTouchOutsideSet = true
    }
    private  fun shouldWindowCloseOnTouchOutside(): Boolean {
        if (!mCanceledOnTouchOutsideSet) {
            val array = context.obtainStyledAttributes(intArrayOf(R.attr.windowCloseOnTouchOutside))
            mCanceledOnTouchOutside = array.getBoolean(0, true)
            array.recycle()
            mCanceledOnTouchOutsideSet = true
        }
        return mCanceledOnTouchOutside
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        return true
    }

    override fun onClick(view: View?) {
        if (mCancelable && isShowing && shouldWindowCloseOnTouchOutside()) {
            cancel()
        }
    }

    val myBottomSheetCallback = object :BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {


            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                cancel()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("ClickableViewAccessibility")
    private  fun wrapContentView(view: View): View {
        val rootLayout = CoordinatorLayout(context)
        rootLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val touchView = View(context)
        touchView.isSoundEffectsEnabled = false
        touchView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        touchView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val contentLayout = FrameLayout(context)
        val layoutParams = CoordinatorLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        layoutParams.behavior = mBottomSheetBehavior
        contentLayout.layoutParams = layoutParams
        contentLayout.addView(view)
        rootLayout.addView(touchView)
        rootLayout.addView(contentLayout)
        touchView.setOnClickListener(this)
        ViewCompat.setAccessibilityDelegate(contentLayout, behaviorAccessibilityDelegate)
        contentLayout.setOnTouchListener(this)
        return rootLayout
    }

    val behaviorAccessibilityDelegate= object :AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            if (mCancelable) {
                info.addAction(AccessibilityNodeInfoCompat.ACTION_DISMISS)
                info.isDismissable = true
            } else {
                info.isDismissable = false
            }
        }

        override fun performAccessibilityAction(host: View, action: Int, args: Bundle?): Boolean {
            if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS && mCancelable) {
                cancel()
                return true
            }
            return super.performAccessibilityAction(host, action, args)
        }
    }
}