package com.moonring.ring.ui.dialogfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.module.common.bean.UserProfileMoonRing
import com.module.common.support.log.LogInstance
import com.moonring.ring.R
import com.moonring.ring.databinding.DialogBirthdaySelectionBinding
import com.moonring.ring.ext.setTextColorCompat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 *    author : Administrator

 *    time   : 2024/10/14/014
 *    desc   :
 */
class BirthdaySelectionBottomSheet(private val btnTitle:String = "Next", private  val userProfile: UserProfileMoonRing, private val onItemClick: (Int, Int, Int) -> Unit) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogBirthdaySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_birthday_selection, container, false)
        return binding.root
    }

    val monthsArray = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.click = ProxyClick()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
        binding.btLogin.text = btnTitle





        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        binding.pickerYear.apply {
            setTextColorCompat(Color.WHITE)
            minValue = 1900
            maxValue = currentYear
            value = 1990
        }


        binding.pickerMonth.apply {
            setTextColorCompat(Color.WHITE)
            displayedValues = monthsArray
            minValue = 1
            maxValue = 12
            value = 1
        }


        binding.pickerDay.apply {
            setTextColorCompat(Color.WHITE)
            minValue = 1
            maxValue = 31
            value = 1
        }
        val timestamp = userProfile.birthday * 1000

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = timestamp
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        if (timestamp>0){
            binding.pickerYear.value  = year
            binding.pickerMonth.value  = month
            binding.pickerDay.value  = day
        }

        val updateDays = {
            val year = binding.pickerYear.value
            val month = binding.pickerMonth.value
            val maxDay = getDaysOfMonth(year, month)
            binding.pickerDay.maxValue = maxDay
            if (binding.pickerDay.value > maxDay) {
                binding.pickerDay.value = maxDay
            }
        }

        binding.pickerYear.setOnValueChangedListener { _, _, _ ->
            updateDays()
        }

        binding.pickerMonth.setOnValueChangedListener { _, _, _ ->
            updateDays()
        }


    }

    private fun getDaysOfMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    inner class ProxyClick {
        fun next() {
            val year = binding.pickerYear.value
            val month = binding.pickerMonth.value
            val day = binding.pickerDay.value
//            val selectedDate = "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
            dismiss()
            onItemClick.invoke(year,month,day)
        }
    }
}
