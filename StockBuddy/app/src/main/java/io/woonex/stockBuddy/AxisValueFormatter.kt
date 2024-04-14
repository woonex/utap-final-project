package io.woonex.stockBuddy

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

public class AxisValueFormatter(private val referenceTimestamp : Long, private val intraDay: Boolean) : ValueFormatter()  {
    companion object {
        private val dataFormat : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        private val intraDayFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
    override fun getFormattedValue(value: Float) :String {
        val data :Long = value.toLong() + referenceTimestamp
        val date :LocalDateTime = LocalDateTime.ofEpochSecond(data, 0, ZoneOffset.UTC)
        return if (intraDay) {
            intraDayFormat.format(date)
        } else {
            dataFormat.format(date)
        }
    }
}