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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.module.common.weight.toast.ToastSingleton
import com.moonring.ring.R
import com.moonring.ring.bean.DayData
import com.moonring.ring.bean.HeartRateStats
import com.moonring.ring.bean.OxygenSensorData
import com.moonring.ring.bean.SensorData
import com.moonring.ring.bean.SensorStatus
import com.moonring.ring.databinding.DialogCheckHeartRateBinding
import com.moonring.ring.databinding.DialogCheckOxgenBinding
import com.moonring.ring.databinding.DialogCommonBinding
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.viewmodule.state.CheckingHeartRateViewModel
import com.moonring.ring.viewmodule.state.CheckingOxgenViewModel


/**
 *    author : Administrator

 *    time   : 2024/10/14/014
 *    desc   :
 */
class DialogCheckOxgenBottomSheet(private val onItemClick: (OxygenSensorData?) -> Unit) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogCheckOxgenBinding
    private val mViewModel : CheckingOxgenViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Initialize data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_check_oxgen, container, false)
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



        mViewModel.startTest(true)
        createObserver()
        binding.tvTip.text = "Please wait for the results......"
        binding.tvTip.visibility = View.VISIBLE
        binding.tvOxgen.visibility= View.INVISIBLE


    }


    override fun onDestroy() {
        super.onDestroy()
        mViewModel.allHeartRates.clear()


    }
    fun createObserver() {
        homeAppViewModel._oxygenSensorData.value = OxygenSensorData(0, 0, 0, 0, 0)  // 清空旧数据
        lifecycleScope.launchWhenStarted {
            homeAppViewModel.oxygenSensorData.collect { data ->

                if (data.oxygen>0){
                    mViewModel.oxygenSensorData.value = data
                    mViewModel.startTest(false)
                    binding.tvTip.visibility = View.INVISIBLE
                    binding.tvOxgen.visibility= View.VISIBLE
                    binding.tvOxgen.text  = "${data.oxygen} %"
                    ToastSingleton.toastSuccess("End of measurement")

                    binding.btComfirm.visibility = View.VISIBLE
                    binding.btCancel.visibility = View.INVISIBLE

                }

            }
        }

        homeAppViewModel.sensorState.value = SensorStatus(0, 0)
        homeAppViewModel.sensorState.observe(viewLifecycleOwner) { sensorStatus ->

            if (sensorStatus.type == 2 ){
              val oxygen =   mViewModel.oxygenSensorData.value?.oxygen?:0
                if (oxygen==0){

                    binding.tvTip.visibility = View.INVISIBLE
                    binding.tvOxgen.visibility= View.VISIBLE
                    binding.tvOxgen.text  = "0 %"
                    ToastSingleton.toastSuccess("End of measurement")

                    binding.btComfirm.visibility = View.VISIBLE
                    binding.btCancel.visibility = View.INVISIBLE
                }

            }

        }
    }

    fun endTest(){

    }


    inner class ProxyClick {

        fun cancel() {
            mViewModel.startTest(false)
            dismiss()

        }
        fun confirm() {
            mViewModel.startTest(false)
            onItemClick.invoke( mViewModel.oxygenSensorData.value)
            dismiss()

        }
    }
}
