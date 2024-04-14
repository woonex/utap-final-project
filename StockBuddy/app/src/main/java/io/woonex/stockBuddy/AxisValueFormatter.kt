package io.woonex.stockBuddy

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

public class AxisValueFormatter(private val referenceTimestamp : Long) : ValueFormatter()  {
    companion object {
        private val dataFormat : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
    override fun getFormattedValue(value: Float) :String {
        val data :Long = value.toLong() + referenceTimestamp
        val date :LocalDate = LocalDate.ofEpochDay(data)
        return dataFormat.format(date)
    }
}