package com.moonring.ring.bean

import com.moonring.ring.bean.room.SleepData
import com.moonring.ring.enums.SleepLevelType

//class ChartSeriesSleepItem {
//    var startDuration: Double = 0.0  // Duration from 21:00 until the first record, in hours
//    var data: List<SleepDurationModel> = emptyList()  // List of sleep duration segments
//    var totalList:List<SleepData> = emptyList()
//    var dayTitle: String = ""  // Title for the day, e.g., the day of the month
//    var weekTitle: String = ""  // Short name of the weekday
//    var dateTitle: String = ""  // Formatted date title, e.g., "MM/d yyyy"
//    var timeTitle: String = ""  // Formatted full date, e.g., "MM/dd/yyyy"
//}


class ChartSeriesSleepItem {

    var hourTitle: String = ""
    var hourPeriodTitle: String = ""
    var hourDateTitle: String = ""

    var dayTitle: String = ""
    var weekTitle: String = ""

    var dateTitle: String = ""
    var timeTitle: String = ""

    /// 从晚上21点开始到入睡前的空白时长（小时）
    var startDuration: Double = 0.0

    /// 总时长（小时）（深睡+浅睡）
    var totalDurationHour: Double = 0.0

    /// 总时长（秒）（深睡+浅睡）
    var totalDurationSecond: Int = 0

    /// 深睡时长（秒）
    var deepSleepDurationSecond: Int = 0

    /// 浅睡时长（秒）
    var lightSleepDurationSecond: Int = 0


    var totalList:List<SleepData> = emptyList()

    private var _data: List<SleepDurationModel> = listOf()
    var data: List<SleepDurationModel>
        get() = _data
        set(value) {
            _data = value
            updateDurations()
        }

    private fun updateDurations() {
        totalDurationHour = _data.filter { it.type != SleepLevelType.Awake }.sumOf { it.durationHour }
        totalDurationSecond = (totalDurationHour * 3600).toInt()

        deepSleepDurationSecond = _data.filter { it.type == SleepLevelType.DeepSleep }.sumOf { it.durationSecond }
        lightSleepDurationSecond = _data.filter { it.type == SleepLevelType.LightSleep }.sumOf { it.durationSecond }
    }
}

