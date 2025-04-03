package com.moonring.ring.ui.fragment

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ThreadUtils
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AAMoveOverEventMessageModel
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.module.common.base.BaseBarFragment
import com.module.common.ext.clickNoRepeat
import com.module.common.ext.toThousandSeparator
import com.module.common.support.log.LogInstance
import com.moonring.ring.bean.DayData
import com.moonring.ring.databinding.FragmentStepBarChartBinding
import com.moonring.ring.enums.DateType
import com.moonring.ring.enums.GetDataByDay
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.moonring.DataFetcherStep
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.utils.calculateDistanceAndCalories
import com.moonring.ring.utils.commonDatePattern
import com.moonring.ring.utils.convertSecondsToHM
import com.moonring.ring.utils.currentTimeInSec
import com.moonring.ring.utils.formatDate
import com.moonring.ring.utils.formatDayToHourRange
import com.moonring.ring.utils.formatTimestampTimeRange
import com.moonring.ring.utils.getDayCalendar
import com.moonring.ring.utils.getDayStartEnd
import com.moonring.ring.utils.getDaylyData
import com.moonring.ring.utils.getMonthStartEnd
import com.moonring.ring.utils.getMonthlyEmptyData
import com.moonring.ring.utils.getStepDefaultChart
import com.moonring.ring.utils.getTodayStartTimestamp
import com.moonring.ring.utils.getWeekStartEnd
import com.moonring.ring.utils.getWeeklyEmptyData
import com.moonring.ring.utils.gradientStepColor
import com.moonring.ring.utils.isToday
import com.moonring.ring.viewmodule.state.SharedChartDataModel
import com.moonring.ring.viewmodule.state.StepBarChartViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 *    author : Administrator

 *    time   : 2024/10/12/012
 *    desc   :
 */
class StepBarChartFragment : BaseBarFragment<StepBarChartViewModel, FragmentStepBarChartBinding>(), AAChartView.AAChartViewCallBack  {

    private val mClick by lazy {
        ProxyClick()
    }

    val title by lazy {
        arguments?.getString("title")?:""
    }
    val  sharedChartDataModel: SharedChartDataModel by activityViewModels()

    var   barChartX = 0f
    var   barChartY = 0f




    var shareTitle = ""
    var shareIsDay = false
    var moveingTime = ""
    var total = ""
    var distance = ""

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel

        mViewModel.type.set(title)

        setupStepChart()

        mDatabind.barChart.callBack = this@StepBarChartFragment

        mDatabind.rippleLeft.clickNoRepeat {
            mViewModel.recordIndex.set(mViewModel.recordIndex.get() - 1)
            changeDate()
        }
        mDatabind.rippleRight.clickNoRepeat {
            mViewModel.recordIndex.set(mViewModel.recordIndex.get() + 1)
            changeDate()
        }

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

    }
    fun changeDate(){
        when (title) {
            DateType.Day.name -> {

                val (start, end) = getDayStartEnd(mViewModel.recordIndex.get())
                val calendar = getDayCalendar(mViewModel.recordIndex.get())

                mDatabind.slTooltip.visibility = View.INVISIBLE

                mViewModel.viewModelScope.launch {
                    val stepDataList = DatabaseUtils.fetchStepsData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = stepDataList.map {
                        DayData(timestamp = it.timestamp, step = it.value)
                    }.toMutableList()
                    dayList(dayDataList,calendar)

                }
            }
            DateType.Week.name -> {


                val (start, end) = getWeekStartEnd(mViewModel.recordIndex.get())
                mDatabind.slTooltip.visibility = View.INVISIBLE

                mViewModel.viewModelScope.launch {
                    val stepDataList = DatabaseUtils.fetchStepsData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = stepDataList.map {
                        DayData(timestamp = it.timestamp, step = it.value)
                    }
                    weekList(dayDataList.toMutableList(), timeRange = Pair (start, end))

                }

            }
            DateType.Month.name -> {

                val (start, end) = getMonthStartEnd(mViewModel.recordIndex.get())
                mDatabind.slTooltip.visibility = View.INVISIBLE

                mViewModel.viewModelScope.launch {
                    val stepDataList = DatabaseUtils.fetchStepsData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = stepDataList.map {
                        DayData(timestamp = it.timestamp, step = it.value)
                    }
                    monthList(dayDataList.toMutableList(), timeRange = Pair (start, end))

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
    fun setsharedData(){
        if (isVisible && title == sharedChartDataModel.flag){
            sharedChartDataModel.title.set(shareTitle)
            sharedChartDataModel.isDay.set(shareIsDay)
            sharedChartDataModel.moveingTime.set(moveingTime)
            sharedChartDataModel.total.set(total)
            sharedChartDataModel.distance.set(distance)
            if (title == DateType.Day.name){
                sharedChartDataModel.isDay.set(true)
            }else{
                sharedChartDataModel.isDay.set(false)
            }
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
                val value = mViewModel.caloriesData.getOrNull(dataIndex)
                if (value != null) {
                    mViewModel.toolTipValue.set("$value".toThousandSeparator())
                } else {
                    LogInstance.d("Error", "Data not found for index: $dataIndex")
                }
                val dataIndexValue  = mViewModel.categoriesOrign.getOrNull(dataIndex)


                if (dataIndexValue != null) {
                    when (title) {
                        DateType.Day.name -> {

                            mViewModel.toolTipTime.set( formatDayToHourRange("${dataIndexValue}"))
                        }
                        DateType.Week.name -> {
                            mViewModel.toolTipTime.set( "${dataIndexValue}")
                        }
                        DateType.Month.name -> {

                            mViewModel.toolTipTime.set( "${dataIndexValue}")
                        }
                        else -> {

                        }
                    }

                }


                mDatabind.clTotal.visibility = View.INVISIBLE
            }
        }


    }
    override fun lazyLoadData() {
        super.lazyLoadData()
        when (title) {
            DateType.Day.name -> {

                val (start, end) = getDayStartEnd()
                mViewModel.viewModelScope.launch {
                    val stepDataList = DatabaseUtils.fetchStepsData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = stepDataList.map {
                        DayData(timestamp = it.timestamp, step = it.value)
                    }.toMutableList()
                    dayList(dayDataList)

                }

            }
            DateType.Week.name -> {

                val (start, end) = getWeekStartEnd()

                mViewModel.viewModelScope.launch {
                    val stepDataList = DatabaseUtils.fetchStepsData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = stepDataList.map {
                        DayData(timestamp = it.timestamp, step = it.value)
                    }
                    weekList(dayDataList.toMutableList(), timeRange = Pair (start, end))

                }

            }
            DateType.Month.name -> {

                val (start, end) = getMonthStartEnd()

                mViewModel.viewModelScope.launch {
                    val stepDataList = DatabaseUtils.fetchStepsData(fromTimestamp = start,toTimestamp = end)


                    val dayDataList = stepDataList.map {
                        DayData(timestamp = it.timestamp, step = it.value)
                    }
                    monthList(dayDataList.toMutableList(), timeRange = Pair (start, end))

                }
            }
            else -> {

            }
        }
    }


    override fun createObserver() {

    }

    fun dayList(stepDataList: MutableList<DayData>,calendar:Calendar =  Calendar.getInstance() ) {



        checkAndAddStepData(stepDataList)

        val dayStepData = getDaylyData(stepDataList)

        val categoriesAndCalories = dayStepData.mapNotNull { (day, dayDatas) ->
            val totalSteps = dayDatas.sumOf { it.step }

            if (totalSteps > 0) {
                day to totalSteps
            } else {
                day to 0
            }
        }


        val categories = categoriesAndCalories.map { it.first }
        val caloriesData = categoriesAndCalories.map { it.second }

        val option = getStepDefaultChart(categories)


        option.series = arrayOf(
            AASeriesElement()
                .data(caloriesData.toTypedArray())
                .color(gradientStepColor)
                .borderRadius(2.5f)
        )
        if (caloriesData.all { it == 0 }) {
            option.yAxis?.apply {
                min = 0
                max = 3000
            }
        }
        mViewModel.chartOptions = option
        mDatabind.barChart.aa_drawChartWithChartOptions(option)



        val totalCalories = caloriesData.sum()
        mViewModel.total.set("$totalCalories")

        if (isToday(calendar)){
            mViewModel.time.set("Today")
        }else{
            mViewModel.time.set(calendar.formatDate(commonDatePattern))
        }

        val distanceTotal = calculateDistanceAndCalories(totalCalories)?.first
        mViewModel.totalDistance.set(distanceTotal)



        val walkingDuration = stepDataList?.count { it.step > 0 } ?: 0


        mViewModel.totalStepTime.set(walkingDuration)


        mViewModel.caloriesData = caloriesData
        mViewModel.categories = categories
        mViewModel.categoriesOrign = categories


        shareTitle = "Total Completed"
        shareIsDay = true
        moveingTime = "${convertSecondsToHM(walkingDuration*60) }"
        total = "$totalCalories"
        distance = "${distanceTotal}"
        if (isVisible){
            setsharedData()
        }



    }

    fun weekList(stepDataList: MutableList<DayData>,timeRange:Pair<Long, Long>) {

        checkAndAddStepData(stepDataList)

        var timestart = System.currentTimeMillis()


        val weeklyData = getWeeklyEmptyData(timeRange)
        var getWeeklyEmptyDataTime = System.currentTimeMillis()

        LogInstance.i("week getWeeklyEmptyDataTime:${getWeeklyEmptyDataTime-timestart}ms")

        val weeklyRealyData = stepDataList.groupBy {
            SimpleDateFormat(commonDatePattern).format(Date(it.timestamp * 1000))
        }.toSortedMap(compareBy {
            SimpleDateFormat(commonDatePattern).parse(it)
        })


        weeklyRealyData.forEach { (date, dataList) ->
            weeklyData[date] = dataList.toMutableList()
        }


        var weeklyDatatimeEnd = System.currentTimeMillis()

        LogInstance.i("week weeklyDatatimeEnd:${weeklyDatatimeEnd-timestart}ms")

        val totalSteps = stepDataList.sumOf {
            it.step
        }




        val avgCals = (totalSteps / 7)
        mViewModel.avg.set("$avgCals")



        val caloriesData = weeklyData.map { (day, entries) ->
            val totalSteps = entries.sumOf { dayData ->
                dayData.step
            } ?: 0


            day to totalSteps
        }


        val maxCaloriesData = caloriesData.maxByOrNull { it.second }
        val maxCalories = maxCaloriesData?.second ?: 0


        val minCaloriesData = caloriesData.minByOrNull { it.second }
        val minCalories = minCaloriesData?.second ?: 0


        val dayWeek = maxCaloriesData?.first ?: 0




        mViewModel.min.set("$minCalories")
        mViewModel.max.set("$maxCalories")




        mViewModel.time.set(formatTimestampTimeRange(timeRange))


        val caloriesOnly: List<Int> = caloriesData.map { it.second }


        val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")




        val categories = weeklyData.keys.map { dateString ->
            val date = SimpleDateFormat(commonDatePattern).parse(dateString)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK)
            daysOfWeek[dayOfWeekIndex - 1]
        }


        mViewModel.caloriesData = caloriesOnly
        mViewModel.categories = categories
        mViewModel.categoriesOrign = weeklyData.keys.map { it }


        val option = getStepDefaultChart(categories)
        option.series = arrayOf(
            AASeriesElement()
                .data(caloriesOnly.toTypedArray())
                .color(gradientStepColor)
                .borderRadius(2.5f)
        )
        if (caloriesOnly.all { it == 0 }) {
            option.yAxis?.apply {
                min = 0
                max = 3000
            }
        }
        mViewModel.chartOptions = option
        mDatabind.barChart.aa_drawChartWithChartOptions(option)

        val walkingDuration = stepDataList?.count { it.step > 0 } ?: 0




        val totalCalories = caloriesOnly.sum()
        mViewModel.total.set("$totalCalories")

        val distanceTotal = calculateDistanceAndCalories(totalCalories)?.first
        mViewModel.totalDistance.set(distanceTotal)


        shareTitle = "Total"
        shareIsDay = false
        moveingTime = "${convertSecondsToHM(walkingDuration*60) }"
        total = "$totalCalories"
        distance = "${distanceTotal}"
        if (isVisible){
            setsharedData()
        }


        var timeEnd = System.currentTimeMillis()
        LogInstance.i("week :${timeEnd-timestart}ms")
    }


    fun monthList(dataList: MutableList<DayData>,timeRange:Pair<Long, Long>) {

        checkAndAddStepData(dataList)

        var timestart = System.currentTimeMillis()

        val monthlyRealyData = dataList.groupBy {
            SimpleDateFormat(commonDatePattern).format(Date(it.timestamp * 1000))
        }
        val monthlyData = getMonthlyEmptyData(timeRange)

        monthlyRealyData.forEach { (date, dataList) ->
            monthlyData[date] = dataList.toMutableList()
        }



        val totalSteps = dataList.sumOf { it.step }



        val avgCals = (totalSteps / 30)
        mViewModel.avg.set("$avgCals")






        val dailyCalories = monthlyData.map { (day, entries) ->
            val dailySteps = entries.sumOf { it.step }
            day to dailySteps
        }


        val maxCaloriesData = dailyCalories.maxByOrNull { it.second }

        val minCaloriesData = dailyCalories.minByOrNull { it.second }


        val maxDay = maxCaloriesData?.first ?: ""
        val maxCalories = maxCaloriesData?.second ?: 0


        val minDay = minCaloriesData?.first ?: ""
        val minCalories = minCaloriesData?.second ?: 0



        mViewModel.time.set(formatTimestampTimeRange(timeRange))


        val categories = dailyCalories.map { it.first.substring(3,5) }
        val caloriesData = dailyCalories.map { it.second }
        mViewModel.caloriesData = caloriesData
        mViewModel.categories = categories
        mViewModel.categoriesOrign = dailyCalories.map { it.first }

        val option = getStepDefaultChart(categories)
        option.series = arrayOf(
            AASeriesElement()
                .data(caloriesData.toTypedArray())
                .color(gradientStepColor)
                .borderRadius(2.5f)
        )
        if (caloriesData.all { it == 0 }) {
            option.yAxis?.apply {
                min = 0
                max = 3000
            }
        }
        mViewModel.chartOptions = option
        mDatabind.barChart.aa_drawChartWithChartOptions(option)

        val walkingDuration = dataList?.count { it.step > 0 } ?: 0




        val totalCalories = caloriesData.sum()
        mViewModel.total.set("$totalCalories")

        val distanceTotal = calculateDistanceAndCalories(totalCalories)?.first
        mViewModel.totalDistance.set(distanceTotal)


        shareTitle = "Total"
        shareIsDay = false
        moveingTime = "${convertSecondsToHM(walkingDuration*60) }"
        total = "$totalCalories"
        distance = "${distanceTotal}"
        if (isVisible){
            setsharedData()
        }



        var timeEnd = System.currentTimeMillis()
        LogInstance.i("month :${timeEnd-timestart}ms")
    }
    fun checkAndAddStepData(stepDataList: MutableList<DayData>) {

    }


    private fun setupStepChart() {

    }

    override fun toolbarBackVisity(): Boolean {
        return true
    }


    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {
        fun refreshChart() {
            mViewModel.loadStepData()
        }
    }


    companion object {
        fun newInstance(title: String): StepBarChartFragment {
            val args = Bundle()
            args.putString("title", title)
            val fragment = StepBarChartFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
