package com.moonring.ring.ui.dialogfragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.module.common.bean.UpgradeBean
import com.moonring.ring.R
import com.moonring.ring.databinding.DialogCommonBinding
import com.moonring.ring.databinding.DialogUpgradeBinding


/**
 *    author : Administrator

 *    time   : 2024/10/14/014
 *    desc   :
 */
class DialogUpgradeBottomSheet(
    context: Context,
    private val upgradeBean: UpgradeBean,
    private val onItemUpdate: () -> Unit,
    private val onItemLater: (DialogUpgradeBottomSheet) -> Unit
) : BottomSheetDialog(context, R.style.Theme_App_BottomSheetDialog) {

    private lateinit var binding: DialogUpgradeBinding

    init {
        setContentView(initView())
        setupDialog()
    }

    private fun initView(): View {

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_upgrade,
            null,
            false
        )
        binding.click = ProxyClick()
        return binding.root
    }

    private fun setupDialog() {

        setCancelable(false)
        window?.apply {

            setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
            setDimAmount(0.5f)
        }

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)


        binding.tvTitle.text = "App Update Available"
        binding.tvContent.text = upgradeBean.update_log
        binding.btLater.text = if (upgradeBean.is_force_update) {
            "Quit"
        } else {
            "Cancel"
        }
        binding.tvContent.movementMethod= ScrollingMovementMethod()
    }

    inner class ProxyClick {
        fun update() {
            onItemUpdate.invoke()
        }

        fun later() {
            onItemLater.invoke(this@DialogUpgradeBottomSheet)
        }
    }
}
