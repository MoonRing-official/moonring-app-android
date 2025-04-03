package com.moonring.ring.ui.dialogfragment

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.module.common.bean.UpgradeBean
import com.module.common.support.log.LogInstance
import com.module.common.util.time.RxTimer
import com.moonring.ring.R
import com.moonring.ring.bean.OtaUpdateState
import com.moonring.ring.databinding.DialogUpgradeFirmwareBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.viewmodule.state.UpgradeFirmWareViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.ext.delay
import com.moonring.jetpackmvvm.ext.download.DownLoadManager
import com.moonring.jetpackmvvm.ext.download.OnDownLoadListener
import java.io.File


/**
 *    author : Administrator

 *    time   : 2024/10/14/014
 *    desc   :
 */
class DialogUpgradeFirmwareBottomSheet(private val upgradeBean: UpgradeBean, private val onItemClick: () -> Unit) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogUpgradeFirmwareBinding
    private val handler = Handler(Looper.getMainLooper())
    private val mViewModel : UpgradeFirmWareViewModel by viewModels()
    init {

        isCancelable = false

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_upgrade_firmware, container, false)
        return binding.root
    }



    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.click = ProxyClick()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        createObserver()

        binding.clDlFirmware.visibility  = View.VISIBLE
        binding.clDlFirmwareStatus.visibility = View.INVISIBLE


        binding.tvTitle.text = "Downloading latest Firmware"

        startDownLoadPorogressUpdate()


    }



    fun createObserver(){



    }

    fun collection(){
        homeAppViewModel._otaUpdateStateFlow.value = OtaUpdateState(0, 0)
        lifecycleScope.launchWhenStarted {
            homeAppViewModel.otaUpdateStateFlow.collect{ state ->

                when (state.step) {

                    OtaUpdateState.STEP_OTA -> when {
                        state.progress == 100 -> {
                            binding.progressBar.progress = state.progress


                            binding.clDlFirmware.visibility  = View.INVISIBLE
                            binding.clDlFirmwareStatus.visibility = View.VISIBLE
                            binding.tvTitleStatus.text = "Update Complete"
                            binding.tvContent.text = "Latest ver.  (v${upgradeBean.version}). "
                            binding.ivStatus.setImageResource(R.drawable.ic_ok)


                            mViewModel.delay(3000) {
                                dismiss()
                            }

                        }
                        state.progress > 0 -> {

                            binding.progressBar.progress = state.progress
                        }
                        else -> {
                            ""
                        }
                    }
                    else -> ""
                }
            }
        }
    }


    fun startOta(path:String){

        binding.progressBar.progress = 1
        binding.tvTitle.text = "Installing latest Firmware"
        homeAppViewModel.startFileOta(filePath = path)
        collection()
    }
    val rxTimer by lazy {
        RxTimer()
    }
    fun startDownLoadPorogressUpdate() {
        binding.progressBar.progress = 0
        mViewModel.viewModelScope.launch {

            val downloadUrl = upgradeBean.download_url
            val saveName = downloadUrl.substringAfterLast("/", "default.bin")


            val logDir = File(requireActivity().filesDir, "files/firmware")
            if (!logDir.exists()) {
                logDir.mkdirs()
            }


            val savePath = logDir.path

            DownLoadManager.downLoad(
                tag = "firmware",
                url = downloadUrl,
                savePath = savePath,
                saveName = saveName,
                reDownload = true,
                loadListener = object : OnDownLoadListener {
                    override fun onDownLoadPrepare(key: String) {
                        LogInstance.i("onDownLoadPrepare", key)
                    }

                    override fun onDownLoadError(key: String, throwable: Throwable) {
                        LogInstance.i("onDownLoadError", "Key: $key, Error: ${throwable.message}")
                    }

                    override fun onDownLoadSuccess(key: String, path: String, size: Long) {
                        LogInstance.i("onDownLoadSuccess", "Key: $key, Path: $path, Size: $size")
                        startOta(path)
                    }

                    override fun onDownLoadPause(key: String) {
                        LogInstance.i("onDownLoadPause", key)
                    }

                    override fun onUpdate(
                        key: String,
                        progress: Int,
                        read: Long,
                        count: Long,
                        done: Boolean
                    ) {
                        LogInstance.i("onUpdate", "Key: $key, Progress: $progress%, Read: $read, Total: $count, Done: $done")
                        if (binding.progressBar.progress < 100) {
                            binding.progressBar.progress = progress
                        }
                    }
                }
            )
        }



    }
    inner class ProxyClick {

        fun update() {
            onItemClick.invoke()
            dismiss()

        }
        fun later() {
            dismiss()

        }



    }
}
