package io.woonex.stockBuddy.api

import android.util.Log
import io.woonex.stockBuddy.alpha.StockPrice
import io.woonex.stockBuddy.alpha.TimeData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

class AlphaRepository (private val alphaApi : AlphaApi) {
    suspend fun getWeekly(symbol:String) : List<TimeData> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        var allData = alphaApi.weeklyData(symbol).weeklyAlphaData.entries.stream()
        var allData = alphaApi.weeklyData().weeklyAlphaData.entries.stream()
            .map {
                TimeData(LocalDate.parse(it.key, dateFormatter), it.value)
            }
            .collect(Collectors.toList())

        allData = allData.sortedBy { it.date }
        return allData
    }
}