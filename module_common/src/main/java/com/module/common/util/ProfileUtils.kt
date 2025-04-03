package com.module.common.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

/**
 *    author : Administrator

 *    time   : 2024/10/27/027
 *    desc   :
 */

fun formatBirthday(timestamp: Long): String {

    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    val date = Date(timestamp)

    return dateFormat.format(date)
}
fun calculateAge(birthdayTimestamp: Long): Int {

    val birthday = Calendar.getInstance().apply {
        timeInMillis = birthdayTimestamp
    }

    val today = Calendar.getInstance()


    var age = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR)


    if (today.get(Calendar.MONTH) < birthday.get(Calendar.MONTH) ||
        (today.get(Calendar.MONTH) == birthday.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birthday.get(
            Calendar.DAY_OF_MONTH))) {
        age--
    }

    return age
}


fun extractFeetAndInches(measurement: String): Pair<Int, Int>? {

    val regex = """(\d+)' (\d+)"""".toRegex()


    val matchResult = regex.find(measurement)


    return matchResult?.destructured?.let { (feet, inches) ->
        Pair(feet.toInt(), inches.toInt())
    }
}


fun feetInchesToCm(feet: Int, inches: Int): Int {
    val cm = feet * 30.48 + inches * 2.54
    return Math.ceil(cm).toInt()
}


fun cmToFeetInches(cm: Double): Pair<Int, Int> {
    val feet = Math.floor(cm / 30.48).toInt()
    val inches = ((cm / 30.48 - feet) * 12).toInt()
    return Pair(feet, inches)
}


fun poundsToKg(pounds: Double): Int {
    val kg = pounds / 2.20462
    return Math.ceil(kg).toInt()
}


fun kgToPounds(kilograms: Double): Int {
    val lb = kilograms * 2.20462
    return lb.roundToInt()
}