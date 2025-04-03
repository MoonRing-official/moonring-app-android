package com.moonring.ring.utils

import com.github.aachartmodel.aainfographics.aachartcreator.AAChartLineDashStyleType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AADataLabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAHover
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAInactive
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AASelect
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStates
import com.github.aachartmodel.aainfographics.aatools.AAColor
import com.moonring.ring.bean.ChartSeriesSleepItem
import com.moonring.ring.bean.DayData
import com.moonring.ring.bean.SleepDurationModel
import com.moonring.ring.bean.room.SleepData
import com.moonring.ring.enums.SleepLevelType
import com.moonring.ring.support.moonring.MRRingManager
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HealthDataDetailsViewModel {


    fun sleepItemByDay(targetDate: Calendar,sleepList: List<SleepData>): ChartSeriesSleepItem {
        val item = ChartSeriesSleepItem()


        targetDate.add(Calendar.DATE, -1)
        targetDate.set(Calendar.HOUR_OF_DAY, 21)
        targetDate.set(Calendar.MINUTE, 0)
        targetDate.set(Calendar.SECOND, 0)
        val yStartTimestamp = targetDate.timeInMillis / 1000


        targetDate.add(Calendar.DATE, 1)
        targetDate.set(Calendar.HOUR_OF_DAY, 11)
        val tEndTimestamp = targetDate.timeInMillis / 1000


        val totalList = sleepList.filter {
            it.timestamp in (yStartTimestamp + 1)..tEndTimestamp
        }
        item.totalList = totalList

        val firstRecord = totalList.minByOrNull { it.timestamp } ?: SleepData(timestamp = 0, value = 0)
        val startTimestamp = if (firstRecord.timestamp > 0) firstRecord.timestamp - yStartTimestamp else 0

        item.startDuration = startTimestamp.toDouble() / 3600





        val timeList = MRRingManager.conversionSleepDurationList(totalList)
        item.data = timeList



        val dateFormatter = SimpleDateFormat("MM/d yyyy", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())


        item.dayTitle = targetDate.get(Calendar.DAY_OF_MONTH).toString()


        val weekDay = targetDate.get(Calendar.DAY_OF_WEEK)
        val weekTitle = DateFormatSymbols(Locale.getDefault()).shortWeekdays[weekDay]
        item.weekTitle = weekTitle


        item.dateTitle = dateFormatter.format(targetDate.time)


        item.timeTitle = timeFormatter.format(targetDate.time)

        return item
    }


    fun sleepItemByDays(dateList: List<Calendar>,sleepList: List<SleepData>): List<ChartSeriesSleepItem> {
        return dateList.map { sleepItemByDay(it,sleepList) }
    }


     fun transformSeriesDay(item: ChartSeriesSleepItem): List<AASeriesElement> {
        var data = item.data.map { mapOf("x" to it.type.yAxisIndex, "y" to it.durationHour, "color" to it.type.yAxisIndexColor) }
        if (item.data.isNotEmpty()) {
            val startDuration = mapOf("x" to item.data.first().type.yAxisIndex, "y" to item.startDuration, "color" to "transparent")
            data = listOf(startDuration) + data
        }

        val element = AASeriesElement().apply {
            type(AAChartType.Waterfall)
            borderWidth(0)
            dashStyle(AAChartLineDashStyleType.ShortDot)
            borderRadius(2f)
            lineWidth(if (item.data.isNotEmpty()) 1 else 0)
            dataLabels(AADataLabels().enabled(false))
            data(data.toTypedArray())
        }

        return listOf(element)
    }

    fun transformSeriesDay(items: List<ChartSeriesSleepItem>,borderRadius :Float = 0f,pointWith:Float = 0f,pointPlacementStr:String?=null): List<AASeriesElement> {
        val series = items.mapIndexedNotNull { index, item ->
            val data = item.data.map { sleepDurationModel ->
                mapOf(
                    "x" to index,
                    "y" to sleepDurationModel.durationHour,
                    "color" to sleepDurationModel.type.yAxisIndexColor
                )
            }.toMutableList()

            item.data.firstOrNull()?.let { first ->
                val startDuration = mapOf(
                    "x" to first.type.yAxisIndex,
                    "y" to item.startDuration,
                    "color" to "rgba(0,0,0,0)"
                )
                data.add(0, startDuration)
            }

            if (item.data.isEmpty()) {
                data.add(mapOf("x" to index, "y" to 0, "color" to "rgba(0,0,0,0)"))
            }

            AASeriesElement().apply {
                type(AAChartType.Waterfall)
                borderWidth(0)
                if (pointWith>0){
                    pointWidth(pointWith)
                }
                if (!pointPlacementStr.isNullOrBlank()){
                    pointPlacement(pointPlacementStr)
                }

                dashStyle(AAChartLineDashStyleType.ShortDot)

                borderRadius(borderRadius)
                lineWidth(0)
                states(
                    AAStates().apply {
                        hover(AAHover().enabled(false))

                        select(AASelect())
                        inactive(AAInactive().enabled(false))
                    }
                )
                data(data.toTypedArray())
            }
        }
        return series
    }

}
