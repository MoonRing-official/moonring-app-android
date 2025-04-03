package com.moonring.ring.bean

data class AutoHeartRateTestSettings(
    var `switch`: Int = 1, // 0: off, 1: on
    var startHour: Int = 0,
    var startMin: Int = 0,
    var endHour: Int = 23,
    var endMin: Int = 59,
    var interval: Int = 15, // Interval in minutes
    var duration: Int = 2  // Duration of each measurement in minutes
) {
    val startTime: String
        get() = String.format("%02d:%02d", startHour, startMin)

    val endTime: String
        get() = String.format("%02d:%02d", endHour, endMin)

    val intervalTime: String
        get() = String.format("%02d分钟", interval)
}


