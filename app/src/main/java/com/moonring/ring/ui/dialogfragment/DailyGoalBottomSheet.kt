package com.moonring.ring.ui.dialogfragment

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ResourceUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.module.common.ext.clickNoRepeat
import com.module.common.ext.setNbOnItemClickListener
import com.moonring.ring.R

import com.moonring.ring.databinding.DialogDailyGoalBinding
import com.moonring.ring.databinding.DialogGenderSelectionBinding
import com.moonring.ring.ext.setTextColorCompat
import com.moonring.ring.ext.setTextSizeCompat

/**
 *    author : Administrator

 *    time   : 2024/3/20/020
 *    desc   :
 */

class DailyGoalBottomSheet( private val onItemClick: ( String) -> Unit): BottomSheetDialogFragment() {

    private lateinit var binding: DialogDailyGoalBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_daily_goal, container, false)
        return binding.root
    }
    val numberOptions = arrayOf("1,000", "2,000", "3,000")
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.click = ProxyClick()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        setDividerColor(binding.picker, Color.parseColor("#FF0000")) // 设置为红色

        val numberPicker = binding.picker

        numberPicker.setTextColorCompat(Color.parseColor("#FF973F"))
        numberPicker.setTextSizeCompat(ConvertUtils.dp2px(32f).toFloat())
        numberPicker.minValue = 0
        numberPicker.maxValue = numberOptions.size - 1
        numberPicker.value = 2
        numberPicker.displayedValues = numberOptions
        numberPicker.wrapSelectorWheel = true


        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val selectedNumber = numberOptions[newVal]
            Log.d("NumberPicker", "Selected Number: $selectedNumber")
        }

    }

    private fun setDividerColor(picker: NumberPicker, color: Int) {
        val pickerFields = NumberPicker::class.java.declaredFields
        for (field in pickerFields) {
            if (field.name == "mSelectionDivider") {
                field.isAccessible = true
                try {
                    val colorDrawable = ColorDrawable(color)
                    field.set(picker, colorDrawable)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }
                break
            }
        }
    }
    inner class ProxyClick {
        fun cancel(){

            dismiss()

        }

        fun confirm(){
            onItemClick.invoke(numberOptions[binding.picker.value])
            dismiss()
        }
    }

}