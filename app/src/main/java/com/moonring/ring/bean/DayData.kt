package com.moonring.ring.bean

import com.moonring.ring.utils.calculateDistanceAndCalories
import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale

open class DayData(
    val type: Int = 0,
    val timestamp: Long = 0,
    var step: Int =0,
    val heartrate: Int =0,
    val recordDate: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp * 1000))
) {

    val calculateCal: String
        get() = calculateDistanceAndCalories(step)?.let { "${it.second}" } ?: ""



    var systolicPressure: Int = 0
    var diastolicPressure: Int = 0
    var oxygen: Int = 0
    var fatigueValue: Int = 0


}
