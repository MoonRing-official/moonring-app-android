package com.moonring.ring.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import androidx.fragment.app.Fragment
import com.module.common.bean.UpgradeBean
import com.module.common.support.log.LogInstance
import com.module.common.util.CacheUtil
import com.module.common.weight.toast.ToastSingleton
import com.moonring.ring.ui.dialogfragment.DialogCommonBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogNotifyBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogUpgradeBottomSheet



fun showUpgradleDialog(context: Context,upgradeBean: UpgradeBean){

    var versionStr= CacheUtil.getUpgradleCancel()
    LogInstance.i("versionStr========${upgradeBean.version}=========${versionStr}")
    if(versionStr==upgradeBean.version)
        return


    val dialog = DialogUpgradeBottomSheet(
        context = context,
        upgradeBean = upgradeBean,
        onItemUpdate = {
            LogInstance.i("======mCancelLinear======${context.packageName}")
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                        "https://play.google.com/store/apps/details?id=${context.packageName}"
                    )
                    setPackage("com.android.vending")
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                LogInstance.i("browser error :===========${e.message}")
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(
                            "https://play.google.com/store/apps/details?id=${context.packageName}"
                        )
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        ToastSingleton.toastWarning("You have not installed the application market, not even the browser")
                    }
                } catch (e: Exception) {
                    ToastSingleton.toastWarning("You have not installed the application market, not even the browser")
                }
            }
        },
        onItemLater = {
            LogInstance.i("mConfirmLinear===========${upgradeBean.is_force_update}")
            if (upgradeBean.is_force_update) {
                System.exit(0)
            } else {

                CacheUtil.setUpgradleCancel(upgradeBean.version)
                it.dismiss()
            }
        }
    )

// Show the dialog
    dialog.show()



}