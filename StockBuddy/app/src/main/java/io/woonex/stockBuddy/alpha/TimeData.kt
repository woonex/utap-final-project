package io.woonex.stockBuddy.alpha

import java.time.LocalDate

data class TimeData(
    val date : LocalDate,
    val stockPrice : StockPrice
)
