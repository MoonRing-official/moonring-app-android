package com.moonring.ring.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ThreadUtils
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartLineDashStyleType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AAMoveOverEventMessageModel
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.module.common.base.BaseBarFragment
import com.module.common.ext.clickNoRepeat
import com.module.common.support.log.LogInstance
import com.moonring.ring.bean.DayData
import com.moonring.ring.bean.room.SleepData
import com.moonring.ring.databinding.FragmentSleepChartBinding
import com.moonring.ring.enums.DateType
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.utils.HealthDataDetailsViewModel
import com.moonring.ring.utils.commonDatePattern
import com.moonring.ring.utils.createSleepChartView2
import com.moonring.ring.utils.formatDate
import com.moonring.ring.utils.formatSecondsToHourMinute
import com.moonring.ring.utils.getLastSevenDays
import com.moonring.ring.utils.getLastThirtyDays
import com.moonring.ring.utils.getMonthStartEnd
import com.moonring.ring.utils.getPreviousSevenDays
import com.moonring.ring.utils.getPreviousThirtyDays
import com.moonring.ring.utils.getTwoDayStartEnd
import com.moonring.ring.utils.getWeekStartEnd
import com.moonring.ring.utils.isToday
import com.moonring.ring.utils.setSleepPlotOptions
import com.moonring.ring.viewmodule.state.SharedChartDataModel
import com.moonring.ring.viewmodule.state.SleepChartViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 *    author : Administrator

 *    time   : 2024/10/12/012
 *    desc   :
 */
class SleepChartFragment : BaseBarFragment<SleepChartViewModel, FragmentSleepChartBinding>() , AAChartView.AAChartViewCallBack {

    private val mClick by lazy {
        ProxyClick()
    }
    val title by lazy {
        arguments?.getString("title")?:""
    }
    val  sharedChartDataModel: SharedChartDataModel by activityViewModels()


    var   barChartX = 0f
    var   barChartY = 0f
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel
        mViewModel.type.set(title)

        initSleepChart()
        mDatabind.rippleLeft.clickNoRepeat {
            mViewModel.recordIndex.set(mViewModel.recordIndex.get() - 1)
            changeDate()
        }
        mDatabind.rippleRight.clickNoRepeat {
            mViewModel.recordIndex.set(mViewModel.recordIndex.get() + 1)
            changeDate()
        }

        mDatabind.barChart.callBack = this@SleepChartFragment


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

    var lightSleepTitle = ""
    var deepSleepTitle = ""
    var deepSleepTime = ""
    var lightSleepTime = ""

    fun changeDate(){
        when (title) {
            DateType.Day.name -> {

                val (start, end) = getTwoDayStartEnd(mViewModel.recordIndex.get())


                mDatabind.slTooltip.visibility = View.INVISIBLE

                mViewModel.viewModelScope.launch {
                    val sleepDataList = DatabaseUtils.fetchSleepData(fromTimestamp = start, toTimestamp = end)
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, mViewModel.recordIndex.get())
                    dayList(calendar, sleepDataList)
                }
            }
            DateType.Week.name -> {


                val (start, end) = getWeekStartEnd(mViewModel.recordIndex.get(),-7)
                mDatabind.slTooltip.visibility = View.INVISIBLE

                mViewModel.viewModelScope.launch {
                    val sleepDataList = DatabaseUtils.fetchSleepData(fromTimestamp = start, toTimestamp = end)
                    val positiveIndex = kotlin.math.abs(mViewModel.recordIndex.get())
                    weekList(getPreviousSevenDays(positiveIndex), sleepDataList)
                }


            }
            DateType.Month.name -> {


                val (start, end) = getMonthStartEnd(mViewModel.recordIndex.get(),-30)
                mDatabind.slTooltip.visibility = View.INVISIBLE

                mViewModel.viewModelScope.launch {
                    val sleepDataList = DatabaseUtils.fetchSleepData(fromTimestamp = start, toTimestamp = end)
                    val positiveIndex = kotlin.math.abs(mViewModel.recordIndex.get())
                    monthList(getPreviousThirtyDays(positiveIndex), sleepDataList)
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
    fun dayList(calendar:Calendar = Calendar.getInstance(), sleepLists: List<SleepData>){

        val dataModel = HealthDataDetailsViewModel()
        val sleepItemToday = dataModel.sleepItemByDay(calendar, sleepLists)

        mViewModel.sleepItem = sleepItemToday

        val option  = createSleepChartView2()

        option.chart?.apply {
            inverted(true)
        }
        option.xAxis?.apply {
            lineWidth(0.0)
            labels(AALabels().style(AAStyle().color("#EBEBF599")))
            categories(arrayOf("Deep Sleep", "Light Sleep","Awake"))
            min = 0
            max = 2

        }
        option.yAxis?.apply {
            lineWidth(1)
            lineColor("#EBEBF599")
            labels(AALabels().style(AAStyle().color("#ffffff")))
            gridLineWidth(0)
        }


        if (sleepItemToday.data.isEmpty()) {

            option.yAxis?.apply {
                tickPositions(Array(categories?.size?:1) { it })
                min = 0
                max = categories?.size?:1 - 1
            }
        }
        option.series = dataModel.transformSeriesDay(sleepItemToday).toTypedArray()

        mViewModel.chartOptions = option
        mDatabind.barChart.aa_drawChartWithChartOptions(option)






        mViewModel.total.set(formatSecondsToHourMinute(sleepItemToday.totalDurationSecond))
        if (isToday(calendar)){
            mViewModel.time.set("Today")
        }else{
            mViewModel.time.set(calendar.formatDate(commonDatePattern))
        }




        lightSleepTitle = "Total Light Sleep"
        deepSleepTitle = "Total Deep Sleep"
        deepSleepTime =  formatSecondsToHourMinute(sleepItemToday.deepSleepDurationSecond)
        lightSleepTime =  formatSecondsToHourMinute(sleepItemToday.lightSleepDurationSecond)

        setsharedData()
    }

    fun weekList(dateList: List<Calendar>, sleepDataList: List<SleepData>){
        val sleepList =  sleepDataList.map {
            DayData(timestamp = it.timestamp, step =  it.value)
        }

        val dataModel = HealthDataDetailsViewModel()

        val dataList = dataModel.sleepItemByDays(dateList,sleepDataList)

        val option  = createSleepChartView2()
        option.setSleepPlotOptions()
        option.chart?.inverted = false

        val categories = dataList.mapNotNull { it.weekTitle }
        option.xAxis?.categories(categories.toTypedArray())


        val yCategoriesList = listOf(21, 22, 23, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)

        val yCategories = yCategoriesList.map { String.format("%02d:00", it) }
        option.yAxis?.categories(yCategories.toTypedArray())?.tickInterval(2)
        option.xAxis?.apply {
            lineWidth(1)
            lineColor("#EBEBF599")

            labels(AALabels().style(AAStyle().color("#ffffff")))

        }
        option.yAxis?.apply {

            lineWidth(0)
            labels(AALabels().style(AAStyle().color("#EBEBF599")))
            gridLineColor("#EBEBF599")
            gridLineDashStyle(AAChartLineDashStyleType.ShortDash.value)


            startOnTick = false


        }


        if (dataList.all { it.data.isEmpty() }) {
            option.yAxis?.let {
                val categoriesSize = it.categories?.size ?: 1

                val tickPositions = Array(categoriesSize / 2 + 1) { i -> i * 2 }

                it.tickPositions(tickPositions as Array<Any>)
                it.min = 0
                it.max = categoriesSize - 1
            }
        }




        option.series = dataModel.transformSeriesDay(dataList,  2f,
            10f).toTypedArray()
        mViewModel.chartOptions = option
        mDatabind.barChart.aa_drawChartWithChartOptions(option)
        mViewModel.sleepItemList = dataList



        var totalSleepTimeCal = 0
        var deepSleepTimeCal = 0
        var lightSleepTimeCal = 0
        dataList.forEach { data ->

            totalSleepTimeCal += data.totalDurationSecond
            deepSleepTimeCal += data.deepSleepDurationSecond
            lightSleepTimeCal += data.lightSleepDurationSecond
        }

        val avgSleepTime = totalSleepTimeCal/7
        val avgdeepSleepTime = deepSleepTimeCal/7
        val avgLightSleepTime = lightSleepTimeCal/7



        mViewModel.avg.set(formatSecondsToHourMinute(avgSleepTime))

        val timeRange = dataList.mapNotNull { it.timeTitle }

        val startWeeklyDay =timeRange.firstOrNull()?.let {
            if (it.length >= 5) it.substring(0, 5) else it
        }
        mViewModel.time.set("${startWeeklyDay}~${timeRange.lastOrNull()}")


        lightSleepTitle = "Avg Light Sleep"
        deepSleepTitle = "Avg Deep Sleep"
        deepSleepTime = formatSecondsToHourMinute(avgdeepSleepTime)
        lightSleepTime = formatSecondsToHourMinute(avgLightSleepTime)

        setsharedData()
    }

    fun monthList(dateList: List<Calendar>, sleepDataList: List<SleepData>){
        val sleepList =  sleepDataList.map {
            DayData(timestamp = it.timestamp, step =  it.value)
        }

        val dataModel = HealthDataDetailsViewModel()

        val dataList = dataModel.sleepItemByDays(dateList,sleepDataList)

        val option  = createSleepChartView2()
        option.setSleepPlotOptions()
        option.chart?.apply {
            inverted = false

        }

        val categories = dataList.mapNotNull { it.dayTitle }
        option.xAxis?.categories(categories.toTypedArray())


        val yCategoriesList = listOf(21, 22, 23, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)

        val yCategories = yCategoriesList.map { String.format("%02d:00", it) }
        option.yAxis?.categories(yCategories.toTypedArray())?.tickInterval(2)


        option.series = dataModel.transformSeriesDay(dataList,2f,6f,"-0.2").toTypedArray()


        option.xAxis?.apply {
            lineWidth(1)
            lineColor("#EBEBF599")

            labels(AALabels().style(AAStyle().color("#ffffff")))


        }
        option.yAxis?.apply {
            startOnTick = false
            lineWidth(0)
            labels(AALabels().style(AAStyle().color("#EBEBF599")))
            gridLineColor("#EBEBF599")
            gridLineDashStyle(AAChartLineDashStyleType.ShortDash.value)
        }
        if (dataList.all { it.data.isEmpty() }) {
            option.yAxis?.let {
                val categoriesSize = it.categories?.size ?: 1

                val tickPositions = Array(categoriesSize / 2 + 1) { i -> i * 2 }

                it.tickPositions(tickPositions as Array<Any>)
                it.min = 0
                it.max = categoriesSize - 1
            }
        }
        mViewModel.chartOptions = option
        mDatabind.barChart.aa_drawChartWithChartOptions(option)

        mViewModel.sleepItemList = dataList

        var totalSleepTimeCal = 0
        var deepSleepTimeCal = 0
        var lightSleepTimeCal = 0
        dataList.forEach { data ->

            totalSleepTimeCal += data.totalDurationSecond
            deepSleepTimeCal += data.deepSleepDurationSecond
            lightSleepTimeCal += data.lightSleepDurationSecond
        }

        val avgSleepTime = totalSleepTimeCal/30
        val avgdeepSleepTime = deepSleepTimeCal/30
        val avgLightSleepTime = lightSleepTimeCal/30




        mViewModel.avg.set(formatSecondsToHourMinute(avgSleepTime))

        val timeRange = dataList.mapNotNull { it.timeTitle }
        val startWeeklyDay =timeRange.firstOrNull()?.let {
            if (it.length >= 5) it.substring(0, 5) else it
        }
        mViewModel.time.set("${startWeeklyDay}~${timeRange.lastOrNull()}")


        lightSleepTitle = "Avg Light Sleep"
        deepSleepTitle = "Avg Deep Sleep"
        deepSleepTime = formatSecondsToHourMinute(avgdeepSleepTime)
        lightSleepTime = formatSecondsToHourMinute(avgLightSleepTime)

        setsharedData()

    }
    override fun lazyLoadData() {
        super.lazyLoadData()
        when (title) {
            DateType.Day.name -> {

                val (start, end) = getTwoDayStartEnd(mViewModel.recordIndex.get())


                mViewModel.viewModelScope.launch {

                    val sleepDataList = DatabaseUtils.fetchSleepData(fromTimestamp = start, toTimestamp = end)
                    dayList(sleepLists = sleepDataList)
                }

            }
            DateType.Week.name -> {


                val (start, end) = getWeekStartEnd(mViewModel.recordIndex.get(),-7)

                mViewModel.viewModelScope.launch {

                    val sleepDataList = DatabaseUtils.fetchSleepData(fromTimestamp = start, toTimestamp = end)
                    weekList(getLastSevenDays(), sleepDataList)
                }


            }
            DateType.Month.name -> {


                val (start, end) = getMonthStartEnd(mViewModel.recordIndex.get(),-30)
                mViewModel.viewModelScope.launch {

                    val sleepDataList = DatabaseUtils.fetchSleepData(fromTimestamp = start, toTimestamp = end)
                    monthList(getLastThirtyDays(), sleepDataList)
                }

            }
            else -> {

            }
        }
    }



    fun test(totalList: List<DayData>){
        totalList.forEachIndexed { index, dayData ->

        }
    }






    private fun initSleepChart() {

    }

    override fun toolbarBackVisity(): Boolean {
        return true
    }

    fun setsharedData(){
        if (isVisible && title == sharedChartDataModel.flag){
            sharedChartDataModel.lightSleepTitle.set(lightSleepTitle)
            sharedChartDataModel.deepSleepTitle.set(deepSleepTitle)
            sharedChartDataModel.deepSleepTime.set(deepSleepTime)
            sharedChartDataModel.lightSleepTime.set(lightSleepTime)
        }

    }
    override fun createObserver() {

    }

    override fun getToolbarTitle(): String {
        return ""
    }

    inner class ProxyClick {
        fun refreshChart() {

            mViewModel.loadSleepChartData()
        }
    }

    companion object {
        fun newInstance(title: String): SleepChartFragment {
            val args = Bundle()
            args.putString("title", title)
            val fragment = SleepChartFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun chartViewDidFinishLoad(aaChartView: AAChartView) {

    }

    @SuppressLint("SuspiciousIndentation")
    override fun chartViewMoveOverEventMessage(
        aaChartView: AAChartView,
        messageModel: AAMoveOverEventMessageModel
    ) {
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


                    when (title) {
                        DateType.Day.name -> {
                            val stIndex = mViewModel.sleepItem?.startDuration?.toInt()?:0

                            val realy = dataIndex - 1

                            mViewModel.sleepItem?.data?.getOrNull(realy)?.let {
                                val seconds = it.durationHour*3600

                                mViewModel.toolTipTitle.set(it.type.titleTip)
                                mViewModel.toolTipValue.set(formatSecondsToHourMinute(seconds.toInt()))
                                mViewModel.toolTipTime.set(it.durationRangeText)



                            }

                        }
                        DateType.Week.name -> {
                            mViewModel.sleepItemList?.getOrNull(x?.toInt()?:-1)?.let {
                                mViewModel.toolTipTitle.set("Sleep Time")

                                mViewModel.toolTipValue.set(formatSecondsToHourMinute(it.totalDurationSecond))
                                mViewModel.toolTipTime.set( it.timeTitle)
                            }

                        }

                        DateType.Month.name -> {
                            mViewModel.sleepItemList?.getOrNull(x?.toInt()?:-1)?.let {
                                mViewModel.toolTipTitle.set("Sleep Time")
                                mViewModel.toolTipValue.set(formatSecondsToHourMinute(it.totalDurationSecond))
                                mViewModel.toolTipTime.set( it.timeTitle)
                            }

                        }
                    }





                mDatabind.clTotal.visibility = View.INVISIBLE
            }
        }
    }
}
