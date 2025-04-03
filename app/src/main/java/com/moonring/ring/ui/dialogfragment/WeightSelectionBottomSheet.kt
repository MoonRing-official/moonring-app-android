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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.module.common.bean.UserProfileMoonRing
import com.module.common.enums.HeightUnit
import com.module.common.enums.WeightUnit
import com.module.common.util.cmToFeetInches
import com.module.common.util.kgToPounds
import com.moonring.ring.R
import com.moonring.ring.databinding.DialogWeightSelectionBinding
import com.moonring.ring.ext.setTextColorCompat

/**
 *    author : Administrator

 *    time   : 2024/10/14/014
 *    desc   :
 */
class WeightSelectionBottomSheet(private val btnTitle:String = "Next", private  val userProfile: UserProfileMoonRing, private val onItemClick: (Int, String) -> Unit) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogWeightSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_weight_selection, container, false)
        return binding.root
    }

    val unitOptions = arrayOf("kg", "lb")

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.click = ProxyClick()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        binding.btLogin.text = btnTitle
        setDividerColor(binding.picker, Color.parseColor("#FF0000")) // 设置为红色

        val weight = userProfile.weight


        val weightPicker = binding.picker
        weightPicker.setTextColorCompat(Color.WHITE)
        weightPicker.minValue = 20
        weightPicker.maxValue = 227
        weightPicker.value = 70
        weightPicker.wrapSelectorWheel = true

        if (weight.isNullOrBlank()){
            weightPicker.value = 70
        }else{
            weightPicker.value = weight.toInt()
        }


        weightPicker.setOnValueChangedListener { picker, oldVal, newVal ->

            val selectedWeight = weightPicker.value
            Log.d("NumberPicker", "Selected Weight: $selectedWeight kg")
        }

        val weightPickerLb = binding.pickerLb
        weightPickerLb.setTextColorCompat(Color.WHITE)
        weightPickerLb.minValue = 44
        weightPickerLb.maxValue = 500
        weightPickerLb.value = 154
        weightPickerLb.wrapSelectorWheel = true


        weightPickerLb.setOnValueChangedListener { picker, oldVal, newVal ->

            val selectedWeightLbs = weightPickerLb.value
            Log.d("NumberPicker", "Selected Weight: $selectedWeightLbs lbs")
        }




        val unitPicker = binding.pickerUnit
        unitPicker.setTextColorCompat(Color.WHITE)
        unitPicker.minValue = 0
        unitPicker.maxValue = unitOptions.size - 1
        unitPicker.value = 0
        unitPicker.wrapSelectorWheel = true
        unitPicker.displayedValues = unitOptions




        unitPicker.setOnValueChangedListener { _, _, newVal ->
            val selectedUnit = unitOptions[newVal]
            if (selectedUnit == WeightUnit.lb.name) {
                weightPicker.visibility = View.INVISIBLE
                weightPickerLb.visibility = View.VISIBLE
            } else {
                weightPicker.visibility = View.VISIBLE
                weightPickerLb.visibility = View.INVISIBLE
            }
            Log.d("unitPicker", "Unit: $selectedUnit")
        }

        if (userProfile.weight_unit == WeightUnit.lb.name){

            if (!weight.isNullOrBlank()){
                val pair =  kgToPounds(weight.toDouble())
                weightPickerLb.value = pair
                unitPicker.value = 1
                weightPicker.visibility = View.INVISIBLE
                weightPickerLb.visibility = View.VISIBLE
            }
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
        fun next() {
            val selectedUnit = unitOptions[binding.pickerUnit.value]
            val selectedWeight = if (selectedUnit == "lb") binding.pickerLb.value else binding.picker.value
            dismiss()
            onItemClick.invoke(selectedWeight, selectedUnit)
        }
    }
}
