package com.moonring.ring.base

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.moonring.ring.R

abstract class BaseBsheetDialogFragment<DB : ViewDataBinding>(@LayoutRes var contentLayoutId: Int) :
    BottomSheetDialogFragment() {
    private var _binding: DB? = null
    val binding: DB get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, contentLayoutId, viewGroup, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setListeners()
        setObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun initView() {

    }

    open fun setListeners() {

    }

    open fun setObserver() {

    }

    open fun dialogSlideStateHidden() {

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        createDialog(super.onCreateDialog(savedInstanceState) as BottomSheetDialog)
    protected var bottomSheet: FrameLayout? = null
    open fun createDialog(dialog: BottomSheetDialog): Dialog {
        dialog.setOnShowListener { dialogInterface ->
            val dialogView = dialogInterface as BottomSheetDialog
            bottomSheet = dialogView.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val coordinatorLayout = bottomSheet!!.parent as CoordinatorLayout
            val bottomSheetBehavior = BottomSheetBehavior.from<View>(bottomSheet!!)
            val dialogHeight = ScreenUtils.getScreenHeight()
            bottomSheetBehavior.peekHeight = dialogHeight
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            coordinatorLayout.parent.requestLayout()
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
            bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                        dismiss()
                        dialogSlideStateHidden()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })
        }
        return dialog
    }

    override fun getTheme(): Int = R.style.Theme_App_BottomSheetDialog
    override fun onDestroy() {
        super.onDestroy()
    }



}