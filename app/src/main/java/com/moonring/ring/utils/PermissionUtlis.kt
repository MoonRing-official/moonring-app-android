package com.moonring.ring.utils

import androidx.fragment.app.Fragment
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.module.common.support.log.LogInstance
import com.moonring.ring.R
import com.moonring.ring.ui.dialogfragment.DialogPermissionBottomSheet
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateAction

fun Fragment.bluePermission(success:()->Unit,denied:()->Unit = {}){
    XXPermissions.with(requireContext())
        .permission(Permission.ACCESS_FINE_LOCATION)

        .permission(Permission.Group.BLUETOOTH)

        .request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                LogInstance.i("onGranted:${all}")
                if (all){
                    success.invoke()

                }
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                LogInstance.i("onDenied")

                deniedDialog{
                    XXPermissions.startPermissionActivity(requireContext(), permissions)
                }

                denied.invoke()

            }
        })
}

fun Fragment.deniedDialog(install:()->Unit = {}){
    val map: Map<String, String> = mapOf(
        "title" to "",
        "content" to getString(R.string.permission_rj_content),
        "confirm" to "Go Install",
        "cancel" to "confirm"
    )
    val dialogFragment = DialogPermissionBottomSheet(
        map,
        onItemAcceptClick = {
            LogInstance.i("onItemAcceptClick")
            install.invoke()

        },
        onItemRejectClick = {
            LogInstance.i("onItemRejectClick")
        })
    dialogFragment.show(childFragmentManager, "permission")
}