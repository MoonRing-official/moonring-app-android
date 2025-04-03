package com.moonring.ring.utils

import com.moonring.ring.bean.DayData
import com.moonring.ring.homeAppViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 *    author : Administrator

 *    time   : 2024/9/11/011
 *    desc   :
 */



fun readDisCMByHeightModify(hei: Int): Double {

    var stepLength = hei * 0.45
    return stepLength
}




fun calculateDistanceAndCalories(totalSteps: Int): Pair<Double, Double>? {
    try {
        homeAppViewModel.userProfile.value?.let {
            val stepLengthCm = readDisCMByHeightModify(it.heightCM)

            val distanceMeters = totalSteps * stepLengthCm / 100.0


            val calories = distanceMeters * it.weightKg * 6 / 10 / 1000

            return Pair(distanceMeters, calories)
        }
    }catch (e:Exception){
        e.printStackTrace()
    }

    return null
}


fun calculateCal(totalSteps: Int): Double {
    try {
        homeAppViewModel.userProfile.value?.let {
            val stepLengthCm = readDisCMByHeightModify(it.heightCM)


            val distanceMeters = totalSteps * stepLengthCm / 100.0


            val calories = distanceMeters * it.weightKg * 6 / 10

            return calories
        }
    }catch (e:Exception){
        e.printStackTrace()
    }

    return 0.0
}



fun getCal(totalSteps: Int): Int {

    val cal = calculateCal(totalSteps).roundToInt()
    println("totalSteps:${totalSteps} cal:${cal}")
    return cal
}


fun isDaysDifferenceValid(stepDataList: List<DayData>, index: Int): Boolean {

    val firstTimestampMillis = (stepDataList.firstOrNull()?.timestamp ?: return false) * 1000


    val date = Date(firstTimestampMillis)
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val recordDateString = sdf.format(date)

    val recordDate = sdf.parse(recordDateString) ?: return false


    val todayDateString = sdf.format(Calendar.getInstance().time)
    val todayDate = sdf.parse(todayDateString) ?: return false


    val daysBetween = TimeUnit.MILLISECONDS.toDays(todayDate.time - recordDate.time).toInt()


    return daysBetween == index
}


fun formatTimestampToHourMinute(timestampInSeconds: Long): String {

    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    formatter.timeZone = TimeZone.getDefault()

    return formatter.format(timestampInSeconds * 1000)
}

fun formatSecondsToHourMinute(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return "${hours}h ${minutes}m"
}