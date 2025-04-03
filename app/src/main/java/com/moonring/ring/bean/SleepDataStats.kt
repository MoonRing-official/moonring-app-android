package com.moonring.ring.bean

import com.moonring.ring.utils.formatSecondsToHourMinute
import com.moonring.ring.utils.formatTimestampToHourMinute

/**
 *    author : Administrator

 *    time   : 2024/8/30/030
 *    desc   :
 */
data class SleepDataStats(
    var totalSleepTime: Int = 0,
    var deepSleepTime: Int = 0,
    var lightSleepTime: Int = 0,
    var awakeTime: Int = 0,
    var timestamp : Long = 0// 默认疲劳值为 0
) {
    fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return String.format("%02d:%02d", hours, minutes)
    }

    val formattedTotalSleepTime: String
        get() = formatTime(totalSleepTime)

    val formattedDeepSleepTime: String
        get() = formatTime(deepSleepTime)

    val formattedLightSleepTime: String
        get() = formatTime(lightSleepTime)

    val formattedAwakeTime: String
        get() = formatTime(awakeTime)



    val formattedTotalSleepTimeHM: String
        get() = formatSecondsToHourMinute(totalSleepTime)

    val formattedDeepSleepTimeHM: String
        get() = formatSecondsToHourMinute(deepSleepTime)

    val formattedlightSleepTimeHM: String
        get() = formatSecondsToHourMinute(lightSleepTime)

    val newestTime: String
        get() = formatTimestampToHourMinute(timestamp)
}
