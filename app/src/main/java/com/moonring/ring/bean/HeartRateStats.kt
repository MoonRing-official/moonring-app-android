package com.moonring.ring.bean

import com.moonring.ring.utils.formatTimestampToHourMinute

data class HeartRateStats(
    val heartrate: Int = 0,        // 默认心率为 0
    val minHeartRate: Int = 0,     // 默认最小心率为 0
    val maxHeartRate: Int = 0,     // 默认最大心率为 0
    val averageHeartRate: Int = 0,  // 默认平均心率为 0
    val timestamp: Long = 0
){
    val newestTime: String
        get() = formatTimestampToHourMinute(timestamp)
}