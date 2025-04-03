package com.moonring.ring.bean

import com.moonring.ring.enums.SleepLevelType



class SleepDurationModel(
    var type: SleepLevelType = SleepLevelType.Awake,
    var durationHour: Double = 0.0,
    var durationSecond: Int = 0,
    var startTimeText: String = "",
    var endTimestamp: Long = 0,
    var endTimeText: String = ""
) {
    val durationRangeText: String
        get() = "$startTimeText - $endTimeText"
}
