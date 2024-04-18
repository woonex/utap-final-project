package io.woonex.stockBuddy

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import com.github.mikephil.charting.formatter.ValueFormatter

/** A utility class that sets up line charts
 *
 */
class LineChartUtils {
    companion object {
        fun setupLineChart(
            lineChart: LineChart,
            entries: List<Entry>,
            title: String = "Stock Prices",
            xAxisFormatter: ValueFormatter? = null) {

            //build x axis
            val xAxis: XAxis = lineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(true)
            xAxis.setDrawLabels(true)

            if (xAxisFormatter != null) {
                xAxis.valueFormatter = xAxisFormatter
            }

            //build y axis
            val yAxisRight: YAxis = lineChart.axisRight
            yAxisRight.setDrawLabels(true)
            yAxisRight.setDrawAxisLine(true)
            yAxisRight.setDrawGridLines(false)
            yAxisRight.setDrawZeroLine(false)


            lineChart.description.isEnabled = false
            lineChart.legend.isEnabled = false
            lineChart.setDrawMarkers(false)

            val dataSet = LineDataSet(entries, title)
            val first = entries[0]
            val last = entries[entries.size - 1]

            //setup colors
            dataSet.color = if (first.y > last.y) {
                Color.RED
            } else {
                Color.GREEN
            }
            dataSet.valueTextColor = Color.BLACK
            dataSet.setDrawValues(false)
            dataSet.setDrawCircles(false)

            //set data
            val lineData = LineData(dataSet)
            lineChart.data = lineData
            lineData.setDrawValues(false)

            lineChart.invalidate()
        }
    }

}