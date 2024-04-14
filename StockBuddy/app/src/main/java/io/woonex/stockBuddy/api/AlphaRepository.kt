package io.woonex.stockBuddy.api

import io.woonex.stockBuddy.alpha.TimeData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

class AlphaRepository (private val alphaApi : AlphaApi) {
    suspend fun getWeekly(symbol:String) : List<TimeData> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        var allData = alphaApi.weeklyData(symbol).weeklyAlphaData.entries.stream()
        var allData = alphaApi.weeklyData().weeklyAlphaData.entries.stream()
            .map {
                TimeData(LocalDate.parse(it.key, dateFormatter).atStartOfDay(), it.value)
            }
            .collect(Collectors.toList())

        allData = allData.sortedBy { it.date }
        return allData
    }

    suspend fun getDaily(symbol: String) : List<TimeData> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//        var allData = alphaApi.interdayData(symbol).interday5.entries.stream()
        var allData = alphaApi.interdayData().interday5.entries.stream()
            .map {
                TimeData(LocalDateTime.parse(it.key, dateFormatter), it.value)
            }
            .collect(Collectors.toList())

        allData = allData.sortedBy { it.date }
        return allData
    }
}