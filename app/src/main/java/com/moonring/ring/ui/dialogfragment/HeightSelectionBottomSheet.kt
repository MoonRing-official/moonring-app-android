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
import com.module.common.util.cmToFeetInches
import com.moonring.ring.R
import com.moonring.ring.databinding.DialogHeightSelectionBinding
import com.moonring.ring.ext.setTextColorCompat

/**
 *    author : Administrator

 *    time   : 2024/10/14/014
 *    desc   :
 */
class HeightSelectionBottomSheet(private val btnTitle:String = "Next", private  val userProfile: UserProfileMoonRing, private val onItemClick: (Int, Int, Int, String) -> Unit): BottomSheetDialogFragment() {
    private lateinit var binding: DialogHeightSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_height_selection, container, false)
        return binding.root
    }

    val unitOptions = arrayOf("cm", "ft")

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.click = ProxyClick()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
        binding.btLogin.text = btnTitle
        setDividerColor(binding.picker, Color.parseColor("#FF0000"))

        val height = userProfile.height



        val heightPicker = binding.picker

        heightPicker.setTextColorCompat(Color.WHITE)
        heightPicker.minValue = 49
        heightPicker.maxValue = 250

        heightPicker.wrapSelectorWheel = true

        if (height.isNullOrBlank()){
            heightPicker.value = 170
        }else{
            heightPicker.value = height.toInt()
        }

        heightPicker.value = height.toInt()

        heightPicker.setOnValueChangedListener { picker, oldVal, newVal ->

            val selectedHeight = heightPicker.value
            Log.d("NumberPicker", "Selected Height: $selectedHeight")
        }

        val pickerFtFirst = binding.pickerFtFirst
        val pickerSecond = binding.pickerSecond

        pickerFtFirst.setTextColorCompat(Color.WHITE)
        pickerFtFirst.minValue = 1
        pickerFtFirst.maxValue = 8
        pickerFtFirst.value = 5
        pickerFtFirst.wrapSelectorWheel = true


        pickerSecond.setTextColorCompat(Color.WHITE)
        pickerSecond.minValue = 0
        pickerSecond.maxValue = 11
        pickerSecond.value = 6
        pickerSecond.wrapSelectorWheel = true






        pickerFtFirst.setOnValueChangedListener { _, _, newVal ->

            when (newVal) {
                1 -> {
                    pickerSecond.minValue = 7
                    pickerSecond.maxValue = 11
                }
                8 -> {
                    pickerSecond.minValue = 0
                    pickerSecond.maxValue = 3
                }
                else -> {
                    pickerSecond.minValue = 0
                    pickerSecond.maxValue = 11
                }
            }


            val selectedFeetFirst = pickerFtFirst.value
            val selectedInches = pickerSecond.value
            Log.d("NumberPicker", "Selected Feet: $selectedFeetFirst ft, Inches Range: ${pickerSecond.minValue} to ${pickerSecond.maxValue}")
            Log.d("NumberPicker", "Selected Inches: $selectedInches in")


            if (pickerSecond.value < pickerSecond.minValue) {
                pickerSecond.value = pickerSecond.minValue
            }
            if (pickerSecond.value > pickerSecond.maxValue) {
                pickerSecond.value = pickerSecond.maxValue
            }


            pickerSecond.invalidate()
        }

        pickerSecond.setOnValueChangedListener { _, _, newVal ->
            val selectedFeetSecond = pickerSecond.value
            Log.d("NumberPicker", "Selected Feet (Second Picker): $selectedFeetSecond ft")
        }

        val unitPicker = binding.pickerUnit

        unitPicker.setTextColorCompat(Color.WHITE)
        unitPicker.minValue = 0
        unitPicker.maxValue = unitOptions.size - 1
        unitPicker.value = 0
        unitPicker.wrapSelectorWheel = true

        unitPicker.displayedValues = unitOptions

        unitPicker.setOnValueChangedListener { picker, oldVal, newVal ->

            val selectedHeight = unitOptions[newVal]

        }

        unitPicker.setOnValueChangedListener { _, _, newVal ->
            val selectedUnit = unitOptions[newVal]


            if (selectedUnit == HeightUnit.cm.name) {

                heightPicker.visibility = View.VISIBLE
                binding.pickerFtFirst.visibility = View.GONE
                binding.pickerSecond.visibility = View.GONE
            } else if (selectedUnit == HeightUnit.ft.name) {

                heightPicker.visibility = View.GONE
                binding.pickerFtFirst.visibility = View.VISIBLE
                binding.pickerSecond.visibility = View.VISIBLE
            }
        }


        if (userProfile.height_unit == HeightUnit.ft.name){

            if (!height.isNullOrBlank()){
                val pair =  cmToFeetInches(height.toDouble())
                pickerFtFirst.value = pair.first
                pickerSecond.value = pair.second
                unitPicker.value = 1

                heightPicker.visibility = View.GONE
                binding.pickerFtFirst.visibility = View.VISIBLE
                binding.pickerSecond.visibility = View.VISIBLE
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
            val unit = unitOptions[binding.pickerUnit.value]
            if (unit == "cm") {

                onItemClick.invoke(binding.picker.value,0,0, unit)
            } else if (unit == "ft") {

                val feet = binding.pickerFtFirst.value
                val inches = binding.pickerSecond.value


                onItemClick.invoke(0,feet,inches, unit)

            }
            dismiss()
        }
    }
}
