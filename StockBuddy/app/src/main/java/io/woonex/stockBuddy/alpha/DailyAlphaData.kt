package io.woonex.stockBuddy.alpha

import com.google.gson.annotations.SerializedName

data class DailyAlphaData(
    @SerializedName("Time Series (Daily)")
    val dailyAlphaData: Map<String, StockPrice>
)
