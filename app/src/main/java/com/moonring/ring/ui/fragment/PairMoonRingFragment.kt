package com.moonring.ring.ui.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.module.common.base.BaseBarFragment
import com.moonring.ring.R
import com.moonring.ring.databinding.FragmentPairMoonringBinding
import com.moonring.ring.support.moonring.CheckUtil
import com.moonring.ring.ui.dialogfragment.DialogPermissionBottomSheet
import com.moonring.ring.utils.bluePermission
import com.moonring.ring.viewmodule.state.PairMoonRingViewModel
import com.moonring.ring.viewmodule.state.PairingStatus
import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateAction

/**
 *    author : Administrator

 *    time   : 2024/10/15/015
 *    desc   :
 */
class PairMoonRingFragment : BaseBarFragment<PairMoonRingViewModel, FragmentPairMoonringBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel


    }

    override fun toolbarBackVisity(): Boolean {
        return true
    }

    override fun createObserver() {

    }

    override fun getToolbarTitle(): String {
        return ""
    }




    inner class ProxyClick {
        fun startPairing() {
            mViewModel.startPairingProcess()
        }

        fun findmyMoonring(){




            if (!XXPermissions.isGranted(requireContext(), Permission.ACCESS_FINE_LOCATION,
                    Permission.BLUETOOTH_SCAN,
                    Permission.BLUETOOTH_CONNECT,
                    Permission.BLUETOOTH_ADVERTISE)) {
//                    显示位置授权弹窗
                val map: Map<String, String> = mapOf(
                    "title" to getString(R.string.permission_title),
                    "content" to getString(R.string.permission_content),
                    "confirm" to "Accept",
                    "cancel" to "Reject"
                )
                val dialogFragment = DialogPermissionBottomSheet(
                    map,
                    onItemAcceptClick = {
                        bluePermission ({
                            nav().navigateAction(R.id.action_pairMoonRingFragment_to_searchMoonRingFragment)
                        })
                    },
                    onItemRejectClick = {

                    })
                dialogFragment.show(childFragmentManager, "permission")
            } else {
                bluePermission ({
                    nav().navigateAction(R.id.action_pairMoonRingFragment_to_searchMoonRingFragment)
                })

            }

        }

        fun later(){
            nav().navigateAction(R.id.action_pairMoonRingFragment_to_mainFragment)
        }
    }
}
