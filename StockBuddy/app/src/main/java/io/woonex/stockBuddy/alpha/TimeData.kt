package io.woonex.stockBuddy.alpha

import java.time.LocalDate
import java.time.LocalDateTime

data class TimeData(
    val date : LocalDateTime,
    val stockPrice : StockPrice
)
