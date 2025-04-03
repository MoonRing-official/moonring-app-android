package com.moonring.ring.utils

import com.moonring.ring.bean.DayData
import com.moonring.ring.bean.OxygenSensorData
import com.moonring.ring.bean.SleepDataStats
import com.moonring.ring.bean.room.OxygenData
import com.moonring.ring.bean.room.SleepData
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 *    author : Administrator

 *    time   : 2024/10/12/012
 *    desc   :
 */



 fun getDaylyData(dataList:List<DayData>):LinkedHashMap<String, MutableList<DayData>>{
    val heartBeatData = linkedMapOf<String, MutableList<DayData>>()


    (0..23).forEach { hour ->
        heartBeatData[hour.toString()] = mutableListOf<DayData>()
    }


    dataList.forEach { dayData ->
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dayData.timestamp * 1000

        val hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        heartBeatData[hour]?.add(dayData)     }
    return heartBeatData


}


fun getWeeklyEmptyData(timeRange: Pair<Long, Long>): LinkedHashMap<String, MutableList<DayData>> {
    val weeklyData = linkedMapOf<String, MutableList<DayData>>()


    val dateFormat = SimpleDateFormat(commonDatePattern)

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeRange.second * 1000

    for (i in 0..6) {

        calendar.add(Calendar.DAY_OF_MONTH, -i)


        val formattedDate = dateFormat.format(calendar.time)
        weeklyData[formattedDate] = emptyList<DayData>().toMutableList()


        calendar.add(Calendar.DAY_OF_MONTH, i)
    }

    val reversedWeeklyData = linkedMapOf<String, MutableList<DayData>>()
    weeklyData.entries.reversed().forEach {
        reversedWeeklyData[it.key] = it.value
    }

    return reversedWeeklyData
}

fun getMonthlyEmptyData(timeRange: Pair<Long, Long>): LinkedHashMap<String, MutableList<DayData>> {
    val monthlyData = linkedMapOf<String, MutableList<DayData>>()



    val dateFormat = SimpleDateFormat(commonDatePattern)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeRange.second * 1000

    for (i in 0..29) {
        calendar.add(Calendar.DAY_OF_MONTH, -i)


        val formattedDate = dateFormat.format(calendar.time)
        monthlyData[formattedDate] = mutableListOf()


        calendar.add(Calendar.DAY_OF_MONTH, i)
    }


    val reversedMonthlyData = linkedMapOf<String, MutableList<DayData>>()
    monthlyData.entries.reversed().forEach {
        reversedMonthlyData[it.key] = it.value
    }

    return reversedMonthlyData
}



fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return String.format("%02d:%02d", hours, minutes)
}


 fun calculateMiles(kilometers: Double): String {
    val miles = kilometers * 0.621371
    return String.format("%.2f", miles)
}

fun Double.distanceInKilometers():Double{
    return  this / 1000.0
}

