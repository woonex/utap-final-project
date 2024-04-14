package io.woonex.stockBuddy.alpha

import com.google.gson.annotations.SerializedName

data class WeeklyAlphaData(
    @SerializedName("Weekly Time Series")
    val weeklyAlphaData: Map<String, StockPrice>
)
