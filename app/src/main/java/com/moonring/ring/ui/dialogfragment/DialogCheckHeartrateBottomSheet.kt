package com.moonring.ring.ui.dialogfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.moonring.ring.R
import com.moonring.ring.bean.DayData
import com.moonring.ring.bean.HeartRateStats
import com.moonring.ring.bean.SensorData
import com.moonring.ring.databinding.DialogCheckHeartRateBinding
import com.moonring.ring.databinding.DialogCommonBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.viewmodule.state.CheckingHeartRateViewModel



/**
 *    author : Administrator

 *    time   : 2024/10/14/014
 *    desc   :
 */
class DialogCheckHeartrateBottomSheet(private val onItemClick: (HeartRateStats?) -> Unit) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogCheckHeartRateBinding
    private val mViewModel : CheckingHeartRateViewModel by viewModels()

    private lateinit var countdownTimer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_check_heart_rate, container, false)
        return binding.root
    }



    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.click = ProxyClick()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = mViewModel
        setCancelable(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)


        createObserver()
        mViewModel.startTest(true)
        startCountdown()

    }

    private fun startCountdown() {
        countdownTimer = object : CountDownTimer(45000, 1000) { // 45 seconds, ticking every 1 second
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                binding.tvTime.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.tvTime.text = "00:00"
            }
        }
        countdownTimer.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        mViewModel.allHeartRates.clear()

        countdownTimer.cancel()
    }
    fun createObserver() {
        homeAppViewModel.sensorData.value = SensorData()
        homeAppViewModel.sensorData.observe(viewLifecycleOwner){
                sensorData->

            if (sensorData.heartrate<=0){
                return@observe
            }




            mViewModel.allHeartRates.add(sensorData)
            val minHeartRate = mViewModel.allHeartRates.minByOrNull { it.heartrate }?.heartrate ?: 0
            val maxHeartRate = mViewModel.allHeartRates.maxByOrNull { it.heartrate }?.heartrate ?: 0
            val averageHeartRate = mViewModel.allHeartRates.map { it.heartrate }.average().toInt()
            val heartRate = sensorData.heartrate?:0


            mViewModel.heartRateStats.value = HeartRateStats(heartRate,minHeartRate, maxHeartRate, averageHeartRate,sensorData.timestamp)



        }
    }


    inner class ProxyClick {

        fun cancel() {

            dismiss()

        }
        fun stop() {
            mViewModel.startTest(false)
            onItemClick.invoke(mViewModel.heartRateStats.value)
            dismiss()

        }
    }
}
