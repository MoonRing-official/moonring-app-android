package com.moonring.ring.bean

import com.moonring.ring.utils.formatTimestampToHourMinute

class OxygenSensorData(
    val heartrate: Int = 0,           // 默认心率为 0
    val systolicPressure: Int = 0,    // 默认收缩压（高压）为 0
    val diastolicPressure: Int = 0,   // 默认舒张压（低压）为 0
    val oxygen: Int = 0,              // 默认血氧水平为 0
    val fatigueValue: Int = 0,
    val timestamp : Long = System.currentTimeMillis()/1000// 默认疲劳值为 0
) {

    val fatigueLevel: String
        get() = when {
            fatigueValue < 34 -> "轻度疲劳"
            fatigueValue < 67 -> "中度疲劳"
            else -> "重度疲劳"
        }

    val newestTime: String
        get() = formatTimestampToHourMinute(timestamp)
}



