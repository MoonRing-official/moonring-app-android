package com.moonring.ring.utils

import com.moonring.ring.bean.DayData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


val commonDatePattern = "MM/dd/yyyy"

fun getDayCalendar(index:Int): Calendar {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, index)
    return calendar
}

fun getDayStartEnd(calendar:Calendar): Pair<Long, Long> {
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfDay = calendar.timeInMillis / 1000

    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val endOfDay = (calendar.timeInMillis - 1) / 1000

    return startOfDay to endOfDay
}
fun getDayStartEnd(date: Int = 0): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, date)
    return getDayStartEnd(calendar)
}

fun getTwoDayStartEnd(date: Int = 0): Pair<Long, Long> {
    val calendar = Calendar.getInstance()


    calendar.add(Calendar.DATE, date)
    val startOfDay = getDayStartEnd(calendar).second


    calendar.add(Calendar.DATE, -1)
    val endOfDay = getDayStartEnd(calendar).first

    return endOfDay to startOfDay
}


fun getWeekStartEnd(weeksAgo: Int = 0,days:Int = -6): Pair<Long, Long> {
    val calendar = Calendar.getInstance()

    calendar.add(Calendar.WEEK_OF_YEAR, weeksAgo)
    return getWeekStartEnd(calendar,days)
}
fun getWeekStartEnd(calendar:Calendar,days:Int = -6): Pair<Long, Long> {

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endOfToday = calendar.timeInMillis / 1000


    calendar.add(Calendar.DAY_OF_YEAR, days)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfPeriod = calendar.timeInMillis / 1000

    return startOfPeriod to endOfToday
}
fun getMonthStartEnd(monthsAgo:Int = 0,days:Int = -29): Pair<Long, Long> {
    val calendar = Calendar.getInstance()

    calendar.add(Calendar.MONTH, monthsAgo)
    return getMonthStartEnd(calendar,days)
}

fun getMonthStartEnd(calendar:Calendar,days:Int = -29): Pair<Long, Long> {

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endOfToday = calendar.timeInMillis / 1000


    calendar.add(Calendar.DAY_OF_YEAR, days)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfPeriod = calendar.timeInMillis / 1000

    return startOfPeriod to endOfToday
}



fun isToday(timestamp: Long): Boolean {
    val (start, end) = getDayStartEnd()
    return timestamp in start..end
}
fun isToday(calendar: Calendar): Boolean {
    val today = Calendar.getInstance()

    return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
}

fun isThisWeek(timestamp: Long): Boolean {
    val (startOfWeek, endOfWeek) = getWeekStartEnd()
    return timestamp in startOfWeek..endOfWeek
}

fun filterNotTodayData(stepDataList: List<DayData>): List<DayData> {
    return stepDataList.filterNot { dayData ->
        isToday(dayData.timestamp)
    }
}

fun formatDayToHourRange(day: String): String {
    val hour = day.toIntOrNull() ?: return ""
    return String.format("%02d:00-%02d:00", hour, hour + 1)
}



fun formatTimestampMyMoonring(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    return formatter.format(date)
}

fun formatTimestampMS(timestamp: Long,pattern:String = "HH:mm"): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(date)
}


fun Long.toTimeString(pattern:String = "HH:mm"): String{
    return formatTimestampMS(this,pattern)
}

fun Long.toTimeMills(): Long{
    return this*1000
}

fun Long.toTimeSec(): Long{
    return this/1000
}

fun convertSecondsToHM(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return   String.format("%02dh %02dm", hours, minutes)
}


fun getLastSevenDays(): List<Calendar> {
    return List(7) { i ->
        Calendar.getInstance().apply {
            add(Calendar.DATE, -i)
        }
    }.asReversed()
}

fun getPreviousSevenDays(startMultiplier: Int = 0): List<Calendar> {
    val startOffset = startMultiplier * 7
    return List(7) { i ->
        Calendar.getInstance().apply {
            add(Calendar.DATE, -(i + startOffset))
        }
    }.asReversed()
}


fun getLastThirtyDays(): List<Calendar> {
    return List(30) { i ->
        Calendar.getInstance().apply {
            add(Calendar.DATE, -i)
        }
    }.asReversed()
}
fun getPreviousThirtyDays(startMultiplier: Int = 0): List<Calendar> {
    val startOffset = startMultiplier * 30
    return List(30) { i ->
        Calendar.getInstance().apply {
            add(Calendar.DATE, -(i + startOffset))
        }
    }.asReversed()
}

fun Calendar.formatDate(pattern: String): String {
    val format = SimpleDateFormat(pattern)
    return format.format(this.time)
}



fun calculateBirthTimestamp(year: Int, month: Int, day: Int): Long {

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    calendar.set(year, month - 1, day, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.timeInMillis/1000
}





fun timestampToDateYMD(timestamp:Long):String{
    return SimpleDateFormat("yyyy-MM-dd").format(Date(timestamp* 1000))
}


fun timestampToDateYMDHMSS(timestamp:Long):String{
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").format(Date(timestamp* 1000))
}


fun timestampToDateHH(timestamp:Long):String{
    return SimpleDateFormat("HH").format(Date(timestamp* 1000))
}



 fun getTodayStartTimestamp(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis/1000
}

fun getTodayEndTimestamp(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis/1000
}

fun getYesterdayStartTimestamp(): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis/1000
}


fun getYesterdayEndTimestamp(): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis/1000
}

fun currentTimeInSec(): Long {
    return System.currentTimeMillis() / 1000
}



fun formatTimestampTimeRange(timeRange: Pair<Long, Long>): String {

    val startDate = Date(timeRange.first * 1000)

    val startDateFormatter = SimpleDateFormat("MM/dd", Locale.getDefault())

    val formattedStartDate = startDateFormatter.format(startDate)


    val endDate = Date(timeRange.second * 1000)

    val endDateFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

    val formattedEndDate = endDateFormatter.format(endDate)


    return "$formattedStartDate~$formattedEndDate"
}
