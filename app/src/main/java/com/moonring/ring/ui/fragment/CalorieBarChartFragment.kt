package com.moonring.ring.ui.fragment

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ThreadUtils
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AAMoveOverEventMessageModel
import com.module.common.base.BaseBarFragment
import com.module.common.ext.clickNoRepeat
import com.module.common.support.log.LogInstance
import com.moonring.ring.bean.DayData
import com.moonring.ring.databinding.FragmentCalorieBarChartBinding
import com.moonring.ring.enums.DateType
import com.moonring.ring.enums.GetDataByDay
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.support.moonring.DataFetcherStep
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.utils.commonDatePattern
import com.moonring.ring.utils.formatDate
import com.moonring.ring.utils.formatDayToHourRange
import com.moonring.ring.utils.formatTimestampTimeRange
import com.moonring.ring.utils.getCal
import com.moonring.ring.utils.getDayCalChart
import com.moonring.ring.utils.getDayCalendar
import com.moonring.ring.utils.getDayCar
import com.moonring.ring.utils.getDayStartEnd
import com.moonring.ring.utils.getDaylyData
import com.moonring.ring.utils.getMonthStartEnd
import com.moonring.ring.utils.getMonthlyEmptyData
import com.moonring.ring.utils.getWeekStartEnd
import com.moonring.ring.utils.getWeeklyCalChart
import com.moonring.ring.utils.getWeeklyEmptyData
import com.moonring.ring.utils.isToday
import com.moonring.ring.viewmodule.state.CalorieBarChartViewModel
import com.moonring.ring.viewmodule.state.SharedChartDataModel
import kotlinx.coroutines.launch
import com.moonring.jetpackmvvm.ext.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

/**
 *    author : Administrator

 *    time   : 2024/10/8/008
 *    desc   :
 */
class CalorieBarChartFragment : BaseBarFragment<CalorieBarChartViewModel, FragmentCalorieBarChartBinding>(), AAChartView.AAChartViewCallBack {

    private val mClick by lazy {
        ProxyClick()
    }

    val title by lazy {
        arguments?.getString("title")?:""
    }


    val  sharedChartDataModel: SharedChartDataModel by activityViewModels()


    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel

        mViewModel.type.set(title)

        mDatabind.textViewCalories.clickNoRepeat {


        }
        mDatabind.barChart.callBack = this@CalorieBarChartFragment

        mDatabind.rippleLeft.clickNoRepeat {
            mViewModel.recordIndex.set(mViewModel.recordIndex.get() - 1)
            changeDate()
        }
        mDatabind.rippleRight.clickNoRepeat {
            mViewModel.recordIndex.set(mViewModel.recordIndex.get() + 1)
            changeDate()
        }
        // 设置触摸监听器
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

    fun defaultTip(){
        mDatabind.slTooltip.visibility = View.INVISIBLE
        mDatabind.clTotal.visibility = View.VISIBLE

        if (mViewModel.chartOptions!=null){
            mDatabind.barChart.aa_refreshChartWithChartOptions(mViewModel.chartOptions!!)
        }

    }

    fun changeDate(){
        when (title) {
            DateType.Day.name -> {

                val (start, end) = getDayStartEnd(mViewModel.recordIndex.get())
                val calendar = getDayCalendar(mViewModel.recordIndex.get())

                mDatabind.slTooltip.visibility = View.INVISIBLE


                mViewModel.viewModelScope.launch {
                    val calDataList = DatabaseUtils.fetchCalData(fromTimestamp = start,toTimestamp = end)
                    val stepDataList =  calDataList.map {
                        DayData(timestamp = it.timestamp, step =  it.value)
                    }
                    dayList(stepDataList,calendar)
                }


            }
            DateType.Week.name -> {


                val (start, end) = getWeekStartEnd(mViewModel.recordIndex.get())
                mDatabind.slTooltip.visibility = View.INVISIBLE
                mViewModel.viewModelScope.launch {
                    val calDataList = DatabaseUtils.fetchCalData(fromTimestamp = start,toTimestamp = end)
                    val stepDataList =  calDataList.map {
                        DayData(timestamp = it.timestamp, step =  it.value)
                    }
                    weekList(stepDataList, Pair(start, end))
                }

            }
            DateType.Month.name -> {


                val (start, end) = getMonthStartEnd(mViewModel.recordIndex.get())
                mDatabind.slTooltip.visibility = View.INVISIBLE

                mViewModel.viewModelScope.launch {
                    val calDataList = DatabaseUtils.fetchCalData(fromTimestamp = start,toTimestamp = end)
                    val stepDataList =  calDataList.map {
                        DayData(timestamp = it.timestamp, step =  it.value)
                    }
                    monthList(stepDataList, timeRange = Pair (start, end))
                }

            }
            else -> {

            }

        }
        defaultTip()
    }

     var   barChartX = 0f
     var   barChartY = 0f

    override fun chartViewDidFinishLoad(aaChartView: AAChartView) {

    }


    var minCaloriesSet  = ""
    var maxCaloriesSet  = ""
    var dayWeekSet  = ""



    fun setsharedData(){
        if (isVisible && title == sharedChartDataModel.flag){
            sharedChartDataModel.min.set(minCaloriesSet)
            sharedChartDataModel.max.set(maxCaloriesSet)
            sharedChartDataModel.date.set(dayWeekSet)
        }

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
                    mViewModel.toolTipValue.set("$value")
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

    private fun setupBarChart() {

        val aaChartModel = getDayCar()

        mDatabind.barChart.aa_drawChartWithChartModel(aaChartModel)
    }

    override fun lazyLoadData() {
        super.lazyLoadData()


        if (title == DateType.Week.name){
            val (start, end) = getWeekStartEnd()
            mViewModel.viewModelScope.launch {
                val calDataList = DatabaseUtils.fetchCalData(fromTimestamp = start,toTimestamp = end)
                val stepDataList =  calDataList.map {
                    DayData(timestamp = it.timestamp, step =  it.value)
                }
                weekList(stepDataList, timeRange = Pair (start, end))
            }


        } else if (title ==  DateType.Day.name){

            val (start, end) = getDayStartEnd()
            mViewModel.viewModelScope.launch {
                val calDataList = DatabaseUtils.fetchCalData(fromTimestamp = start,toTimestamp = end)
                val stepDataList =  calDataList.map {
                    DayData(timestamp = it.timestamp, step =  it.value)
                }
                dayList(stepDataList)
            }

        } else if (title == DateType.Month.name){
            val (start, end) = getMonthStartEnd()
            mViewModel.viewModelScope.launch {
                val calDataList = DatabaseUtils.fetchCalData(fromTimestamp = start,toTimestamp = end)
                val stepDataList =  calDataList.map {
                    DayData(timestamp = it.timestamp, step =  it.value)
                }
                monthList(stepDataList, timeRange = Pair (start, end))
            }
        }
    }

    fun dayList(stepDataList: List<DayData>,calendar:Calendar =  Calendar.getInstance() ) {
        var timestart = System.currentTimeMillis()


        val dayStepData = getDaylyData(stepDataList)


        val categoriesAndCalories = dayStepData.mapNotNull { (day, dayDatas) ->
            val totalSteps = dayDatas.sumOf { it.step } .div(1000.0).roundToInt()


            if (totalSteps > 0) {
               day to totalSteps
            } else {
                day to 0
            }
        }


        val maxCaloriesData = categoriesAndCalories.maxByOrNull { it.second }
        val maxDay = maxCaloriesData?.first




        val categories = categoriesAndCalories.map { it.first }
        val caloriesData = categoriesAndCalories.map { it.second }


        val chartOptions = getDayCalChart(categories, caloriesData)


        if (caloriesData.all { it == 0 }) {
            chartOptions.yAxis?.apply {
                min = 0
                max = 100
            }
        }
        mViewModel.chartOptions = chartOptions



        mDatabind.barChart.aa_drawChartWithChartOptions(chartOptions)


        val maxCalories = caloriesData.maxOrNull()
        val minCalories = caloriesData.minOrNull()


        mViewModel.min.set("$minCalories")
        mViewModel.max.set("$maxCalories")


        minCaloriesSet = "$minCalories"
        maxCaloriesSet = "$maxCalories"
        dayWeekSet = "at "+formatDayToHourRange("${maxDay}")
        setsharedData()


        val totalCalories = stepDataList.sumOf { it.step }.div(1000.0).toInt()


        mViewModel.total.set("$totalCalories")


        if (isToday(calendar)){
            mViewModel.time.set("Today")
        }else{
            mViewModel.time.set(calendar.formatDate(commonDatePattern))
        }



        mViewModel.caloriesData = caloriesData
        mViewModel.categories = categories
        mViewModel.categoriesOrign = categories




    }

    fun weekList(stepDataList: List<DayData>,timeRange:Pair<Long, Long>) {
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
        }.div(1000.0).roundToInt()


        val totalCals = if (totalSteps > 0) {
            totalSteps
        } else {
            0.0
        }


        val avgCals = (totalCals.toDouble() / 7).toInt()
        mViewModel.avg.set("$avgCals")


        val caloriesData = weeklyData.map { (day, entries) ->
            val totalSteps = entries.sumOf { dayData ->
                dayData.step
            } .div(1000.0).roundToInt()


            val calories = if (totalSteps > 0) {
                totalSteps
            } else {
                0
            }
            day to calories
        }


        val maxCaloriesData = caloriesData.maxByOrNull { it.second }
        val maxCalories = maxCaloriesData?.second ?: 0


        val minCaloriesData = caloriesData.minByOrNull { it.second }
        val minCalories = minCaloriesData?.second ?: 0


        val dayWeek = maxCaloriesData?.first ?: 0



        mViewModel.min.set("$minCalories")
        mViewModel.max.set("$maxCalories")


        minCaloriesSet = "$minCalories"
        maxCaloriesSet = "$maxCalories"
        dayWeekSet = "on $dayWeek"
        setsharedData()



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

        val chartOptions = getWeeklyCalChart(categories, caloriesOnly)
        if (caloriesOnly.all { it == 0 }) {
            chartOptions.yAxis?.apply {
                min = 0
                max = 100
            }
        }
        mViewModel.chartOptions = chartOptions
        mDatabind.barChart.aa_drawChartWithChartOptions(chartOptions)

        var timeEnd = System.currentTimeMillis()
        LogInstance.i("week:${timeEnd-timestart}ms")
    }

    fun monthList(dataList: List<DayData>,timeRange:Pair<Long, Long>) {
        var timestart = System.currentTimeMillis()

        val monthlyRealyData = dataList.groupBy {
            SimpleDateFormat(commonDatePattern).format(Date(it.timestamp * 1000))
        }
        val monthlyData = getMonthlyEmptyData(timeRange)

        monthlyRealyData.forEach { (date, dataList) ->
            monthlyData[date] = dataList.toMutableList()
        }



        val totalSteps = dataList.sumOf { it.step } .div(1000.0).roundToInt()


        val totalCals = if (totalSteps > 0) {
            totalSteps
        } else {
            0.0
        }


        val avgCals = (totalCals.toDouble() / 30).toInt()
        mViewModel.avg.set("$avgCals")




        val dailyCalories = monthlyData.map { (day, entries) ->
            val dailySteps = entries.sumOf { it.step }.div(1000.0).roundToInt()
            day to dailySteps
        }


        val maxCaloriesData = dailyCalories.maxByOrNull { it.second }

        val minCaloriesData = dailyCalories.minByOrNull { it.second }


        val maxDay = maxCaloriesData?.first ?: ""
        val maxCalories = maxCaloriesData?.second ?: 0


        val minDay = minCaloriesData?.first ?: ""
        val minCalories = minCaloriesData?.second ?: 0



        minCaloriesSet = "$minCalories"
        maxCaloriesSet = "$maxCalories"
        dayWeekSet = "on $maxDay"
        setsharedData()


        mViewModel.time.set(formatTimestampTimeRange(timeRange))


        val categories = dailyCalories.map { it.first.substring(3,5) }
        val caloriesData = dailyCalories.map { it.second }
        mViewModel.caloriesData = caloriesData
        mViewModel.categories = categories
        mViewModel.categoriesOrign = dailyCalories.map { it.first }

        val chartOptions = getWeeklyCalChart(categories, caloriesData)
        if (caloriesData.all { it == 0 }) {
            chartOptions.yAxis?.apply {
                min = 0
                max = 100
            }
        }
        mViewModel.chartOptions = chartOptions

        mDatabind.barChart.aa_drawChartWithChartOptions(chartOptions)

        var timeEnd = System.currentTimeMillis()
        LogInstance.i("month :${timeEnd-timestart}ms")
    }


    override fun toolbarBackVisity(): Boolean {
        return true
    }

    override fun createObserver() {
        super.createObserver()





    }

    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {
        fun refreshChart() {
            mViewModel.loadCalorieData()
        }
    }


    companion object {
        fun newInstance(title: String): CalorieBarChartFragment {
            val args = Bundle()
            args.putString("title", title)
            val fragment = CalorieBarChartFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
