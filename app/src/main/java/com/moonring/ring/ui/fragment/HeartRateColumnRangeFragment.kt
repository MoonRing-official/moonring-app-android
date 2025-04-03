package com.moonring.ring.ui.fragment

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ThreadUtils
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AAMoveOverEventMessageModel
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAMarker
import com.module.common.base.BaseBarFragment
import com.module.common.ext.clickNoRepeat
import com.module.common.support.log.LogInstance
import com.moonring.ring.bean.DayData
import com.moonring.ring.bean.HeartRateStats
import com.moonring.ring.bean.room.HeartRateData
import com.moonring.ring.databinding.FragmentHeartRateColumnRangeBinding
import com.moonring.ring.enums.DateType
import com.moonring.ring.enums.GetDataByDay
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.moonring.DataFetcherHeartBeat
import com.moonring.ring.support.moonring.DataFetcherStep
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.utils.commonDatePattern
import com.moonring.ring.utils.currentTimeInSec
import com.moonring.ring.utils.emptyDayAASeriesElement
import com.moonring.ring.utils.formatDate
import com.moonring.ring.utils.formatDayToHourRange
import com.moonring.ring.utils.formatTimestampMS
import com.moonring.ring.utils.formatTimestampTimeRange
import com.moonring.ring.utils.getDailyHeartRateChart
import com.moonring.ring.utils.getDayCalendar
import com.moonring.ring.utils.getDayStartEnd
import com.moonring.ring.utils.getDaylyData
import com.moonring.ring.utils.getHeartRateChart
import com.moonring.ring.utils.getMonthStartEnd
import com.moonring.ring.utils.getMonthlyEmptyData
import com.moonring.ring.utils.getWeekStartEnd
import com.moonring.ring.utils.getWeeklyEmptyData
import com.moonring.ring.utils.gradientCaloriesColor
import com.moonring.ring.utils.gradientHeartRateColor
import com.moonring.ring.utils.isToday
import com.moonring.ring.utils.toTimeMills
import com.moonring.ring.viewmodule.state.HeartRateColumnRangeViewModel
import com.moonring.ring.viewmodule.state.SharedChartDataModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 *    author : Administrator

 *    time   : 2024/10/10/010
 *    desc   :
 */
class HeartRateColumnRangeFragment : BaseBarFragment<HeartRateColumnRangeViewModel, FragmentHeartRateColumnRangeBinding>() , AAChartView.AAChartViewCallBack {

    private val mClick by lazy {
        ProxyClick()
    }
    val  sharedChartDataModel: SharedChartDataModel by activityViewModels()

    val title by lazy {
        arguments?.getString("title")?:""
    }
    var minCaloriesSet  = ""
    var maxCaloriesSet  = ""
    var dayWeekSet  = ""
    var lastSynHeartRate = ""

    var restHR = ""

    var   barChartX = 0f
    var   barChartY = 0f
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel
        mViewModel.type.set(title)

        setupColumnRangeChart()
        mDatabind.barChart.callBack = this

        mDatabind.barChart.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    barChartX = event.rawX
                    barChartY = event.rawY

                    LogInstance.d("TouchEvent", "barChart User touched at X: $barChartX, Y: $barChartY")
                }
            }
            false
        }


        mDatabind.rippleLeft.clickNoRepeat {
            mViewModel.recordIndex.set(mViewModel.recordIndex.get() - 1)
            changeDate()
        }
        mDatabind.rippleRight.clickNoRepeat {
            mViewModel.recordIndex.set(mViewModel.recordIndex.get() + 1)
            changeDate()
        }

    }

    var dayTodayList: MutableList<DayData>?=null
    fun changeDate(){
        when (title) {
            DateType.Day.name -> {

                val (start, end) = getDayStartEnd(mViewModel.recordIndex.get())
                val calendar = getDayCalendar(mViewModel.recordIndex.get())
                //
                mDatabind.slTooltip.visibility = View.INVISIBLE


                mViewModel.viewModelScope.launch {
                    val heartRateDataList = DatabaseUtils.fetchHeartRateData(fromTimestamp = start,toTimestamp = end)
                    val stepDataList =  heartRateDataList.map {
                        DayData(timestamp = it.timestamp, heartrate =  it.value)
                    }

                    dayList(stepDataList,calendar)
                }

            }
            DateType.Week.name -> {


                val (start, end) = getWeekStartEnd(mViewModel.recordIndex.get())
                mDatabind.slTooltip.visibility = View.INVISIBLE


                mViewModel.viewModelScope.launch {
                    val heartRateDataList = DatabaseUtils.fetchHeartRateData(fromTimestamp = start,toTimestamp = end)
                    val stepDataList =  heartRateDataList.map {
                        DayData(timestamp = it.timestamp, heartrate =  it.value)
                    }
                    weekList(stepDataList, timeRange = Pair (start, end))
                }

            }
            DateType.Month.name -> {



                val (start, end) = getMonthStartEnd(mViewModel.recordIndex.get())
                mDatabind.slTooltip.visibility = View.INVISIBLE

                mViewModel.viewModelScope.launch {
                    val heartRateDataList = DatabaseUtils.fetchHeartRateData(fromTimestamp = start,toTimestamp = end)
                    val stepDataList =  heartRateDataList.map {
                        DayData(timestamp = it.timestamp, heartrate =  it.value)
                    }
                    monthList(stepDataList, timeRange = Pair (start, end))
                }

            }
            else -> {

            }
        }
        defaultTip()
    }
    fun defaultTip(){
        mDatabind.slTooltip.visibility = View.INVISIBLE
        mDatabind.clTotal.visibility = View.VISIBLE

        if (mViewModel.chartOptions!=null){
            mDatabind.barChart.aa_refreshChartWithChartOptions(mViewModel.chartOptions!!)
        }

    }
    override fun chartViewDidFinishLoad(aaChartView: AAChartView) {

    }

    override fun chartViewMoveOverEventMessage(aaChartView: AAChartView, messageModel: AAMoveOverEventMessageModel) {

        ThreadUtils.runOnUiThread {
            val clTopWidth = mDatabind.clTop.width
            val tooltipWidth = mDatabind.slTooltip.width

            messageModel.apply {
                LogInstance.d("ChartEvent", "barChart Name: $name, x: $x, y: $y, category: $category, index: $index")
                val xPos = barChartX?.toInt() ?: 0
                var tooltipX = xPos - tooltipWidth / 2


                if (tooltipX < 0) {
                    tooltipX = 0
                }


                if (tooltipX + tooltipWidth > clTopWidth) {
                    tooltipX = clTopWidth - tooltipWidth
                }


                mDatabind.slTooltip.visibility = View.VISIBLE
                mDatabind.slTooltip.translationX = tooltipX.toFloat()

                val dataIndex = index ?: 0

                val dataIndexValue  = mViewModel.caloriesData.getOrNull(dataIndex)


                if (dataIndexValue != null) {
                    when (title) {
                        DateType.Day.name -> {

                            LogInstance.i(" Day -->${dataIndexValue[0]}")

                            mViewModel.toolTipTime.set( formatDayToHourRange("${dataIndexValue[0]}"))

                            val valueList = mViewModel.originData["${dataIndexValue[0]}"]
                            if (valueList != null) {
                               val max =  valueList.maxByOrNull { it.heartrate }?.heartrate?:0
                               val min =  valueList.minByOrNull { it.heartrate }?.heartrate?:0
                                mViewModel.toolTipValue.set("$min~$max")
                            } else {
                                LogInstance.d("Error", "Data not found for index: $dataIndex")
                            }
                        }
                        DateType.Week.name -> {


                            LogInstance.i(" Week -->${dataIndexValue[0]}")
                            val time = mViewModel.originData.keys.toList()[dataIndex]
                            mViewModel.toolTipTime.set("${time}")
                            val valueList = mViewModel.caloriesData[dataIndex]
                            val min = valueList[0]
                            val max = valueList[1]
                            mViewModel.toolTipValue.set("$min~$max")

                        }
                        DateType.Month.name -> {

                            LogInstance.i(" Month -->${dataIndexValue[0]}")
                            val time = mViewModel.originData.keys.toList()[dataIndex]
                            mViewModel.toolTipTime.set("${time}")
                            val valueList = mViewModel.caloriesData[dataIndex]
                            val min = valueList[0]
                            val max = valueList[1]
                            mViewModel.toolTipValue.set("$min~$max")
                        }
                        else -> {

                        }
                    }



                }




                mDatabind.clTotal.visibility = View.INVISIBLE
            }
        }


    }

    private fun setupColumnRangeChart() {

    }

    override fun toolbarBackVisity(): Boolean {
        return true
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        when (title) {
            DateType.Day.name -> {


                val (start, end) = getDayStartEnd()

                mViewModel.viewModelScope.launch {
                    val heartRateDataList = DatabaseUtils.fetchHeartRateData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = heartRateDataList.map {
                        DayData(timestamp = it.timestamp, heartrate = it.value)
                    }
                    dayTodayList = dayDataList.toMutableList()
                    dayList(dayDataList)
                    homeAppViewModel.callRemoteGetData(GetDataByDay.HeartBeat.value,0)
                }



            }
            DateType.Week.name -> {

                val (start, end) = getWeekStartEnd()
                mViewModel.viewModelScope.launch {
                    val heartRateDataList = DatabaseUtils.fetchHeartRateData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = heartRateDataList.map {
                        DayData(timestamp = it.timestamp, heartrate = it.value)
                    }
                    weekList(dayDataList, timeRange = Pair (start, end))
                    DataFetcherStep.startFetching(GetDataByDay.HeartBeat.value)
                }

            }
            DateType.Month.name -> {

                val (start, end) = getMonthStartEnd()
                mViewModel.viewModelScope.launch {
                    val heartRateDataList = DatabaseUtils.fetchHeartRateData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = heartRateDataList.map {
                        DayData(timestamp = it.timestamp, heartrate = it.value)
                    }
                    monthList(dayDataList, timeRange = Pair (start, end))

                }
            }
            else -> {

            }
        }
    }


    fun calculateRestingHeartRate(dataList: List<DayData>): Int {

        val filteredData = dataList.filter { it.heartrate > 0 }


        val minHeartRateData = filteredData.minByOrNull { it.heartrate }


        val minTimestamp = minHeartRateData?.timestamp?.let { (it - 30 * 60) * 1000 } ?: 0L
        val maxTimestamp = minHeartRateData?.timestamp?.let { (it + 30 * 60) * 1000 } ?: 0L


        val rangeData = filteredData.filter { it.timestamp * 1000 in minTimestamp..maxTimestamp }


        return if (rangeData.isNotEmpty()) {
            rangeData.map { it.heartrate }.average().toInt()
        } else {
            0
        }
    }



    fun dayList(dataList: List<DayData>,calendar:Calendar =  Calendar.getInstance(),isRefresh:Boolean = false) {

        val heartBeatData = getDaylyData(dataList)



        val categories = heartBeatData.keys


        val results = mutableListOf<Array<Int>>()
        heartBeatData.forEach { (hour, data) ->
            data.forEach { dayData ->
                results.add(arrayOf(hour.toInt(), dayData.heartrate))
            }
        }

        val option = getDailyHeartRateChart(categories.toTypedArray())
        option.series = arrayOf(AASeriesElement()
            .data(results.toTypedArray())
            .color(gradientHeartRateColor)
            .marker(AAMarker()
                .radius(4f)
                .symbol("circle")
            ),emptyDayAASeriesElement())

        mViewModel.chartOptions = option

        if (isRefresh){
            mDatabind.barChart.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(arrayOf(AASeriesElement()
                .data(results.toTypedArray())
                .color(gradientHeartRateColor)
                .marker(AAMarker()
                    .radius(4f)
                    .symbol("circle")
                ),emptyDayAASeriesElement()))
        }else{
            mDatabind.barChart.aa_drawChartWithChartOptions(option)
        }







        val filteredData = dataList.filter { it.heartrate > 0 }


        val minHeartRate = filteredData.minByOrNull { it.heartrate }?.heartrate ?: 0  // 如果没有数据，返回0


        val maxHeartRate = filteredData.maxByOrNull { it.heartrate }?.heartrate ?: 0  // 如果没有数据，返回0


        mViewModel.min.set("$minHeartRate")
        mViewModel.max.set("$maxHeartRate")

        val restHRValue = calculateRestingHeartRate(dataList)
        restHR = "$restHRValue"


        minCaloriesSet = ""
        maxCaloriesSet = ""

        val lastSyncData = dataList
            .filter { it.heartrate > 0 }
            .maxByOrNull { it.timestamp }
        val todayHeartBeat = lastSyncData?.heartrate ?: 0
        val todayHeartBeatTime = lastSyncData?.timestamp ?: 0

        mViewModel.latest.set("$todayHeartBeat")


        dayWeekSet =  if (todayHeartBeatTime == 0L){
            ""
        }else{
            "at ${formatTimestampMS(todayHeartBeatTime*1000)}"
        }
        lastSynHeartRate = "$todayHeartBeat"

        setsharedData()



        if (isToday(calendar)){
            mViewModel.time.set("Today")
        }else{
            mViewModel.time.set(calendar.formatDate(commonDatePattern))
        }

        mViewModel.caloriesData = results
        mViewModel.originData = heartBeatData



    }

    override fun onDestroyView() {
        super.onDestroyView()
        dayTodayList?.clear()
    }

    fun weekList(dataList: List<DayData>,timeRange:Pair<Long, Long>) {


        val weeklyData = getWeeklyEmptyData(timeRange)
        val weeklyRealyData = dataList.groupBy {
            SimpleDateFormat(commonDatePattern).format(Date(it.timestamp * 1000))
        }.toSortedMap(compareBy {
            SimpleDateFormat(commonDatePattern).parse(it)
        })

        weeklyRealyData.forEach { (date, dataList) ->
            weeklyData[date] = dataList.toMutableList()
        }

        val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        val categories = weeklyData.keys.map { dateString ->
            val date = SimpleDateFormat(commonDatePattern).parse(dateString)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK)
            daysOfWeek[dayOfWeekIndex - 1]
        }

        val resultArray = Array(7) { arrayOf(0, 0) }

        var lastSyncData: DayData? = null
        var recordIndex = -1

        weeklyData.values.forEachIndexed { index, dayData ->
            val minHeartBeat = dayData.minByOrNull { it.heartrate }?.heartrate ?: 0
            val maxHeartBeat = dayData.maxByOrNull { it.heartrate }?.heartrate ?: 0


            val currentDayLastSyncData = dayData.filter { it.heartrate > 0 }
                .maxByOrNull { it.timestamp }


            if (currentDayLastSyncData != null &&
                (lastSyncData == null || currentDayLastSyncData.timestamp > lastSyncData!!.timestamp)) {
                lastSyncData = currentDayLastSyncData
                recordIndex = index
            }

            resultArray[index] = arrayOf(minHeartBeat, maxHeartBeat)
        }


        val todayHeartBeat = lastSyncData?.heartrate ?: 0
        val todayHeartBeatTime = lastSyncData?.timestamp ?: 0

        val heartRateTodayData = Array<Any>(7) { -1 }
        if (recordIndex > -1 && recordIndex <= 6) {
            heartRateTodayData[recordIndex] = todayHeartBeat
        }


        println("Last Sync Data: $lastSyncData")
        println("Record Index: $recordIndex")

        val option =  getHeartRateChart(categories.toTypedArray())
        option.series = arrayOf(
            AASeriesElement()
                .name("")
                .borderRadius(2)
                .data(resultArray as Array<Any>)
                .color(gradientHeartRateColor)
            ,
            AASeriesElement()
                .type(AAChartType.Scatter)
                .name("")
                .showInLegend(false)
                .data(heartRateTodayData)
                .color(gradientCaloriesColor)
                .marker(
                    AAMarker()
                        .radius(5f)
                        .symbol("circle")

                )
        )
        mViewModel.chartOptions = option
        mDatabind.barChart.aa_drawChartWithChartOptions(option)



        val filteredData = dataList.filter { it.heartrate > 0 }


        val minHeartRate = filteredData.minByOrNull { it.heartrate }?.heartrate ?: 0


        val maxHeartRate = filteredData.maxByOrNull { it.heartrate }?.heartrate ?: 0





        mViewModel.min.set("$minHeartRate")
        mViewModel.max.set("$maxHeartRate")



        val weeklyRestHRValues = weeklyData.values.map { dayDataList ->
            calculateRestingHeartRate(dayDataList)
        }.filter {
            it>0
        }


        val maxRestHRValue = weeklyRestHRValues.maxOrNull() ?: 0
        val minRestHRValue = weeklyRestHRValues.minOrNull() ?: 0







        mViewModel.latest.set("$todayHeartBeat")


        mViewModel.time.set(formatTimestampTimeRange(timeRange))


        mViewModel.caloriesData = resultArray.toList() as List<Array<Int>>
        mViewModel.originData = weeklyData


        dayWeekSet =  if (todayHeartBeatTime == 0L){
            ""
        }else{
            "at ${formatTimestampMS(todayHeartBeatTime*1000)}"
        }
        lastSynHeartRate = "$todayHeartBeat"


        minCaloriesSet = "$minRestHRValue"
        maxCaloriesSet = "$maxRestHRValue"
        setsharedData()



    }

    fun monthList(dataList: List<DayData>,timeRange:Pair<Long, Long>) {


        val monthlyRealyData = dataList.groupBy {
            SimpleDateFormat(commonDatePattern).format(Date(it.timestamp * 1000)) // 将时间戳转换为日期格式
        }





        val monthlyData = getMonthlyEmptyData(timeRange)

        monthlyRealyData.forEach { (date, dataList) ->
            monthlyData[date] = dataList.toMutableList()
        }

        val resultList = mutableListOf<Array<Int>>()

        var lastSyncData: DayData? = null
        var recordIndex = -1

        monthlyData.entries.forEachIndexed { index, (_, dayData) ->
            val minHeartBeat = dayData.minByOrNull { it.heartrate }?.heartrate ?: 0
            val maxHeartBeat = dayData.maxByOrNull { it.heartrate }?.heartrate ?: 0


            val currentDayLastSyncData = dayData.filter { it.heartrate > 0 }
                .maxByOrNull { it.timestamp }

            if (currentDayLastSyncData != null &&
                (lastSyncData == null || currentDayLastSyncData.timestamp > lastSyncData!!.timestamp)) {
                lastSyncData = currentDayLastSyncData
                recordIndex = index
            }

            resultList.add(arrayOf(minHeartBeat, maxHeartBeat))
        }

        val resultArray = resultList.toTypedArray()


        val categories = monthlyData.keys.map { it.substring(3,5) }


        val todayHeartBeat = lastSyncData?.heartrate ?: 0
        val todayHeartBeatTime = lastSyncData?.timestamp ?: 0


        val heartRateTodayData = Array<Any>(monthlyData.size) { -1 }
        if (recordIndex > -1 && recordIndex < monthlyData.size) {
            heartRateTodayData[recordIndex] = todayHeartBeat
        }


        val option =  getHeartRateChart(categories.toTypedArray())
        option.series = arrayOf(
            AASeriesElement()
                .name("")
                .borderRadius(1)
                .data(resultArray as Array<Any>)
                .color(gradientHeartRateColor)
            ,
            AASeriesElement()
                .type(AAChartType.Scatter)
                .name("")
                .showInLegend(false)
                .data(heartRateTodayData)
                .color(gradientCaloriesColor)
                .marker(
                    AAMarker()
                        .radius(2f)
                        .symbol("circle")

                )
        )

        mViewModel.chartOptions = option
        mDatabind.barChart.aa_drawChartWithChartOptions(option)



        val filteredData = dataList.filter { it.heartrate > 0 }

        val minHeartRate = filteredData.minByOrNull { it.heartrate }?.heartrate ?: 0


        val maxHeartRate = filteredData.maxByOrNull { it.heartrate }?.heartrate ?: 0



        mViewModel.min.set("$minHeartRate")
        mViewModel.max.set("$maxHeartRate")

        mViewModel.time.set(formatTimestampTimeRange(timeRange))


        mViewModel.caloriesData = resultArray.toList() as List<Array<Int>>
        mViewModel.originData = monthlyData

        val weeklyRestHRValues = monthlyData.values.map { dayDataList ->
            calculateRestingHeartRate(dayDataList)
        }.filter {
            it>0
        }


        val maxRestHRValue = weeklyRestHRValues.maxOrNull() ?: 0
        val minRestHRValue = weeklyRestHRValues.minOrNull() ?: 0


        println("Maximum Resting Heart Rate of the Week: $maxRestHRValue")
        println("Minimum Resting Heart Rate of the Week: $minRestHRValue")




        minCaloriesSet = "$minRestHRValue"
        maxCaloriesSet = "$maxRestHRValue"


        dayWeekSet =  if (todayHeartBeatTime == 0L){
            ""
        }else{
            "at ${formatTimestampMS(todayHeartBeatTime*1000)}"
        }
        lastSynHeartRate = "$todayHeartBeat"


        setsharedData()


    }

    override fun createObserver() {

    }
    fun setsharedData(){
        if (isVisible && title == sharedChartDataModel.flag){
            sharedChartDataModel.min.set(minCaloriesSet)
            sharedChartDataModel.max.set(maxCaloriesSet)
            sharedChartDataModel.lastSynHeartRate.set(lastSynHeartRate)
            sharedChartDataModel.date.set(dayWeekSet)
            if (title == DateType.Day.name){
                sharedChartDataModel.restHR.set(restHR)
            }

        }

    }

    fun updateLasyHeartrate(rate :HeartRateStats?){
        if (rate?.averageHeartRate?:0 >0){
            val heartRateData = HeartRateData(timestamp = currentTimeInSec(),value  = rate?.averageHeartRate?:0,isMeasurement =true)
            DatabaseUtils.insertHeartRateData(heartRateData = heartRateData){
                if (mViewModel.recordIndex.get() == 0){
                    dayTodayList?.add( DayData(timestamp = it.timestamp, heartrate = it.value))
                    dayTodayList?.let { it1 -> dayList(it1, isRefresh = true) }

                    dayWeekSet = "at ${formatTimestampMS(rate?.timestamp?.toTimeMills()?:0)}"
                    sharedChartDataModel.date.set(dayWeekSet)
                    sharedChartDataModel.lastSynHeartRate.set("${rate?.averageHeartRate}")
                }

            }








        }
    }

    override fun getToolbarTitle(): String {
        return  ""
    }

    inner class ProxyClick {
        fun refreshChart() {
            mViewModel.loadHeartRateRangeData()
        }
    }

    companion object {
        fun newInstance(title: String): HeartRateColumnRangeFragment {
            val args = Bundle()
            args.putString("title", title)
            val fragment = HeartRateColumnRangeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
