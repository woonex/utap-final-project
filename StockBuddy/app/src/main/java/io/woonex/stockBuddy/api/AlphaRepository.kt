package io.woonex.stockBuddy.api

import io.woonex.stockBuddy.alpha.TimeData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

class AlphaRepository (private val alphaApi : AlphaApi) {

    suspend fun getDaily(symbol: String) : List<TimeData> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        var allData = alphaApi.dailyData(symbol).dailyAlphaData.entries.stream()
        var allData = alphaApi.dailyData().dailyAlphaData.entries.stream()
            .map {
                TimeData(LocalDate.parse(it.key, dateFormatter).atStartOfDay(), it.value)
            }
            .collect(Collectors.toList())

        allData = allData.sortedBy { it.date }
        return allData
    }
}