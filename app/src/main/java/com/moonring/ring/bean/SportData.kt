package com.moonring.ring.bean

import com.moonring.ring.utils.formatTime
import com.moonring.ring.utils.formatTimestampToHourMinute


data class SportData(
    val type: Int = 0,
    val timestamp: Long = System.currentTimeMillis() / 1000,
    var step: Int = 0,
    var distance: Int = 0,
    var cal: Int = 0,
    val cursleeptime: Int = 0,
    val totalrunningtime: Int = 0,
    var steptime: Int = 0
) {

    val distanceInKilometers: Double
        get() = distance / 1000.0


    val stepTimeHMS: String
        get() = convertSecondsToHMS(steptime)


    val newestTime: String
        get() = formatTimestampToHourMinute(timestamp)

    private fun convertSecondsToHMS(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
}

