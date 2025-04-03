package com.moonring.ring.utils

import com.github.aachartmodel.aainfographics.aachartcreator.AAChartAnimationType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartLineDashStyleType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartStackingType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartSymbolStyleType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartSymbolType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AADataElement
import com.github.aachartmodel.aainfographics.aachartcreator.AAOptions
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aachartcreator.aa_toAAOptions
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAAnimation
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAChart
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAChartEvents
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAColumn
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAColumnrange
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AACrosshair
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AADataLabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAInactive
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALegend
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAMarker
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAPlotOptions
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAPoint
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAPointEvents
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AASeries
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStates
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AATitle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AATooltip
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAXAxis
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAYAxis
import com.github.aachartmodel.aainfographics.aatools.AAColor
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
import java.util.Collections

/**
 *    author : Administrator

 *    time   : 2024/9/29/029
 *    desc   :
 */



 fun getDayCar(): AAChartModel {
     val aaChartModel = AAChartModel()
         .chartType(AAChartType.Column)
         .title("卡路里-周")
         .titleStyle(AAStyle().color("#FFFFFF"))
         .subtitle("")
         .subtitleStyle(AAStyle().color("#FFFFFF"))
         .backgroundColor("rgba(0, 0, 0, 0)")
         .axesTextColor("#FFFFFF")
         .yAxisTitle("")
         .categories(arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日"))
         .series(arrayOf(
             AASeriesElement()
                 .name("卡路里")
                 .data(arrayOf(200, 300, 250, 400, 350, 450, 300))
                 .color("#800080")
         ))





     return  aaChartModel
 }

val gradientCaloriesColor = AAGradientColor.linearGradient(
    direction = AALinearGradientDirection.ToRight,
    startColor = "#FF0DFF",
    endColor = "#FDAD63"
)
fun getWeeklyCalChart(categories:List<String>,caloriesData:List<Int>,): AAOptions {


    val aaChartModel = AAChartModel()
        .chartType(AAChartType.Column)
        .title("")
        .titleStyle(AAStyle().color("#FFFFFF"))
        .subtitle("")
        .subtitleStyle(AAStyle().color("#FFFFFF"))
        .backgroundColor("rgba(0, 0, 0, 0)")
        .axesTextColor("#FFFFFF")
        .yAxisTitle("")
        .categories(categories.toTypedArray())
        .dataLabelsEnabled(false)
        .legendEnabled(false)
        .touchEventEnabled(true)
        .series(arrayOf(
            AASeriesElement()
                .data(caloriesData.toTypedArray())
                .color(gradientCaloriesColor)
                .borderRadius(2.5f)
        ))

    val aaOptions = aaChartModel.aa_toAAOptions()
    aaOptions.defaultOption()

    return aaOptions
}


val gradientStepColor = AAGradientColor.linearGradient(
    direction = AALinearGradientDirection.ToRight,
    startColor = "#FF903E",
    endColor = "#FBE947"
)

fun getStepDefaultChart(categories: List<String>): AAOptions {
    val aaChartModel = AAChartModel()
        .chartType(AAChartType.Column)
        .title("")
        .titleStyle(AAStyle().color("#FFFFFF"))
        .subtitle("")
        .subtitleStyle(AAStyle().color("#FFFFFF"))
        .backgroundColor("rgba(0, 0, 0, 0)")
        .axesTextColor("#FFFFFF")
        .yAxisTitle("")
        .categories(categories.toTypedArray())
        .dataLabelsEnabled(false)
        .legendEnabled(false)
        .touchEventEnabled(true)

    val aaOptions = aaChartModel.aa_toAAOptions()
    aaOptions.defaultOption()

    return aaOptions
}
fun getDayCalChart(categories: List<String>, caloriesData: List<Int>): AAOptions {
    val aaChartModel = AAChartModel()
        .chartType(AAChartType.Column)
        .title("")
        .titleStyle(AAStyle().color("#FFFFFF"))
        .subtitle("")
        .subtitleStyle(AAStyle().color("#FFFFFF"))
        .backgroundColor("rgba(0, 0, 0, 0)")
        .axesTextColor("#FFFFFF")
        .yAxisTitle("")
        .categories(categories.toTypedArray())
        .dataLabelsEnabled(false)
        .legendEnabled(false)
        .touchEventEnabled(true)
        .series(arrayOf(
            AASeriesElement()
                .data(caloriesData.toTypedArray())
                .color(gradientCaloriesColor)
                .borderRadius(2.5f)
        ))

    val aaOptions = aaChartModel.aa_toAAOptions()
    aaOptions.defaultOption()
    return aaOptions
}


fun AAOptions.defaultOption():AAOptions{


    yAxis?.apply {
        startOnTick = false
        gridLineColor("#EBEBF599")
        gridLineDashStyle(AAChartLineDashStyleType.ShortDash.value)
        labels(AALabels().style(AAStyle().color("#EBEBF599")))
    }

    xAxis?.apply {
        lineWidth(1f)
        lineColor("#808080")
        crosshair(AACrosshair().color("#FFFFFF").dashStyle(AAChartLineDashStyleType.Dash).width(1f))
    }

    tooltip?.apply {
        valueSuffix = " kcal"
        shared = true
        enabled = false
        useHTML = true
    }
    return this
}



fun getHeartRateChart(categories: Array<String>): AAOptions {

    val aaChartModel = AAChartModel()
        .chartType(AAChartType.Columnrange)
        .title("")
        .titleStyle(AAStyle().color("#FFFFFF"))
        .subtitle("")
        .subtitleStyle(AAStyle().color("#FFFFFF"))
        .categories(categories)
        .backgroundColor("rgba(0, 0, 0, 0)")
        .axesTextColor("#FFFFFF")
        .yAxisTitle("")
        .yAxisMin(0)
        .yAxisMax(200)
        .dataLabelsEnabled(false)
        .legendEnabled(false)
        .touchEventEnabled(true)

    val aaOptions = aaChartModel.aa_toAAOptions()
    aaOptions.defaultOption()
    return aaOptions
}
















val gradientOxygenColor = AAGradientColor.linearGradient(
    direction = AALinearGradientDirection.ToRight,
    startColor = "#2CE6FF00",
    endColor = "#2CE6FF"
)

fun getDailyOxygenChart(categories: Array<String>): AAOptions {
    val aaChartModel = defaultAAChartModel()
    aaChartModel.yAxisMin(0)
        .yAxisMax(200)
    aaChartModel.chartType(AAChartType.Scatter).categories(categories)


    val aaOptions = aaChartModel.aa_toAAOptions()
    aaOptions.defaultOption()

    return aaOptions
}

fun defaultAAChartModel():AAChartModel{
    val aaChartModel = AAChartModel()
        .title("")
        .titleStyle(AAStyle().color("#FFFFFF"))
        .subtitle("")
        .subtitleStyle(AAStyle().color("#FFFFFF"))
        .backgroundColor("rgba(0, 0, 0, 0)")
        .axesTextColor("#FFFFFF")
        .yAxisTitle("")
        .dataLabelsEnabled(false)
        .legendEnabled(false)
        .touchEventEnabled(true)
    return  aaChartModel
}


val gradientHeartRateColor = AAGradientColor.linearGradient(
    direction = AALinearGradientDirection.ToRight,
    startColor = "#FF5A5A33",
    endColor = "#FF5A5A"
)
fun getDailyHeartRateChart(categories: Array<String>): AAOptions {



    val aaChartModel = AAChartModel()
        .chartType(AAChartType.Scatter)
        .title("")
        .titleStyle(AAStyle().color("#FFFFFF"))
        .backgroundColor("rgba(0, 0, 0, 0)")
        .axesTextColor("#FFFFFF")
        .xAxisLabelsEnabled(true)
        .xAxisGridLineWidth(0)
        .yAxisTitle("")
        .yAxisGridLineWidth(1)
        .yAxisMin(0)
        .yAxisMax(200)
        .categories(categories)
        .dataLabelsEnabled(false)
        .legendEnabled(false)
        .touchEventEnabled(true)
    val aaOptions = aaChartModel.aa_toAAOptions()
    aaOptions.defaultOption()

    return aaOptions
}

fun emptyDayAASeriesElement():AASeriesElement{
    val results2 = mutableListOf<Array<Int>>()
    (0..23).forEach { hour ->
        results2.add(arrayOf(hour.toInt(), 0))
    }

    return  AASeriesElement()
        .data(results2.toTypedArray())
        .color(AAColor.Clear)
        .allowPointSelect(false)
        .enableMouseTracking(false)
        .marker(AAMarker()
            .radius(0f)
            .symbol("circle")
        )
}



fun createSleepChartView2(): AAOptions {
    val aaOptions = AAOptions()
        .chart(AAChart().type(AAChartType.Waterfall).inverted(true).backgroundColor("rgba(0,0,0,0)"))
        .title(AATitle().text("").style(AAStyle().color("#ffffff")))
        .xAxis(AAXAxis().visible(true).categories(arrayOf("Deep Sleep", "Light Sleep","Awake"))
            .gridLineWidth(0.0).lineWidth(0.0)
            .labels(AALabels().style(AAStyle().color("#ffffff"))))
        .yAxis(AAYAxis()
            .min(0)
            .lineWidth(0.3)
            .lineColor("#FFFFFF")
            .labels(AALabels().style(AAStyle().color("#ffffff")))
            .title(AATitle().text(""))
        )
        .legend(AALegend().enabled(false))
        .touchEventEnabled(true)
        .plotOptions(AAPlotOptions().series(AASeries()
            .animation(AAAnimation().easing(AAChartAnimationType.Linear).duration(800))
            .borderWidth(0))
        )


    val categoriesList = listOf(21, 22, 23, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
    val categories = categoriesList.map { it.toString() }
    aaOptions.yAxis?.categories(categories.toTypedArray())


    aaOptions.tooltip( AATooltip()
        .enabled(false))
    return aaOptions
}

fun AAOptions.setSleepPlotOptions():AAOptions{
    plotOptions?.apply {

        columnrange = AAColumnrange()
            .borderRadius(25)
            .grouping(false)
            .borderWidth(0f)


        series = AASeries()
            .stacking(AAChartStackingType.False)
            .dataLabels(
                AADataLabels()
                    .enabled(false)
                    .style(AAStyle()
                        .color("#ffffff")
                        .lineWidth(5f)
                    )
            )
            .animation(
                AAAnimation().easing(AAChartAnimationType.Linear).duration(800)
            )
            .groupPadding(0.5f)
            .maxPointWidth(10f)
            .pointWidth(10f)
            .allowPointSelect(false)
            .point(
                AAPoint()
                    .events(
                        AAPointEvents()
                    )
            )
            .marker(
                AAMarker()
                    .enabled(true)
                    .radius(5f)
            )


        column = AAColumn()
            .borderRadius(25)
            .borderWidth(0f)
    }
    return  this
}


val deepSleepColor = "#FFFFFF"
val lightSleepColor = "#57A1FF"
val awakeSleepColor = AAGradientColor.linearGradient(
    direction = AALinearGradientDirection.ToRight,
    startColor = "#52ACFF",
    endColor = "#725CFA"
)
