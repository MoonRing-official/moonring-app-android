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
import com.moonring.ring.bean.OxygenSensorData
import com.moonring.ring.bean.room.OxygenData
import com.moonring.ring.databinding.FragmentBloodOxygenChartBinding
import com.moonring.ring.enums.DateType
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.utils.commonDatePattern
import com.moonring.ring.utils.emptyDayAASeriesElement
import com.moonring.ring.utils.formatDate
import com.moonring.ring.utils.formatDayToHourRange
import com.moonring.ring.utils.formatTimestampMS
import com.moonring.ring.utils.formatTimestampTimeRange
import com.moonring.ring.utils.getDailyOxygenChart
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
import com.moonring.ring.utils.gradientOxygenColor
import com.moonring.ring.utils.isToday
import com.moonring.ring.viewmodule.state.BloodOxygenChartViewModel
import com.moonring.ring.viewmodule.state.SharedChartDataModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 *    author : Administrator

 *    time   : 2024/10/12/012
 *    desc   :
 */
class BloodOxygenChartFragment : BaseBarFragment<BloodOxygenChartViewModel, FragmentBloodOxygenChartBinding>() , AAChartView.AAChartViewCallBack {

    private val mClick by lazy {
        ProxyClick()
    }

    val title by lazy {
        arguments?.getString("title")?:""
    }
    val  sharedChartDataModel: SharedChartDataModel by activityViewModels()

    var   barChartX = 0f
    var   barChartY = 0f

    var minCaloriesSet  = ""
    var maxCaloriesSet  = ""
    var dayWeekSet  = ""

   var oxgentitle = ""
    var oxgenValue = ""
    var fatigue = 0
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel
        mViewModel.type.set(title)

        initBloodOxygenChart()

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

    fun changeDate(){
        when (title) {
            DateType.Day.name -> {

                val (start, end) = getDayStartEnd(mViewModel.recordIndex.get())
                val calendar = getDayCalendar(mViewModel.recordIndex.get())
                //
                mDatabind.slTooltip.visibility = View.INVISIBLE


                mViewModel.viewModelScope.launch {
                    val oxygenDataList = DatabaseUtils.fetchOxygenData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = oxygenDataList.map {
                        DayData(timestamp = it.timestamp).apply {
                            oxygen = it.value
                            fatigueValue = it.fatigueLevel
                        }
                    }
                    dayList(dayDataList,calendar)
                }


            }
            DateType.Week.name -> {


                val (start, end) = getWeekStartEnd(mViewModel.recordIndex.get())
                mDatabind.slTooltip.visibility = View.INVISIBLE

                mViewModel.viewModelScope.launch {
                    val oxygenDataList = DatabaseUtils.fetchOxygenData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = oxygenDataList.map {
                        DayData(timestamp = it.timestamp).apply {
                            oxygen = it.value
                            fatigueValue = it.fatigueLevel
                        }
                    }
                    weekList(dayDataList, timeRange = Pair (start, end))
                }


            }
            DateType.Month.name -> {


                val (start, end) = getMonthStartEnd(mViewModel.recordIndex.get())
                mDatabind.slTooltip.visibility = View.INVISIBLE

                mViewModel.viewModelScope.launch {
                    val oxygenDataList = DatabaseUtils.fetchOxygenData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = oxygenDataList.map {
                        DayData(timestamp = it.timestamp).apply {
                            oxygen = it.value
                            fatigueValue = it.fatigueLevel
                        }
                    }
                    monthList(dayDataList, timeRange = Pair (start, end))
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
                                val max =  valueList.maxByOrNull { it.oxygen }?.oxygen?:0
                                val min =  valueList.minByOrNull { it.oxygen }?.oxygen?:0
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

    fun setsharedData(){
        if (isVisible && title == sharedChartDataModel.flag){
            sharedChartDataModel.oxgentitle.set(oxgentitle)
            sharedChartDataModel.oxgenValue.set(oxgenValue)
            sharedChartDataModel.fatigue.set(fatigue)

        }
    }
    private fun initBloodOxygenChart() {

    }

    override fun toolbarBackVisity(): Boolean {
        return true
    }

    override fun createObserver() {



    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        when (title) {
            DateType.Day.name -> {


                val (start, end) = getDayStartEnd()

                mViewModel.viewModelScope.launch {
                    val oxygenDataList = DatabaseUtils.fetchOxygenData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = oxygenDataList.map {
                        DayData(timestamp = it.timestamp).apply {
                            oxygen = it.value
                            fatigueValue = it.fatigueLevel
                        }
                    }
                    dayTodayList = dayDataList.toMutableList()
                    dayList(dayDataList)
                }




            }
            DateType.Week.name -> {

                val (start, end) = getWeekStartEnd()

                mViewModel.viewModelScope.launch {
                    val oxygenDataList = DatabaseUtils.fetchOxygenData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = oxygenDataList.map {
                        DayData(timestamp = it.timestamp).apply {
                            oxygen = it.value
                            fatigueValue = it.fatigueLevel
                        }
                    }
                    weekList(dayDataList, timeRange = Pair (start, end))
                }




            }
            DateType.Month.name -> {

                val (start, end) = getMonthStartEnd()
                mViewModel.viewModelScope.launch {
                    val oxygenDataList = DatabaseUtils.fetchOxygenData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = oxygenDataList.map {
                        DayData(timestamp = it.timestamp).apply {
                            oxygen = it.value
                            fatigueValue = it.fatigueLevel
                        }
                    }
                    monthList(dayDataList, timeRange = Pair (start, end))
                }
            }
            else -> {

            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        dayTodayList?.clear()
    }

    fun dayList(dataList: List<DayData>,calendar:Calendar =  Calendar.getInstance(),isRefresh :Boolean = false ) {

        val heartBeatData = getDaylyData(dataList)



        val categories = heartBeatData.keys


        val results = mutableListOf<Array<Int>>()
        heartBeatData.forEach { (hour, data) ->
            data.forEach { dayData ->
                results.add(arrayOf(hour.toInt(), dayData.oxygen))
            }
        }

        val option = getDailyOxygenChart(categories.toTypedArray())
        option.yAxis?.apply {
            min = 60
            max = 100
        }
        option.series = arrayOf(
            AASeriesElement()
                .data(results.toTypedArray())
                .color(gradientOxygenColor)
                .marker(
                    AAMarker()
                        .radius(4f)
                        .symbol("circle")
                ), emptyDayAASeriesElement()
        )

        mViewModel.chartOptions = option
        if (isRefresh){
            mDatabind.barChart.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(arrayOf(
                AASeriesElement()
                    .data(results.toTypedArray())
                    .color(gradientOxygenColor)
                    .marker(
                        AAMarker()
                            .radius(4f)
                            .symbol("circle")
                    ), emptyDayAASeriesElement()
            ))
        }else{
            mDatabind.barChart.aa_drawChartWithChartOptions(option)
        }








        val filteredData = dataList.filter { it.oxygen > 0 }


        val minHeartRate = filteredData.minByOrNull { it.oxygen }?.oxygen ?: 0  // 如果没有数据，返回0


        val maxHeartRate = filteredData.maxByOrNull { it.oxygen }?.oxygen ?: 0  // 如果没有数据，返回0



        mViewModel.min.set("$minHeartRate")
        mViewModel.max.set("$maxHeartRate")



        minCaloriesSet = "$minHeartRate"
        maxCaloriesSet = "$maxHeartRate"

        val lastSyncData = dataList
            .filter { it.oxygen > 0 }
            .maxByOrNull { it.timestamp }
        val todayHeartBeat = lastSyncData?.oxygen ?: 0
        val todayHeartBeatTime = lastSyncData?.timestamp ?: 0

        mViewModel.latest.set("$todayHeartBeat")

        val fatigueValue = lastSyncData?.fatigueValue ?: 0

        dayWeekSet = "at ${formatTimestampMS(todayHeartBeatTime*1000)}"

        oxgentitle = "Last Sync"
        oxgenValue = "${todayHeartBeat}%"
        fatigue = fatigueValue
        setsharedData()





        if (isToday(calendar)){
            mViewModel.time.set("Today")
        }else{
            mViewModel.time.set(calendar.formatDate(commonDatePattern))
        }


        mViewModel.caloriesData = results
        mViewModel.originData = heartBeatData

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

        weeklyData.values.forEachIndexed { index, dayData ->
            val minHeartBeat = dayData.minByOrNull { it.oxygen }?.oxygen ?: 0
            val maxHeartBeat = dayData.maxByOrNull { it.oxygen }?.oxygen ?: 0
            resultArray[index] = arrayOf(minHeartBeat, maxHeartBeat)
        }



        val todayHeartBeat = weeklyData.values
            .flatten()
            .filter { it.oxygen > 0 }
            .maxByOrNull { it.timestamp }?.oxygen ?: -1

        val heartRateTodayData = Array<Any>(7) { -1 }
        heartRateTodayData[6] = todayHeartBeat


        val option =  getHeartRateChart(categories.toTypedArray())
        option.series = arrayOf(
            AASeriesElement()
                .name("")
                .borderRadius(2)
                .data(resultArray as Array<Any>)
                .color(gradientOxygenColor)
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
        option.yAxis?.apply {
            min = 60
            max = 100
        }
        mViewModel.chartOptions = option
        mDatabind.barChart.aa_drawChartWithChartOptions(option)



        val filteredData = dataList.filter { it.oxygen > 0 }


        val minHeartRate = filteredData.minByOrNull { it.oxygen }?.oxygen ?: 0


        val maxHeartRate = filteredData.maxByOrNull { it.oxygen }?.oxygen ?: 0



        mViewModel.min.set("$minHeartRate")
        mViewModel.max.set("$maxHeartRate")

       val fatigueSum =  filteredData.sumOf { it.fatigueValue }

        val avgfatigue = fatigueSum/7




        mViewModel.latest.set("$todayHeartBeat")


        mViewModel.time.set(formatTimestampTimeRange(timeRange))


        mViewModel.caloriesData = resultArray.toList() as List<Array<Int>>
        mViewModel.originData = weeklyData


        oxgentitle = "Min Oxygen Saturation"
        oxgenValue = "${minHeartRate}%"
        fatigue = avgfatigue
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

        monthlyData.forEach { (_, dayData) ->
            val minHeartBeat = dayData.minByOrNull { it.oxygen }?.oxygen ?: 0
            val maxHeartBeat = dayData.maxByOrNull { it.oxygen }?.oxygen ?: 0
            resultList.add(arrayOf(minHeartBeat, maxHeartBeat))
        }

        val resultArray = resultList.toTypedArray()


        val categories = monthlyData.keys.map { it.substring(3,5) }


        val todayHeartBeat = dataList.filter { it.oxygen > 0 }
            .maxByOrNull { it.timestamp }?.oxygen ?: -1

        val heartRateTodayData = Array<Any>(monthlyData.size) { -1 }
        heartRateTodayData[monthlyData.size-1] = todayHeartBeat


        val option =  getHeartRateChart(categories.toTypedArray())
        option.series = arrayOf(
            AASeriesElement()
                .name("")
                .borderRadius(1)
                .data(resultArray as Array<Any>)
                .color(gradientOxygenColor)
            ,
            AASeriesElement()
                .type(AAChartType.Scatter)
                .name("")
                .showInLegend(false)
                .data(heartRateTodayData)
                .color(gradientCaloriesColor)
                .marker(
                    AAMarker()
                        .radius(1f)
                        .symbol("circle")

                )
        )

        option.yAxis?.apply {
            min = 60
            max = 100
        }
        mViewModel.chartOptions = option
        mDatabind.barChart.aa_drawChartWithChartOptions(option)



        val filteredData = dataList.filter { it.oxygen > 0 }

        val minHeartRate = filteredData.minByOrNull { it.oxygen }?.oxygen ?: 0


        val maxHeartRate = filteredData.maxByOrNull { it.oxygen }?.oxygen ?: 0



        mViewModel.min.set("$minHeartRate")
        mViewModel.max.set("$maxHeartRate")

        mViewModel.time.set(formatTimestampTimeRange(timeRange))


        mViewModel.caloriesData = resultArray.toList() as List<Array<Int>>
        mViewModel.originData = monthlyData



        val fatigueSum =  filteredData.sumOf { it.fatigueValue }

        val avgfatigue = fatigueSum/30


        oxgentitle = "Min Oxygen Saturation"
        oxgenValue = "${minHeartRate}%"
        fatigue = avgfatigue
        setsharedData()

    }
    var dayTodayList: MutableList<DayData>?=null
    fun updateLasyOxgen(rate : OxygenSensorData?){
        var oxygen = rate?.oxygen?:0
        if (oxygen>0){

            if (mViewModel.recordIndex.get() == 0){
                oxgenValue = "${rate?.oxygen}%"
                sharedChartDataModel.oxgenValue.set(oxgenValue)
                dayTodayList?.add( DayData(timestamp = System.currentTimeMillis()/1000).apply {
                    this.oxygen = oxygen
                    this.fatigueValue = rate?.fatigueValue?:0
                })
                dayTodayList?.let { it1 -> dayList(it1, isRefresh = true) }
            }

        }

    }

    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {
        fun refreshChart() {

            mViewModel.loadChartData()
        }
    }

    companion object {
        fun newInstance(title: String): BloodOxygenChartFragment {
            val args = Bundle()
            args.putString("title", title)
            val fragment = BloodOxygenChartFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
