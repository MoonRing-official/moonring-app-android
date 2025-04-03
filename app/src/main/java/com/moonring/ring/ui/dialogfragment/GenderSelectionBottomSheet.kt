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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.module.common.bean.UserProfileMoonRing
import com.module.common.enums.Gender
import com.module.common.ext.clickNoRepeat
import com.module.common.ext.setNbOnItemClickListener
import com.moonring.ring.R
import com.moonring.ring.databinding.DialogGenderSelectionBinding
import com.moonring.ring.ext.setTextColorCompat

/**
 *    author : Administrator

 *    time   : 2024/3/20/020
 *    desc   :
 */

class GenderSelectionBottomSheet(private val btnTitle:String = "Next", private  val userProfile: UserProfileMoonRing,
                                 private val onItemClick: (Int, String) -> Unit): BottomSheetDialogFragment() {

    private lateinit var binding: DialogGenderSelectionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_gender_selection, container, false)
        return binding.root
    }
    var selectedGender = "Male"
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.click = ProxyClick()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
        binding.btLogin.text = btnTitle
        setDividerColor(binding.picker, Color.parseColor("#FF0000"))

        val genderPicker = binding.picker
        val genderOptions = arrayOf( "Female", "Male","Prefer Not to Tell")
        genderPicker.setTextColorCompat(Color.WHITE)
        genderPicker.minValue = 0
        genderPicker.maxValue = genderOptions.size - 1

        genderPicker.displayedValues = genderOptions
        genderPicker.wrapSelectorWheel = true


        genderPicker.setOnValueChangedListener { picker, oldVal, newVal ->

            selectedGender = genderOptions[newVal]
            Log.d("NumberPicker", "Selected Gender: $selectedGender")
        }


        val name = userProfile.gender
        val value = Gender.values().find { it.name == name }?.value ?: Gender.male.value
        genderPicker.value = value

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
        fun next(){

            dismiss()

            onItemClick.invoke(binding.picker.value,selectedGender)
        }
    }

}